package com.example.levelUpKotlinProyect.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.levelUpKotlinProyect.data.local.dao.OrdenDao
import com.example.levelUpKotlinProyect.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProyect.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProyect.data.local.relations.OrdenConDetalles


class OrdenRepository(private val ordenDao: OrdenDao) {

    suspend fun insertOrden(orden: OrdenEntity): Long {
        return ordenDao.insertOrden(orden)
    }

    suspend fun insertDetalles(detalles: List<DetalleOrdenEntity>) {
        ordenDao.insertDetalles(detalles)
    }

    fun obtenerOrdenesXUsuario(rutCliente: String): Flow<List<OrdenConDetalles>> {
        return ordenDao.obtenerOrdenesXUsuario(rutCliente)
    }

    suspend fun actualizarEstado(ordenId: Int, nuevoEstado: String)  {
        ordenDao.actualizarEstado(ordenId, nuevoEstado)

    }

}