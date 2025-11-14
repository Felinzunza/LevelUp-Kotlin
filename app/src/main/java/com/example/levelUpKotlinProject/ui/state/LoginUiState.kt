package com.example.levelUpKotlinProject.ui.state

/**
 * Modelo de datos para el formulario de Login (solo email y password)
 */
data class FormularioLogin(
    val email: String = "",
    val password: String = ""
)

/**
 * Errores específicos del formulario de Login
 */
data class ErroresLogin(
    val emailError: String? = null,
    val passwordError: String? = null,
    val credencialesInvalidasError: String? = null
)

/**
 * Estado de la UI de Login
 */
data class LoginUiState(
    val formulario: FormularioLogin = FormularioLogin(),
    val errores: ErroresLogin = ErroresLogin(),
    val estaCargando: Boolean = false,
    val loginExitoso: Boolean = false
)