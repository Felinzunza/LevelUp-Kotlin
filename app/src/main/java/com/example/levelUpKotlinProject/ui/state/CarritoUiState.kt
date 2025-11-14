package com.example.levelUpKotlinProject.ui.state

import com.example.levelUpKotlinProject.domain.model.Carrito
import com.example.levelUpKotlinProject.domain.model.ItemCarrito

/**
 * Estado de la UI del carrito
 * Mantiene los items y el carrito calculado
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class CarritoUiState(
    val items: List<ItemCarrito> = emptyList(),
    val estaCargando: Boolean = false
) {
    // Calcula el carrito en base a los items
    val carrito: Carrito
        get() = Carrito(items)
}
