package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProject.domain.model.Producto

/**
 * Entidad Room para productos
 * Se guarda en la tabla "productos"

 */
@Entity(tableName = "productos")
data class ProductoEntity(
    // CAMBIO: ID String y sin autogenerar (usamos el del servidor)
    @PrimaryKey(autoGenerate = false)
    val id: String,

    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int
)

fun ProductoEntity.toProducto() = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)

fun Producto.toEntity() = ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)