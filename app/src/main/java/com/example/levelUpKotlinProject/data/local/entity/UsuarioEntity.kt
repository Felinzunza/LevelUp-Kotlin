package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import java.util.Date

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rut: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: Long, // Almacenado como Long
    val email: String,
    val password: String,
    val telefono: String?,
    val fechaRegistro: Long, // Almacenado como Long
    val rol: String = "USUARIO", // Almacenado como String
)

// --- A) De Entidad a Modelo (Al leer de la DB - CORRECCIÓN CRÍTICA) ---
fun UsuarioEntity.toUsuario() = Usuario(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    // Conversión: Long a Date
    fechaNacimiento = Date(fechaNacimiento),
    email = email,
    password = password,
    telefono = telefono,
    // Conversión: Long a Date
    fechaRegistro = Date(fechaRegistro),
    // CONVERSIÓN CRÍTICA: Intenta convertir String a Enum, usa USUARIO si falla (DEFENSIVO)
    rol = try {
        Rol.valueOf(rol)
    } catch (e: IllegalArgumentException) {
        Rol.USUARIO // Valor por defecto si el string no coincide
    }
)

// --- B) De Modelo a Entidad (Al guardar en la DB) ---
fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    // Conversión: Date a Long
    fechaNacimiento = fechaNacimiento.time,
    email = email,
    password = password,
    telefono = telefono,
    // Conversión: Date a Long
    fechaRegistro = fechaRegistro.time,
    // Conversión: Enum a String
    rol = rol.name
)