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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation // Asegúrate de tener este también
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import com.example.levelUpKotlinProject.domain.validator.ValidadorFormulario // 👈 Importamos el validador
import com.example.levelUpKotlinProject.ui.components.SelectorRegionComuna // 👈 Importamos el selector
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
    var passwordVisible by remember { mutableStateOf(false) }

    // Nuevos campos necesarios
    var telefono by remember { mutableStateOf(usuarioExistente?.telefono ?: "") }
    var direccion by remember { mutableStateOf(usuarioExistente?.direccion ?: "") }
    var region by remember { mutableStateOf(usuarioExistente?.region ?: "") }
    var comuna by remember { mutableStateOf(usuarioExistente?.comuna ?: "") }

    var rolSeleccionado by remember { mutableStateOf(usuarioExistente?.rol ?: Rol.USUARIO) }

    // --- VARIABLES DE ERROR (VALIDACIÓN) ---
    var errorRut by remember { mutableStateOf<String?>(null) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorEmail by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorDireccion by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }

    // --- LÓGICA DE IMAGEN ---
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenGuardadaRuta by remember { mutableStateOf(usuarioExistente?.fotoPerfil ?: "") }
    val archivoTemporal = remember { crearArchivoImagenFormulario(context) }
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
        else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
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
                val imagenParaMostrar = if (imagenUri != null) imagenUri else if (imagenGuardadaRuta.isNotEmpty()) imagenGuardadaRuta else null
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
                        onClick = { launcherGaleria.launch("image/*") },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) { Icon(Icons.Default.Image, "Galería") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPOS DE TEXTO CON VALIDACIÓN ---
            // RUT
            OutlinedTextField(
                value = rut,
                onValueChange = {
                    rut = it
                    errorRut = null // Limpiamos el error rojo cuando el usuario escribe
                },
                label = { Text("RUT") },
                placeholder = { Text("Ej: 12345678-9") },
                modifier = Modifier.fillMaxWidth(),

                // Muestra rojo si hay error
                isError = errorRut != null,

                // Muestra el mensaje de error abajo
                supportingText = {
                    errorRut?.let { mensaje ->
                        Text(mensaje, color = MaterialTheme.colorScheme.error)
                    }
                },
                // Usamos Text para permitir la 'K' y el guion '-'
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // NOMBRE
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorNombre = null },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorNombre != null,
                supportingText = { errorNombre?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // APELLIDO (Validación simple manual)
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            // USERNAME
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            // EMAIL (Con Validador)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorEmail = null },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = errorEmail != null,
                supportingText = { errorEmail?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // TELEFONO (Con Validador)
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it; errorTelefono = null },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = errorTelefono != null,
                supportingText = { errorTelefono?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )


            // --- SELECTOR DE REGIÓN Y COMUNA ---
            SelectorRegionComuna(
                regionSeleccionada = region,
                comunaSeleccionada = comuna,
                onRegionChange = { nuevaRegion ->
                    region = nuevaRegion
                    comuna = "" // Resetear comuna al cambiar región
                },
                onComunaChange = { nuevaComuna -> comuna = nuevaComuna }
            )

            // DIRECCIÓN (Con Validador)
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it; errorDireccion = null },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorDireccion != null,
                supportingText = { errorDireccion?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // PASSWORD (Con Validador)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorPassword = null },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },

                isError = errorPassword != null,
                supportingText = { errorPassword?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // SELECTOR DE ROL
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
                        // --- LÓGICA DE VALIDACIÓN ANTES DE GUARDAR ---

                        // 1. Validar campos individuales usando ValidadorFormulario
                        val valRut = ValidadorFormulario.validarRut(rut)
                        val valEmail = ValidadorFormulario.validarEmail(email)
                        val valTelefono = ValidadorFormulario.validarTelefono(telefono)
                        val valDireccion = ValidadorFormulario.validarDireccion(direccion)
                        val valPassword = ValidadorFormulario.validarPassword(password)

                        // Validación manual para nombre (ya que el validador usa "nombre completo")
                        val valNombre = if (nombre.isBlank()) "El nombre es obligatorio" else null

                        // 2. Actualizar estados de error
                        errorRut = valRut
                        errorEmail = valEmail
                        errorTelefono = valTelefono
                        errorDireccion = valDireccion
                        errorPassword = valPassword
                        errorNombre = valNombre

                        // 3. Verificar si hay errores
                        if (valRut == null && valEmail == null && valTelefono == null && valDireccion == null && valPassword == null && valNombre == null) {
                            // TODO VALIDO -> GUARDAR
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
                                telefono = telefono,
                                region = region,
                                comuna = comuna,
                                direccion = direccion,
                                fechaNacimiento = usuarioExistente?.fechaNacimiento ?: Date(),
                                fechaRegistro = usuarioExistente?.fechaRegistro ?: Date()
                            )
                            onGuardar(usuarioAGuardar)
                        } else {
                            // HAY ERRORES
                            Toast.makeText(context, "Por favor corrige los errores", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }
        }
    }
}

fun crearArchivoImagenFormulario(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(imageFileName, ".jpg", context.externalCacheDir)
}

