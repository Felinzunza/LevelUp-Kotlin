package com.example.levelUpKotlinProject.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.levelUpKotlinProject.data.local.dao.OrdenDao
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.relations.OrdenConDetalles
import com.example.levelUpKotlinProject.data.local.dao.CarritoDao
import com.example.levelUpKotlinProject.domain.model.ItemOrden


class OrdenRepository(private val ordenDao: OrdenDao, private val carritoDao: CarritoDao) {

    suspend fun insertOrden(orden: OrdenEntity): Long {
        return ordenDao.insertOrden(orden)
    }

    suspend fun insertDetalles(detalles: List<DetalleOrdenEntity>) {
        ordenDao.insertDetalles(detalles)
    }

    fun obtenerTodasLasOrdenes(): Flow<List<OrdenConDetalles>> {
        return ordenDao.obtenerTodasLasOrdenes()
    }

    fun obtenerOrdenPorId(ordenId: Long): Flow<OrdenConDetalles?> {
        return ordenDao.obtenerOrdenPorId(ordenId)
    }

    fun obtenerOrdenesXUsuario(rutCliente: String): Flow<List<OrdenConDetalles>> {
        return ordenDao.obtenerOrdenesXUsuario(rutCliente)
    }

    suspend fun actualizarEstado(ordenId: Long, nuevoEstado: String)  {
        ordenDao.actualizarEstado(ordenId, nuevoEstado)

    }



    suspend fun finalizarCompraTransaccional(
        orden: OrdenEntity,
        detalles: List<DetalleOrdenEntity>
    ) {
        // 1. Insertar la cabecera de la orden
        val ordenId = ordenDao.insertOrden(orden) // 👈 Retorna el ID autogenerado

        // 2. Vincular el ID de la orden a cada detalle
        val detallesConId = detalles.map { detalle ->
            // Usa el Long devuelto directamente
            detalle.copy(ordenId = ordenId)
        }

        // 3. Insertar todos los ítems de detalle
        ordenDao.insertDetalles(detallesConId)

        // 4. Limpiar el carrito
        carritoDao.vaciar()
    }

    suspend fun obtenerItemsOrden(ordenId: Long): List<ItemOrden> {
        return ordenDao.obtenerItemsOrden(ordenId)
    }

}

