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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

/**
 * Pantalla de Perfil de Usuario
 * Permite ver datos, editarlos y tomar una foto de perfil.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    usuarioActual: Usuario?, // El usuario logueado actualmente
    registroViewModel: RegistroViewModel, // Reusamos este VM para actualizar
    onVolverClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Estados de los campos editables
    var nombre by remember { mutableStateOf(usuarioActual?.nombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioActual?.apellido ?: "") }
    var telefono by remember { mutableStateOf(usuarioActual?.telefono ?: "") }
    var direccion by remember { mutableStateOf(usuarioActual?.direccion ?: "") }

    // Estado de la imagen (URI temporal o ruta guardada)
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenGuardadaRuta by remember { mutableStateOf(usuarioActual?.fotoPerfil ?: "") }

    // Lógica para la Cámara
    val archivoTemporal = remember { crearArchivoImagen(context) }
    val uriTemporal = remember {
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider",
            archivoTemporal
        )
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            imagenUri = uriTemporal
            imagenGuardadaRuta = uriTemporal.toString()
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            launcherCamara.launch(uriTemporal)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

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
                imagenGuardadaRuta = uriTemporal.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback si falla la copia
                imagenUri = uri
                imagenGuardadaRuta = uri.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.Default.ExitToApp, "Cerrar Sesión")
                    }
                }
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
            // --- ZONA DE FOTO DE PERFIL ---
            // FOTO
            Box(contentAlignment = Alignment.BottomEnd) {
                val imagenParaMostrar = if(imagenUri != null) imagenUri else if(imagenGuardadaRuta.isNotEmpty()) imagenGuardadaRuta else null

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
                            else launcherPermiso.launch(Manifest.permission.CAMERA)
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

            Text(
                text = "@${usuarioActual?.username}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            HorizontalDivider()

            // --- CAMPOS EDITABLES ---
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            //
            // Campos de solo lectura (Email y RUT no se suelen cambiar fácil)
            OutlinedTextField(
                value = usuarioActual?.email ?: "",
                onValueChange = {},
                label = { Text("Email (No editable)") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    if (usuarioActual != null) {
                        val usuarioActualizado = usuarioActual.copy(
                            nombre = nombre,
                            apellido = apellido,
                            telefono = telefono,
                            direccion = direccion,
                            // Guardamos la ruta de la imagen como String
                            // (En un proyecto real, aquí subirías la imagen al servidor y guardarías la URL remota)
                            fotoPerfil = imagenGuardadaRuta
                        )

                        // Usamos el ViewModel existente para actualizar
                        registroViewModel.actualizarUsuario(usuarioActualizado)
                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        onVolverClick()
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

// Helper para crear archivo temporal
fun crearArchivoImagen(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        context.externalCacheDir
    )
}