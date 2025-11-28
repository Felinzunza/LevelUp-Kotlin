package com.example.levelUpKotlinProject.data.remote.dto

import com.example.levelUpKotlinProject.domain.model.Producto
import com.google.gson.annotations.SerializedName

data class ProductoDto(
    @SerializedName("id")
    val identificador: Int,

    @SerializedName("title")
    val titulo: String,

    @SerializedName("description")
    val descripcion: String,

    @SerializedName("price")
    val precio: Double,

    @SerializedName("image")
    val urlImagen: String,

    @SerializedName("category")
    val categoria: String
)

fun ProductoDto.aModelo(): Producto {
    return Producto(
        id = this.identificador,
        nombre = this.titulo,
        descripcion = this.descripcion,
        precio = this.precio,
        imagenUrl = this.urlImagen,
        categoria = this.categoria,
        stock = 10  // Valor por defecto (API no lo provee)
    )
}

// Extension function: Modelo de dominio → DTO
fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        identificador = this.id,
        titulo = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio,
        urlImagen = this.imagenUrl,
        categoria = this.categoria
    )
}