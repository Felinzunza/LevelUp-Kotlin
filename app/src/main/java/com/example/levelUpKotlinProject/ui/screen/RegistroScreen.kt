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
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario
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

            // RUT ACTUALIZADO CON VALIDACIÓN
            OutlinedTextField(
                value = uiState.formulario.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT *") },
                placeholder = { Text("Ej: 12.345.678-9") }, // Ayuda visual
                modifier = Modifier.fillMaxWidth(),

                // Conectamos con el estado de error
                isError = uiState.errores.rutError != null,
                supportingText = {
                    uiState.errores.rutError?.let { mensaje ->
                        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                    }
                }
            )



            // NOMBRE COMPLETO
            OutlinedTextField(
                value = uiState.formulario.nombreCompleto,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                // 1. Mostrar error si existe en el estado
                isError = uiState.errores.nombreCompletoError != null,
                // 2. Texto de ayuda (el mensaje de error)
                supportingText = {
                    uiState.errores.nombreCompletoError?.let { mensaje ->
                        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // EMAIL
            OutlinedTextField(
                value = uiState.formulario.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errores.emailError != null,
                supportingText = { uiState.errores.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // TELÉFONO
            OutlinedTextField(
                value = uiState.formulario.telefono,
                onValueChange = { viewModel.onTelefonoChange(it) },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = uiState.errores.telefonoError != null,
                supportingText = { uiState.errores.telefonoError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )



            // PASSWORD
            OutlinedTextField(
                value = uiState.formulario.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Text(if(passwordVisible) "Ocultar" else "Ver") } },
                isError = uiState.errores.passwordError != null,
                supportingText = { uiState.errores.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // CONFIRMAR PASSWORD
            OutlinedTextField(
                value = uiState.formulario.confirmarPassword,
                onValueChange = { viewModel.onConfirmarPasswordChange(it) },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) { Text(if(confirmarPasswordVisible) "Ocultar" else "Ver") } },
                isError = uiState.errores.confirmarPasswordError != null,
                supportingText = { uiState.errores.confirmarPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // DIRECCIÓN
            OutlinedTextField(
                value = uiState.formulario.direccion,
                onValueChange = { viewModel.onDireccionChange(it) },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errores.direccionError != null,
                supportingText = { uiState.errores.direccionError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            SelectorRegionComuna(
                regionSeleccionada = uiState.formulario.region,
                comunaSeleccionada = uiState.formulario.comuna,
                onRegionChange = { viewModel.onRegionChange(it) },
                onComunaChange = { viewModel.onComunaChange(it) }
            )

            // TÉRMINOS
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.formulario.aceptaTerminos,
                        onCheckedChange = { viewModel.onTerminosChange(it) }
                    )
                    Text("Acepto los términos y condiciones")
                }
                // Error de términos
                if (uiState.errores.terminosError != null) {
                    Text(
                        text = uiState.errores.terminosError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            // BOTÓN REGISTRAR
            Button(
                onClick = {
                    // LLAMAMOS A LA NUEVA FUNCIÓN DE REGISTRO QUE VALIDA
                    // Nota: Aquí pasamos una lambda para construir el objeto final si la validación pasa

                    // PASO CRÍTICO: Primero validamos en el UI o VM.
                    // Vamos a hacer una validación manual rápida aquí para actualizar el UI State y luego decidir

                    // Como el VM ahora maneja la validación completa en `registrarUsuario`,
                    // necesitamos pasarle la foto. El VM no sabe de la variable local `rutaImagenFinal`.
                    // TRUCO: Actualizamos el usuario en el callback onSuccess o pasamos la foto al VM antes.

                    val form = uiState.formulario
                    // Usamos el validador directamente para chequear antes de enviar (opcional, pero el VM lo hace mejor)
                    viewModel.registrarUsuario {
                        // Este bloque se ejecuta SI la validación fue exitosa y se guardó en BD
                        // Pero espera... el VM crea el usuario sin la foto correcta porque la foto está en la UI var `rutaImagenFinal`.

                        // CORRECCIÓN: Vamos a construir el usuario AQUÍ en la UI y usar una función auxiliar del VM para validar.

                        val nuevoId = UUID.randomUUID().toString()
                        val usuarioFinal = Usuario(
                            id = nuevoId,
                            rut = form.rut,
                            nombre = form.nombreCompleto.substringBefore(" "),
                            apellido = form.nombreCompleto.substringAfter(" ", ""),
                            username = form.email.substringBefore("@"),
                            email = form.email,
                            password = form.password,
                            telefono = form.telefono,
                            region = form.region,
                            comuna = form.comuna,
                            direccion = form.direccion,
                            fechaNacimiento = Date(),
                            fechaRegistro = Date(),
                            rol = Rol.USUARIO,
                            fotoPerfil = rutaImagenFinal // AQUÍ USAMOS LA FOTO
                        )

                        // Guardamos sesión
                        preferenciasManager.guardarSesionUsuario(
                            id = nuevoId,
                            email = usuarioFinal.email,
                            nombre = usuarioFinal.nombre,
                            rut = usuarioFinal.rut
                        )

                    }



                    val errores = ValidadorFormulario.validarFormulario(uiState.formulario)

                    // Necesitarás agregar esta función a tu ViewModel: fun setErrores(errores: ErroresFormulario)
                    // _uiState.update { it.copy(errores = errores) }

                    if (errores.esValido()) {
                        val nuevoId = UUID.randomUUID().toString()
                        val usuarioFinal = Usuario(
                            id = nuevoId,
                            rut = uiState.formulario.rut,
                            nombre = uiState.formulario.nombreCompleto.substringBefore(" "),
                            apellido = uiState.formulario.nombreCompleto.substringAfter(" ", ""),
                            username = uiState.formulario.email.substringBefore("@"),
                            email = uiState.formulario.email,
                            password = uiState.formulario.password,
                            telefono = uiState.formulario.telefono,
                            region = uiState.formulario.region,
                            comuna = uiState.formulario.comuna,
                            direccion = uiState.formulario.direccion,
                            fechaNacimiento = Date(),
                            fechaRegistro = Date(),
                            rol = Rol.USUARIO,
                            fotoPerfil = rutaImagenFinal
                        )

                        viewModel.agregarUsuario(usuarioFinal) {
                            preferenciasManager.guardarSesionUsuario(nuevoId, usuarioFinal.email, usuarioFinal.nombre, usuarioFinal.rut)
                            Toast.makeText(context, "Bienvenido!", Toast.LENGTH_LONG).show()
                            onRegistroExitoso()
                        }
                    } else {
                        // Truco para actualizar el estado de errores en el VM desde la UI si no quieres cambiar toda la lógica del VM
                        // Lo ideal es tener un método en el VM: fun reportarErrores(errores: ErroresFormulario)
                        viewModel.actualizarErrores(errores) // <--- CREA ESTA FUNCIÓN EN TU VM
                        Toast.makeText(context, "Por favor corrige los errores", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Quitamos el enabled = aceptaTerminos para que el usuario pueda hacer click y ver el error de términos
                enabled = !uiState.estaGuardando
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
