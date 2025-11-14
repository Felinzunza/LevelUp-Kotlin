package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.ui.state.ProductoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ProductoViewModel: Gestiona el estado de los productos
 * 
 * ¿Qué hace?
 * - Carga productos desde el repositorio
 * - Maneja estados (cargando, éxito, error)
 * - Sobrevive a rotaciones de pantalla
 *
 */
class ProductoViewModel(
    private val repositorio: ProductoRepository
) : ViewModel() {
    
    // _uiState: privado, solo este ViewModel puede modificarlo
    private val _uiState = MutableStateFlow(ProductoUiState())
    
    // uiState: público pero solo lectura, las pantallas observan cambios
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()
    
    init {
        // Cargar productos al crear el ViewModel
        cargarProductos()
    }
    
    /**
     * Carga la lista de productos desde el repositorio
     */
    fun cargarProductos() {
        viewModelScope.launch {
            // Indicar que está cargando
            _uiState.value = _uiState.value.copy(estaCargando = true)
            
            // Observar cambios en productos (Flow)
            repositorio.obtenerProductos()
                .catch { exception ->
                    // Si hay error, actualizar estado
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
                .collect { productos ->
                    // Actualizar con los productos obtenidos
                    _uiState.value = _uiState.value.copy(
                        estaCargando = false,
                        productos = productos,
                        error = null
                    )
                }
        }
    }
    
    /**
     * Busca un producto por ID
     */
    suspend fun obtenerProductoPorId(id: Int) = repositorio.obtenerProductoPorId(id)
    
    /**
     * Agrega un nuevo producto
     */
    fun agregarProducto(producto: com.example.levelUpKotlinProject.domain.model.Producto) {
        viewModelScope.launch {
            repositorio.insertarProducto(producto)
        }
    }
    
    /**
     * Actualiza un producto existente
     */
    fun actualizarProducto(producto: com.example.levelUpKotlinProject.domain.model.Producto) {
        viewModelScope.launch {
            repositorio.actualizarProducto(producto)
        }
    }
    
    /**
     * Elimina un producto
     */
    fun eliminarProducto(producto: com.example.levelUpKotlinProject.domain.model.Producto) {
        viewModelScope.launch {
            repositorio.eliminarProducto(producto)
        }
    }
}

/**
 * Factory: Crea instancias del ViewModel con parámetros
 * 
 * ¿Por qué necesitamos esto?
 * - Los ViewModels normalmente no aceptan parámetros en el constructor
 * - El Factory le dice a Android cómo crear el ViewModel con el repositorio
 * 
 * Sin Factory:    ❌ ProductoViewModel() // No funciona, necesita repositorio
 * Con Factory:    ✅ ProductoViewModel(repositorio) // Funciona!
 */
class ProductoViewModelFactory(
    private val repositorio: ProductoRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que estamos creando el ViewModel correcto
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
