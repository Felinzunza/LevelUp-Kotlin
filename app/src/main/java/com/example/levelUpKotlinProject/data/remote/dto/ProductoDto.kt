package com.example.levelUpKotlinProject.data.remote.dto

import com.example.levelUpKotlinProject.domain.model.Producto
import com.google.gson.annotations.SerializedName

data class ProductoDto(
    @SerializedName("id")
    val identificador: Int? = null,

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
        id = this.identificador ?:0,
        nombre = this.titulo,
        descripcion = this.descripcion,
        precio = this.precio,
        imagenUrl = this.urlImagen,
        categoria = this.categoria,
        stock = this.stock ?: 0
    )
}

// Extension function: Modelo de dominio → DTO
fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        identificador =if (this.id == 0) null else this.id,
        titulo = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        urlImagen = this.imagenUrl,
        categoria = this.categoria
    )
}