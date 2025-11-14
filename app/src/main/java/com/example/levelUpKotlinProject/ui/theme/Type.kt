package com.example.levelUpKotlinProyect.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- 1. DEFINICIÓN DE COLORES BASE ---
// Definimos los colores aquí para asegurar que RetroColorPalette los vea.
val BlackBackground = Color(0xFF000000)
val WhiteText = Color(0xFFFFFFFF)
val RetroViolet = Color(0xFF8A2BE2)

// --- 2. ESQUEMA DE COLOR RETRO (DARK) ---
private val RetroColorPalette = darkColorScheme(
    primary = RetroViolet,          // Violeta para Cards, Iconos (el color principal)
    secondary = RetroViolet,        // Se puede usar para elementos secundarios
    background = BlackBackground,   // FONDO NEGRO
    surface = BlackBackground,      // FONDO NEGRO
    onPrimary = WhiteText,          // Texto que va sobre el violeta
    onBackground = WhiteText,       // Texto que va sobre el negro
    onSurface = WhiteText           // Texto que va sobre el negro
)

// --- 3. FUNCIÓN DEL TEMA ---
@Composable
fun LevelUpKotlinProjectTheme(
    // ... Parámetros por defecto
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RetroColorPalette,
        typography = Typography(), // Esta es la variable Typography de Type.kt
        content = content
    )
}