package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.domain.model.Producto
import kotlinx.coroutines.launch

/**
 * DetalleProductoScreen: Muestra información completa de un producto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Int,
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    onVolverClick: () -> Unit
) {
    // Estado del producto
    var producto by remember { mutableStateOf<Producto?>(null) }
    var estaCargando by remember { mutableStateOf(true) }
    var mostrarMensaje by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Cargar producto al crear la pantalla
    LaunchedEffect(productoId) {
        estaCargando = true
        producto = productoRepository.obtenerProductoPorId(productoId)
        estaCargando = false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Estado: Cargando
                estaCargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                // Estado: Producto no encontrado
                producto == null -> {
                    Text(
                        text = "Producto no encontrado",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                // Estado: Mostrar detalle
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Imagen grande del producto
                        val context = LocalContext.current
                        val imageResId = context.resources.getIdentifier(
                            producto!!.imagenUrl,
                            "drawable",
                            context.packageName
                        )
                        
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(if (imageResId != 0) imageResId else producto!!.imagenUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = producto!!.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Nombre del producto
                        Text(
                            text = producto!!.nombre,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Categoría
                        Text(
                            text = "Categoría: ${producto!!.categoria}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Divider()
                        
                        // Descripción
                        Text(
                            text = "Descripción",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = producto!!.descripcion,
                            fontSize = 16.sp
                        )
                        
                        Divider()
                        
                        // Precio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Precio:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = producto!!.precioFormateado(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Stock
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Stock disponible:",
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${producto!!.stock} unidades",
                                fontSize = 16.sp,
                                color = if (producto!!.hayStock) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Botón agregar al carrito
                        Button(
                            onClick = {
                                scope.launch {
                                    producto?.let {
                                        carritoRepository.agregarProducto(it)
                                        mostrarMensaje = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = producto!!.hayStock
                        ) {
                            Text("Agregar al Carrito")
                        }
                        
                        // Mensaje de confirmación
                        if (mostrarMensaje) {
                            Text(
                                text = "✓ Producto agregado al carrito",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(2000)
                                mostrarMensaje = false
                            }
                        }
                    }
                }
            }
        }
    }
}
