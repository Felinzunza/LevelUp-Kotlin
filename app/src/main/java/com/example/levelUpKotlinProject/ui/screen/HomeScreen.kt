package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.levelUpKotlinProject.R
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.domain.model.Producto
import com.example.levelUpKotlinProject.domain.model.Usuario
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
    onPerfilClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    onIniciarSesionClick: () -> Unit,
    estaLogueado: Boolean,
    nombreUsuario: String?,
    //  CAMBIO: Recibimos el objeto completo para tener la foto
    usuarioActual: Usuario?
) {
    // Datos originales
    val productos by productoRepository.obtenerProductos().collectAsState(initial = emptyList())

    // ✅ NUEVO: Observamos el carrito para contar los productos
    // Asumo que tu repositorio tiene una función 'obtenerCarrito()' que devuelve un Flow
    val itemsCarrito by carritoRepository.obtenerCarrito().collectAsState(initial = emptyList())

    // ✅ NUEVO: Calculamos el total sumando las cantidades de cada item
    val cantidadTotalEnCarrito = remember(itemsCarrito) {
        itemsCarrito.sumOf { it.cantidad }
    }

    val scope = rememberCoroutineScope()

    //  NUEVO: Estado para el Snackbar (Mensaje emergente)
    val snackbarHostState = remember { SnackbarHostState() }

    // --- ESTADOS DE FILTRO ---
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    // Calculamos las categorías disponibles dinámicamente
    val categorias = remember(productos) {
        listOf("Todos") + productos.map { it.categoria }.distinct().sorted()
    }

    // Lógica de Filtrado
    val productosFiltrados = remember(productos, textoBusqueda, categoriaSeleccionada) {
        productos.filter { producto ->
            val coincideNombre = producto.nombre.contains(textoBusqueda, ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == null || categoriaSeleccionada == "Todos" || producto.categoria == categoriaSeleccionada

            coincideNombre && coincideCategoria
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.icono_levelup),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp) // Ajusté el tamaño a 40dp para que se vea mejor
                    )
                },
                actions = {
                    if (estaLogueado) {
                        Text("Hola, ${nombreUsuario ?: "User"}", fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colorScheme.secondary)

                        // ✅ FOTO DE PERFIL (ICONO)
                        IconButton(onClick = onPerfilClick) {
                            if (!usuarioActual?.fotoPerfil.isNullOrBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = usuarioActual!!.fotoPerfil),
                                    contentDescription = "Mi Perfil",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Mi Perfil",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                    } else {
                        IconButton(onClick = onIniciarSesionClick) { Icon(Icons.Default.Person, "Login") }
                    }
                    IconButton(onClick = onCarritoClick) {
                        BadgedBox(
                            badge = {
                                if (cantidadTotalEnCarrito > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        // Mostramos el número. Si es más de 99, mostramos "99+"
                                        Text(
                                            text = if (cantidadTotalEnCarrito > 99) "99+" else cantidadTotalEnCarrito.toString(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito de compras"
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            scope.launch {
                                // 1. Vaciamos el carrito localmente antes de salir
                                carritoRepository.vaciarCarrito()
                                // 2. Ejecutamos la navegación de cierre de sesión
                                onCerrarSesion()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ExitToApp, "Salir")
                    }
                }
            )
        },
        // ✅ NUEVO: Agregamos el Host del Snackbar aquí para que se dibuje
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
            )

            // 2. FILTRO DE CATEGORÍAS (CHIPS)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp),



            ) {
                items(categorias) { categoria ->
                    val selected = (categoria == "Todos" && categoriaSeleccionada == null) || categoria == categoriaSeleccionada

                    FilterChip(
                        selected = selected,
                        onClick = {
                            categoriaSeleccionada = if (categoria == "Todos") null else categoria
                        },
                        label = { Text(categoria) },


                    )
                }
            }

            Divider()

            // 3. LISTA DE PRODUCTOS
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (productos.isEmpty()) {
                        CircularProgressIndicator() // Cargando inicial
                    } else {
                        Text("No se encontraron productos", color = Color.Gray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { onProductoClick(producto.id) },
                            onAgregarAlCarrito = {
                                scope.launch {
                                    carritoRepository.agregarProducto(producto)
                                    // ✅ NUEVO: Mostramos el mensaje de confirmación
                                    snackbarHostState.showSnackbar(
                                        message = "${producto.nombre} agregado al carrito",
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true
                                    )
                                }
                            }
                        )
                    }
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

    val resourceId = remember(producto.imagenUrl) {
        try {
            if (producto.imagenUrl.isNotBlank()) {
                context.resources.getIdentifier(producto.imagenUrl, "drawable", context.packageName)
            } else 0
        } catch (e: Exception) { 0 }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                if (resourceId != 0) {
                    Image(
                        painter = painterResource(id = resourceId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Sin Imagen", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = producto.nombre,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "$${producto.precio.toInt()}",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

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