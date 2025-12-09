package com.example.levelUpKotlinProject.domain.model

/**
 * Errores de validación del formulario
 * Cada campo puede tener su propio mensaje de error
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class ErroresFormulario(
    val rutError: String? = null, // 👈 AGREGAR ESTO
    val nombreCompletoError: String? = null,
    val emailError: String? = null,
    val telefonoError: String? = null,
    val direccionError: String? = null,
    val passwordError: String? = null,
    val confirmarPasswordError: String? = null,
    val terminosError: String? = null
) {
    // Función auxiliar para saber si NO hay ningún error
    fun esValido(): Boolean {
        return  rutError == null &&
                nombreCompletoError == null &&
                emailError == null &&
                telefonoError == null &&
                direccionError == null &&
                passwordError == null &&
                confirmarPasswordError == null &&
                terminosError == null
    }
}
