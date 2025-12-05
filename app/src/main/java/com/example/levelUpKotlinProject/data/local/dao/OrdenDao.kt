package com.example.levelUpKotlinProject.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.relations.OrdenConDetalles
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrden(orden: OrdenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<DetalleOrdenEntity>)

    @Transaction
    @Query("SELECT * FROM ordenes ORDER BY fechaCreacion DESC")
    fun obtenerTodasLasOrdenes(): Flow<List<OrdenConDetalles>>

    @Transaction
    @Query("SELECT * FROM ordenes WHERE rutCliente = :rutCliente ORDER BY fechaCreacion DESC")
    fun obtenerOrdenesXUsuario(rutCliente: String): Flow<List<OrdenConDetalles>>

    @Transaction
    @Query("SELECT * FROM ordenes WHERE id = :ordenId")
    fun obtenerOrdenPorId(ordenId: String): Flow<OrdenConDetalles?> // ID String

    @Query("UPDATE ordenes SET estado = :nuevoEstado WHERE id = :ordenId")
    suspend fun actualizarEstado(ordenId: String, nuevoEstado: String) // ID String

    @Query("""
        SELECT 
            productoId, ordenId, nombre AS nombreProducto, imagenUrl, precio AS precioUnitarioFijo, cantidad
        FROM detalle_orden WHERE ordenId = :ordenId
    """)
    suspend fun obtenerItemsOrden(ordenId: String): List<ItemOrden>
}



