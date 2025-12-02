package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.ui.viewmodel.LoginViewModel

/**
 * LoginAdminScreen: Pantalla de autenticación para administradores
 * 
 * Funcionalidades:
 * - Input de usuario y contraseña
 * - Validación de credenciales
 * - Mostrar/ocultar contraseña
 * - Mensajes de error
 * 
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAdminScreen(
    viewModel: LoginViewModel, // 1. Recibimos el ViewModel conectado a la BD
    onLoginExitoso: (String) -> Unit, // Pasamos el username para guardar sesión
    onVolverClick: () -> Unit
) {
    // 2. Observamos el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Error local para rol
    var errorRol by remember { mutableStateOf<String?>(null) }

    // Limpiar error al escribir
    LaunchedEffect(uiState.formulario.email, uiState.formulario.password) {
        if (errorRol != null) errorRol = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login Administrador") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (uiState.estaCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🔐", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Panel de Administración",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // CAMPO USUARIO
                OutlinedTextField(
                    value = uiState.formulario.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Usuario o Email") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO PASSWORD
                var mostrarPassword by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = uiState.formulario.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                            Icon(Icons.Default.Lock, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ERRORES
                if (uiState.errores.credencialesInvalidasError != null) {
                    Text(text = uiState.errores.credencialesInvalidasError!!, color = MaterialTheme.colorScheme.error)
                }
                if (errorRol != null) {
                    Text(text = errorRol!!, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN
                Button(
                    onClick = {
                        // 3. Usamos la lógica del ViewModel (Base de Datos)
                        viewModel.iniciarSesion { usuarioEncontrado ->
                            // 4. Verificamos que sea ADMIN
                            if (usuarioEncontrado.rol == Rol.ADMIN) {
                                onLoginExitoso(usuarioEncontrado.username)
                            } else {
                                errorRol = "⛔ Acceso denegado: No eres Administrador"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !uiState.estaCargando
                ) {
                    Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold)
                }

                // NOTA: Quité la tarjeta de "Credenciales de prueba" hardcoded
                // porque ahora usas usuarios reales de la BD.
            }
        }
    }
}