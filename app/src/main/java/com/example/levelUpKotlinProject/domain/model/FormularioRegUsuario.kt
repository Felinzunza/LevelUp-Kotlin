package com.example.levelUpKotlinProject.domain.model

/**
 * Datos del formulario de registro

 */
data class FormularioRegUsuario(

    val rut: String = "", // ¡Importante! Ahora se guarda aquí
    val nombreCompleto: String = "",
    val email: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val comuna: String = "", // Nuevo
    val region: String = "", // Nuevo
    val aceptaTerminos: Boolean = false

)
