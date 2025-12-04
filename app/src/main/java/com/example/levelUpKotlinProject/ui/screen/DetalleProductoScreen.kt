package com.example.levelUpKotlinProject.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.domain.model.Producto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: String,
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    onVolverClick: () -> Unit
) {
    var producto by remember { mutableStateOf<Producto?>(null) }
    var estaCargando by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(productoId) {
        if (productoId.isBlank()) {
            estaCargando = false
            return@LaunchedEffect
        }
        val encontrado = productoRepository.obtenerProductoPorId(productoId)
        producto = encontrado
        estaCargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // Usamos Box para superponer elementos (contenido y snackbar)
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (estaCargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (producto == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Text("Producto no encontrado", fontSize = 20.sp)
                }
            } else {
                val prod = producto!!

                // CAMBIO ESTRUCTURAL:
                // Usamos Column normal con scroll, y quitamos el weight(1f) que causaba el crash.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio automático entre elementos
                ) {
                    // --- IMAGEN ---
                    val resourceId = try {
                        context.resources.getIdentifier(
                            prod.imagenUrl, "drawable", context.packageName
                        )
                    } catch (e: Exception) { 0 }

                    if (resourceId != 0) {
                        Image(
                            painter = painterResource(id = resourceId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(250.dp)
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Imagen: ${prod.imagenUrl} no encontrada")
                            }
                        }
                    }

                    // --- DATOS ---
                    Text(text = prod.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Text(
                        text = "$${prod.precio.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(text = "Categoría: ${prod.categoria}", color = Color.Gray)

                    if (prod.stock > 0) {
                        Text("Stock disponible: ${prod.stock}", color = Color(0xFF2E7D32))
                    } else {
                        Text("Agotado", color = Color.Red, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider()

                    Text(text = "Descripción", fontWeight = FontWeight.Bold)
                    Text(text = prod.descripcion)

                    // Espacio final para que el botón no quede pegado al borde si hay mucho texto
                    Spacer(modifier = Modifier.height(80.dp))
                }

                // BOTÓN FLOTANTE O FIJO AL FONDO
                // Lo ponemos fuera del Column con scroll para que siempre esté visible abajo
                Button(
                    onClick = {
                        scope.launch {
                            carritoRepository.agregarProducto(prod)
                            mensaje = "¡Agregado al carrito!"
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    enabled = prod.stock > 0
                ) {
                    Icon(Icons.Default.ShoppingCart, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar al Carrito")
                }
            }

            // Snackbar
            if (mensaje != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp, start = 16.dp, end = 16.dp), // Subimos un poco para no tapar el botón
                    action = { TextButton(onClick = { mensaje = null }) { Text("OK") } }
                ) { Text(mensaje!!) }
            }
        }
    }
}