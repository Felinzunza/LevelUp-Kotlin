package com.example.levelUpKotlinProject.data.local.dao

import androidx.room.*
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun obtenerTodosLosusuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerusuarioPorId(id: Int): UsuarioEntity

    @Query("SELECT * FROM usuarios WHERE rut = :rut")
    suspend fun obtenerusuarioPorRut(rut: String): UsuarioEntity

    // Métodos de búsqueda opcionales (nullable)
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE username = :username")
    suspend fun obtenerUsuarioPorUsername(username: String): UsuarioEntity?

    // Escritura
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarusuarios(usuarios: List<UsuarioEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarusuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizarusuario(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminarusuario(usuario: UsuarioEntity)
}