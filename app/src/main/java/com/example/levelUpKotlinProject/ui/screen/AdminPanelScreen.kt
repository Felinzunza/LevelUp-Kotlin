package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.levelUpKotlinProject.domain.model.Orden
import com.example.levelUpKotlinProject.domain.model.Producto

/**
 * AdminPanelScreen: Panel principal de administración con módulos de Productos y Órdenes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    //listas necesarias para el panel de administración
    productos: List<Producto>,
    ordenes: List<Orden>,

    usernameAdmin: String,
    onAgregarProducto: () -> Unit,
    onEditarProducto: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit,
    onCerrarSesion: () -> Unit,

    onVerDetalleOrden: (Long) -> Unit,
    onCambiarEstadoOrden: (ordenId: Long, nuevoEstado: String) -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf<Producto?>(null) }
    // 0=Productos, 1=Órdenes, 2=Estadísticas
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Admin")
                        Text(
                            text = "Sesión: $usernameAdmin",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (pestanaSeleccionada == 0) {
                FloatingActionButton(
                    onClick = onAgregarProducto,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar Producto"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pestañas
            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("Productos") },
                    icon = { Icon(Icons.Filled.ShoppingCart, null) }
                )
                // PESTAÑA: ÓRDENES (Índice 1)
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Órdenes") },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                )
                // Pestaña de Estadísticas (Índice 2)
                Tab(
                    selected = pestanaSeleccionada == 2,
                    onClick = { pestanaSeleccionada = 2 },
                    text = { Text("Estadísticas") },
                    icon = { Icon(Icons.Filled.Info, null) }
                )
            }

            // Contenido según pestaña
            when (pestanaSeleccionada) {
                0 -> {
                    // Lista de productos
                    if (productos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay productos", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onAgregarProducto) {
                                    Text("Agregar Primero")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(productos) { producto ->
                                AdminProductoCard(
                                    producto = producto,
                                    onEditar = { onEditarProducto(producto) },
                                    onEliminar = { mostrarDialogoEliminar = producto }
                                )
                            }
                        }
                    }
                }
                // MÓDULO DE ÓRDENES
                1 -> {
                    OrdenesPanelContent(
                        ordenes = ordenes,
                        onVerDetalle = onVerDetalleOrden,
                        onCambiarEstadoOrden = onCambiarEstadoOrden
                    )
                }
                2 -> {
                    // Estadísticas
                    EstadisticasPanel(productos, ordenes)
                }
            }
        }
    }

    // Diálogo de confirmación de eliminación
    if (mostrarDialogoEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Eliminar '${mostrarDialogoEliminar!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminarProducto(mostrarDialogoEliminar!!)
                        mostrarDialogoEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ---------------------------------------------------------------------
// --- COMPONENTES AUXILIARES ---
// ---------------------------------------------------------------------

@Composable
fun AdminProductoCard(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = producto.categoria,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Stock: ${producto.stock} | $${producto.precio.toInt()}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// --- NUEVO COMPONENTE: ORDENES PANEL CONTENT ---

@Composable
fun OrdenesPanelContent(
    ordenes: List<Orden>, // Lista de modelos de dominio Orden
    onVerDetalle: (Long) -> Unit,
    onCambiarEstadoOrden: (ordenId: Long, nuevoEstado: String) -> Unit
) {
    if (ordenes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay órdenes registradas.", fontSize = 18.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ordenes) { orden ->
                OrdenCard(
                    orden = orden,
                    onDetalleClick = { onVerDetalle(orden.id.toLong()) }, // Aseguramos que sea Long
                    onCambiarEstado = { ordenId, nuevoEstado ->
                        onCambiarEstadoOrden(ordenId, nuevoEstado)
                    }
                )
            }
        }
    }
}

@Composable
fun OrdenCard(
    orden: Orden,
    onDetalleClick: () -> Unit,
    onCambiarEstado: (Long, String) -> Unit

) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Orden #${orden.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fecha: ${orden.fechaCreacionFormateada()}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: ${orden.totalFormateado()}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Sección de Estado y Detalle
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Estado
                Text(
                    text = orden.estadoDisplay,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Button(
                    onClick = onDetalleClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Detalle")
                }
            }
        }
    }
}

/**
 * Panel de estadísticas básicas
 */
@Composable
fun EstadisticasPanel(productos: List<Producto>, ordenes: List<Orden>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total de productos
        EstadisticaCard(
            titulo = "Total Productos",
            valor = productos.size.toString(),
            icono = Icons.Filled.ShoppingCart
        )
        // Total de Órdenes
        EstadisticaCard(
            titulo = "Total Órdenes",
            valor = ordenes.size.toString(),
            icono = Icons.AutoMirrored.Filled.List
        )

        // Stock total
        EstadisticaCard(
            titulo = "Stock Total",
            valor = productos.sumOf { it.stock }.toString(),
            icono = Icons.Filled.Star
        )

        // Valor inventario
        EstadisticaCard(
            titulo = "Valor Inventario",
            valor = "$${productos.sumOf { it.precio * it.stock }.toInt()}",
            icono = Icons.Filled.Star
        )

        // Categorías
        EstadisticaCard(
            titulo = "Categorías",
            valor = productos.map { it.categoria }.distinct().size.toString(),
            icono = Icons.Filled.Info
        )
    }
}

@Composable
fun EstadisticaCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = valor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}