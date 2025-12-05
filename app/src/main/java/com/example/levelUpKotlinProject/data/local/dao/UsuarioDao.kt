package com.example.levelUpKotlinProject.data.local.dao

import androidx.room.*
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun obtenerTodosLosusuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerusuarioPorId(id: String): UsuarioEntity? // ID String

    @Query("SELECT * FROM usuarios WHERE rut = :rut LIMIT 1")
    suspend fun obtenerusuarioPorRut(rut: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE username = :username LIMIT 1")
    suspend fun obtenerUsuarioPorUsername(username: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarusuario(usuario: UsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarios(usuarios: List<UsuarioEntity>)

    @Update
    suspend fun actualizarusuario(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminarusuario(usuario: UsuarioEntity)
}