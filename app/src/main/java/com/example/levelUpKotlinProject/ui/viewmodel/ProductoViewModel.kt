package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.domain.model.Producto
import com.example.levelUpKotlinProject.ui.state.ProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
 * ProductoViewModel: Gestiona el estado de los productos de forma REACTIVA
 */
class ProductoViewModel(
    private val repositorio: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        // Al iniciar, nos suscribimos a los cambios de la base de datos
        cargarProductosEnTiempoReal()
    }

    /**
     * Mantiene una conexión abierta con el repositorio.
     * Si el Repositorio usa 'emitAll' (Room), este collect NUNCA termina,
     * por lo que siempre recibirá las actualizaciones automáticas.
     */
    fun cargarProductosEnTiempoReal() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            repositorio.obtenerProductos()
                .catch { exception ->
                    _uiState.update {
                        it.copy(estaCargando = false, error = exception.message)
                    }
                }
                .collect { productos ->
                    _uiState.update {
                        it.copy(estaCargando = false, productos = productos, error = null)
                    }
                }
        }
    }

    /**
     * Busca un producto por ID (Útil para la pantalla de edición)
     */
    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return repositorio.obtenerProductoPorId(id)
    }

    // --- ACCIONES CRUD ---
    // Nota: No necesitamos llamar a "cargarProductos" de nuevo manualmente,
    // porque el flujo del init se enterará solo del cambio en la BD.

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            try {
                repositorio.insertarProducto(producto)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al crear: ${e.message}") }
            } finally {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            try {
                repositorio.actualizarProducto(producto)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar: ${e.message}") }
            } finally {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            try {
                repositorio.eliminarProducto(producto)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar: ${e.message}") }
            } finally {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }
}

class ProductoViewModelFactory(
    private val repositorio: ProductoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}