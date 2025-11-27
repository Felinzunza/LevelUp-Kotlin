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

/**
 * FormularioProductoScreen: Formulario para agregar o editar productos
 * 
 * Funcionalidades:
 * - Modo agregar (productoExistente = null)
 * - Modo editar (productoExistente != null)
 * - Validaciones de todos los campos
 * - Mensajes de error específicos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    productoExistente: Producto?,
    onGuardar: (Producto) -> Unit,
    onCancelar: () -> Unit
) {
    // Estados del formulario
    var nombre by remember { mutableStateOf(productoExistente?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(productoExistente?.descripcion ?: "") }
    var precio by remember { mutableStateOf(productoExistente?.precio?.toInt()?.toString() ?: "") }
    var stock by remember { mutableStateOf(productoExistente?.stock?.toString() ?: "") }
    var categoria by remember { mutableStateOf(productoExistente?.categoria ?: "") }
    var imagenUrl by remember { mutableStateOf(productoExistente?.imagenUrl ?: "") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    
    val esEdicion = productoExistente != null
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (esEdicion) "Editar Producto" else "Nuevo Producto") 
                },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Cancelar"
                        )
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
            // Instrucciones
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
                    mensajeError = null
                },
                label = { Text("Nombre del producto *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Campo: Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { 
                    descripcion = it
                    mensajeError = null
                },
                label = { Text("Descripción *") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Campo: Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { 
                    precio = it.filter { char -> char.isDigit() }
                    mensajeError = null
                },
                label = { Text("Precio (CLP) *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Campo: Stock
            OutlinedTextField(
                value = stock,
                onValueChange = { 
                    stock = it.filter { char -> char.isDigit() }
                    mensajeError = null
                },
                label = { Text("Stock disponible *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Campo: Categoría
            OutlinedTextField(
                value = categoria,
                onValueChange = { 
                    categoria = it
                    mensajeError = null
                },
                label = { Text("Categoría *") },
                singleLine = true,
                placeholder = { Text("Ej: Periféricos, Audio, Video...") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Campo: ID de imagen
            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { 
                    imagenUrl = it.lowercase().replace(" ", "_")
                    mensajeError = null
                },
                label = { Text("ID de imagen (drawable) *") },
                singleLine = true,
                placeholder = { Text("Ej: teclado_mecanico") },
                supportingText = {
                    Text(
                        text = "Debe coincidir con un archivo en drawable/ (sin extensión)",
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Mensaje de error
            if (mensajeError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botones
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
                        // Validaciones
                        when {
                            nombre.isBlank() -> {
                                mensajeError = "El nombre es obligatorio"
                            }
                            descripcion.isBlank() -> {
                                mensajeError = "La descripción es obligatoria"
                            }
                            precio.isBlank() || precio.toIntOrNull() == null || precio.toInt() <= 0 -> {
                                mensajeError = "Ingresa un precio válido mayor a 0"
                            }
                            stock.isBlank() || stock.toIntOrNull() == null || stock.toInt() < 0 -> {
                                mensajeError = "Ingresa un stock válido (0 o más)"
                            }
                            categoria.isBlank() -> {
                                mensajeError = "La categoría es obligatoria"
                            }
                            imagenUrl.isBlank() -> {
                                mensajeError = "El ID de imagen es obligatorio"
                            }
                            else -> {
                                // Todo válido, crear o actualizar producto
                                val producto = Producto(
                                    id = productoExistente?.id ?: 0,
                                    nombre = nombre.trim(),
                                    descripcion = descripcion.trim(),
                                    precio = precio.toDouble(),
                                    imagenUrl = imagenUrl.trim(),
                                    categoria = categoria.trim(),
                                    stock = stock.toInt()
                                )
                                onGuardar(producto)
                            }
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
