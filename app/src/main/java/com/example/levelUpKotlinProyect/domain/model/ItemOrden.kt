package com.example.levelUpKotlinProyect.domain.model

data class ItemOrden(
    val producto: Producto,
    val cantidad: Int = 1
) {
    // Propiedad calculada: subtotal del item
    val subtotal: Double
        get() = producto.precio * cantidad
}