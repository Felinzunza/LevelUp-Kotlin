package com.example.levelUpKotlinProject.data.repository

import com.example.levelUpKotlinProject.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import com.example.levelUpKotlinProject.data.local.entity.toEntity
import com.example.levelUpKotlinProject.data.local.entity.toUsuario
import com.example.levelUpKotlinProject.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // --- LECTURA DE LISTAS (Correcto) ---
    fun obtenerUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosusuarios().map { entities ->
            entities.map { it.toUsuario() } // Convierte la lista de Entity a lista de Usuario
        }
    }

    // --- LECTURA POR ID (Seguridad Añadida) ---
    /** Devuelve Usuario o null si no existe o falla la lectura/mapeo. */
    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return try {
            usuarioDao.obtenerusuarioPorId(id).toUsuario()
        } catch (e: Exception) {
            // Room lanza una excepción si no encuentra el ID o falla el mapeo
            null
        }
    }

    // --- LECTURA POR RUT (Seguridad Añadida) ---
    /** Devuelve Usuario o null si no existe o falla la lectura/mapeo. */
    suspend fun obtenerUsuarioPorRut(rut: String): Usuario? {
        return try {
            usuarioDao.obtenerusuarioPorRut(rut).toUsuario()
        } catch (e: Exception) {
            // Captura si no encuentra el RUT
            null
        }
    }

    // --- LECTURA: VALIDACIÓN DE CREDENCIALES (Seguridad Añadida) ---
    /**
     * Valida si existe un usuario con el email y password dados.
     */
    suspend fun validarCredenciales(identificador: String, password: String): Boolean {
        // 1. Definir una variable para el usuario encontrado
        var usuarioEncontrado: UsuarioEntity? = null

        // Usamos el identificador para buscar, ya sea por email o por username

        // A) Intentar buscar por Email (si el identificador parece un email)
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(identificador).matches()) {
            usuarioEncontrado = usuarioDao.obtenerUsuarioPorEmail(identificador)
        }

        // B) Si no se encontró por email o no era un email, intentar buscar por Username
        if (usuarioEncontrado == null) {
            usuarioEncontrado = usuarioDao.obtenerUsuarioPorUsername(identificador)
        }

        // 2. Intentar validar la contraseña
        return try {
            if (usuarioEncontrado != null) {
                // **PUNTO CLAVE:** Comparar la contraseña en texto plano con la contraseña almacenada.
                // NOTA DE SEGURIDAD: En producción, aquí se usaría bcrypt o PBKDF2.
                return usuarioEncontrado.password == password
            }

            // Si no se encontró el usuario, retorna false
            false

        } catch (e: Exception) {
            // Capturar excepciones de Room (I/O, conversión, etc.)
            false
        }
    }

    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return try {
            usuarioDao.obtenerUsuarioPorEmail(email)?.toUsuario()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerUsuarioPorUsername(username: String): Usuario? {
        return try {
            usuarioDao.obtenerUsuarioPorUsername(username)?.toUsuario()
        } catch (e: Exception) {
            null
        }
    }


    // --- ESCRITURA: INSERTAR (Correcto) ---
    suspend fun insertarUsuario(usuario: Usuario): Long {
        return usuarioDao.insertarusuario(usuario.toEntity())
    }

    // --- ESCRITURA: ACTUALIZAR (Correcto) ---
    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizarusuario(usuario.toEntity())
    }

    // --- ESCRITURA: ELIMINAR (Correcto) ---
    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDao.eliminarusuario(usuario.toEntity())
    }
}