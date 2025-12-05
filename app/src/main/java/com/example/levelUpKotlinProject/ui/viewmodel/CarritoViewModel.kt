package com.example.levelUpKotlinProject.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.local.AppDatabase
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.domain.model.EstadoOrden
import com.example.levelUpKotlinProject.domain.model.ItemCarrito
import com.example.levelUpKotlinProject.domain.model.Producto
import com.example.levelUpKotlinProject.domain.model.TipoCompra
import com.example.levelUpKotlinProject.domain.model.TipoCourier
import com.example.levelUpKotlinProject.domain.model.toDetalleEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class CarritoViewModel(
    application: Application,
    private val carritoRepository: CarritoRepository,
    private val ordenRepository: OrdenRepository
) : AndroidViewModel(application) {

    private val repository: CarritoRepository

    init {
        val database = AppDatabase.getDatabase(application)
        val dao = database.carritoDao()
        repository = CarritoRepository(dao)
    }

    // ✅ RESTAURADO: Lista de productos disponibles (necesaria para que compile tu UI)
    // Ajustado a IDs String para ser compatible con tu nuevo modelo.
    val productosDisponibles = listOf(
        Producto(id = "1", nombre = "Mouse Gamer", descripcion = "Mouse óptico RGB", precio = 25000.0, imagenUrl = "mouse_gamer", categoria = "Periféricos", stock = 10),
        Producto(id = "2", nombre = "Teclado Mecánico", descripcion = "Teclado RGB", precio = 45000.0, imagenUrl = "teclado_mecanico", categoria = "Periféricos", stock = 5),
        Producto(id = "3", nombre = "Audífonos RGB", descripcion = "Audífonos gaming", precio = 35000.0, imagenUrl = "audifonos_rgb", categoria = "Audio", stock = 8)
    )

    // StateFlow para items en carrito
    val itemsCarrito: StateFlow<List<ItemCarrito>> = repository.obtenerCarrito()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // StateFlow para total del carrito
    val totalCarrito: StateFlow<Double> = repository.obtenerTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            Log.d("CARRITO_DB", "➕ Agregando: ${producto.nombre}")
            repository.agregarProducto(producto)
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            Log.d(" CARRITO_DB", " Vaciando carrito completo")
            repository.vaciarCarrito()
        }
    }

    // Lógica de compra con IDs String
    suspend fun finalizarCompra(
        rutCliente: String,
        nombreCliente: String,
        direccionEnvio: String,
        metodoPago: TipoCompra,
        courier: TipoCourier,
        subtotal: Double,
        descuento: Double,
        costoEnvio: Double,
        totalPagar: Double
    ) {
        try {
            val items = carritoRepository.obtenerCarrito().first()
            if (items.isEmpty()) throw IllegalStateException("Carrito vacío")

            // 1. Crear cabecera con ID String vacío ("") para indicar nueva orden
            val ordenCabecera = OrdenEntity(
                id = "",
                rutCliente = rutCliente,
                nombreCliente = nombreCliente,
                fechaCreacion = Date().time,
                direccion = direccionEnvio,
                tipoCourier = courier.name,
                estado = EstadoOrden.EN_PREPARACION.name,
                tipoCompra = metodoPago.name,
                subtotal = subtotal,
                descuento = descuento,
                costoEnvio = costoEnvio,
                totalPagar = totalPagar
            )

            // 2. Mapear detalles con ID String vacío
            val detalles: List<DetalleOrdenEntity> = items.map { item ->
                item.toDetalleEntity(ordenId = "")
            }

            // 3. Finalizar transacción
            ordenRepository.finalizarCompraTransaccional(ordenCabecera, detalles)

        } catch (e: Exception) {
            throw e
        }
    }
}

class CarritoViewModelFactory(
    private val application: Application,
    private val carritoRepository: CarritoRepository,
    private val ordenRepository: OrdenRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            return CarritoViewModel(application, carritoRepository, ordenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}