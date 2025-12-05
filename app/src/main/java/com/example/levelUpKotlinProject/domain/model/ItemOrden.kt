package com.example.levelUpKotlinProject.domain.model

data class ItemOrden(
    // CAMBIO: Ahora es String para coincidir con Producto.id
    val productoId: String,

    val ordenId: String = "",
    val nombreProducto: String,
    val imagenUrl: String?,
    val precioUnitarioFijo: Double,
    val cantidad: Int
) {
    val subtotal: Double
        get() = precioUnitarioFijo * cantidad
}

