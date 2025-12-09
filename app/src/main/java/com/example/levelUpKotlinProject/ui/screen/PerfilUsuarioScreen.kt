package com.example.levelUpKotlinProject.ui.screen

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

/**
 * Pantalla de Perfil de Usuario CORREGIDA
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    usuarioActual: Usuario?,
    registroViewModel: RegistroViewModel,
    onVolverClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()


    var nombre by remember(usuarioActual) { mutableStateOf(usuarioActual?.nombre ?: "") }
    var apellido by remember(usuarioActual) { mutableStateOf(usuarioActual?.apellido ?: "") }
    var telefono by remember(usuarioActual) { mutableStateOf(usuarioActual?.telefono ?: "") }
    var direccion by remember(usuarioActual) { mutableStateOf(usuarioActual?.direccion ?: "") }
    var region by remember(usuarioActual) { mutableStateOf(usuarioActual?.region ?: "") }
    var comuna by remember(usuarioActual) { mutableStateOf(usuarioActual?.comuna ?: "") }

    // La imagen también debe actualizarse si viene del usuario
    var imagenGuardadaRuta by remember(usuarioActual) { mutableStateOf(usuarioActual?.fotoPerfil ?: "") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // --- ESTADOS DE ERROR ---
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorApellido by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorDireccion by remember { mutableStateOf<String?>(null) }

    // --- VARIABLES DE SEGURIDAD ---
    var passwordActualInput by remember { mutableStateOf("") }
    var passwordNuevaInput by remember { mutableStateOf("") }
    var passwordConfirmarInput by remember { mutableStateOf("") }
    var mostrarSeccionPassword by remember { mutableStateOf(false) }

    // --- LÓGICA DE IMAGEN (Igual que antes) ---
    val archivoTemporal = remember { crearArchivoImagen(context) }
    val uriTemporal = remember {
        FileProvider.getUriForFile(Objects.requireNonNull(context), context.packageName + ".provider", archivoTemporal)
    }

    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            imagenUri = uriTemporal
            imagenGuardadaRuta = archivoTemporal.absolutePath
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido ->
        if (esConcedido) launcherCamara.launch(uriTemporal)
        else Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    java.io.FileOutputStream(archivoTemporal).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                imagenUri = uriTemporal
                imagenGuardadaRuta = archivoTemporal.absolutePath
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = { IconButton(onClick = onVolverClick) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = { IconButton(onClick = onCerrarSesion) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- FOTO ---
            Box(contentAlignment = Alignment.BottomEnd) {
                val imagenParaMostrar = if(imagenUri != null) imagenUri else if(imagenGuardadaRuta.isNotEmpty()) try {
                    if(imagenGuardadaRuta.startsWith("/")) Uri.fromFile(File(imagenGuardadaRuta)) else Uri.parse(imagenGuardadaRuta)
                } catch(e:Exception){ null } else null

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

                Row(modifier = Modifier.offset(y = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) launcherCamara.launch(uriTemporal)
                            else launcherPermiso.launch(Manifest.permission.CAMERA)
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { Icon(Icons.Default.CameraAlt, "Cámara") }

                    SmallFloatingActionButton(
                        onClick = { launcherGaleria.launch("image/*") },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) { Icon(Icons.Default.Image, "Galería") }
                }
            }

            Text("@${usuarioActual?.username}", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            HorizontalDivider()

            Text("Datos Personales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))

            // --- CAMPOS EDITABLES ---
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it; errorNombre = null },
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(),
                isError = errorNombre != null, supportingText = { errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            OutlinedTextField(
                value = apellido, onValueChange = { apellido = it; errorApellido = null },
                label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth(),
                isError = errorApellido != null, supportingText = { errorApellido?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            SelectorRegionComuna(
                regionSeleccionada = region,
                comunaSeleccionada = comuna,
                onRegionChange = { region = it; comuna = "" },
                onComunaChange = { comuna = it }
            )

            OutlinedTextField(
                value = direccion, onValueChange = { direccion = it; errorDireccion = null },
                label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(),
                isError = errorDireccion != null, supportingText = { errorDireccion?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            OutlinedTextField(
                value = telefono, onValueChange = { telefono = it; errorTelefono = null },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = errorTelefono != null, supportingText = { errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // Solo lectura
            OutlinedTextField(value = usuarioActual?.email ?: "", onValueChange = {}, label = { Text("Email (No editable)") }, readOnly = true, enabled = false, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            // --- SECCIÓN SEGURIDAD ---
            Row(
                modifier = Modifier.fillMaxWidth().clickable { mostrarSeccionPassword = !mostrarSeccionPassword },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cambiar Contraseña", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Icon(imageVector = if (mostrarSeccionPassword) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }

            if (mostrarSeccionPassword) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = passwordActualInput, onValueChange = { passwordActualInput = it }, label = { Text("Contraseña Actual") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = passwordNuevaInput, onValueChange = { passwordNuevaInput = it }, label = { Text("Nueva Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = passwordConfirmarInput, onValueChange = { passwordConfirmarInput = it }, label = { Text("Confirmar Nueva") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    if (usuarioActual != null) {

                        // 🟢 CORRECCIÓN 2: Validación de Nombre Simplificada
                        // Ya no usamos ValidadorFormulario.validarNombreCompleto(nombre) porque busca espacio.
                        // Ahora solo verificamos que no esté vacío.
                        val errNom = if(nombre.isBlank()) "El nombre es obligatorio" else null

                        val errApe = if(apellido.isBlank()) "El apellido es obligatorio" else null

                        // Teléfono y Dirección siguen usando el validador estándar
                        val errTel = ValidadorFormulario.validarTelefono(telefono)
                        val errDir = ValidadorFormulario.validarDireccion(direccion)

                        // Validación Región/Comuna
                        val regionValida = region.isNotBlank() && region != "Selecciona una región"
                        val comunaValida = comuna.isNotBlank() && comuna != "Selecciona una comuna"

                        errorNombre = errNom
                        errorApellido = errApe
                        errorTelefono = errTel
                        errorDireccion = errDir

                        if (errorNombre != null || errorApellido != null || errorTelefono != null || errorDireccion != null) {
                            Toast.makeText(context, "Por favor corrige los errores", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!regionValida || !comunaValida) {
                            Toast.makeText(context, "Debes seleccionar Región y Comuna", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Lógica Contraseña (igual)
                        var nuevaPasswordFinal = usuarioActual.password
                        var cambioPasswordValido = true

                        if (passwordActualInput.isNotEmpty() || passwordNuevaInput.isNotEmpty()) {
                            if (passwordActualInput != usuarioActual.password) {
                                Toast.makeText(context, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show()
                                cambioPasswordValido = false
                            } else if (passwordNuevaInput.isBlank()) {
                                Toast.makeText(context, "La nueva contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                                cambioPasswordValido = false
                            } else if (passwordNuevaInput != passwordConfirmarInput) {
                                Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                cambioPasswordValido = false
                            } else {
                                val errorPassFuerte = ValidadorFormulario.validarPassword(passwordNuevaInput)
                                if (errorPassFuerte != null) {
                                    Toast.makeText(context, errorPassFuerte, Toast.LENGTH_LONG).show()
                                    cambioPasswordValido = false
                                } else {
                                    nuevaPasswordFinal = passwordNuevaInput
                                }
                            }
                        }

                        // Guardar
                        if (cambioPasswordValido) {
                            val usuarioActualizado = usuarioActual.copy(
                                nombre = nombre,
                                apellido = apellido,
                                telefono = telefono,
                                direccion = direccion,
                                region = region,
                                comuna = comuna,
                                fotoPerfil = imagenGuardadaRuta,
                                password = nuevaPasswordFinal
                            )

                            registroViewModel.actualizarUsuario(usuarioActualizado)
                            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()

                            // Limpiar pass
                            passwordActualInput = ""
                            passwordNuevaInput = ""
                            passwordConfirmarInput = ""
                            mostrarSeccionPassword = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios")
            }
        }
    }
}

fun crearArchivoImagen(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(imageFileName, ".jpg", context.externalCacheDir)
}