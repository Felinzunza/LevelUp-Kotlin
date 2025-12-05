package com.example.levelUpKotlinProject.data.remote.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.util.Date


data class UsuarioDto(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("rut")
    val rut: String,

    @SerializedName("name")
    val nombre: String,

    @SerializedName("lastname")
    val apellido: String,

    @SerializedName("birthday")
    val fechaNacimiento: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phone")
    val telefono: String?,

    @SerializedName("direction")
    val direccion: String,

    @SerializedName("city")
    val comuna: String,

    @SerializedName("region")
    val region: String,

    @SerializedName("registerDate")
    val fechaRegistro: String,

    @SerializedName("role")
    val rol: String
)

fun UsuarioDto.aModelo(): Usuario{

    return Usuario(


        id = this.id ?: "",
        rut = rut,
        nombre = nombre,
        apellido = apellido,
        fechaNacimiento = try {
            Date.from(Instant.parse(fechaNacimiento))
        } catch (e: Exception) {
            Date()
        },
        username = username,
        email = email,
        password = password,
        telefono = telefono,
        direccion=direccion,
        comuna=comuna,
        region=region,
        fechaRegistro = try {
            Date.from(Instant.parse(fechaRegistro))
        } catch (e: Exception) {
            Date()
        },
        rol = try {
            Rol.valueOf(rol)
        } catch (e: Exception) {
            Rol.USUARIO
        }
)
}

fun Usuario.aDto(): UsuarioDto {
    return UsuarioDto(
        id = if (this.id.isBlank()) null else this.id, // Enviar null si es nuevo
        rut = rut,
        nombre = nombre,
        apellido = apellido,
        fechaNacimiento = fechaNacimiento.toString(),
        username = username,
        email = email,
        password = password,
        telefono = telefono,
        direccion=direccion,
        comuna=comuna,
        region=region,
        fechaRegistro = fechaRegistro.toString(),
        rol = rol.name

    )

}








