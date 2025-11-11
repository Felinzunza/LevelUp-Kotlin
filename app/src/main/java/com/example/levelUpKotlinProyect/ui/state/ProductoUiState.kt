package com.example.levelUpKotlinProyect.ui.state

import com.example.levelUpKotlinProyect.domain.model.Producto

/**
 * Estado de la UI de productos
 * Usa data class simple (sin sealed class como en StingCommerce)
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class ProductoUiState(
    val estaCargando: Boolean = false,
    val productos: List<Producto> = emptyList(),
    val error: String? = null
) {
    // Helper: verifica si hay productos
    val hayProductos: Boolean
        get() = productos.isNotEmpty()
}
