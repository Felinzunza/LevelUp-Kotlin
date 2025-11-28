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

/**
 * ViewModel que gestiona la lógica de UI
 * AndroidViewModel provee Context para Database
 */
class CarritoViewModel(application: Application,
                       private val carritoRepository: CarritoRepository,
                       private val ordenRepository: OrdenRepository
) : AndroidViewModel(application) {

    // Repository
    private val repository: CarritoRepository

    init {
        val database = AppDatabase.getDatabase(application)
        val dao = database.carritoDao()
        repository = CarritoRepository(dao)
    }

    // StateFlow para productos disponibles (hardcoded para este lab)
    val productosDisponibles = listOf(
        Producto(
            id = 1, 
            nombre = "Mouse Gamer", 
            descripcion = "Mouse óptico RGB con 6 botones", 
            precio = 25000.0, 
            imagenUrl = "", 
            categoria = "Periféricos", 
            stock = 10
        ),
        Producto(
            id = 2, 
            nombre = "Teclado Mecánico", 
            descripcion = "Teclado mecánico RGB retroiluminado", 
            precio = 45000.0, 
            imagenUrl = "", 
            categoria = "Periféricos", 
            stock = 5
        ),
        Producto(
            id = 3, 
            nombre = "Audífonos RGB", 
            descripcion = "Audífonos gaming con micrófono", 
            precio = 35000.0, 
            imagenUrl = "", 
            categoria = "Audio", 
            stock = 8
        )
    )

    // StateFlow para items en carrito (observa cambios en Room)
    val itemsCarrito: StateFlow<List<ItemCarrito>> = repository.obtenerCarrito()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Observer para logear cambios en el carrito
        viewModelScope.launch {
            itemsCarrito.collect { items ->
                Log.d("CARRITO_DB", "═══════════════════════════════")
                Log.d("CARRITO_DB", "Items en carrito: ${items.size}")
                items.forEachIndexed { index, item ->
                    Log.d("CARRITO_DB", "${index + 1}. ${item.producto.nombre} x${item.cantidad} - Subtotal: \$${item.subtotal.toInt()}")
                }
                Log.d("CARRITO_DB", "═══════════════════════════════")
            }
        }
    }

    // StateFlow para total del carrito
    val totalCarrito: StateFlow<Double> = repository.obtenerTotal()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    /**
     * Agrega un producto al carrito
     */
    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            Log.d("CARRITO_DB", "➕ Agregando: ${producto.nombre}")
            repository.agregarProducto(producto)
        }
    }

    /**
     * Vacía el carrito completo
     */
    fun vaciarCarrito() {
        viewModelScope.launch {
            Log.d(" CARRITO_DB", " Vaciando carrito completo")
            repository.vaciarCarrito()
        }
    }

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
            // 1. OBTENER ITEMS DEL CARRITO DE FORMA SÍNCRONA
            val itemsCarrito: List<ItemCarrito> = carritoRepository.obtenerCarrito().first()

            if (itemsCarrito.isEmpty()) {
                throw IllegalStateException("El carrito está vacío, no se puede generar la orden.")
            }

            // 2. CREAR LA CABECERA (OrdenEntity) - Ahora las variables son visibles
            val ordenCabecera = OrdenEntity(
                rutCliente = rutCliente, // ✅ Visible (es un parámetro)
                nombreCliente = nombreCliente, // ✅ Visible (es un parámetro)
                fechaCreacion = Date().time,
                direccion = direccionEnvio, // ✅ Visible (es un parámetro)
                tipoCourier = courier.name,
                estado = EstadoOrden.EN_PREPARACION.name, // El estado es fijo/derivado
                tipoCompra = metodoPago.name,
                subtotal = subtotal,
                descuento = descuento,
                costoEnvio = costoEnvio,
                totalPagar = totalPagar // ✅ Visible (es un parámetro)
            )

            // ... (El resto de la lógica de mapeo y transacción)
            val detalles: List<DetalleOrdenEntity> = itemsCarrito.map { item ->
                item.toDetalleEntity(ordenId = 0)
            }

            ordenRepository.finalizarCompraTransaccional(ordenCabecera, detalles)

        } catch (e: Exception) {
            // ... (Manejo de errores)
            throw e
        }
    }


}

class CarritoViewModelFactory(
    private val application: Application, // 👈 1. Aceptar la Application
    private val carritoRepository: CarritoRepository,
    private val ordenRepository: OrdenRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que la clase solicitada sea CarritoViewModel
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            // 👈 2. Pasar los TRES argumentos en el orden correcto
            return CarritoViewModel(application, carritoRepository, ordenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

