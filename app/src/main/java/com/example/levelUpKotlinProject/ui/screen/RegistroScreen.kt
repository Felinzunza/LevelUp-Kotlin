package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModelFactory
import java.util.Date

/**
 * RegistroScreen: Formulario de registro de usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    usuarioRepository: UsuarioRepository,
    onVolverClick: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val viewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(usuarioRepository)
    )

    val uiState by viewModel.uiState.collectAsState()

    // Estados locales para campos que el ViewModel original agrupaba o no tenía
    // (Idealmente, deberías agregar estos campos al RegistroUiState en el futuro)
    var nombrePila by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var usernameEditadoManualmente by remember { mutableStateOf(false) }

    // Control de visibilidad de contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos Personales",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 1. RUT
            OutlinedTextField(
                value = uiState.formulario.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT (ej: 12345678-9) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. NOMBRE Y APELLIDO (Separados para cumplir con el JSON)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombrePila,
                    onValueChange = {
                        nombrePila = it
                        // Actualizamos el VM también para validaciones básicas
                        viewModel.onNombreChange("$it $apellido")
                    },
                    label = { Text("Nombre *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = apellido,
                    onValueChange = {
                        apellido = it
                        viewModel.onNombreChange("$nombrePila $it")
                    },
                    label = { Text("Apellido *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // 3. EMAIL
            OutlinedTextField(
                value = uiState.formulario.email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                    // Lógica inteligente: Si el usuario no ha editado el username manualmente,
                    // sugerimos el username basado en el email (ej: felipe@... -> felipe)
                    if (!usernameEditadoManualmente) {
                        username = it.substringBefore("@")
                    }
                },
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errores.emailError != null,
                supportingText = {
                    uiState.errores.emailError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            )

            // 4. USERNAME (Nuevo campo requerido)
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameEditadoManualmente = true
                },
                label = { Text("Nombre de Usuario *") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 5. TELÉFONO
            OutlinedTextField(
                value = uiState.formulario.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = uiState.errores.telefonoError != null
            )

            Divider()

            Text(
                text = "Dirección",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 6. REGIÓN Y COMUNA
            SelectorRegionComuna(
                regionSeleccionada = uiState.formulario.region,
                comunaSeleccionada = uiState.formulario.comuna,
                onRegionChange = { viewModel.onRegionChange(it) },
                onComunaChange = { viewModel.onComunaChange(it) }
            )

            // 7. CALLE
            OutlinedTextField(
                value = uiState.formulario.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Calle y Número *") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.direccionError != null
            )

            Divider()

            Text(
                text = "Seguridad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 8. PASSWORD
            OutlinedTextField(
                value = uiState.formulario.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Ocultar" else "Mostrar")
                    }
                },
                isError = uiState.errores.passwordError != null,
                supportingText = {
                    uiState.errores.passwordError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                }
            )

            // 9. CONFIRMAR PASSWORD
            OutlinedTextField(
                value = uiState.formulario.confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar Contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                        Text(if (confirmarPasswordVisible) "Ocultar" else "Mostrar")
                    }
                },
                isError = uiState.errores.confirmarPasswordError != null
            )

            // 10. TÉRMINOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.formulario.aceptaTerminos,
                    onCheckedChange = { viewModel.onTerminosChange(it) }
                )
                Text(text = "Acepto los términos y condiciones")
            }
            if (uiState.errores.terminosError != null) {
                Text(text = uiState.errores.terminosError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN REGISTRAR
            Button(
                onClick = {
                    // Validamos que los campos locales también tengan datos
                    if (viewModel.esFormularioValido() && nombrePila.isNotBlank() && apellido.isNotBlank() && username.isNotBlank()) {

                        val nuevoUsuario = Usuario(
                            // IMPORTANTE: id = 0 indica a Room (y a JSON Server) que es un NUEVO usuario
                            id = 0,

                            rut = uiState.formulario.rut,

                            // Usamos los campos separados correctamente
                            nombre = nombrePila,
                            apellido = apellido,
                            username = username,

                            email = uiState.formulario.email,
                            password = uiState.formulario.password,
                            telefono = uiState.formulario.telefono,

                            fechaNacimiento = Date(), // Podrías agregar un DatePicker aquí si quisieras

                            region = uiState.formulario.region,
                            comuna = uiState.formulario.comuna,
                            direccion = uiState.formulario.direccion,

                            fechaRegistro = Date(),
                            rol = Rol.USUARIO // Por defecto siempre Usuario Normal
                        )

                        viewModel.agregarUsuario(
                            usuario = nuevoUsuario,
                            onSuccess = { onRegistroExitoso() }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Solo habilitar si los términos están aceptados y no está guardando
                enabled = uiState.formulario.aceptaTerminos && !uiState.estaGuardando
            ) {
                if (uiState.estaGuardando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Registrarse")
                }
            }
        }
    }
}