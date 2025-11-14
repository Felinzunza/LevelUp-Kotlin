package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    onLoginExitoso: () -> Unit,
    onVolverClick: () -> Unit,
    onValidarCredenciales: (String, String) -> Boolean,
    onGuardarSesion: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login Administrador") },
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
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de admin
            Text(
                text = "🔐",
                fontSize = 72.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Panel de Administración",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Ingresa tus credenciales",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Campo de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it
                    mensajeError = null
                },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    mensajeError = null
                },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (mostrarPassword) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (mostrarPassword) 
                                "Ocultar contraseña" 
                            else 
                                "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mensaje de error
            if (mensajeError != null) {
                Text(
                    text = mensajeError!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de login
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        mensajeError = "Completa todos los campos"
                    } else if (onValidarCredenciales(username, password)) {
                        onGuardarSesion(username)
                        onLoginExitoso()
                    } else {
                        mensajeError = "Credenciales incorrectas"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Info de credenciales (solo para desarrollo)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ Credenciales de prueba:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Usuario: admin",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Contraseña: admin123",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
