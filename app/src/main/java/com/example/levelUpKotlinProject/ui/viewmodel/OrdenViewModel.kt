package com.example.levelUpKotlinProject.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.toItemOrden
import com.example.levelUpKotlinProject.data.local.entity.toOrden
import com.example.levelUpKotlinProject.data.local.relations.toOrden
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import com.example.levelUpKotlinProject.domain.model.Orden
import com.example.levelUpKotlinProject.ui.state.OrdenesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdenViewModel(
    private val repositorio: OrdenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdenesUiState())
    val uiState: StateFlow<OrdenesUiState> = _uiState.asStateFlow()

    // Estado para la pantalla de detalle
    var ordenSeleccionada by mutableStateOf<Orden?>(null)
        private set

    var itemsOrdenSeleccionada by mutableStateOf<List<ItemOrden>>(emptyList())
        private set

    init {
        cargarOrdenes()
    }

    /**
     * Carga TODAS las órdenes (Admin).
     */
    fun cargarOrdenes() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            repositorio.obtenerTodasLasOrdenes()
                .catch { exception ->
                    _uiState.update {
                        it.copy(estaCargando = false, error = exception.message ?: "Error desconocido")
                    }
                }
                .collect { ordenesConDetalles ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            estaCargando = false,
                            // Mapeamos la relación completa al modelo de dominio
                            ordenes = ordenesConDetalles.map { it.toOrden() },
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Filtra órdenes por RUT (Cliente).
     */
    fun obtenerOrdenesXUsuario(rutCliente: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            repositorio.obtenerOrdenesXUsuario(rutCliente)
                .catch { exception ->
                    _uiState.update {
                        it.copy(estaCargando = false, error = exception.message)
                    }
                }
                .collect { ordenesConDetalles ->
                    _uiState.update {
                        it.copy(
                            estaCargando = false,
                            ordenes = ordenesConDetalles.map { it.toOrden() },
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Carga una orden específica por su ID (String).
     */
    fun cargarDetalleOrden(ordenId: String) {
        viewModelScope.launch {
            repositorio.obtenerOrdenPorId(ordenId)
                .catch {
                    ordenSeleccionada = null
                    itemsOrdenSeleccionada = emptyList()
                }
                .collect { ordenCompleta ->
                    if (ordenCompleta != null) {
                        val ordenDominio = ordenCompleta.toOrden()
                        ordenSeleccionada = ordenDominio
                        itemsOrdenSeleccionada = ordenDominio.items
                    } else {
                        ordenSeleccionada = null
                        itemsOrdenSeleccionada = emptyList()
                    }
                }
        }
    }

    /**
     * Actualiza el estado de una orden (ID String).
     */
    fun actualizarEstadoOrden(ordenId: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                repositorio.actualizarEstado(ordenId, nuevoEstado)
                // Forzamos recarga del detalle por si acaso, aunque el Flow debería manejarlo
                cargarDetalleOrden(ordenId)
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun limpiarDetalle() {
        ordenSeleccionada = null
        itemsOrdenSeleccionada = emptyList()
    }

}


// 🏭 AQUÍ ESTÁ LA FÁBRICA QUE FALTABA 🏭
// Esta clase le enseña a Android cómo crear tu ViewModel inyectándole el repositorio.
class OrdenesViewModelFactory(
    private val repositorio: OrdenRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdenViewModel::class.java)) {
            return OrdenViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}