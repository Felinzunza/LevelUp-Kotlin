package com.example.levelUpKotlinProject.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioUsuarioScreen(
    usuarioExistente: Usuario?,
    onGuardar: (Usuario) -> Unit,
    onCancelar: () -> Unit
) {
    // FORMATO SEGURO CON GUIONES
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // ESTADOS
    var rut by remember { mutableStateOf(usuarioExistente?.rut ?: "") }
    var nombre by remember { mutableStateOf(usuarioExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioExistente?.apellido ?: "") }
    var username by remember { mutableStateOf(usuarioExistente?.username ?: "") }
    var email by remember { mutableStateOf(usuarioExistente?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf(usuarioExistente?.telefono ?: "") }
    var rol by remember { mutableStateOf(usuarioExistente?.rol ?: Rol.USUARIO) }

    // ESTADO DE FECHA (Objeto Date real para evitar errores de parseo)
    var fechaNacimientoSeleccionada by remember { mutableStateOf(usuarioExistente?.fechaNacimiento) }
    // Texto visual para el campo (se actualiza solo)
    val fechaVisual = fechaNacimientoSeleccionada?.let { dateFormat.format(it) } ?: ""

    // ESTADOS DE UI
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = fechaNacimientoSeleccionada?.time
    )

    val esEdicion = usuarioExistente != null
    val scrollState = rememberScrollState()

    // ESTADOS DROPDOWN
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    // DIÁLOGO DE CALENDARIO
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Ajuste de zona horaria para evitar el error de "un día menos"
                        val offset = TimeZone.getDefault().getOffset(millis)
                        fechaNacimientoSeleccionada = Date(millis + offset)
                        mensajeError = null
                    }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Usuario" else "Nuevo Usuario") },
                navigationIcon = { IconButton(onClick = onCancelar) { Icon(Icons.Default.ArrowBack, "Cancelar") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = if (esEdicion) "Editando: ${usuarioExistente?.nombre}" else "Completa los datos", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }

            // CAMPOS DE TEXTO
            OutlinedTextField(value = rut, onValueChange = { rut = it.uppercase(); mensajeError = null }, label = { Text("RUT/Cédula *") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it; mensajeError = null }, label = { Text("Nombre *") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = apellido, onValueChange = { apellido = it; mensajeError = null }, label = { Text("Apellido *") }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(value = username, onValueChange = { username = it; mensajeError = null }, label = { Text("Username *") }, modifier = Modifier.fillMaxWidth())

            // CAMPO FECHA (SOLO LECTURA + CALENDARIO)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaVisual,
                    onValueChange = { },
                    label = { Text("Fecha de Nacimiento *") },
                    placeholder = { Text("dd-mm-yyyy") },
                    readOnly = true, // No permite escribir manual para evitar crash
                    trailingIcon = {
                        IconButton(onClick = { mostrarCalendario = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = remember { MutableInteractionSource() }.also { source ->
                        LaunchedEffect(source) {
                            source.interactions.collect { if (it is PressInteraction.Release) mostrarCalendario = true }
                        }
                    }
                )
            }

            OutlinedTextField(value = email, onValueChange = { email = it; mensajeError = null }, label = { Text("Email *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())

            // PASSWORD CON VISIBILIDAD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; mensajeError = null },
                label = { Text(if (esEdicion) "Contraseña (vacío mantiene)" else "Contraseña *") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val texto = if (passwordVisible) "Ocultar" else "Ver"
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(texto, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(value = telefono, onValueChange = { telefono = it.filter { char -> char.isDigit() }; mensajeError = null }, label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())

            // ROL DROPDOWN
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = rol.name, onValueChange = {}, modifier = Modifier.fillMaxWidth().onGloballyPositioned { textFieldSize = it.size.toSize() },
                    label = { Text("Rol *") }, readOnly = true, trailingIcon = { Icon(icon, "Rol", Modifier.clickable { expanded = !expanded }) }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })) {
                    Rol.entries.forEach { selection ->
                        DropdownMenuItem(text = { Text(selection.name) }, onClick = { rol = selection; expanded = false })
                    }
                }
            }

            if (mensajeError != null) {
                Text(text = mensajeError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }

            // BOTONES (CON VALIDACIÓN SEGURA)
            Button(
                onClick = {
                    when {
                        rut.isBlank() -> mensajeError = "El RUT es obligatorio"
                        nombre.isBlank() -> mensajeError = "El Nombre es obligatorio"
                        apellido.isBlank() -> mensajeError = "El Apellido es obligatorio"
                        username.isBlank() -> mensajeError = "El Username es obligatorio"
                        fechaNacimientoSeleccionada == null -> mensajeError = "Selecciona una fecha de nacimiento"
                        email.isBlank() -> mensajeError = "El Email es obligatorio"
                        !esEdicion && password.isBlank() -> mensajeError = "La contraseña es obligatoria"
                        else -> {
                            // Todo válido, creamos el objeto
                            val usuario = Usuario(
                                id = usuarioExistente?.id ?: 0,
                                rut = rut, nombre = nombre, apellido = apellido,
                                fechaNacimiento = fechaNacimientoSeleccionada!!, // Seguro porque validamos null arriba
                                username = username, email = email,
                                password = password.takeIf { it.isNotBlank() } ?: usuarioExistente?.password ?: "",
                                telefono = telefono,
                                fechaRegistro = usuarioExistente?.fechaRegistro ?: Date(),
                                rol = rol
                            )
                            onGuardar(usuario)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Actualizar Usuario" else "Guardar Usuario")
            }
        }
    }
}