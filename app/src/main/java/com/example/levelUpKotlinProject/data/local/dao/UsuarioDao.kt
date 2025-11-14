package com.example.levelUpKotlinProject.data.local.dao

import androidx.room.*
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {


    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun obtenerTodosLosusuarios(): Flow<List<UsuarioEntity>> // Correcto

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerusuarioPorId(id: Int): UsuarioEntity

    @Query("SELECT * FROM usuarios WHERE rut = :rut")
    suspend fun obtenerusuarioPorRut(rut: String): UsuarioEntity

    // AÑADIDO: Método para buscar usuario por email y password para el login
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun obtenerUsuarioPorEmailYPassword(email: String, password: String): UsuarioEntity? // Se devuelve nulo si no se encuentra

    // --- Escritura ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarusuarios(usuarios: List<UsuarioEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarusuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizarusuario(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminarusuario(usuario: UsuarioEntity)
}