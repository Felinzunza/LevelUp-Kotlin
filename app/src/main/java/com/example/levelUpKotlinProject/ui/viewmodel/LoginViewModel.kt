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
import kotlinx.coroutines.withContext

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        // Tu lógica actual de validación se mantiene igual
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(email = email),
            errores = _uiState.value.errores.copy(credencialesInvalidasError = null)
        )
    }

    fun onPasswordChange(password: String) {
        // Tu lógica actual de validación se mantiene igual
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(password = password),
            errores = _uiState.value.errores.copy(credencialesInvalidasError = null)
        )
    }

    /**
     * Intenta iniciar sesión llamando al repositorio.
     */
    fun iniciarSesion(onExito: (Usuario) -> Unit) {
        // 1. Indicamos que está cargando
        _uiState.value = _uiState.value.copy(estaCargando = true, errores = ErroresLogin())

        viewModelScope.launch {
            val identificador = _uiState.value.formulario.email // Puede ser email o username
            val password = _uiState.value.formulario.password

            // 2. Validamos credenciales en hilo IO
            val credencialesValidas = usuarioRepository.validarCredenciales(identificador, password)

            if (credencialesValidas) {
                // 3. Si son válidas, recuperamos el objeto Usuario completo para obtener su ID
                val usuarioEncontrado = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorEmail(identificador)
                        ?: usuarioRepository.obtenerUsuarioPorUsername(identificador)
                }

                _uiState.value = _uiState.value.copy(estaCargando = false)

                if (usuarioEncontrado != null) {
                    // ✅ ÉXITO: Pasamos el usuario completo al callback
                    onExito(usuarioEncontrado)
                } else {
                    // Caso raro: validó ok pero no pudo recuperar el objeto (no debería pasar)
                    _uiState.value = _uiState.value.copy(
                        errores = ErroresLogin(credencialesInvalidasError = "Error al recuperar datos del usuario")
                    )
                }
            } else {
                // ❌ ERROR: Credenciales incorrectas
                val errores = ErroresLogin(credencialesInvalidasError = "Usuario o contraseña incorrectos")
                _uiState.value = _uiState.value.copy(estaCargando = false, errores = errores)
            }
        }
    }

    // Factory se mantiene igual
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