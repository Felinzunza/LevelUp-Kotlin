package com.example.levelUpKotlinProject.ui.state

import com.example.levelUpKotlinProject.domain.model.Orden

data class OrdenesUiState(
    // Bandera para mostrar un spinner de carga
    val estaCargando: Boolean = true, // Inicia en true, ya que siempre carga al abrir

    // Lista de las órdenes completas obtenidas del Repositorio
    val ordenes: List<Orden> = emptyList(),

    // Mensaje de error para mostrar al usuario si falla la DB o la red
    val error: String? = null
) {
    // Propiedad Helper para la UI
    val hayOrdenes: Boolean
        get() = ordenes.isNotEmpty()

    // Propiedad Helper: Útil para ocultar la lista si está cargando O si hay error
    val mostrarLista: Boolean
        get() = !estaCargando && error == null && hayOrdenes
}
