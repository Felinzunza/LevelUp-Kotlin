package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
fun HomeScreen(
    productoRepository: ProductoRepository,
    carritoRepository: CarritoRepository,
    onProductoClick: (String) -> Unit,
    onCarritoClick: () -> Unit,
    onRegistroClick: () -> Unit,
    onVolverPortada: () -> Unit,
    onCerrarSesion: () -> Unit,
    onIniciarSesionClick: () -> Unit,
    estaLogueado: Boolean,
    nombreUsuario: String?
) {
    val productos by productoRepository.obtenerProductos().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LevelUp Store") },
                actions = {
                    if (estaLogueado) {
                        Text("Hola, ${nombreUsuario ?: "User"}", fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                        IconButton(onClick = onCerrarSesion) { Icon(Icons.Default.ExitToApp, "Salir") }
                    } else {
                        IconButton(onClick = onIniciarSesionClick) { Icon(Icons.Default.Person, "Login") }
                    }
                    IconButton(onClick = onCarritoClick) { Icon(Icons.Default.ShoppingCart, "Carrito") }
                }
            )
        }
    ) { padding ->
        if (productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        onClick = { onProductoClick(producto.id) },
                        onAgregarAlCarrito = { scope.launch { carritoRepository.agregarProducto(producto) } }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    val context = LocalContext.current

    // --- LÓGICA DE IMAGEN SEGURA ---
    val resourceId = remember(producto.imagenUrl) {
        try {
            if (producto.imagenUrl.isNotBlank()) {
                context.resources.getIdentifier(producto.imagenUrl, "drawable", context.packageName)
            } else 0
        } catch (e: Exception) { 0 }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Protección: Solo usamos painterResource si el ID es válido (distinto de 0)
            if (resourceId != 0) {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            } else {
                // Fallback: Cuadro gris si la imagen falla
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin Imagen", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = producto.nombre, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = "$${producto.precio.toInt()}", color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAgregarAlCarrito,
                modifier = Modifier.fillMaxWidth(),
                enabled = producto.stock > 0
            ) {
                Text(if(producto.stock > 0) "Agregar" else "Agotado")
            }
        }
    }
}