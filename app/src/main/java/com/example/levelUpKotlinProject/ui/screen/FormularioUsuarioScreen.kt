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
    usuarioExistente: Usuario? = null,
    onGuardar: (Usuario) -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- VARIABLES DE DATOS ---
    var nombre by remember { mutableStateOf(usuarioExistente?.nombre ?: "") }
    var apellido by remember { mutableStateOf(usuarioExistente?.apellido ?: "") }
    var email by remember { mutableStateOf(usuarioExistente?.email ?: "") }
    var username by remember { mutableStateOf(usuarioExistente?.username ?: "") }
    var rut by remember { mutableStateOf(usuarioExistente?.rut ?: "") }
    var password by remember { mutableStateOf(usuarioExistente?.password ?: "") }
    var rolSeleccionado by remember { mutableStateOf(usuarioExistente?.rol ?: Rol.USUARIO) }

    // --- LÓGICA DE IMAGEN ---

    // 1. Inicializamos con la foto que ya tenga el usuario (si estamos editando)
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenGuardadaRuta by remember { mutableStateOf(usuarioExistente?.fotoPerfil ?: "") }

    // 2. Preparamos el archivo temporal único para este formulario
    // Usamos la función que pusimos al final del archivo
    val archivoTemporal = remember { crearArchivoImagenFormulario(context) }

    val uriTemporal = remember {
        FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            context.packageName + ".provider",
            archivoTemporal
        )
    }

    // 3. Launcher de CÁMARA (Guarda directo en el archivo temporal)
    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            imagenUri = uriTemporal
            imagenGuardadaRuta = archivoTemporal.absolutePath
        }
    }

    // 4. Launcher de PERMISOS
    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido ->
        if (esConcedido) launcherCamara.launch(uriTemporal)
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    // 5. Launcher de GALERÍA (Tu lógica corregida con .use) ✅
    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                // ✅ CORRECCIÓN: Usamos .use para cerrar los flujos automáticamente y evitar errores
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    java.io.FileOutputStream(archivoTemporal).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                imagenUri = uriTemporal
                imagenGuardadaRuta = archivoTemporal.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (usuarioExistente == null) "Nuevo Usuario" else "Editar Usuario") }) }
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

            // --- VISUALIZADOR DE FOTO ---
            Box(contentAlignment = Alignment.BottomEnd) {
                // Lógica de qué mostrar: ¿Hay nueva URI? -> úsala. ¿Si no, hay ruta guardada? -> úsala.
                val imagenParaMostrar = if (imagenUri != null) imagenUri
                else if (imagenGuardadaRuta.isNotEmpty()) imagenGuardadaRuta
                else null

                if (imagenParaMostrar != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imagenParaMostrar),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(60.dp))
                    }
                }

                // Botones pequeños para cambiar foto
                Row(modifier = Modifier.offset(y = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                                launcherCamara.launch(uriTemporal)
                            else launcherPermiso.launch(Manifest.permission.CAMERA)
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { Icon(Icons.Default.CameraAlt, "Cámara") }

                    SmallFloatingActionButton(
                        onClick = { launcherGaleria.launch("image/*") }, // Lanza el selector corregido
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) { Icon(Icons.Default.Image, "Galería") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPOS ---
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())

            // Selector de Rol
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rol: ", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = rolSeleccionado == Rol.USUARIO, onClick = { rolSeleccionado = Rol.USUARIO }, label = { Text("Usuario") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = rolSeleccionado == Rol.ADMIN, onClick = { rolSeleccionado = Rol.ADMIN }, label = { Text("Admin") })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTONES
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        val usuarioAGuardar = Usuario(
                            id = usuarioExistente?.id ?: "",
                            nombre = nombre,
                            apellido = apellido,
                            email = email,
                            username = username,
                            rut = rut,
                            password = password,
                            rol = rolSeleccionado,
                            fotoPerfil = imagenGuardadaRuta,

                            // ✅ CORRECCIÓN: Agregamos los campos que faltaban
                            // Si el usuario existe, mantenemos sus datos. Si es nuevo, ponemos vacío o fecha actual.
                            telefono = usuarioExistente?.telefono ?: "",
                            region = usuarioExistente?.region ?: "",
                            comuna = usuarioExistente?.comuna ?: "",
                            direccion = usuarioExistente?.direccion ?: "",
                            fechaNacimiento = usuarioExistente?.fechaNacimiento ?: Date(),
                            fechaRegistro = usuarioExistente?.fechaRegistro ?: Date()
                        )
                        onGuardar(usuarioAGuardar)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }
        }
    }
}


fun crearArchivoImagenFormulario(context: Context): File {
    // Quitamos "pattern =" para evitar errores en versiones viejas de Java/Android
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        context.externalCacheDir
    )
}