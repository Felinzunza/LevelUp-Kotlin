package com.example.levelUpKotlinProject.data.repository

import com.example.levelUpKotlinProject.data.local.dao.UsuarioDao
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
    suspend fun validarCredenciales(email: String, password: String): Boolean {
        // La función del DAO devuelve UsuarioEntity? (nullable), pero Room puede lanzar una excepción en la conversión.
        return try {
            usuarioDao.obtenerUsuarioPorEmailYPassword(email, password) != null
        } catch (e: Exception) {
            // Capturar cualquier excepción de Room (corrupción de datos, error de I/O)
            false
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