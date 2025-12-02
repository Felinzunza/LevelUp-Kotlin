package com.example.levelUpKotlinProject.data.remote.api

import com.example.levelUpKotlinProject.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApiService {
    //Obtener todos los usuarios
    @GET("users")
    suspend fun obtenerTodosLosUsuarios(): Response<List<UsuarioDto>>

    //Obtener usuario por ID
    @GET("users/{id}")
    suspend fun obtenerUsuarioPorId(
        @Path("id") identificador: Int
    ): Response<UsuarioDto>

    //Obtener usuario por email
    @GET("users/email/{email}")
    suspend fun obtenerUsuarioPorEmail(
        @Path("email") email: String
    ): Response<UsuarioDto>

    //Obtener usuarios por email
    @GET("users/email/{email}")
    suspend fun obtenerUsuariosPorEmail(
        @Path("email") email: String
    ): Response<List<UsuarioDto>>


    //Obtener usuario por username
    @GET("users/username/{username}")
    suspend fun obtenerUsuarioPorUsername(
        @Path("username") username: String
    ): Response<UsuarioDto>


    //Obtener usuario por RUT
    @GET("users/rut/{rut}")
    suspend fun obtenerUsuarioPorRut(
        @Path("rut") rut: String
    ): Response<UsuarioDto>

    //Crear nuevo usuario
    @POST("users")
    suspend fun agregarUsuario(
        @Body nuevoUsuario: UsuarioDto
    ): Response<UsuarioDto>

    //Actualizar usuario existente
    @PUT("users/{id}")
    suspend fun modificarUsuario(
        @Path("id") identificador: Int,
        @Body usuarioActualizado: UsuarioDto
    ): Response<UsuarioDto>

    //Eliminar usuario
    @DELETE("users/{id}")
    suspend fun borrarUsuario(
        @Path("id") identificador: Int
    ): Response<Unit>





}