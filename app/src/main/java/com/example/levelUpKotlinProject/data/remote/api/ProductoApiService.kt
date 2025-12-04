package com.example.levelUpKotlinProject.data.remote.api


import com.example.levelUpKotlinProject.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface del servicio API - Define endpoints disponibles
 */

interface ProductoApiService {

    @GET("products")
    suspend fun obtenerTodosLosProductos(): Response<List<ProductoDto>>

    // CAMBIO: ID es String
    @GET("products/{id}")
    suspend fun obtenerProductoPorId(@Path("id") id: String): Response<ProductoDto>

    // POST no lleva ID en la URL
    @POST("products")
    suspend fun agregarProducto(@Body producto: ProductoDto): Response<ProductoDto>

    // CAMBIO: ID es String
    @PUT("products/{id}")
    suspend fun modificarProducto(
        @Path("id") id: String,
        @Body producto: ProductoDto
    ): Response<ProductoDto>

    // CAMBIO: ID es String
    @DELETE("products/{id}")
    suspend fun borrarProducto(@Path("id") id: String): Response<Unit>
}