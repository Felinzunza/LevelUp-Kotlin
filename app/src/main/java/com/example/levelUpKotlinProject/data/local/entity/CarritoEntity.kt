package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProject.domain.model.Producto

/**
 * Entidad Room para tabla "carrito"
 * Representa un producto en el carrito del usuario
 * Versión actualizada con todos los campos
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productoId: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int,
    val cantidad: Int = 1
)

/**
 * Extension function para convertir Entity a Producto
 */
fun CarritoEntity.toDomain(): Producto {
    return Producto(
        id = productoId,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
        imagenUrl = imagenUrl,
        categoria = categoria,
        stock = stock
    )
}