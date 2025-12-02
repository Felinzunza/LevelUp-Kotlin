package com.example.levelUpKotlinProject.data.remote.api


import com.example.levelUpKotlinProject.data.remote.dto.ItemOrdenDto
import com.example.levelUpKotlinProject.data.remote.dto.OrdenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path


interface OrdenApiService {

    @GET("orders")
    suspend fun obtenerTodasLasOrdenes(): Response<List<OrdenDto>>

    @GET("orders/user/{rut}")
    suspend fun obtenerOrdenesXUsuario(@Path("rut") rut: String): Response<List<OrdenDto>>

    @GET("orders/{id}")
    suspend fun obtenerOrdenPorId(@Path("id") id: Long): Response<OrdenDto>

    @POST("orders")
    suspend fun crearOrden(@Body orden: OrdenDto): Response<OrdenDto>


    @PATCH("orders/{id}")
    suspend fun actualizarEstado(@Path("id") id: Long, @Body estado: Map<String, String>): Response<OrdenDto>

    @GET("orders/{id}/items")
    suspend fun obtenerItemsOrden(@Path("id") id: Long): Response<List<ItemOrdenDto>>

}




