package com.example.levelUpKotlinProject.data.remote.dto

import com.example.levelUpKotlinProject.domain.model.Producto
import com.google.gson.annotations.SerializedName

data class ProductoDto(
    // CAMBIO: String nullable
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("title")
    val titulo: String,

    @SerializedName("description")
    val descripcion: String,

    @SerializedName("price")
    val precio: Double,

    @SerializedName("image")
    val urlImagen: String,

    @SerializedName("category")
    val categoria: String,

    @SerializedName("stock")
    val stock: Int? = 0
)

fun ProductoDto.aModelo(): Producto {
    return Producto(
        // Si viene nulo, usamos cadena vacía
        id = this.id ?: "",
        nombre = this.titulo,
        descripcion = this.descripcion,
        precio = this.precio,
        imagenUrl = this.urlImagen,
        categoria = this.categoria,
        stock = this.stock ?: 0
    )
}

fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        // Si el ID está vacío (nuevo), enviamos null para que el server genere uno
        id = if (this.id.isBlank()) null else this.id,
        titulo = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        urlImagen = this.imagenUrl,
        categoria = this.categoria,
        stock = this.stock
    )
}