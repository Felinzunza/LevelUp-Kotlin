package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository // AÑADIDO
import com.example.levelUpKotlinProject.domain.model.Usuario // AÑADIDO
import com.example.levelUpKotlinProject.domain.model.Rol // AÑADIDO
import java.util.Date // AÑADIDO
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario
import com.example.levelUpKotlinProject.ui.state.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * RegistroViewModel: Gestiona el formulario de registro
 * * Autor: Prof. Sting Adams Parra Silva
 */
class RegistroViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() { // MODIFICADO

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre completo y valida
     */
    fun onNombreChange(nombre: String) {
        val errores = _uiState.value.errores.copy(
            nombreCompletoError = ValidadorFormulario.validarNombreCompleto(nombre)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(nombreCompleto = nombre),
            errores = errores
        )
    }

    /**
     * Actualiza el email y valida formato
     */
    fun onEmailChange(email: String) {
        val errores = _uiState.value.errores.copy(
            emailError = ValidadorFormulario.validarEmail(email)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(email = email),
            errores = errores
        )
    }

    /**
     * Actualiza el teléfono y valida formato chileno
     */
    fun onTelefonoChange(telefono: String) {
        val errores = _uiState.value.errores.copy(
            telefonoError = ValidadorFormulario.validarTelefono(telefono)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(telefono = telefono),
            errores = errores
        )
    }

    /**
     * Actualiza la dirección y valida longitud mínima
     */
    fun onDireccionChange(direccion: String) {
        val errores = _uiState.value.errores.copy(
            direccionError = ValidadorFormulario.validarDireccion(direccion)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(direccion = direccion),
            errores = errores
        )
    }

    /**
     * Actualiza la contraseña y valida requisitos de seguridad
     */
    fun onPasswordChange(password: String) {
        val errores = _uiState.value.errores.copy(
            passwordError = ValidadorFormulario.validarPassword(password)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(password = password),
            errores = errores
        )
    }

    /**
     * Actualiza confirmación de contraseña y valida que coincidan
     */
    fun onConfirmarPasswordChange(confirmarPassword: String) {
        val errores = _uiState.value.errores.copy(
            confirmarPasswordError = ValidadorFormulario.validarConfirmarPassword(
                _uiState.value.formulario.password,
                confirmarPassword
            )
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(confirmarPassword = confirmarPassword),
            errores = errores
        )
    }

    /**
     * Actualiza el checkbox de términos
     */
    fun onTerminosChange(acepta: Boolean) {
        val errores = _uiState.value.errores.copy(
            terminosError = ValidadorFormulario.validarTerminos(acepta)
        )
        _uiState.value = _uiState.value.copy(
            formulario = _uiState.value.formulario.copy(aceptaTerminos = acepta),
            errores = errores
        )
    }

    /**
     * Verifica si el formulario completo es válido
     */
    fun esFormularioValido(): Boolean {
        val form = _uiState.value.formulario
        val errors = _uiState.value.errores

        return form.nombreCompleto.isNotBlank() &&
                form.email.isNotBlank() &&
                form.telefono.isNotBlank() &&
                form.direccion.isNotBlank() &&
                form.password.isNotBlank() &&
                form.confirmarPassword.isNotBlank() &&
                form.aceptaTerminos &&
                errors.nombreCompletoError == null &&
                errors.emailError == null &&
                errors.telefonoError == null &&
                errors.direccionError == null &&
                errors.passwordError == null &&
                errors.confirmarPasswordError == null &&
                errors.terminosError == null
    }

    /**
     * Intenta registrar al usuario (LÓGICA REAL IMPLEMENTADA Y CONSTRUCTOR CORREGIDO)
     */
    fun registrar(onExito: () -> Unit) {
        if (esFormularioValido()) {
            _uiState.value = _uiState.value.copy(estaGuardando = true)

            viewModelScope.launch {
                val formulario = _uiState.value.formulario

                // 1. Mapear FormularioRegUsuario a Usuario (CORREGIDO)
                val nuevoUsuario = Usuario(
                    id = 0,
                    rut = "TEMPORAL_0", // Relleno
                    nombre = formulario.nombreCompleto.substringBefore(" ", formulario.nombreCompleto), // Extrae el nombre
                    apellido = formulario.nombreCompleto.substringAfterLast(" ", "N/A"), // Intenta obtener el apellido
                    fechaNacimiento = Date(), // Relleno
                    email = formulario.email,
                    password = formulario.password,
                    telefono = formulario.telefono,
                    fechaRegistro = Date(), // Relleno
                    rol = Rol.USUARIO
                    // Se omitió 'direccion' ya que no existe en el modelo Usuario
                )

                // 2. Insertar en el repositorio (base de datos)
                val idGenerado = usuarioRepository.insertarUsuario(nuevoUsuario)

                // ... (lógica de manejo de éxito)
                if (idGenerado > 0) {
                    onExito()
                }
            }
        }
    }
}

/**
 * Factory para RegistroViewModel (MODIFICADO)
 */
class RegistroViewModelFactory(private val usuarioRepository: UsuarioRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            return RegistroViewModel(usuarioRepository) as T // MODIFICADO: Pasa el repositorio
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}