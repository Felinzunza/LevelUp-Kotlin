package com.example.levelUpKotlinProject.data.repository

import android.util.Log
import android.util.Patterns
import com.example.levelUpKotlinProject.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProject.data.local.entity.toEntity
import com.example.levelUpKotlinProject.data.local.entity.toUsuario
import com.example.levelUpKotlinProject.data.remote.api.UsuarioApiService
import com.example.levelUpKotlinProject.data.remote.dto.aDto
import com.example.levelUpKotlinProject.data.remote.dto.aModelo
import com.example.levelUpKotlinProject.domain.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val apiService: UsuarioApiService
) {
    companion object { private const val TAG = "UsuarioRepository" }

    fun obtenerUsuarios(): Flow<List<Usuario>> = flow {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.obtenerTodosLosUsuarios()
                if (response.isSuccessful && response.body() != null) {
                    val usuarios = response.body()!!.map { it.aModelo() }
                    usuarioDao.insertarUsuarios(usuarios.map { it.toEntity() })
                }
            } catch (e: Exception) { Log.e(TAG, "Sync error: ${e.message}") }
        }
        emitAll(usuarioDao.obtenerTodosLosusuarios().map { list -> list.map { it.toUsuario() } })
    }

    suspend fun obtenerUsuarioPorId(id: String): Usuario? {
        return usuarioDao.obtenerusuarioPorId(id)?.toUsuario()
    }

    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorEmail(email)?.toUsuario()
    }

    suspend fun obtenerUsuarioPorUsername(username: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorUsername(username)?.toUsuario()
    }

    // Los métodos de búsqueda por email/username se mantienen igual (devuelven Usuario?)

    suspend fun insertarUsuario(usuario: Usuario): String {
        return try {
            val response = apiService.agregarUsuario(usuario.aDto())
            if (response.isSuccessful && response.body() != null) {
                val nuevo = response.body()!!.aModelo()
                usuarioDao.insertarusuario(nuevo.toEntity())
                nuevo.id
            } else {
                // Fallback local: Usamos UUID temporal si falla la red
                val idTemp = java.util.UUID.randomUUID().toString()
                usuarioDao.insertarusuario(usuario.copy(id = idTemp).toEntity())
                idTemp
            }
        } catch (e: Exception) {
            val idTemp = java.util.UUID.randomUUID().toString()
            usuarioDao.insertarusuario(usuario.copy(id = idTemp).toEntity())
            idTemp
        }
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizarusuario(usuario.toEntity())
        try { apiService.modificarUsuario(usuario.id, usuario.aDto()) } catch (e: Exception) {}
    }

    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDao.eliminarusuario(usuario.toEntity())
        try { apiService.borrarUsuario(usuario.id) } catch (e: Exception) {}
    }

    suspend fun validarCredenciales(identificador: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            // 1. Intentamos buscar por Email
            var usuarioEntity = usuarioDao.obtenerUsuarioPorEmail(identificador)

            // 2. Si no existe por email, buscamos por Username
            if (usuarioEntity == null) {
                usuarioEntity = usuarioDao.obtenerUsuarioPorUsername(identificador)
            }

            // 3. Si encontramos al usuario, verificamos la contraseña
            if (usuarioEntity != null) {
                // NOTA: En una app real, aquí se comparan hashes, no texto plano.
                return@withContext usuarioEntity.password == password
            } else {
                return@withContext false // Usuario no existe
            }
        }
    }


}