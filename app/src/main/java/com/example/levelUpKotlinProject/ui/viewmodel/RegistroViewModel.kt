package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.ui.state.RegistroUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    init {
        cargarUsuariosEnTiempoReal()
    }

    private fun cargarUsuariosEnTiempoReal() {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarios().collect { listaUsuarios ->
                _uiState.update { it.copy(usuarios = listaUsuarios) }
            }
        }
    }

    // Funciones de UI (Inputs)
    fun onRutChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(rut = valor)) } }
    fun onNombreChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(nombreCompleto = valor)) } }
    fun onEmailChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(email = valor)) } }
    fun onTelefonoChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(telefono = valor)) } }
    fun onDireccionChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(direccion = valor)) } }
    fun onPasswordChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(password = valor)) } }
    fun onConfirmarPasswordChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(confirmarPassword = valor)) } }
    fun onTerminosChange(valor: Boolean) { _uiState.update { it.copy(formulario = it.formulario.copy(aceptaTerminos = valor)) } }
    fun onRegionChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(region = valor, comuna = "")) } }
    fun onComunaChange(valor: String) { _uiState.update { it.copy(formulario = it.formulario.copy(comuna = valor)) } }

    // VALIDACIÓN COMPLETA
    fun esFormularioValido(): Boolean {
        val form = _uiState.value.formulario

        // Reglas básicas (puedes agregar más)
        val rutValido = form.rut.isNotBlank()
        val nombreValido = form.nombreCompleto.isNotBlank()
        val emailValido = form.email.contains("@")
        val passValido = form.password.isNotBlank() && form.password == form.confirmarPassword
        val terminos = form.aceptaTerminos

        return rutValido && nombreValido && emailValido && passValido && terminos
    }

    // CRUD CON CALLBACK
    fun agregarUsuario(usuario: Usuario, onSuccess: () -> Unit) {
        _uiState.update { it.copy(estaGuardando = true) }
        viewModelScope.launch {
            try {
                usuarioRepository.insertarUsuario(usuario)
                _uiState.update { it.copy(estaGuardando = false, registroExitoso = true) }
                onSuccess() // Llamamos al callback para navegar
            } catch (e: Exception) {
                _uiState.update { it.copy(estaGuardando = false) }
            }
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioRepository.actualizarUsuario(usuario)
        }
    }

    fun eliminarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioRepository.eliminarUsuario(usuario)
        }
    }
}

class RegistroViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            return RegistroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}