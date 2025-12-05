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
    // Asumimos "admin123" si no tienes acceso a PreferenciasManager aquí, o úsalo si es estático.
    // Para este ejemplo usaré un string directo para evitar errores de compilación si PreferenciasManager no tiene el const accesible estáticamente.
    private const val ADMIN_PASS_POR_DEFECTO = "admin123"

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
                    // ✅ CORRECTO: Asignamos un ID String fijo ("101").
                    // Room con autoGenerate=false requiere que nosotros demos el ID.
                    id = "101",
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

                // Usamos la función privada de abajo
                usuarioDao.insertarusuario(usuarioAdmin.toEntity())
            }
        }
    }
}

// Función de extensión privada (Tu versión personalizada)
// Convierte Usuario (Dominio) -> UsuarioEntity (BD)
private fun Usuario.toEntity() = UsuarioEntity(
    id = id, // String -> String
    rut = rut,
    nombre = nombre,
    apellido = apellido,
    fechaNacimiento = this.fechaNacimiento.time, // Date -> Long
    username = username,
    email = email,
    password = password,
    telefono = telefono,
    direccion = direccion,
    comuna = comuna,
    region = region,
    fechaRegistro = this.fechaRegistro.time, // Date -> Long
    rol = rol.name // Enum -> String
)