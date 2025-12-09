package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelUpKotlinProject.domain.model.Producto
import com.example.levelUpKotlinProject.domain.validator.ValidadorProducto // 👈 Asegúrate de importar esto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    productoExistente: Producto?,
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit
) {
    // --- ESTADOS DE DATOS ---
    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(productoExistente?.descripcion ?: "") }
    var precio by remember { mutableStateOf(productoExistente?.precio?.toInt()?.toString() ?: "") }
    var stock by remember { mutableStateOf(productoExistente?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(productoExistente?.categoria ?: "") }
    var imagenUrl by remember { mutableStateOf(productoExistente?.imagenUrl ?: "") }

    // --- ESTADOS DE ERROR (VALIDACIÓN) ---

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorDescripcion by remember { mutableStateOf<String?>(null) }
    var errorPrecio by remember { mutableStateOf<String?>(null) }
    var errorStock by remember { mutableStateOf<String?>(null) }
    var errorCategoria by remember { mutableStateOf<String?>(null) }
    var errorImagen by remember { mutableStateOf<String?>(null) }

    val esEdicion = productoExistente != null
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Producto" else "Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancelar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta informativa
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (esEdicion) "Modificando: ${productoExistente?.nombre}" else "Completa todos los campos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Campo: Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = null // Limpiamos error al escribir
                },
                label = { Text("Nombre del producto *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errorNombre != null,
                supportingText = { errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Campo: Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    errorDescripcion = null
                },
                label = { Text("Descripción *") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                isError = errorDescripcion != null,
                supportingText = { errorDescripcion?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Campo: Precio
            OutlinedTextField(
                value = precio,
                onValueChange = {
                    // Filtramos solo números
                    if (it.all { char -> char.isDigit() }) {
                        precio = it
                        errorPrecio = null
                    }
                },
                label = { Text("Precio (CLP) *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorPrecio != null,
                supportingText = { errorPrecio?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Campo: Stock
            OutlinedTextField(
                value = stock,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        stock = it
                        errorStock = null
                    }
                },
                label = { Text("Stock disponible *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = errorStock != null,
                supportingText = { errorStock?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Campo: Categoría
            OutlinedTextField(
                value = categoria,
                onValueChange = {
                    categoria = it
                    errorCategoria = null
                },
                label = { Text("Categoría *") },
                singleLine = true,
                placeholder = { Text("Ej: Periféricos, Audio...") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorCategoria != null,
                supportingText = { errorCategoria?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Campo: ID de imagen
            OutlinedTextField(
                value = imagenUrl,
                onValueChange = {
                    // Convertimos a minúsculas y reemplazamos espacios automáticamente
                    imagenUrl = it.lowercase().replace(" ", "_")
                    errorImagen = null
                },
                label = { Text("ID de imagen (drawable) *") },
                singleLine = true,
                placeholder = { Text("Ej: teclado_mecanico") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorImagen != null,
                supportingText = {
                    if (errorImagen != null) {
                        Text(errorImagen!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Debe coincidir con un archivo en drawable/ (sin extensión)", fontSize = 12.sp)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                // Botón Guardar
                Button(
                    onClick = {
                        // 1. Ejecutar validaciones
                        val valNombre = ValidadorProducto.validarNombre(nombre)
                        val valDesc = ValidadorProducto.validarDescripcion(descripcion)
                        val valPrecio = ValidadorProducto.validarPrecio(precio)
                        val valStock = ValidadorProducto.validarStock(stock)
                        val valCat = ValidadorProducto.validarCategoria(categoria)
                        val valImg = ValidadorProducto.validarImagenUrl(imagenUrl)

                        // 2. Actualizar estado visual de errores
                        errorNombre = valNombre
                        errorDescripcion = valDesc
                        errorPrecio = valPrecio
                        errorStock = valStock
                        errorCategoria = valCat
                        errorImagen = valImg

                        // 3. Verificar si todo está limpio
                        if (valNombre == null && valDesc == null && valPrecio == null &&
                            valStock == null && valCat == null && valImg == null) {

                            // Todo válido, crear o actualizar producto
                            val producto = Producto(
                                id = productoExistente?.id ?: "", // Mantener ID
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                precio = precio.toDouble(),
                                imagenUrl = imagenUrl.trim(),
                                categoria = categoria.trim(),
                                stock = stock.toInt()
                            )
                            onGuardar(producto)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (esEdicion) "Actualizar" else "Guardar")
                }
            }
        }
    }
}