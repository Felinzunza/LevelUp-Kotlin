package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import com.example.levelUpKotlinProject.domain.model.ItemOrden

@Entity(
    tableName = "detalle_orden",
    primaryKeys = ["ordenId", "productoId"]
)
data class DetalleOrdenEntity(
    // CORRECCIÓN: Usar Int para que coincida con la PK de OrdenEntity
    val ordenId: Long = 0,
    val productoId: Int,
    val nombre: String,
    val precio: Double, // 👈 Este es el campo que contiene el precio cobrado
    val imagenUrl: String,
    val cantidad: Int = 1
)


/**
 * Convierte DetalleOrdenEntity (Datos crudos) a ItemOrden (Modelo de Dominio).
 */
fun DetalleOrdenEntity.toItemOrden() = ItemOrden(
    productoId = productoId,
    ordenId = ordenId,

    nombre = nombre,
    imagenUrl = imagenUrl,

    // CORRECCIÓN CRÍTICA: Usar 'precio' (el nombre del campo en la Entidad)
    precioUnitarioFijo = precio,
    cantidad = cantidad
)


