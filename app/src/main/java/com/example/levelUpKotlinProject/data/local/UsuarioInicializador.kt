package com.example.levelUpKotlinProject.data.local

import android.content.Context
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Inicializador para asegurar que el usuario administrador por defecto exista en la BD.
 */
object UsuarioInicializador {

    private const val ADMIN_EMAIL_POR_DEFECTO = "admin@admin.cl"
    private const val ADMIN_USERNAME_POR_DEFECTO = "admin"
    private const val ADMIN_PASS_POR_DEFECTO = PreferenciasManager.ADMIN_PASSWORD

    suspend fun inicializarAdmin(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val usuarioDao = database.usuarioDao()

        // CRÍTICO: Ejecutar en hilo IO para no bloquear la UI
        withContext(Dispatchers.IO) {

            // 1. Verificar existencia (Email o Username)
            val adminPorEmail = usuarioDao.obtenerUsuarioPorEmail(email = ADMIN_EMAIL_POR_DEFECTO)

            val adminPorUsername = if (adminPorEmail == null) {
                usuarioDao.obtenerUsuarioPorUsername(username = ADMIN_USERNAME_POR_DEFECTO)
            } else null

            val adminExistente = adminPorEmail ?: adminPorUsername

            // 2. Si no existe, crear e insertar
            if (adminExistente == null) {
                val usuarioAdmin = Usuario(
                    id = 0, // Room generará el ID
                    rut = "1.111.111-1",
                    nombre = "Admin",
                    apellido = "System",
                    fechaNacimiento = Date(),
                    username = ADMIN_USERNAME_POR_DEFECTO,
                    email = ADMIN_EMAIL_POR_DEFECTO,
                    password = ADMIN_PASS_POR_DEFECTO,
                    telefono = null,
                    direccion = "Casa matriz",
                    comuna = "Santiago",
                    region = "Metropolitana",
                    fechaRegistro = Date(),
                    rol = Rol.ADMIN
                )

                usuarioDao.insertarusuario(usuarioAdmin.toEntity())
            }
        }
    }
}

// Función de extensión para mapear Usuario -> UsuarioEntity
// Corrige tipos: Date -> Long y Enum -> String
private fun Usuario.toEntity() = UsuarioEntity(
    id = id,
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    fechaNacimiento = this.fechaNacimiento.time, // Convertir a Timestamp
    username = username,
    email = email,
    password = password,
    telefono = telefono,
    direccion=direccion,
    comuna=comuna,
    region=region,
    fechaRegistro = this.fechaRegistro.time, // Convertir a Timestamp
    rol = rol.name // Convertir Enum a String
)

