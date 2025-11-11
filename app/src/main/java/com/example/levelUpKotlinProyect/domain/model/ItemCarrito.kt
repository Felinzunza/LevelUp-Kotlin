package com.example.levelUpKotlinProyect.domain.model

/**
 * Representa un item individual en el carrito
 * Incluye lógica de negocio (subtotal calculado)
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int = 1
) {
    // Propiedad calculada: subtotal del item
    val subtotal: Double 
        get() = producto.precio * cantidad
}
