package com.example.levelUpKotlinProyect.data.local.entity

import androidx.room.Entity
import com.example.levelUpKotlinProyect.domain.model.Producto


@Entity(
    tableName = "detalle_orden",
    primaryKeys = ["ordenId", "productoId"]
)
data class DetalleOrdenEntity(
    val ordenId: Long = 0,
    val productoId: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagenUrl: String,
    val categoria: String,
    val stock: Int,
    val cantidad: Int = 1

){

}

fun DetalleOrdenEntity.toDomain(): Producto {
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
