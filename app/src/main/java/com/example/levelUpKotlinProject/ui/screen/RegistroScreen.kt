package com.example.levelUpKotlinProject.ui.screen

import android.Manifest
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
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    usuarioRepository: UsuarioRepository,
    onVolverClick: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val viewModel: RegistroViewModel = viewModel(factory = RegistroViewModelFactory(usuarioRepository))
    val uiState by viewModel.uiState.collectAsState()

    // --- LÓGICA CÁMARA Y GALERÍA ---
    val context = LocalContext.current
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    // Variable para guardar la ruta final (sea de cámara o galería)
    var rutaImagenFinal by remember { mutableStateOf("") }

    val archivoTemporal = remember {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        File.createTempFile(imageFileName, ".jpg", context.externalCacheDir)
    }

    val uriTemporal = remember {
        FileProvider.getUriForFile(Objects.requireNonNull(context), context.packageName + ".provider", archivoTemporal)
    }

    // Launcher Cámara
    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            imagenUri = uriTemporal
            rutaImagenFinal = uriTemporal.toString()
        }
    }

    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) launcherCamara.launch(uriTemporal)
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    // Launcher Galería
    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                // Copiamos la imagen de la galería al archivo temporal local
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = java.io.FileOutputStream(archivoTemporal)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO DE PERFIL
            Box(contentAlignment = Alignment.BottomEnd) {
                if (imagenUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imagenUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(50.dp))
                    }
                }

                // Botones flotantes (Cámara y Galería)
                Row(
                    modifier = Modifier.offset(y = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                                launcherCamara.launch(uriTemporal)
                            else
                                launcherPermiso.launch(Manifest.permission.CAMERA)
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { Icon(Icons.Default.CameraAlt, "Cámara") }

                    SmallFloatingActionButton(
                        onClick = { launcherGaleria.launch("image/*") },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) { Icon(Icons.Default.Image, "Galería") }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Completa tus datos", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // CAMPOS DE TEXTO
            OutlinedTextField(
                value = uiState.formulario.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.formulario.nombreCompleto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.formulario.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = uiState.formulario.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña *") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Text(if(passwordVisible) "Ocultar" else "Ver") } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            OutlinedTextField(
                value = uiState.formulario.confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar Contraseña *") },
                visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) { Text(if(confirmarPasswordVisible) "Ocultar" else "Ver") } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            SelectorRegionComuna(
                regionSeleccionada = uiState.formulario.region,
                comunaSeleccionada = uiState.formulario.comuna,
                onRegionChange = { viewModel.onRegionChange(it) },
                onComunaChange = { viewModel.onComunaChange(it) }
            )

            OutlinedTextField(
                value = uiState.formulario.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.formulario.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            // TÉRMINOS
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = uiState.formulario.aceptaTerminos, onCheckedChange = { viewModel.onTerminosChange(it) })
                Text("Acepto los términos y condiciones")
            }

            // BOTÓN REGISTRO
            Button(
                onClick = {
                    if (viewModel.esFormularioValido()) {
                        val nuevoUsuario = Usuario(
                            id = "", // ID vacío para nuevo usuario
                            rut = uiState.formulario.rut,

                            // Separamos nombre completo en dos para cumplir con el modelo
                            nombre = uiState.formulario.nombreCompleto.trim().substringBefore(" "),
                            apellido = uiState.formulario.nombreCompleto.trim().substringAfter(" ", ""),

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

                            // ✅ CORRECCIÓN FINAL: Usamos 'fotoPerfil' como indicaste
                            fotoPerfil = rutaImagenFinal
                        )
                        viewModel.agregarUsuario(nuevoUsuario) { onRegistroExitoso() }
                    } else {
                        // Feedback si falla la validación
                        Toast.makeText(context, "Faltan datos obligatorios o las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Opcional: Deshabilitar visualmente si no acepta términos
                // enabled = uiState.formulario.aceptaTerminos
            ) {
                if (uiState.estaGuardando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Registrarse")
                }
            }
        }
    }
}