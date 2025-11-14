package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario
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
    fun iniciarSesion(onExito: () -> Unit) {
        val emailError = ValidadorFormulario.validarEmail(_uiState.value.formulario.email)
        val passwordError = if (_uiState.value.formulario.password.isBlank()) "La contraseña es obligatoria" else null

        if (emailError != null || passwordError != null) {
            _uiState.value = _uiState.value.copy(
                errores = _uiState.value.errores.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    credencialesInvalidasError = null
                )
            )
            return
        }

        if (esFormularioValidoParaEnviar()) {
            _uiState.value = _uiState.value.copy(estaCargando = true)

            val email = _uiState.value.formulario.email
            val password = _uiState.value.formulario.password

            // La llamada al repositorio se hace en IO (hilo secundario)
            viewModelScope.launch(Dispatchers.IO) {
                val credencialesValidas = usuarioRepository.validarCredenciales(email, password)

                // El cambio de UI y la navegación DEBEN hacerse en el hilo principal
                withContext(Dispatchers.Main) { // SOLUCIÓN: Cambiar al hilo principal para la UI/navegación
                    if (credencialesValidas) {
                        _uiState.value = _uiState.value.copy(
                            estaCargando = false,
                            loginExitoso = true,
                            errores = _uiState.value.errores.copy(credencialesInvalidasError = null)
                        )
                        onExito() // ESTO LLAMA A NAVIGATE, AHORA ES SEGURO EN EL HILO PRINCIPAL
                    } else {
                        _uiState.value = _uiState.value.copy(
                            estaCargando = false,
                            errores = _uiState.value.errores.copy(
                                credencialesInvalidasError = "Correo o contraseña incorrectos."
                            )
                        )
                    }
                }
            }
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