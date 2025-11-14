package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProject.domain.model.Producto

/**
 * Entidad Room para productos
 * Se guarda en la tabla "productos"
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int
)

/**
 * Convierte la entidad de base de datos al modelo del dominio
 */
fun ProductoEntity.toProducto() = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)

/**
 * Convierte el modelo del dominio a entidad de base de datos
 */
fun Producto.toEntity() = ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)
