package com.example.levelUpKotlinProject.domain.model

/**
 * Datos del formulario de registro
 * Autor: Prof. Sting Adams Parra Silva
 */
data class FormularioRegUsuario(
    val nombreCompleto: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val aceptaTerminos: Boolean = false
)
