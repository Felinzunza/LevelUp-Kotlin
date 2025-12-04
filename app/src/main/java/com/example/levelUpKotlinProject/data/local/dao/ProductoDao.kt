package com.example.levelUpKotlinProject.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.levelUpKotlinProject.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de productos
 * Define las operaciones de base de datos
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun obtenerTodosLosProductos(): Flow<List<ProductoEntity>>

    // CAMBIO: ID String
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerProductoPorId(id: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos")
    suspend fun eliminarTodosLosProductos()
}