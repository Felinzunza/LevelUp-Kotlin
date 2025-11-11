package com.example.levelUpKotlinProyect.domain.model

import java.util.Date

/**
 * Usuario: Modelo de dominio para usuarios del sistema
 * 
 * En una app real:
 * - Las contraseñas se hashean (bcrypt, argon2)
 * - Se usa JWT o OAuth2 para autenticación
 * - Las credenciales se almacenan en backend seguro
 * 
 * Para fines educativos: Usamos SharedPreferences local
 *
 *
 */
data class Usuario(
    val id: Int = 0,
    val rut: String,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: Date,
    val email: String,
    val password: String,
    val telefono: String?,
    val fechaRegistro: Date,
    val rol: Rol = Rol.USUARIO
)

enum class Rol {
    USUARIO,
    ADMIN
}
