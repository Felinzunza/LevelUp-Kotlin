package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProject.R

/**
 * PortadaScreen: Pantalla de bienvenida de la tienda

 */
@Composable
fun PortadaScreen(
    onEntrarClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                // Carga la imagen desde res/drawable/logo_tienda.png
                painter = painterResource(id = R.drawable.icono_levelup),
                contentDescription = "Logo",
                modifier = Modifier.size(280.dp)
                // Nota: Se elimina el 'tint' porque la imagen ya tiene color
            )
            Spacer(modifier = Modifier.height(-2.dp))
            // Slogan
            Text(
                text = "Tu tienda gaming de confianza",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (-30).dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎮 Equipamiento Gaming",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Periféricos • Audio • Video\nMonitores • Almacenamiento • Mobiliario",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Botón principal
            Button(
                onClick = onEntrarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "ENTRAR A LA TIENDA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón Admin
            OutlinedButton(
                onClick = onAdminClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Panel Administrador",
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}
