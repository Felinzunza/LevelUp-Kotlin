package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.model.ErroresFormulario
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario
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


    fun actualizarErrores(errores: ErroresFormulario) {
        _uiState.update { it.copy(errores = errores) }
    }

    fun agregarUsuario(usuario: Usuario, onSuccess: () -> Unit) {
        // Marcamos que está cargando (para bloquear el botón o mostrar spinner)
        _uiState.update { it.copy(estaGuardando = true) }

        viewModelScope.launch {
            try {
                // Insertamos en BD
                usuarioRepository.insertarUsuario(usuario)

                // Si todo sale bien, actualizamos estado
                _uiState.update { it.copy(estaGuardando = false, registroExitoso = true) }

                // Y EJECUTAMOS LA ACCIÓN DE ÉXITO (Navegar / Guardar Sesión)
                onSuccess()

            } catch (e: Exception) {
                // Si falla, quitamos el loading y podrías manejar un mensaje de error global aquí
                _uiState.update { it.copy(estaGuardando = false) }
                e.printStackTrace()
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

    // VALIDACIÓN COMPLETA
    fun registrarUsuario(onSuccess: () -> Unit) {
        // 1. Ejecutar validación usando tu Object Validador
        val resultadoValidacion = ValidadorFormulario.validarFormulario(_uiState.value.formulario)

        // 2. Actualizar el estado con los errores (si los hay)
        _uiState.update { it.copy(errores = resultadoValidacion) }

        // 3. Verificar si es válido
        if (resultadoValidacion.esValido()) {
            // Si es válido, procedemos a construir el usuario y guardar
            val form = _uiState.value.formulario

            // Construcción del objeto Usuario (lógica movida desde la UI al VM para limpieza)
            val nuevoUsuario = Usuario(
                id = java.util.UUID.randomUUID().toString(),
                rut = form.rut,
                nombre = form.nombreCompleto.substringBefore(" "),
                apellido = form.nombreCompleto.substringAfter(" ", ""),
                username = form.email.substringBefore("@"),
                email = form.email,
                password = form.password,
                telefono = form.telefono,
                direccion = form.direccion,
                region = form.region,
                comuna = form.comuna,
                fechaNacimiento = java.util.Date(),
                fechaRegistro = java.util.Date(),
                rol = com.example.levelUpKotlinProject.domain.model.Rol.USUARIO,
                fotoPerfil = "" // La foto se pasará como argumento si es necesario o se maneja en UI
            )

            guardarEnBaseDeDatos(nuevoUsuario, onSuccess)
        }


}
    private fun guardarEnBaseDeDatos(usuario: Usuario, onSuccess: () -> Unit) {
        _uiState.update { it.copy(estaGuardando = true) }
        viewModelScope.launch {
            try {
                usuarioRepository.insertarUsuario(usuario)
                _uiState.update { it.copy(estaGuardando = false, registroExitoso = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(estaGuardando = false) }
            }
        }
    }

    // Para actualizar la foto antes de guardar (si la lógica de foto sigue en la UI)
    // Puedes llamar a esto justo antes de registrarUsuario si prefieres construir el objeto en la UI
    fun agregarUsuarioDesdeUi(usuario: Usuario, onSuccess: () -> Unit) {
        // Validamos de nuevo por seguridad o confiamos en que la UI llamó a validar antes
        // Para este ejemplo, usaremos la lógica anterior simplificada:

        guardarEnBaseDeDatos(usuario, onSuccess)
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