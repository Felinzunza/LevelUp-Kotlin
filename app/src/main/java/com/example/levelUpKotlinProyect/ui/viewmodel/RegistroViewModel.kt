package com.example.levelUpKotlinProyect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelUpKotlinProyect.domain.validator.ValidadorFormulario
import com.example.levelUpKotlinProyect.ui.state.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * RegistroViewModel: Gestiona el formulario de registro
 * 
 * Responsabilidades:
 * - Guardar datos del formulario mientras el usuario escribe
 * - Validar cada campo en tiempo real
 * - Determinar si el formulario es válido para enviar
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
class RegistroViewModel : ViewModel() {
    
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
     * 
     * ¿Cuándo es válido?
     * - Todos los campos tienen contenido
     * - Ningún campo tiene errores
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
     * Intenta registrar al usuario
     * 
     * En un proyecto real, aquí llamarías a:
     * - Un repositorio que guarde en Room
     * - Una API que envíe los datos al servidor
     */
    fun registrar(onExito: () -> Unit) {
        if (esFormularioValido()) {
            _uiState.value = _uiState.value.copy(estaGuardando = true)
            
            // Simular envío (en la vida real sería: repositorio.registrarUsuario(formulario))
            // Por ahora solo marcamos como exitoso
            _uiState.value = _uiState.value.copy(
                estaGuardando = false,
                registroExitoso = true
            )
            
            onExito()
        }
    }
}

/**
 * Factory para RegistroViewModel
 * 
 * Nota: Este ViewModel no necesita parámetros, pero igual usamos Factory
 * para mantener consistencia en el código y facilitar futuras expansiones
 */
class RegistroViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            return RegistroViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
