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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.levelUpKotlinProject.data.local.PreferenciasManager
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModelFactory
import java.util.Date
import java.util.Objects
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    usuarioRepository: UsuarioRepository,
    preferenciasManager: PreferenciasManager, // 👈 1. AÑADIR ESTE PARÁMETRO
    onVolverClick: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val viewModel: RegistroViewModel = viewModel(factory = RegistroViewModelFactory(usuarioRepository))
    val uiState by viewModel.uiState.collectAsState()

    // --- LÓGICA CÁMARA ---
    val context = LocalContext.current
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    // En registro guardamos la ruta string para enviarla al objeto Usuario
    var rutaImagenFinal by remember { mutableStateOf("") }

    val archivoTemporal = remember { crearArchivoImagenRegistro(context) }
    val uriTemporal = remember {
        FileProvider.getUriForFile(Objects.requireNonNull(context), context.packageName + ".provider", archivoTemporal)
    }

    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            imagenUri = uriTemporal
            rutaImagenFinal = archivoTemporal.absolutePath
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { esConcedido ->
        if (esConcedido) launcherCamara.launch(uriTemporal)
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                // Usamos .use para que se cierren solos (evita el error 'Unresolved reference close')
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    java.io.FileOutputStream(archivoTemporal).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                imagenUri = uriTemporal
                rutaImagenFinal = archivoTemporal.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // ---------------------

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
                navigationIcon = { IconButton(onClick = onVolverClick) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centrar foto
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



            Text("Completa tus datos", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // CAMPOS DE TEXTO (Igual que antes)
            OutlinedTextField(
                value = uiState.formulario.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT *") },
                modifier = Modifier.fillMaxWidth()
            )

            // ... (Resto de campos: Nombre, Email, Teléfono, Dirección) ...
            // Simplificado para el ejemplo, asegúrate de mantener tus campos de Nombre, Apellido, Username separados si ya los tenías así en la versión anterior.
            // Aquí asumiré que usas las variables locales para nombre/apellido si tu VM no las tiene separadas en el state.

            OutlinedTextField(value = uiState.formulario.nombreCompleto, onValueChange = { viewModel.onNombreChange(it) }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.formulario.email, onValueChange = { viewModel.onEmailChange(it) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.formulario.password, onValueChange = { viewModel.onPasswordChange(it) }, label = { Text("Contraseña") }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Text(if(passwordVisible) "Ocultar" else "Ver") } }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = uiState.formulario.confirmarPassword, onValueChange = { viewModel.onConfirmarPasswordChange(it) }, label = { Text("Confirmar Contraseña") }, visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) { Text(if(confirmarPasswordVisible) "Ocultar" else "Ver") } }, modifier = Modifier.fillMaxWidth())

            SelectorRegionComuna(
                regionSeleccionada = uiState.formulario.region,
                comunaSeleccionada = uiState.formulario.comuna,
                onRegionChange = { viewModel.onRegionChange(it) },
                onComunaChange = { viewModel.onComunaChange(it) }
            )

            OutlinedTextField(value = uiState.formulario.direccion, onValueChange = { viewModel.onDireccionChange(it) }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            // Términos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = uiState.formulario.aceptaTerminos, onCheckedChange = { viewModel.onTerminosChange(it) })
                Text("Acepto los términos y condiciones")
            }

            Button(
                onClick = {
                    if (viewModel.esFormularioValido()) {

                        val nuevoId = UUID.randomUUID().toString()
                        // Construimos el usuario
                        // OJO: Ajusta esto si usas campos separados nombre/apellido en tu VM
                        val nuevoUsuario = Usuario(
                            id = nuevoId,
                            rut = uiState.formulario.rut,
                            nombre = uiState.formulario.nombreCompleto.substringBefore(" "), // Simple split
                            apellido = uiState.formulario.nombreCompleto.substringAfter(" ", ""),
                            username = uiState.formulario.email.substringBefore("@"),
                            email = uiState.formulario.email,
                            password = uiState.formulario.password,
                            telefono = uiState.formulario.telefono,
                            fechaNacimiento = Date(),
                            region = uiState.formulario.region,
                            comuna = uiState.formulario.comuna,
                            direccion = uiState.formulario.direccion,
                            fechaRegistro = Date(),
                            rol = Rol.USUARIO,

                            // ✅ GUARDAMOS LA FOTO
                            fotoPerfil = rutaImagenFinal
                        )
                        viewModel.agregarUsuario(nuevoUsuario) {
                            // 2. ✅ LÓGICA DE AUTO-LOGIN
                            // Guardamos la sesión localmente para que la App crea que ya entramos.
                            // Nota: Como el ID se genera en el servidor y aquí aun no lo tenemos devuelto,
                            // enviamos un string vacío en ID.
                            // Gracias a tu NavGraph robusto, te encontrará por el nombre/email.

                            preferenciasManager.guardarSesionUsuario(
                                id = nuevoId, // El NavGraph usará el nombre como fallback
                                email = nuevoUsuario.email,
                                nombre = nuevoUsuario.nombre,
                                rut = nuevoUsuario.rut
                            )

                            Toast.makeText(context, "¡Bienvenido, ${nuevoUsuario.nombre}!", Toast.LENGTH_LONG).show()

                            // Navegamos
                            onRegistroExitoso()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = uiState.formulario.aceptaTerminos
            ) {
                if (uiState.estaGuardando) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Registrarse")
            }
        }
    }

}


fun crearArchivoImagenRegistro(context: Context): File {
    // 1. Corrección: Quitamos "pattern ="
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        context.externalCacheDir
    )
}
