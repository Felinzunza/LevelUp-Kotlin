package com.example.levelUpKotlinProject.domain.model

/**
 * Modelo de dominio para Producto
 * Versión extendida con todos los campos del e-commerce
 * NO tiene anotaciones de Room (eso es en ProductoEntity)
 * 
 */
data class Producto(
    val id: String = "",
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int
) {
    /**
     * Formatea el precio con separador de miles
     * Ejemplo: 25000.0 -> "$25.000"
     */
    fun precioFormateado(): String {
        val precioEntero = precio.toInt()
        return "$${precioEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"

    }
    
    /**
     * Verifica si hay stock disponible
     */
    val hayStock: Boolean
        get() = stock > 0
}
