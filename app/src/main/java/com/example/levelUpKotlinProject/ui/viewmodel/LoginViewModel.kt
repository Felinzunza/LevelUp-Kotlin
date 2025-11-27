package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario
import com.example.levelUpKotlinProject.ui.state.ErroresLogin
import com.example.levelUpKotlinProject.ui.state.LoginUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // AÑADIDO

/**
 * LoginViewModel: Gestiona la lógica de inicio de sesión de usuario
 */
class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        val erroresActualizados = _uiState.value.errores.copy(
            emailError = ValidadorFormulario.validarEmail(email),
            credencialesInvalidasError = null
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(email = email),
            errores = erroresActualizados
        )
    }

    fun onPasswordChange(password: String) {
        val erroresActualizados = _uiState.value.errores.copy(
            passwordError = if (password.isBlank()) "La contraseña es obligatoria" else null,
            credencialesInvalidasError = null
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(password = password),
            errores = erroresActualizados
        )
    }

    private fun esFormularioValidoParaEnviar(): Boolean {
        val form = _uiState.value.formulario
        val errors = _uiState.value.errores

        return ValidadorFormulario.validarEmail(form.email) == null &&
                form.password.isNotBlank() &&
                errors.emailError == null &&
                errors.passwordError == null
    }

    /**
     * Intenta iniciar sesión llamando al repositorio.
     */
    fun iniciarSesion(onExito: (Usuario) -> Unit) {
        _uiState.value = _uiState.value.copy(estaCargando = true, errores = ErroresLogin())

        viewModelScope.launch {
            val email = _uiState.value.formulario.email
            val password = _uiState.value.formulario.password

            // 1. Buscamos el usuario completo en la BD
            // Nota: Esto asume que tienes un método en tu repo que devuelve el usuario completo si la pass coincide
            // Si no lo tienes, úsalo así:

            val esValido = usuarioRepository.validarCredenciales(email, password)

            if (esValido) {
                // Si es válido, buscamos sus datos para obtener el nombre
                // (UsuarioRepository debe tener obtenerUsuarioPorEmail o similar)
                val usuario = usuarioRepository.obtenerUsuarioPorEmail(email)
                    ?: usuarioRepository.obtenerUsuarioPorUsername(email) // Intento por username

                if (usuario != null) {
                    onExito(usuario) // 👈 Pasamos el usuario encontrado a la pantalla
                } else {
                    _uiState.value = _uiState.value.copy(estaCargando = false)
                }
            } else {
                val errores = ErroresLogin(credencialesInvalidasError = "Credenciales incorrectas")
                _uiState.value = _uiState.value.copy(estaCargando = false, errores = errores)
            }
        }
    }

/**
 * Factory para LoginViewModel
 */
class LoginViewModelFactory(private val usuarioRepository: UsuarioRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
}