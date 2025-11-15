package com.example.levelUpKotlinProyect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// 👈 1. Importar R para acceder a la carpeta res/font
import com.example.levelUpKotlinProject.R

// 👈 2. Definir la familia de fuentes "Roboto"
// Asegúrate de que los nombres de archivo (ej. roboto_regular) coincidan
// con los nombres de tus archivos .ttf en la carpeta res/font.
val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold)
    // Puedes añadir más (italic, light, etc.) si los tienes
)

// 👈 3. Crear el objeto Typography USANDO la familia Roboto
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Roboto, // <--- Usando Roboto
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Roboto, // <--- Usando Roboto
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    /* Otros estilos de texto van aquí */
)