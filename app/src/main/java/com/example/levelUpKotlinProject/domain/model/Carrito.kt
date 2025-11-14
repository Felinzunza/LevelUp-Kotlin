package com.example.levelUpKotlinProject.domain.model

/**
 * Representa el carrito de compras completo
 * Incluye lógica de negocio (totales calculados)
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class Carrito(
    val items: List<ItemCarrito> = emptyList()
) {
    // Cantidad total de items en el carrito
    val cantidadTotal: Int
        get() = items.sumOf { it.cantidad }

    // Precio total del carrito
    val precioTotal: Double
        get() = items.sumOf { it.subtotal }

    // Verifica si el carrito está vacío
    val estaVacio: Boolean
        get() = items.isEmpty()
}
