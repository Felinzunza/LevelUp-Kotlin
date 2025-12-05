package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import java.util.Date

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val rut: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: Long, // Room guarda Long
    val username: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val direccion: String,
    val comuna: String,
    val region: String,
    val fechaRegistro: Long, // Room guarda Long
    val rol: String = "USUARIO", // Room guarda String
)

// Conversión Entidad -> Modelo
fun UsuarioEntity.toUsuario() = Usuario(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    fechaNacimiento = Date(fechaNacimiento), // Long a Date
    username = username,
    email = email,
    password = password,
    telefono = telefono,
    direccion=direccion,
    comuna=comuna,
    region=region,
    fechaRegistro = Date(fechaRegistro), // Long a Date
    rol = try {
        Rol.valueOf(rol)
    } catch (e: Exception) {
        Rol.USUARIO
    }
)

// Conversión Modelo -> Entidad
fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    fechaNacimiento = fechaNacimiento.time, // Date a Long
    username = username,
    email = email,
    password = password,
    telefono = telefono,
    direccion=direccion,
    comuna=comuna,
    region=region,
    fechaRegistro = fechaRegistro.time, // Date a Long
    rol = rol.name
)