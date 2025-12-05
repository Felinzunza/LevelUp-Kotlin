package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.levelUpKotlinProject.data.local.PreferenciasManager
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.domain.model.ItemCarrito
import com.example.levelUpKotlinProject.domain.model.TipoCompra
import com.example.levelUpKotlinProject.domain.model.TipoCourier
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna
import com.example.levelUpKotlinProject.ui.navigation.Rutas
import com.example.levelUpKotlinProject.ui.viewmodel.CarritoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavHostController,
    viewModel: CarritoViewModel,
    carritoRepository: CarritoRepository,
    onVolverClick: () -> Unit,
    onProductoClick: (String) -> Unit,
    preferenciasManager: PreferenciasManager,
    onIrALogin: () -> Unit
) {
    // 1. LÓGICA REACTIVA
    val itemsCarrito by carritoRepository.obtenerCarrito().collectAsState(initial = emptyList())
    val total by carritoRepository.obtenerTotal().collectAsState(initial = 0.0)
    val scope = rememberCoroutineScope()

    // 2. ESTADOS DE FORMULARIO
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf( "") }
    val rutUsuarioLogueado = preferenciasManager.obtenerRutUsuario()
    val nombreUsuario = preferenciasManager.obtenerNombreUsuario() ?: "Cliente"
    val direccionState = remember { mutableStateOf("") }
    val direccionCompleta = "${direccionState.value}, $comuna, $region"
    val courierState = remember { mutableStateOf(TipoCourier.CORREOS_CHILE) }
    val metodoPagoState = remember { mutableStateOf(TipoCompra.TARJETA_DEBITO) }

    // 3. TOTALES
    val subtotal = total
    val costoEnvio = if (total > 0) 5.0 else 0.0
    val totalPagar = total + costoEnvio

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito (${itemsCarrito.size})") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (itemsCarrito.isNotEmpty()) {
                        IconButton(onClick = { scope.launch { carritoRepository.vaciarCarrito() } }) {
                            Icon(Icons.Default.Delete, "Vaciar carrito", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (itemsCarrito.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = formatearPrecio(totalPagar),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        BotonFinalizarCompra(
                            viewModel = viewModel,
                            coroutineScope = scope,
                            onCompraExitosa = {
                                navController.navigate(Rutas.HOME) {
                                    popUpTo(Rutas.HOME) { inclusive = true }
                                }
                            },
                            rutCliente = rutUsuarioLogueado,
                            nombreCliente = nombreUsuario,
                            direccion = direccionCompleta,
                            metodoPago = metodoPagoState.value,
                            courier = courierState.value,
                            subtotal = subtotal,
                            costoEnvio = costoEnvio,
                            totalPagar = totalPagar,
                            preferenciasManager = preferenciasManager,
                            onIrALogin = onIrALogin
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()), // Scroll global activado
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (itemsCarrito.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛒", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tu carrito está vacío", fontSize = 18.sp)
                    Button(onClick = onVolverClick) { Text("Ir a comprar") }
                }
            } else {
                // ✅ CORRECCIÓN: Usamos Column en lugar de LazyColumn.
                // Esto permite que la lista se expanda completamente y use el scroll de la pantalla (parent).
                // Así puedes ver todos los productos, no solo 3.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsCarrito.forEach { item ->
                        CarritoItemCard(
                            item = item,
                            onCantidadChange = { nuevaCantidad ->
                                scope.launch { carritoRepository.modificarCantidad(item.producto.id, nuevaCantidad) }
                            },
                            onEliminarClick = {
                                scope.launch { carritoRepository.eliminarProducto(item.producto.id) }
                            },
                            onClick = { onProductoClick(item.producto.id) }
                        )
                    }
                }

                // FORMULARIOS DE CHECKOUT
                Column(modifier = Modifier.padding(16.dp)) {
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Detalles de Pago y Envío", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = direccionState.value,
                        onValueChange = { direccionState.value = it },
                        label = { Text("Dirección de Envío Completa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SelectorRegionComuna(
                        regionSeleccionada = region,
                        comunaSeleccionada = comuna,
                        onRegionChange = { nuevaRegion ->
                            region = nuevaRegion
                            comuna = ""
                        },
                        onComunaChange = { nuevaComuna ->
                            comuna = nuevaComuna
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    CourierSelector(courierState.value) { courierState.value = it }

                    Spacer(modifier = Modifier.height(24.dp))
                    MetodoPagoSelector(metodoPagoState.value) { metodoPagoState.value = it }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// --- FUNCIONES AUXILIARES ---

fun formatearPrecio(precio: Double): String {
    val precioEntero = precio.toInt()
    return "$${precioEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"
}

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
            val context = LocalContext.current
            // Intentamos cargar recurso local, si falla usamos 0
            val imageResId = try {
                if(item.producto.imagenUrl.isNotEmpty())
                    context.resources.getIdentifier(item.producto.imagenUrl, "drawable", context.packageName)
                else 0
            } catch (e: Exception) { 0 }

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

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.producto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Precio: ${formatearPrecio(item.producto.precio)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (item.cantidad > 1) onCantidadChange(item.cantidad - 1) },
                        modifier = Modifier.size(32.dp),
                        enabled = item.cantidad > 1
                    ) {
                        Text(
                            text = "-",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.cantidad > 1) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    Text(
                        text = "${item.cantidad}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 30.dp).wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    IconButton(
                        onClick = { onCantidadChange(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, "Aumentar", tint = MaterialTheme.colorScheme.secondary)
                    }
                }
                Text(
                    text = "Subtotal: ${formatearPrecio(item.subtotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onEliminarClick) {
                Icon(Icons.Filled.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierSelector(selectedCourier: TipoCourier, onCourierSelected: (TipoCourier) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCourier.displayString,
            onValueChange = {},
            readOnly = true,
            label = { Text("Seleccionar Courier") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TipoCourier.entries.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.displayString) },
                    onClick = {
                        onCourierSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun MetodoPagoSelector(selectedPago: TipoCompra, onPagoSelected: (TipoCompra) -> Unit) {
    Column(modifier = Modifier.selectableGroup()) {
        Text("Método de Pago", style = MaterialTheme.typography.titleSmall)
        TipoCompra.entries.forEach { tipo ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .selectable(
                        selected = (tipo == selectedPago),
                        onClick = { onPagoSelected(tipo) },
                        role = androidx.compose.ui.semantics.Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (tipo == selectedPago),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = tipo.displayString,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun BotonFinalizarCompra(
    viewModel: CarritoViewModel,
    coroutineScope: CoroutineScope,
    onCompraExitosa: () -> Unit,
    rutCliente: String,
    nombreCliente: String,
    direccion: String,
    metodoPago: TipoCompra,
    courier: TipoCourier,
    subtotal: Double,
    costoEnvio: Double,
    totalPagar: Double,
    preferenciasManager: PreferenciasManager,
    onIrALogin: () -> Unit
) {
    Button(
        onClick = {
            if (!preferenciasManager.estaUsuarioLogueado()) {
                onIrALogin()
                return@Button
            }
            if (direccion.isBlank()) {
                return@Button
            }

            coroutineScope.launch {
                try {
                    viewModel.finalizarCompra(
                        rutCliente = rutCliente,
                        nombreCliente= nombreCliente,
                        direccionEnvio = direccion,
                        metodoPago = metodoPago,
                        courier = courier,
                        subtotal = subtotal,
                        descuento = 0.0,
                        costoEnvio = costoEnvio,
                        totalPagar = totalPagar
                    )
                    onCompraExitosa()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        },
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) {
        val texto = if (preferenciasManager.estaUsuarioLogueado())
            "PAGAR ${formatearPrecio(totalPagar)}"
        else
            "INICIAR SESIÓN PARA COMPRAR"

        Text(texto, fontWeight = FontWeight.Bold)
    }
}