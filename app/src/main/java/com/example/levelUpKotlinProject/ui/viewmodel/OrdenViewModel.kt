package com.example.levelUpKotlinProject.ui.viewmodel

import com.example.levelUpKotlinProject.domain.model.Orden
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
import com.example.levelUpKotlinProject.data.local.relations.OrdenConDetalles
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.ui.state.OrdenesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.levelUpKotlinProject.data.local.relations.toOrden
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import kotlinx.coroutines.flow.Flow
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


            //3.  Llama al repositorio para obtener SELECT * FROM ordenes
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

    fun obtenerOrdenPorId(ordenId: Long): Flow<OrdenConDetalles?> {

        return repositorio.obtenerOrdenPorId(ordenId)
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



    var ordenSeleccionada by mutableStateOf<Orden?>(null)
        private set
    var itemsOrdenSeleccionada by mutableStateOf<List<ItemOrden>>(emptyList())
        private set

    fun cargarDetalleOrden(ordenId: Long) {
        viewModelScope.launch {
            // 1. Llamamos al repositorio (que devuelve Flow<OrdenConDetalles?>)
            repositorio.obtenerOrdenPorId(ordenId).collect { ordenCompleta ->

                if (ordenCompleta != null) {
                    // ¡AQUÍ ESTÁ LA MAGIA!
                    // Usamos la función de tu archivo para convertir todo de una vez
                    val ordenDominio = ordenCompleta.toOrden()

                    // Actualizamos los estados de la UI
                    ordenSeleccionada = ordenDominio

                    // Si tu modelo Orden ya tiene la lista 'items', puedes usar:
                    // itemsOrdenSeleccionada = ordenDominio.items

                    // Si prefieres extraerlos manualmente desde el objeto intermedio:
                    itemsOrdenSeleccionada = ordenCompleta.detalles.map { it.toItemOrden() }
                } else {
                    ordenSeleccionada = null
                    itemsOrdenSeleccionada = emptyList()
                }
            }
        }
    }

    fun limpiarDetalle() {
        ordenSeleccionada = null
        itemsOrdenSeleccionada = emptyList()
    }

    fun actualizarEstadoOrden(ordenId: Long, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                // 1. Ejecutar la actualización en la BD
                repositorio.actualizarEstado(ordenId, nuevoEstado)

                // 2. Recargar el detalle para que la pantalla se actualice
                // (Aunque si usas Flow, esto podría ser automático,
                // pero forzar la recarga asegura que veas el cambio)
                cargarDetalleOrden(ordenId)

            } catch (e: Exception) {
                e.printStackTrace() // Mira el Logcat si falla aquí
            }
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

