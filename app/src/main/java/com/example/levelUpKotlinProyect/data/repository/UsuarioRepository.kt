package com.example.levelUpKotlinProyect.data.repository

import com.example.levelUpKotlinProyect.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProyect.data.local.entity.toEntity
import com.example.levelUpKotlinProyect.data.local.entity.toUsuario
import com.example.levelUpKotlinProyect.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // --- LECTURA DE LISTAS (Correcto) ---
    fun obtenerUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosusuarios().map { entities ->
            entities.map { it.toUsuario() } // Convierte la lista de Entity a lista de Usuario
        }
    }

    // --- LECTURA POR ID ---
    suspend fun obtenerUsuarioPorId(id: Int): Usuario {
        // Obtiene Entity del DAO y la convierte a Modelo de Dominio
        return usuarioDao.obtenerusuarioPorId(id).toUsuario()
    }

    // --- LECTURA POR RUT  ---
    suspend fun obtenerUsuarioPorRut(rut: String): Usuario {
        // Obtiene Entity del DAO y la convierte a Modelo de Dominio
        return usuarioDao.obtenerusuarioPorRut(rut).toUsuario()
    }

    // --- ESCRITURA: INSERTAR (Corrección: Aplicar .toEntity() y retornar Long) ---
    suspend fun insertarUsuario(usuario: Usuario): Long { // 👈 Retorna Long
        // 1. Convierte el Modelo de Dominio a la Entidad
        val usuarioEntity = usuario.toEntity()
        // 2. Llama al DAO con la Entidad
        return usuarioDao.insertarusuario(usuarioEntity)
    }

    // --- ESCRITURA: ACTUALIZAR (Correcto) ---
    suspend fun actualizarUsuario(usuario: Usuario) {
        val usuarioEntity = usuario.toEntity()
        usuarioDao.actualizarusuario(usuarioEntity)
    }

    // --- ESCRITURA: ELIMINAR (Correcto) ---
    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDao.eliminarusuario(usuario.toEntity())
    }
}