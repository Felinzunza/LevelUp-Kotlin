package com.example.levelUpKotlinProyect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProyect.domain.model.Rol
import com.example.levelUpKotlinProyect.domain.model.Usuario
import java.util.Date

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rut: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: Long, //  DEBE SER LONG para almacenamiento eficiente
    val email: String,
    val password: String,
    val telefono: String?,
    val fechaRegistro: Long, //  DEBE SER LONG para almacenamiento eficiente
    val rol: String = "USUARIO", //  DEBE SER STRING
)

// --- A) De Entidad a Modelo (Al leer de la DB) ---
fun UsuarioEntity.toUsuario() = Usuario(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    // CONVERSIÓN CRÍTICA: Long (timestamp) a Date
    fechaNacimiento = Date(fechaNacimiento),
    email = email,
    password = password,
    telefono = telefono,
    //  CONVERSIÓN CRÍTICA: Long (timestamp) a Date
    fechaRegistro = Date(fechaRegistro),
    //  CONVERSIÓN CRÍTICA: String a Enum
    rol = Rol.valueOf(rol)
)

// --- B) De Modelo a Entidad (Al guardar en la DB) ---
fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    //  CONVERSIÓN CRÍTICA: Date a Long (timestamp)
    fechaNacimiento = fechaNacimiento.time,
    email = email,
    password = password,
    telefono = telefono,
    //  CONVERSIÓN CRÍTICA: Date a Long (timestamp)
    fechaRegistro = fechaRegistro.time,
    //  CONVERSIÓN CRÍTICA: Enum a String
    rol = rol.name
)




