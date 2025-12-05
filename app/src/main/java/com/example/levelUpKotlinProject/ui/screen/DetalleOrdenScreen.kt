package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProject.domain.model.EstadoOrden
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import com.example.levelUpKotlinProject.ui.viewmodel.OrdenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOrdenScreen(
    ordenId: String, // ✅ CAMBIO: De Long a String
    viewModel: OrdenViewModel,
    onVolverClick: () -> Unit
) {
    // 1. Cargar los datos al abrir la pantalla
    LaunchedEffect(ordenId) {
        viewModel.cargarDetalleOrden(ordenId) // Asegúrate de que el ViewModel acepte String aquí también
    }

    // 2. Observar el estado del ViewModel
    val orden = viewModel.ordenSeleccionada

    // Estados para el menú desplegable
    var menuExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Orden #$ordenId") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (orden == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // --- TARJETA DE RESUMEN CON EDICIÓN ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Resumen de la Compra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Fila de Fecha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fecha:")
                            Text(orden.fechaCreacionFormateada(), fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // --- FILA DE ESTADO CON MENÚ ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Estado:")

                            Box {
                                FilterChip(
                                    selected = true,
                                    onClick = { menuExpandido = true },
                                    // Mostramos el texto bonito (displayString) si coincide, o el texto crudo si no
                                    label = { Text(orden.estadoDisplay) },
                                    trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )

                                DropdownMenu(
                                    expanded = menuExpandido,
                                    onDismissRequest = { menuExpandido = false }
                                ) {
                                    EstadoOrden.entries.forEach { estadoEnum ->
                                        DropdownMenuItem(
                                            // 1. Mostramos el texto amigable ("En preparación")
                                            text = { Text(estadoEnum.displayString) },
                                            onClick = {
                                                // 2. Enviamos el NOMBRE técnico ("EN_PREPARACION") a la BD
                                                // ✅ CAMBIO: Usamos el ID como String (asumiendo que orden.id ya es String en tu modelo actualizado)
                                                viewModel.actualizarEstadoOrden(orden.id, estadoEnum.name)
                                                menuExpandido = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


                        Spacer(modifier = Modifier.height(4.dp))
                        // 👇 DATOS DEL CLIENTE 👇
                        Text("Datos de Envío", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)

                        // RUT
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("RUT:", style = MaterialTheme.typography.bodyMedium)
                            Text(orden.rut, fontWeight = FontWeight.Bold)
                        }

                        // 👇 FILA CLIENTE DIRECTA
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cliente:", style = MaterialTheme.typography.bodyMedium)
                            Text(orden.nombreCliente, fontWeight = FontWeight.Bold)
                        }


                        // Fila Dirección (Multilínea)
                        Column {
                            Text("Dirección:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = orden.direccionEnvio, // Aquí se verá "Calle 123, Santiago, Metropolitana"
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Fila Courier
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Courier:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = orden.courierDisplay,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge

                            )
                        }
                        // Fila de Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", style = MaterialTheme.typography.titleLarge)
                            Text(orden.totalFormateado(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ... (Resto del código: Título Productos y Lista LazyColumn igual que antes) ...
                Text(text = "Productos (${orden.items.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(orden.items) { item ->
                        ItemOrdenCard(item) // Usamos el componente que ya creaste
                    }
                }
            }
        }
    }
}

/**
 * Componente para dibujar cada fila de producto
 */
@Composable
fun ItemOrdenCard(item: ItemOrden) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Columna Izquierda: Nombre y Cantidad
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombreProducto,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${item.cantidad} x $${item.precioUnitarioFijo.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Columna Derecha: Subtotal
            Text(
                text = "$${item.subtotal.toInt()}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}