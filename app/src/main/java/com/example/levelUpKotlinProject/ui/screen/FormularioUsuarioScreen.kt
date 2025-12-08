package com.example.levelUpKotlinProject.ui.screen

// ... (Imports idénticos a RegistroScreen) ...
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioUsuarioScreen(
    usuarioExistente: Usuario?,
    onGuardar: (Usuario) -> Unit,
    onCancelar: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val context = LocalContext.current

    // ESTADOS
    var rut by remember { mutableStateOf(usuarioExistente?.rut ?: "") }
    var nombre by remember { mutableStateOf(usuarioExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioExistente?.apellido ?: "") }
    var username by remember { mutableStateOf(usuarioExistente?.username ?: "") }
    var email by remember { mutableStateOf(usuarioExistente?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf(usuarioExistente?.telefono ?: "") }
    var direccion by remember { mutableStateOf(usuarioExistente?.direccion ?: "") }
    var region by remember { mutableStateOf(usuarioExistente?.region ?: "") }
    var comuna by remember { mutableStateOf(usuarioExistente?.comuna ?: "") }
    var rol by remember { mutableStateOf(usuarioExistente?.rol ?: Rol.USUARIO) }

    // FOTO
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var rutaImagenFinal by remember { mutableStateOf(usuarioExistente?.fotoPerfil ?: "") }

    var fechaNacimientoSeleccionada by remember { mutableStateOf(usuarioExistente?.fechaNacimiento) }
    val fechaVisual = fechaNacimientoSeleccionada?.let { dateFormat.format(it) } ?: ""

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    // PREPARACIÓN DE ARCHIVOS
    val archivoTemporal = remember { crearArchivoImagen(context) }
    val uriTemporal = remember { FileProvider.getUriForFile(Objects.requireNonNull(context), context.packageName + ".provider", archivoTemporal) }

    // 1. LAUNCHER CÁMARA
    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            imagenUri = uriTemporal
            rutaImagenFinal = uriTemporal.toString()
        }
    }
    val launcherPermisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) launcherCamara.launch(uriTemporal) }

    // 2. LAUNCHER GALERÍA (NUEVO)
    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Copiamos la imagen de galería al archivo temporal para tener control total
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = java.io.FileOutputStream(archivoTemporal)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Usamos la URI de nuestro archivo local (uriTemporal)
                imagenUri = uriTemporal
                rutaImagenFinal = uriTemporal.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback si falla la copia
                imagenUri = uri
                rutaImagenFinal = uri.toString()
            }
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaNacimientoSeleccionada?.time)
    val esEdicion = usuarioExistente != null
    val scrollState = rememberScrollState()

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { fechaNacimientoSeleccionada = Date(it) }; mostrarCalendario = false }) { Text("Aceptar") } },
            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO
            Box(contentAlignment = Alignment.BottomEnd) {
                val imagenParaMostrar = if(imagenUri != null) imagenUri else if(rutaImagenFinal.isNotEmpty()) rutaImagenFinal else null

                if (imagenParaMostrar != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imagenParaMostrar),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(50.dp))
                    }
                }

                // BOTONES DE FOTO (CÁMARA Y GALERÍA)
                Row(
                    modifier = Modifier.offset(y = 10.dp), // Bajamos un poco los botones
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Cámara
                    SmallFloatingActionButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                                PackageManager.PERMISSION_GRANTED) launcherCamara.launch(uriTemporal)
                            else launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { Icon(Icons.Default.CameraAlt, "Cámara") }

                    // Botón Galería
                    SmallFloatingActionButton(
                        onClick = { launcherGaleria.launch("image/*") },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) { Icon(Icons.Default.Image, "Galería") }
                }
            }

            // Espacio extra por los botones flotantes
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = rut, onValueChange = { rut = it.uppercase() }, label = { Text("RUT *") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())

            // Fecha
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = fechaVisual, onValueChange = {}, label = { Text("Nacimiento") }, readOnly = true, trailingIcon = { IconButton(onClick = { mostrarCalendario = true }) { Icon(Icons.Default.DateRange, null) } }, modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Text(if(passwordVisible) "Ocultar" else "Ver") } }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())

            SelectorRegionComuna(region, comuna, { region = it; comuna = "" }, { comuna = it })
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            // Rol
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = rol.name, onValueChange = {}, modifier = Modifier.fillMaxWidth().onGloballyPositioned { textFieldSize = it.size.toSize() }, label = { Text("Rol") }, readOnly = true, trailingIcon = { Icon(icon, null, Modifier.clickable { expanded = !expanded }) })
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })) {
                    Rol.entries.forEach { s -> DropdownMenuItem(text = { Text(s.name) }, onClick = { rol = s; expanded = false }) }
                }
            }

            if (mensajeError != null) Text(mensajeError!!, color = MaterialTheme.colorScheme.error)

            Button(
                onClick = {
                    if (rut.isNotBlank() && nombre.isNotBlank()) {
                        val usuario = Usuario(
                            id = usuarioExistente?.id ?: "",
                            rut = rut, nombre = nombre, apellido = apellido,
                            fechaNacimiento = fechaNacimientoSeleccionada ?: Date(),
                            username = username, email = email,
                            password = password.takeIf { it.isNotBlank() } ?: usuarioExistente?.password ?: "",
                            telefono = telefono, direccion = direccion, comuna = comuna, region = region,
                            fechaRegistro = usuarioExistente?.fechaRegistro ?: Date(),
                            rol = rol,
                            fotoPerfil = rutaImagenFinal // ✅
                        )
                        onGuardar(usuario)
                    } else {
                        mensajeError = "Faltan datos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (esEdicion) "Actualizar" else "Guardar") }
        }
    }
}