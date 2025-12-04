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

    // --- 1. INSERCIÓN TRANSACCIONAL ---

    /**
     * Inserta una única orden. Debe devolver el ID (Long) generado para insertarle los detalles.
     * Si no existe un ID autogenerado, Room asume que el objeto es nuevo.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT) // Si la orden existe, aborta. No se debe reemplazar.
    suspend fun insertOrden(orden: OrdenEntity): Long // 👈 Retorna el ID generado

    /**
     * Inserta los ítems de detalle (parte de la transacción).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalles(detalles: List<DetalleOrdenEntity>)


    // --- 2. CONSULTAS REACTIVAS DE HISTORIAL (Para la UI) ---

    /**
     * Obtiene el historial de órdenes para todos los usuarios.
     * Retorna un Flow para observar cambios en tiempo real (si se añade una nueva orden).
     */
    @Transaction
    @Query("SELECT * FROM ordenes ORDER BY fechaCreacion DESC")
    fun obtenerTodasLasOrdenes(): Flow<List<OrdenConDetalles>> // 👈 Retorno corregido

    /**
     * Obtiene el historial de órdenes de un usuario específico, con todos los detalles.
     * Usamos @Transaction para hacer el JOIN de OrdenEntity y DetalleOrdenEntity.
     */
    @Transaction
    @Query("SELECT * FROM ordenes WHERE rutCliente = :rutCliente ORDER BY fechaCreacion DESC")
    fun obtenerOrdenesXUsuario(rutCliente: String): Flow<List<OrdenConDetalles>> // Retorno ya estaba correcto


    @Query("SELECT * FROM ordenes WHERE id = :ordenId")
    fun obtenerOrdenPorId(ordenId: Long): Flow<OrdenConDetalles?>


    // --- 3. ACTUALIZACIÓN (Cambio de estado) ---

    /**
     * Cambia el estado de la orden (e.g., de 'PENDIENTE' a 'ENVIADA').
     */
    @Query("UPDATE ordenes SET estado = :nuevoEstado WHERE id = :ordenId")
    suspend fun actualizarEstado(ordenId: Long, nuevoEstado: String)

    // Usamos 'AS' para que Room sepa mapear la columna SQL a la variable de Kotlin
    @Query("""SELECT productoId,
                    ordenId,
                    nombre AS nombreProducto,
                    imagenUrl,
                    precio AS precioUnitarioFijo,  
                    cantidad
                FROM detalle_orden 
        WHERE ordenId = :ordenId
    """)
    suspend fun obtenerItemsOrden(ordenId: Long): List<ItemOrden>
}



