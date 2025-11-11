package com.example.levelUpKotlinProyect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProyect.domain.model.Producto

/**
 * AdminPanelScreen: Panel principal de administración
 * 
 * Funcionalidades:
 * - Ver todos los productos
 * - Agregar nuevo producto
 * - Editar producto existente
 * - Eliminar producto
 * - Ver estadísticas básicas
 * - Cerrar sesión
 * 
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    productos: List<Producto>,
    usernameAdmin: String,
    onAgregarProducto: () -> Unit,
    onEditarProducto: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf<Producto?>(null) }
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
                            imageVector = Icons.Default.ExitToApp,
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
                        imageVector = Icons.Default.Add,
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
                    icon = { Icon(Icons.Default.ShoppingCart, null) }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Estadísticas") },
                    icon = { Icon(Icons.Default.Info, null) }
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
                1 -> {
                    // Estadísticas
                    EstadisticasPanel(productos)
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

/**
 * Card de producto con acciones de admin
 */
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
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Panel de estadísticas básicas
 */
@Composable
fun EstadisticasPanel(productos: List<Producto>) {
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
            icono = Icons.Default.ShoppingCart
        )
        
        // Stock total
        EstadisticaCard(
            titulo = "Stock Total",
            valor = productos.sumOf { it.stock }.toString(),
            icono = Icons.Default.Star
        )
        
        // Valor inventario
        EstadisticaCard(
            titulo = "Valor Inventario",
            valor = "$${productos.sumOf { it.precio * it.stock }.toInt()}",
            icono = Icons.Default.Star
        )
        
        // Categorías
        EstadisticaCard(
            titulo = "Categorías",
            valor = productos.map { it.categoria }.distinct().size.toString(),
            icono = Icons.Default.Info
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
