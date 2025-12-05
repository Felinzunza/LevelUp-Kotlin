package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.levelUpKotlinProject.domain.model.ItemOrden

@Entity(
    tableName = "detalle_orden",
    primaryKeys = ["ordenId", "productoId"], // Llave compuesta
    foreignKeys = [
        ForeignKey(
            entity = OrdenEntity::class,
            parentColumns = ["id"],
            childColumns = ["ordenId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ordenId"])]
)
data class DetalleOrdenEntity(
    val ordenId: String,

    // CAMBIO: Ahora es String
    val productoId: String,
    val nombre: String,
    val imagenUrl: String?,
    val precio: Double,
    val cantidad: Int
)


// Convierte la entidad de base de datos al modelo de dominio que usa la UI
fun DetalleOrdenEntity.toItemOrden(): ItemOrden {
    return ItemOrden(
        productoId = productoId,
        ordenId = ordenId,
        nombreProducto = nombre, // Mapeamos 'nombre' a 'nombreProducto'
        imagenUrl = imagenUrl,
        precioUnitarioFijo = precio, // Mapeamos 'precio' a 'precioUnitarioFijo'
        cantidad = cantidad
    )
}