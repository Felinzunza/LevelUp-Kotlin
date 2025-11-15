package com.example.levelUpKotlinProject.domain.model

import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity

/**
 * Representa un item individual en el carrito
 * Incluye lógica de negocio (subtotal calculado)
 * 
 */
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int = 1
) {
    // Propiedad calculada: subtotal del item
    val subtotal: Double 
        get() = producto.precio * cantidad
}


fun ItemCarrito.toDetalleEntity(ordenId: Long): DetalleOrdenEntity {
    return DetalleOrdenEntity(
        ordenId = ordenId, // Usamos el ID temporal o 0
        productoId = this.producto.id,
        nombre = this.producto.nombre,
        descripcion = this.producto.descripcion,
        precio = this.producto.precio,
        imagenUrl = this.producto.imagenUrl,
        categoria = this.producto.categoria,
        stock = this.producto.stock,
        cantidad = this.cantidad
    )
}