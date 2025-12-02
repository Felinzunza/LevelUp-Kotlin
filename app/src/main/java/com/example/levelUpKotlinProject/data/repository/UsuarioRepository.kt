package com.example.levelUpKotlinProject.data.repository

import android.util.Log
import android.util.Patterns
import com.example.levelUpKotlinProject.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import com.example.levelUpKotlinProject.data.local.entity.toEntity
import com.example.levelUpKotlinProject.data.local.entity.toUsuario
import com.example.levelUpKotlinProject.data.remote.api.UsuarioApiService
import com.example.levelUpKotlinProject.data.remote.dto.aDto
import com.example.levelUpKotlinProject.data.remote.dto.aModelo
import com.example.levelUpKotlinProject.domain.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException


/**
 * Valida credenciales intentando actualizar el usuario desde la API primero.
 * CLAVE PARA TU PROBLEMA DE ADMIN: Si el admin existe en el servidor pero no en el celular,
 * esto lo descarga antes de validar.
 */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val apiService: UsuarioApiService
) {

    companion object {
        private const val TAG = "UsuarioRepository"
    }

    // =================================================================
    // 1. LECTURA DE LISTAS (REACTIVA - Single Source of Truth)
    // =================================================================

    /**
     * Devuelve un Flow que SIEMPRE observa la base de datos local.
     * Al mismo tiempo, intenta actualizar esos datos desde la API en segundo plano.
     */
    fun obtenerUsuarios(): Flow<List<Usuario>> = flow {
        // 1. Lanzamos la sincronización con la API en una corrutina paralela (IO)
        // Esto asegura que la UI reciba datos locales inmediatamente y se actualice
        // cuando llegue la respuesta de la API.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔄 Sincronizando usuarios desde API...")
                val respuesta = apiService.obtenerTodosLosUsuarios()

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val usuariosDto = respuesta.body()!!
                    val listaUsuarios = usuariosDto.map { it.aModelo() }

                    // Actualizamos la DB local con los datos frescos del servidor
                    val entidades = listaUsuarios.map { it.toEntity() }

                    // Insertamos/Reemplazamos masivamente
                    // Nota: Si usas onConflict = REPLACE en tu DAO, esto actualiza todo.
                    entidades.forEach { usuarioDao.insertarusuario(it) }

                    Log.d(TAG, "✓ Caché local sincronizado con ${listaUsuarios.size} usuarios")
                } else {
                    Log.w(TAG, "⚠ La API respondió con error: ${respuesta.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "✗ Falló la sincronización API (Usando datos offline): ${e.message}")
            }
        }

        // 2. EMITIMOS LA FUENTE DE VERDAD (Base de Datos Local)
        // Usamos emitAll para conectar el Flow de Room directamente a la salida.
        // Cualquier cambio en la BD (Insert/Update/Delete) disparará una nueva lista aquí.
        val flowLocal = usuarioDao.obtenerTodosLosusuarios().map { entities ->
            entities.map { it.toUsuario() }
        }
        emitAll(flowLocal)
    }

    // =================================================================
    // 2. MÉTODOS DE BÚSQUEDA INDIVIDUAL
    // =================================================================

    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        // Estrategia: API primero, Fallback Local
        try {
            val response = apiService.obtenerUsuarioPorId(id)
            if (response.isSuccessful && response.body() != null) {
                val usuarioApi = response.body()!!.aModelo()
                // Actualizamos este usuario específico en local para mantener consistencia
                usuarioDao.insertarusuario(usuarioApi.toEntity())
                return usuarioApi
            }
        } catch (e: Exception) { /* Fallback a local */ }

        return try {
            usuarioDao.obtenerusuarioPorId(id).toUsuario()
        } catch (e: Exception) { null }
    }

    suspend fun obtenerUsuarioPorRut(rut: String): Usuario? {
        try {
            val response = apiService.obtenerUsuarioPorRut(rut)
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!.aModelo()
            }
        } catch (e: Exception) { /* Ignorar */ }

        return try {
            usuarioDao.obtenerusuarioPorRut(rut).toUsuario()
        } catch (e: Exception) { null }
    }

    suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        try {
            val response = apiService.obtenerUsuarioPorEmail(email)
            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!.aModelo()
                usuarioDao.insertarusuario(usuario.toEntity())
                return usuario
            }
        } catch (e: Exception) { /* Ignorar */ }

        return usuarioDao.obtenerUsuarioPorEmail(email)?.toUsuario()
    }

    suspend fun obtenerUsuarioPorUsername(username: String): Usuario? {
        try {
            val response = apiService.obtenerUsuarioPorUsername(username)
            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!.aModelo()
                usuarioDao.insertarusuario(usuario.toEntity())
                return usuario
            }
        } catch (e: Exception) { /* Ignorar */ }

        return usuarioDao.obtenerUsuarioPorUsername(username)?.toUsuario()
    }

    // =================================================================
    // 3. VALIDACIÓN DE CREDENCIALES
    // =================================================================

    suspend fun validarCredenciales(identificador: String, password: String): Boolean {
        try {
            // Intento actualizar datos antes de validar
            val esEmail = Patterns.EMAIL_ADDRESS.matcher(identificador).matches()
            val response = if (esEmail) {
                apiService.obtenerUsuarioPorEmail(identificador)
            } else {
                apiService.obtenerUsuarioPorUsername(identificador)
            }

            if (response.isSuccessful && response.body() != null) {
                usuarioDao.insertarusuario(response.body()!!.aModelo().toEntity())
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠ Validación offline activa")
        }

        var usuarioEncontrado: UsuarioEntity? = null
        if (Patterns.EMAIL_ADDRESS.matcher(identificador).matches()) {
            usuarioEncontrado = usuarioDao.obtenerUsuarioPorEmail(identificador)
        }
        if (usuarioEncontrado == null) {
            usuarioEncontrado = usuarioDao.obtenerUsuarioPorUsername(identificador)
        }

        return usuarioEncontrado != null && usuarioEncontrado.password == password
    }

    // =================================================================
    // 4. ESCRITURA (CRUD) - CORREGIDO ID Y SINCRONIZACIÓN
    // =================================================================

    suspend fun insertarUsuario(usuario: Usuario): Long {
        return try {
            val response = apiService.agregarUsuario(usuario.aDto())

            if (response.isSuccessful && response.body() != null) {
                // CORRECCIÓN CRÍTICA:
                // Usamos el usuario que devuelve la API (que contiene el ID real del servidor)
                val usuarioCreadoEnServidor = response.body()!!.aModelo()
                Log.d(TAG, "✓ Usuario creado en API con ID: ${usuarioCreadoEnServidor.id}")

                // Guardamos en local con el ID correcto del servidor
                usuarioDao.insertarusuario(usuarioCreadoEnServidor.toEntity())
                // Retornamos ese ID real
                usuarioCreadoEnServidor.id.toLong()
            } else {
                Log.e(TAG, "⚠ Error API al crear (${response.code()}). Guardando localmente.")
                usuarioDao.insertarusuario(usuario.toEntity())
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error red al crear: ${e.message}")
            usuarioDao.insertarusuario(usuario.toEntity())
        }
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        // Primero actualizamos DB Local para que la UI responda instantáneamente ("Optimistic UI")
        usuarioDao.actualizarusuario(usuario.toEntity())

        try {
            val response = apiService.modificarUsuario(usuario.id, usuario.aDto())
            if (response.isSuccessful) {
                Log.d(TAG, "✓ Usuario actualizado en API")
            } else {
                Log.e(TAG, "⚠ Error al actualizar en API: ${response.code()}")
                // Opcional: Revertir cambio local si es crítico
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error red al actualizar: ${e.message}")
        }
    }

    suspend fun eliminarUsuario(usuario: Usuario) {
        // Primero eliminamos de DB Local para respuesta inmediata
        usuarioDao.eliminarusuario(usuario.toEntity())

        try {
            val response = apiService.borrarUsuario(usuario.id)
            if (response.isSuccessful) {
                Log.d(TAG, "✓ Usuario eliminado de API")
            } else {
                Log.e(TAG, "⚠ Error al eliminar de API: ${response.code()} (ID: ${usuario.id})")
                // Posible causa: El ID local no coincidía con el del servidor
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error red al eliminar: ${e.message}")
        }
    }
}