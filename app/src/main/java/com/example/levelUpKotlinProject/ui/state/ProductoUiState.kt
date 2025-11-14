package com.example.levelUpKotlinProject.ui.state

import com.example.levelUpKotlinProject.domain.model.Producto

/**
 * Estado de la UI de productos
 *
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
