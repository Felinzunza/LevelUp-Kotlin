package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
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
                text = "Completa tus datos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // 1. CAMPO RUT (Manual)
            OutlinedTextField(
                value = uiState.formulario.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT (ej: 12345678-9) *") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Nombre Completo
            OutlinedTextField(
                value = uiState.formulario.nombreCompleto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.nombreCompletoError != null,
                supportingText = {
                    uiState.errores.nombreCompletoError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo: Email
            OutlinedTextField(
                value = uiState.formulario.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errores.emailError != null,
                supportingText = {
                    uiState.errores.emailError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo: Teléfono
            OutlinedTextField(
                value = uiState.formulario.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono (9 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = uiState.errores.telefonoError != null,
                supportingText = {
                    uiState.errores.telefonoError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector Región/Comuna
            SelectorRegionComuna(
                regionSeleccionada = uiState.formulario.region,
                comunaSeleccionada = uiState.formulario.comuna,
                onRegionChange = { nuevaRegion ->
                    viewModel.onRegionChange(nuevaRegion)
                },
                onComunaChange = { nuevaComuna ->
                    viewModel.onComunaChange(nuevaComuna)
                }
            )

            // Campo: Dirección (Calle)
            OutlinedTextField(
                value = uiState.formulario.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección (Calle y Número)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.direccionError != null,
                supportingText = {
                    uiState.errores.direccionError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo: Contraseña
            OutlinedTextField(
                value = uiState.formulario.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Ocultar" else "Mostrar",
                            fontSize = 12.sp
                        )
                    }
                },
                isError = uiState.errores.passwordError != null,
                supportingText = {
                    uiState.errores.passwordError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo: Confirmar Contraseña
            OutlinedTextField(
                value = uiState.formulario.confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmarPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                        Text(
                            text = if (confirmarPasswordVisible) "Ocultar" else "Mostrar",
                            fontSize = 12.sp
                        )
                    }
                },
                isError = uiState.errores.confirmarPasswordError != null,
                supportingText = {
                    uiState.errores.confirmarPasswordError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Checkbox: Términos y condiciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = uiState.formulario.aceptaTerminos,
                    onCheckedChange = { viewModel.onTerminosChange(it) }
                )
                Text(
                    text = "Acepto los términos y condiciones",
                    modifier = Modifier.padding(start = 8.dp, top = 12.dp)
                )
            }

            uiState.errores.terminosError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Registrarse
            Button(
                onClick = {
                    // Validamos formulario
                    if (viewModel.esFormularioValido()) {

                        val nuevoUsuario = Usuario(
                            id = 0,
                            // ✅ CORRECCIÓN: Usamos el RUT escrito por el usuario
                            rut = uiState.formulario.rut,

                            nombre = uiState.formulario.nombreCompleto,
                            apellido = "", // (Opcional: podrías separar el nombre)
                            username = uiState.formulario.email.split("@")[0],
                            email = uiState.formulario.email,
                            password = uiState.formulario.password,
                            telefono = uiState.formulario.telefono,
                            fechaNacimiento = Date(),

                            // ✅ DATOS GEOGRÁFICOS
                            region = uiState.formulario.region,
                            comuna = uiState.formulario.comuna,
                            direccion = uiState.formulario.direccion,

                            fechaRegistro = Date(),
                            rol = Rol.USUARIO
                        )

                        // Guardamos y navegamos
                        viewModel.agregarUsuario(
                            usuario = nuevoUsuario,
                            onSuccess = {
                                onRegistroExitoso()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState.formulario.aceptaTerminos
            ) {
                if (uiState.estaGuardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }
        }
    }
}