package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.ui.state.OrdenesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.levelUpKotlinProject.data.local.relations.toOrden
import kotlinx.coroutines.flow.update

class OrdenViewModel(
    private val repositorio: OrdenRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(OrdenesUiState())
    val uiState: StateFlow<OrdenesUiState> = _uiState.asStateFlow()

    init {
        // Carga la lista completa de órdenes al inicio
        cargarOrdenes()
    }

    /**
     * FUNCIÓN PÚBLICA: Carga TODAS las órdenes (Modo Administrador).
     * Usa el método del repositorio que no tiene filtro WHERE.
     */
    fun cargarOrdenes() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            // Llama al repositorio para obtener SELECT * FROM ordenes
            repositorio.obtenerTodasLasOrdenes()

                .catch { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            estaCargando = false,
                            error = exception.message ?: "Error desconocido"
                        )
                    }
                }
                .collect { ordenesConDetalles ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            estaCargando = false,
                            // Aplica el mapeo
                            ordenes = ordenesConDetalles.map { it.toOrden() },
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * FUNCIÓN PÚBLICA: Carga órdenes filtradas por RUT de cliente.
     * Usa el método del repositorio que aplica el filtro WHERE.
     */
    fun obtenerOrdenesXUsuario(rutCliente: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            // Llama al repositorio para obtener SELECT * FROM ordenes WHERE rutCliente = :rutCliente
            repositorio.obtenerOrdenesXUsuario(rutCliente)

                .catch { exception ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            estaCargando = false,
                            error = exception.message ?: "Error al filtrar órdenes"
                        )
                    }
                }
                .collect { ordenesConDetalles ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            estaCargando = false,
                            // Aplica el mapeo
                            ordenes = ordenesConDetalles.map { it.toOrden() },
                            error = null
                        )
                    }
                }
        }
    }


    // --- MÉTODOS DE ESCRITURA (Transacciones y Actualización) ---

    fun actualizarEstado(ordenId: Long, nuevoEstado: String) {
        viewModelScope.launch {
            repositorio.actualizarEstado(ordenId, nuevoEstado)
        }
    }

    fun insertarOrden(orden: OrdenEntity): Long {
        viewModelScope.launch {
            repositorio.insertOrden(orden)
        }
        return 0L
    }

    fun insertarDetalles(detalles: List<DetalleOrdenEntity>) {
        viewModelScope.launch {
            repositorio.insertDetalles(detalles)
        }
    }
}

class OrdenesViewModelFactory(
    // Acepta la dependencia que se necesita inyectar
    private val repositorio: OrdenRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que la clase solicitada sea OrdenesViewModel
        if (modelClass.isAssignableFrom(OrdenViewModel::class.java)) {
            // Retorna una nueva instancia, pasándole el repositorio al constructor
            return OrdenViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

