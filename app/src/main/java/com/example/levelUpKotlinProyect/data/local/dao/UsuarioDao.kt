package com.example.levelUpKotlinProyect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.levelUpKotlinProyect.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UsuarioDao {


    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun obtenerTodosLosusuarios(): Flow<List<UsuarioEntity>> // Correcto

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerusuarioPorId(id: Int): UsuarioEntity

    @Query("SELECT * FROM usuarios WHERE rut = :rut")
    suspend fun obtenerusuarioPorRut(rut: String): UsuarioEntity

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