package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.levelUpKotlinProject.data.local.PreferenciasManager
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.ui.viewmodel.LoginViewModel

/**
 * LoginUsuarioScreen: Pantalla de inicio de sesión de usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUsuarioScreen(
    usuarioRepository: UsuarioRepository,
    preferenciasManager: PreferenciasManager, // 👈 AÑADIR ESTO
    onVolverClick: () -> Unit,
    onLoginExitoso: () -> Unit

) {
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.LoginViewModelFactory(usuarioRepository)
    )

    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Accede a tu cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de error de credenciales inválidas (global)
            uiState.errores.credencialesInvalidasError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botón Iniciar Sesión
            Button(
                onClick = {
                    // Ahora recibimos el objeto 'usuario' en el callback
                    viewModel.iniciarSesion { usuarioLogueado ->

                        // Guardamos email Y NOMBRE
                        preferenciasManager.guardarSesionUsuario(
                            email = usuarioLogueado.email,
                            nombre = usuarioLogueado.nombre,// 👈 Aquí está la magia
                            rut = usuarioLogueado.rut
                        )

                        onLoginExitoso()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.estaCargando
            ) {
                if (uiState.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("INICIAR SESIÓN", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Olvidé mi Contraseña (Placeholder)
            TextButton(
                onClick = { /* TODO: Implementar navegación a recuperar contraseña */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Olvidaste tu contraseña?")
            }
        }
    }
}