package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProject.domain.model.Orden
import com.example.levelUpKotlinProject.domain.model.Producto
import com.example.levelUpKotlinProject.domain.model.Usuario

/**
 * AdminPanelScreen: Panel principal de administración con módulos de Productos y Órdenes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    //listas necesarias para el panel de administración
    usuarios: List<Usuario>,
    productos: List<Producto>,
    ordenes: List<Orden>,

    onAgregarUsuario: () -> Unit,
    onEditarUsuario: (Usuario) -> Unit,
    onEliminarUsuario: (Usuario) -> Unit,

    usernameAdmin: String,
    onAgregarProducto: () -> Unit,
    onEditarProducto: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit,
    onCerrarSesion: () -> Unit,

    onVerDetalleOrden: (String) -> Unit,
    onCambiarEstadoOrden: (ordenId: String, nuevoEstado: String) -> Unit,
) {

    var mostrarDialogoEliminarUsuario by remember { mutableStateOf<Usuario?>(null) }

    var mostrarDialogoEliminarProducto by remember { mutableStateOf<Producto?>(null) }
    // 0=Productos, 1=Órdenes, 2=Estadísticas
    var pestanaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Admin",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
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
        // 🛑 CAMBIO: Eliminado el FloatingActionButton para que no tape contenido
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Pestañas
            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("Usuarios", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.Filled.Face, null, tint = MaterialTheme.colorScheme.secondary)},
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Productos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.Filled.ShoppingCart, null, tint = MaterialTheme.colorScheme.secondary) }
                )
                // PESTAÑA: ÓRDENES (Índice 1)
                Tab(
                    selected = pestanaSeleccionada == 2,
                    onClick = { pestanaSeleccionada = 2 },
                    text = { Text("Órdenes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) }
                )
                // Pestaña de Estadísticas (Índice 2)
                Tab(
                    selected = pestanaSeleccionada == 3,
                    onClick = { pestanaSeleccionada = 3 },
                    text = { Text("Estadísticas", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.secondary) }
                )
            }

            // Contenido según pestaña
            when (pestanaSeleccionada) {
                0 -> {
                    // Contenido: Lista de Usuarios
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ✅ CAMBIO: Botón Agregar al principio de la lista
                        item {
                            Button(
                                onClick = onAgregarUsuario,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar Nuevo Usuario")
                            }
                        }

                        if(usuarios.isEmpty()){
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay usuarios registrados", fontSize = 16.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else {
                            items(usuarios, key = { it.id }) { usuario ->
                                AdminUsuarioCard(
                                    usuario = usuario,
                                    onEditar = { onEditarUsuario(usuario) },
                                    onEliminar = { mostrarDialogoEliminarUsuario = usuario }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ✅ CAMBIO: Botón Agregar al principio de la lista
                        item {
                            Button(
                                onClick = onAgregarProducto,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar Nuevo Producto")
                            }
                        }

                        if (productos.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay productos", fontSize = 16.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else {
                            items(productos) { producto ->
                                AdminProductoCard(
                                    producto = producto,
                                    onEditar = { onEditarProducto(producto) },
                                    onEliminar = { mostrarDialogoEliminarProducto = producto }
                                )
                            }
                        }
                    }
                }
                // MÓDULO DE ÓRDENES
                2 -> {
                    OrdenesPanelContent(
                        ordenes = ordenes,
                        onVerDetalle = onVerDetalleOrden,
                        onCambiarEstadoOrden = onCambiarEstadoOrden
                    )
                }
                3 -> {
                    // Estadísticas
                    EstadisticasPanel(productos, ordenes)
                }
            }
        }
    }

    if (mostrarDialogoEliminarUsuario != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarUsuario = null },
            title = { Text("Confirmar Eliminación de Usuario") },
            text = { Text("¿Eliminar a '${mostrarDialogoEliminarUsuario!!.nombre} ${mostrarDialogoEliminarUsuario!!.apellido}' (RUT: ${mostrarDialogoEliminarUsuario!!.rut})?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminarUsuario(mostrarDialogoEliminarUsuario!!)
                        mostrarDialogoEliminarUsuario = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminarUsuario = null }) {
                    Text("Cancelar")
                }
            }
        )
    }


    // Diálogo de confirmación de eliminación
    if (mostrarDialogoEliminarProducto != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarProducto = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Eliminar '${mostrarDialogoEliminarProducto!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminarProducto(mostrarDialogoEliminarProducto!!)
                        mostrarDialogoEliminarProducto = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminarProducto = null }) {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
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
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Stock: ${producto.stock} | $${producto.precio.toInt()}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.secondary
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
fun AdminUsuarioCard(
    usuario: Usuario,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Nombre completo
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Email
                Text(
                    text = usuario.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )

                // Rol y RUT
                Text(
                    text = "Rol: ${usuario.rol.name} | RUT: ${usuario.rut}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEditar) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar Usuario",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar Usuario",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun OrdenesPanelContent(
    ordenes: List<Orden>, // Lista de modelos de dominio Orden
    onVerDetalle: (String) -> Unit,
    onCambiarEstadoOrden: (ordenId: String, nuevoEstado: String) -> Unit
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
                    onDetalleClick = { onVerDetalle(orden.id)}, // Aseguramos que sea Long
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
    onCambiarEstado: (String, String) -> Unit

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
                    color = MaterialTheme.colorScheme.secondary
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
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
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
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}