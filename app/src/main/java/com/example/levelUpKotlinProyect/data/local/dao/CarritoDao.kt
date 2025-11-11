package com.example.levelUpKotlinProyect.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.levelUpKotlinProyect.data.local.entity.CarritoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para tabla carrito
 * Define las operaciones SQL disponibles
 */
@Dao
interface CarritoDao {

    /**
     * Obtiene todos los items del carrito en tiempo real
     * Flow emite nuevos valores cuando cambia la tabla
     */
    @Query("SELECT * FROM carrito")
    fun obtenerTodo(): Flow<List<CarritoEntity>>

    /**
     * Inserta un nuevo producto al carrito
     * suspend = operación asíncrona
     */
    @Insert
    suspend fun insertar(item: CarritoEntity)

    /**
     * Elimina todos los items del carrito
     */
    @Query("DELETE FROM carrito")
    suspend fun vaciar()

    /**
     * Calcula el total sumando todos los precios * cantidades
     * Flow para observar cambios en tiempo real
     */
    @Query("SELECT SUM(precio * cantidad) FROM carrito")
    fun obtenerTotal(): Flow<Double?>

    /**
     * NUEVA: Busca si un producto ya está en el carrito
     * @param productoId ID del producto a buscar
     * @return CarritoEntity si existe, null si no
     */
    @Query("SELECT * FROM carrito WHERE productoId = :productoId LIMIT 1")
    suspend fun obtenerPorProductoId(productoId: Int): CarritoEntity?

    /**
     * NUEVA: Actualiza la cantidad de un producto en el carrito
     * @param productoId ID del producto
     * @param cantidad Nueva cantidad (puede ser incremento o valor absoluto)
     */
    @Query("UPDATE carrito SET cantidad = :cantidad WHERE productoId = :productoId")
    suspend fun actualizarCantidad(productoId: Int, cantidad: Int)

    /**
     * NUEVA: Elimina un producto específico del carrito
     * @param productoId ID del producto a eliminar
     */
    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun eliminarProducto(productoId: Int)
}