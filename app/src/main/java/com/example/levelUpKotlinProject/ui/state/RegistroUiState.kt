package com.example.levelUpKotlinProject.ui.state

import com.example.levelUpKotlinProject.domain.model.ErroresFormulario
import com.example.levelUpKotlinProject.domain.model.FormularioRegUsuario
import com.example.levelUpKotlinProject.domain.model.Usuario

/**
 * Estado de la UI de registro
 * Incluye el formulario actual y los errores de validación
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class RegistroUiState(
    //cambio
    val usuarios: List<Usuario> = emptyList(),

    val formulario: FormularioRegUsuario = FormularioRegUsuario(),
    val errores: ErroresFormulario = ErroresFormulario(),
    val estaGuardando: Boolean = false,
    val registroExitoso: Boolean = false
)
