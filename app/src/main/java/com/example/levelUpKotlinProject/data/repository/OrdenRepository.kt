package com.example.levelUpKotlinProject.data.repository

import android.util.Log
import com.example.levelUpKotlinProject.data.local.dao.CarritoDao
import com.example.levelUpKotlinProject.data.local.dao.OrdenDao
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.toEntity
import com.example.levelUpKotlinProject.data.local.entity.toOrden
import com.example.levelUpKotlinProject.data.local.relations.OrdenConDetalles
import com.example.levelUpKotlinProject.data.remote.api.OrdenApiService
import com.example.levelUpKotlinProject.data.remote.dto.OrdenDto
import com.example.levelUpKotlinProject.data.remote.dto.aDto
import com.example.levelUpKotlinProject.data.remote.dto.aModelo
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import com.example.levelUpKotlinProject.domain.model.Orden
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrdenRepository(
    private val ordenDao: OrdenDao,
    private val carritoDao: CarritoDao,
    private val apiService: OrdenApiService
) {
    companion object { private const val TAG = "OrdenRepository" }

    fun obtenerTodasLasOrdenes(): Flow<List<OrdenConDetalles>> = flow {
        sincronizarOrdenesDesdeApi(null)
        emitAll(ordenDao.obtenerTodasLasOrdenes())
    }

    fun obtenerOrdenesXUsuario(rutCliente: String): Flow<List<OrdenConDetalles>> = flow {
        sincronizarOrdenesDesdeApi(rutCliente)
        emitAll(ordenDao.obtenerOrdenesXUsuario(rutCliente))
    }

    fun obtenerOrdenPorId(ordenId: String): Flow<OrdenConDetalles?> = flow {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.obtenerOrdenPorId(ordenId)
                if (response.isSuccessful && response.body() != null) {
                    guardarOrdenDesdeApi(response.body()!!)
                }
            } catch (e: Exception) { }
        }
        emitAll(ordenDao.obtenerOrdenPorId(ordenId))
    }

    suspend fun finalizarCompraTransaccional(orden: OrdenEntity, detalles: List<DetalleOrdenEntity>) {
        // Para String, no necesitamos calcular ID. Enviamos sin ID para que el server genere.
        try {
            // Convertimos a modelo con ID vacío para enviar
            val ordenModelo = orden.toOrden().copy(id = "", items = detalles.map { it.toItemOrden() })
            val response = apiService.crearOrden(ordenModelo.aDto())

            if (response.isSuccessful && response.body() != null) {
                val ordenCreada = response.body()!!.aModelo()
                Log.d(TAG, "✓ Orden creada servidor ID: ${ordenCreada.id}")
                guardarOrdenLocal(ordenCreada)
            } else {
                // Fallback offline: Generar UUID
                val idTemp = java.util.UUID.randomUUID().toString()
                guardarOrdenLocal(orden.toOrden().copy(id = idTemp, items = detalles.map { it.toItemOrden().copy(ordenId = idTemp) }))
            }
        } catch (e: Exception) {
            val idTemp = java.util.UUID.randomUUID().toString()
            guardarOrdenLocal(orden.toOrden().copy(id = idTemp, items = detalles.map { it.toItemOrden().copy(ordenId = idTemp) }))
        }
        carritoDao.vaciar()
    }

    suspend fun actualizarEstado(ordenId: String, nuevoEstado: String) {
        ordenDao.actualizarEstado(ordenId, nuevoEstado)
        try {
            apiService.actualizarEstado(ordenId, mapOf("status" to nuevoEstado))
        } catch (e: Exception) { }
    }

    // Helpers
    private fun sincronizarOrdenesDesdeApi(rut: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = if (rut == null) apiService.obtenerTodasLasOrdenes() else apiService.obtenerOrdenesXUsuario(rut)
                if (res.isSuccessful && res.body() != null) {
                    res.body()!!.forEach { guardarOrdenDesdeApi(it) }
                }
            } catch (e: Exception) { }
        }
    }

    private suspend fun guardarOrdenDesdeApi(dto: OrdenDto) {
        guardarOrdenLocal(dto.aModelo())
    }

    private suspend fun guardarOrdenLocal(orden: Orden) {
        ordenDao.insertOrden(orden.toEntity())
        val detalles = orden.items.map {
            DetalleOrdenEntity(orden.id, it.productoId, it.nombreProducto, it.imagenUrl, it.precioUnitarioFijo, it.cantidad)
        }
        ordenDao.insertDetalles(detalles)
    }

    // Helper para convertir entidad detalle a item
    private fun DetalleOrdenEntity.toItemOrden() = ItemOrden(productoId, ordenId, nombre, imagenUrl, precio, cantidad)
}