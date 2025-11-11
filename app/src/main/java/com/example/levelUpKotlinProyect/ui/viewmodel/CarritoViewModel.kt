package com.example.levelUpKotlinProyect.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProyect.data.local.AppDatabase
import com.example.levelUpKotlinProyect.data.repository.CarritoRepository
import com.example.levelUpKotlinProyect.domain.model.ItemCarrito
import com.example.levelUpKotlinProyect.domain.model.Producto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de UI
 * AndroidViewModel provee Context para Database
 */
class CarritoViewModel(application: Application) : AndroidViewModel(application) {

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
}