package com.example.levelUpKotlinProyect.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.levelUpKotlinProyect.data.repository.CarritoRepository
import com.example.levelUpKotlinProyect.domain.model.ItemCarrito
import kotlinx.coroutines.launch

/**
 * CarritoScreen: Muestra todos los productos en el carrito
 * 
 * Funcionalidades:
 * - Ver lista de productos agregados
 * - Ver total a pagar
 * - Vaciar carrito
 * - Click en producto para ver detalle
 * 
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    carritoRepository: CarritoRepository,
    onVolverClick: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    // Observar items del carrito
    val itemsCarrito by carritoRepository.obtenerCarrito().collectAsState(initial = emptyList())
    val total by carritoRepository.obtenerTotal().collectAsState(initial = 0.0)
    
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito (${itemsCarrito.size})") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón vaciar carrito
                    if (itemsCarrito.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    carritoRepository.vaciarCarrito()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Vaciar carrito",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Barra de total
            if (itemsCarrito.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatearPrecio(total),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (itemsCarrito.isEmpty()) {
                // Carrito vacío
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛒",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onVolverClick) {
                        Text("Ir a comprar")
                    }
                }
            } else {
                // Lista de productos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itemsCarrito) { item ->
                        CarritoItemCard(
                            item = item,
                            onCantidadChange = { nuevaCantidad ->
                                scope.launch {
                                    carritoRepository.modificarCantidad(
                                        item.producto.id,
                                        nuevaCantidad
                                    )
                                }
                            },
                            onEliminarClick = {
                                scope.launch {
                                    carritoRepository.eliminarProducto(item.producto.id)
                                }
                            },
                            onClick = { onProductoClick(item.producto.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * CarritoItemCard: Muestra un producto en el carrito con controles de cantidad
 * 
 * MEJORAS:
 * - Muestra cantidad actual
 * - Botones + / - para modificar cantidad
 * - Subtotal calculado automáticamente
 * - Botón eliminar independiente
 */
@Composable
fun CarritoItemCard(
    item: ItemCarrito,
    onCantidadChange: (Int) -> Unit,
    onEliminarClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            val context = LocalContext.current
            val imageResId = context.resources.getIdentifier(
                item.producto.imagenUrl,
                "drawable",
                context.packageName
            )
            
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(if (imageResId != 0) imageResId else item.producto.imagenUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Información y controles
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nombre
                Text(
                    text = item.producto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Precio unitario
                Text(
                    text = "Precio: ${formatearPrecio(item.producto.precio)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Menos (-)
                    IconButton(
                        onClick = { 
                            if (item.cantidad > 1) {
                                onCantidadChange(item.cantidad - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        enabled = item.cantidad > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Disminuir",
                            tint = if (item.cantidad > 1) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    
                    // Cantidad actual
                    Text(
                        text = "${item.cantidad}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .widthIn(min = 30.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    
                    // Botón Más (+)
                    IconButton(
                        onClick = { onCantidadChange(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Subtotal
                Text(
                    text = "Subtotal: ${formatearPrecio(item.subtotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Botón eliminar
            IconButton(onClick = onEliminarClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar producto",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Función helper para formatear precio
 */
fun formatearPrecio(precio: Double): String {
    val precioEntero = precio.toInt()
    return "$$${precioEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"
}
