package com.example.levelUpKotlinProject.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography

// --- 1. DEFINICIÓN DE COLORES ---
// Usamos los códigos hexadecimales solicitados
val BlackBackground = Color(0xFF000000) // #000000
val WhiteText = Color(0xFFFFFFFF)       // #FFFFFF
val RetroViolet = Color(0xFF8A2BE2)     // El color de las cards

// --- 2. ESQUEMA DE COLOR OSCURO ---
// Este esquema asegura el contraste para un look "Dark Mode"
private val RetroColorPalette = darkColorScheme(
    primary = RetroViolet,          // Color principal (ej. botones/interactivos)
    background = BlackBackground,   // ¡FONDO NEGRO!
    surface = BlackBackground,      // ¡FONDO NEGRO para contenedores!
    onPrimary = WhiteText,          // Texto sobre el violeta
    onBackground = WhiteText,       // ¡TEXTO BLANCO sobre el negro!
    onSurface = WhiteText           // ¡TEXTO BLANCO sobre el negro!
)

// --- 3. APLICAR EL TEMA ---
@Composable
fun LevelUpKotlinProjectTheme(
    // ... Parámetros por defecto
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RetroColorPalette, // Usa el esquema de Fondo Negro
        typography = Typography(),         // Usa la tipografía Roboto configurada
        content = content
    )
}