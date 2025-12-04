package com.example.levelUpKotlinProject.data.repository

import android.util.Log
import com.example.levelUpKotlinProject.data.local.dao.CarritoDao
import com.example.levelUpKotlinProject.data.local.dao.OrdenDao
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.toOrden
import com.example.levelUpKotlinProject.data.local.relations.OrdenConDetalles
import com.example.levelUpKotlinProject.data.remote.api.OrdenApiService
import com.example.levelUpKotlinProject.data.remote.dto.ItemOrdenDto
import com.example.levelUpKotlinProject.data.remote.dto.OrdenDto
import com.example.levelUpKotlinProject.data.remote.dto.aModelo
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdenRepository(
    private val ordenDao: OrdenDao,
    private val carritoDao: CarritoDao,
    private val apiService: OrdenApiService
) {

    companion object {
        private const val TAG = "OrdenRepository"
    }

    // =================================================================
    // 1. LECTURA (Sincronización Reactiva)
    // =================================================================

    fun obtenerTodasLasOrdenes(): Flow<List<OrdenConDetalles>> = flow {
        sincronizarOrdenesDesdeApi(rutCliente = null)
        emitAll(ordenDao.obtenerTodasLasOrdenes())
    }

    fun obtenerOrdenesXUsuario(rutCliente: String): Flow<List<OrdenConDetalles>> = flow {
        sincronizarOrdenesDesdeApi(rutCliente = rutCliente)
        emitAll(ordenDao.obtenerOrdenesXUsuario(rutCliente))
    }

    fun obtenerOrdenPorId(ordenId: Long): Flow<OrdenConDetalles?> = flow {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.obtenerOrdenPorId(ordenId)
                if (response.isSuccessful && response.body() != null) {
                    guardarOrdenDesdeApi(response.body()!!)
                }
            } catch (e: Exception) { Log.e(TAG, "Error refrescando orden $ordenId: ${e.message}") }
        }
        emitAll(ordenDao.obtenerOrdenPorId(ordenId))
    }

    // =================================================================
    // 2. ESCRITURA (Checkout y Actualización)
    // =================================================================

    suspend fun finalizarCompraTransaccional(
        orden: OrdenEntity,
        detalles: List<DetalleOrdenEntity>
    ) {
        var idFinal = orden.id

        try {
            Log.d(TAG, "Enviando orden a API...")
            // Mapeamos la entidad local al DTO para enviar
            val ordenDto = mapearEntidadesADto(orden, detalles)
            val response = apiService.crearOrden(ordenDto)

            if (response.isSuccessful && response.body() != null) {
                val ordenCreada = response.body()!!
                idFinal = ordenCreada.id
                Log.d(TAG, "✓ Orden creada en servidor con ID: $idFinal")
            } else {
                Log.e(TAG, "⚠ Error API al crear orden: ${response.code()}. Guardando localmente.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error de red al crear orden. Guardando localmente: ${e.message}")
        }

        val ordenConIdCorrecto = orden.copy(id = idFinal)

        // Usamos try-catch por si Room intenta abortar por ID duplicado
        val idInsertado = try {
            ordenDao.insertOrden(ordenConIdCorrecto)
        } catch (e: Exception) {
            if(idFinal != 0L) idFinal else 0L // Si falla inserción, asumimos que ya existe o usamos 0
        }

        val idParaDetalles = if (idFinal != 0L) idFinal else idInsertado

        val detallesConId = detalles.map { detalle ->
            detalle.copy(ordenId = idParaDetalles)
        }
        ordenDao.insertDetalles(detallesConId)

        carritoDao.vaciar()
        Log.d(TAG, "✓ Compra finalizada y guardada localmente (ID: $idParaDetalles)")
    }

    suspend fun actualizarEstado(ordenId: Long, nuevoEstado: String) {
        ordenDao.actualizarEstado(ordenId, nuevoEstado)

        try {
            val body = mapOf("status" to nuevoEstado)
            apiService.actualizarEstado(ordenId, body)
            Log.d(TAG, "✓ Estado actualizado en API")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error actualizando estado en API: ${e.message}")
        }
    }

    suspend fun obtenerItemsOrden(ordenId: Long): List<ItemOrden> {
        return ordenDao.obtenerItemsOrden(ordenId)
    }

    // --- MÉTODOS BÁSICOS RESTAURADOS ---
    suspend fun insertOrden(orden: OrdenEntity): Long {
        return ordenDao.insertOrden(orden)
    }

    suspend fun insertDetalles(detalles: List<DetalleOrdenEntity>) {
        ordenDao.insertDetalles(detalles)
    }

    // =================================================================
    // 3. FUNCIONES PRIVADAS (Helpers)
    // =================================================================

    private fun sincronizarOrdenesDesdeApi(rutCliente: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (rutCliente == null) {
                    apiService.obtenerTodasLasOrdenes()
                } else {
                    apiService.obtenerOrdenesXUsuario(rutCliente)
                }

                if (response.isSuccessful && response.body() != null) {
                    val ordenesDto = response.body()!!
                    ordenesDto.forEach { dto ->
                        guardarOrdenDesdeApi(dto)
                    }
                    Log.d(TAG, "✓ Sincronización completada: ${ordenesDto.size} órdenes")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Fallo sincronización silenciosa: ${e.message}")
            }
        }
    }

    // --- CORRECCIÓN PRINCIPAL DE TIPADO ---
    private suspend fun guardarOrdenDesdeApi(dto: OrdenDto) {
        val modelo = dto.aModelo()

        // Aquí hacemos la conversión de tipos manual para que coincida con OrdenEntity
        val entidadOrden = OrdenEntity(
            id = modelo.id,
            rutCliente = modelo.rut,
            nombreCliente = modelo.nombreCliente,

            // 1. Date -> Long
            fechaCreacion = modelo.fechaCreacion.time,

            // 2. Mapeo de nombres correctos (según tu screenshot)
            direccion = modelo.direccionEnvio,

            // 3. Enum -> String (.name)
            tipoCompra = modelo.metodoPago.name,
            tipoCourier = modelo.courier.name,
            estado = modelo.estado.name,

            subtotal = modelo.subtotal,
            costoEnvio = modelo.costoEnvio,
            descuento = modelo.descuento,

            // 4. Nombre correcto en Entity
            totalPagar = modelo.total
        )

        try {
            ordenDao.insertOrden(entidadOrden)
        } catch (e: Exception) {
            // Ignoramos conflicto si ya existe
        }

        val detalles = dto.items.map { itemDto ->
            DetalleOrdenEntity(
                ordenId = dto.id,
                productoId = itemDto.productoId,
                nombre = itemDto.nombreProducto,
                cantidad = itemDto.cantidad,
                precio = itemDto.precioUnitarioFijo,
                imagenUrl = itemDto.imagenUrl ?: ""
            )
        }
        ordenDao.insertDetalles(detalles)
    }

    // --- CORRECCIÓN SECUNDARIA DE TIPADO ---
    private fun mapearEntidadesADto(orden: OrdenEntity, detalles: List<DetalleOrdenEntity>): OrdenDto {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        // Convertimos Long -> Date -> String
        val fechaString = try {
            formatoFecha.format(Date(orden.fechaCreacion))
        } catch (e: Exception) {
            formatoFecha.format(Date())
        }

        return OrdenDto(
            id = 0,
            rut = orden.rutCliente,
            nombreCliente = orden.nombreCliente,
            fechaCreacion = fechaString,

            // Usamos los campos de la ENTIDAD (Strings)
            estado = orden.estado,
            direccionEnvio = orden.direccion,
            metodoPago = orden.tipoCompra,
            courier = orden.tipoCourier,

            subtotal = orden.subtotal,
            costoEnvio = orden.costoEnvio,
            descuento = orden.descuento,
            total = orden.totalPagar,

            items = detalles.map { detalle ->
                ItemOrdenDto(
                    productoId = detalle.productoId,
                    ordenId = detalle.ordenId,
                    nombreProducto = detalle.nombre,
                    cantidad = detalle.cantidad,
                    precioUnitarioFijo = detalle.precio,
                    imagenUrl = detalle.imagenUrl
                )
            }
        )
    }
}