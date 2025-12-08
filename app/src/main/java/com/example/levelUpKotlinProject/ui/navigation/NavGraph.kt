package com.example.levelUpKotlinProject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.levelUpKotlinProject.data.local.PreferenciasManager
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.ui.screen.*
import com.example.levelUpKotlinProject.ui.viewmodel.*
import com.example.levelUpKotlinProject.ui.viewmodel.LoginViewModel.LoginViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    usuarioRepository: UsuarioRepository,
    productoRepository: ProductoRepository,
    ordenRepository: OrdenRepository,
    carritoRepository: CarritoRepository,
    preferenciasManager: PreferenciasManager,
    registroViewModel: RegistroViewModel,
    productoViewModel: ProductoViewModel,
    ordenViewModel: OrdenViewModel,
    carritoViewModel: CarritoViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.PORTADA,
        modifier = modifier
    ) {

        // Ruta 0: Portada
        composable(route = Rutas.PORTADA) {
            PortadaScreen(
                onEntrarClick = { navController.navigate(Rutas.OPCIONES_ACCESO) },
                onAdminClick = { navController.navigate(Rutas.LOGIN_ADMIN) }
            )
        }

        // Opciones de Acceso
        composable(route = Rutas.OPCIONES_ACCESO) {
            OpcionesAccesoScreen(
                onLoginClick = { navController.navigate(Rutas.LOGIN_USER) },
                onRegistroClick = { navController.navigate(Rutas.REGISTRO) },
                onInvitadoClick = { navController.navigate(Rutas.HOME) { popUpTo(Rutas.PORTADA) { inclusive = true } } },
                onVolverClick = { navController.popBackStack() }
            )
        }

        composable(route = Rutas.HOME) {
            val estaLogueado = preferenciasManager.estaUsuarioLogueado()

            // 1. Obtenemos la lista de usuarios
            val usuariosState by registroViewModel.uiState.collectAsState()

            // 2. Obtenemos el identificador y lo limpiamos (quitamos espacios)
            val identifier = preferenciasManager.obtenerNombreUsuario()?.trim() ?: ""

            // 1. Obtenemos el ID guardado
            val storedId = preferenciasManager.obtenerIdUsuario()

            // 2. Buscamos en la lista usando el ID (esto es infalible)
            val usuarioActual = remember(usuariosState.usuarios, storedId, identifier, estaLogueado) {
                if (estaLogueado) {
                    usuariosState.usuarios.find {
                        // A) Búsqueda exacta por ID (La más segura)
                        (storedId != null && it.id == storedId) ||

                                // B) Fallback: Búsqueda por nombre/email (si falla el ID)
                                (identifier.isNotEmpty() && (
                                        it.username.equals(identifier, ignoreCase = true) ||
                                                it.email.equals(identifier, ignoreCase = true) ||
                                                it.nombre.equals(identifier, ignoreCase = true)
                                        ))
                    }
                } else null
            }

            HomeScreen(
                productoRepository = productoRepository,
                carritoRepository = carritoRepository,
                onProductoClick = { productoId -> navController.navigate("${Rutas.DETALLE}/$productoId") },

                // ✅ CORRECCIÓN PRINCIPAL AQUÍ
                onPerfilClick = {
                    if (usuarioActual != null) {
                        navController.navigate(Rutas.perfilUsuario(usuarioActual.id))
                    } else {
                        // Si entra aquí, verás el error en los logs de nuevo
                        println("ERROR NAV: Usuario no encontrado. Identifier: $identifier")
                    }
                },

                onCarritoClick = { navController.navigate(Rutas.CARRITO) },
                onRegistroClick = { navController.navigate(Rutas.REGISTRO) },
                onVolverPortada = { navController.navigate(Rutas.PORTADA) { popUpTo(Rutas.HOME) { inclusive = true } } },

                estaLogueado = estaLogueado,
                usuarioActual = usuarioActual,
                nombreUsuario = usuarioActual?.nombre ?: "Invitado", // Mostramos el nombre real del objeto, no el ID

                onCerrarSesion = {
                    preferenciasManager.cerrarSesionUsuario()
                    navController.navigate(Rutas.PORTADA) { popUpTo(0) }
                },
                onIniciarSesionClick = { navController.navigate(Rutas.LOGIN_USER) }
            )
        }

        // ✅ RUTA PERFIL (Ahora recibe un ID para saber a quién mostrar)
        composable(
            route = Rutas.PERFIL_USUARIO, // "perfil_usuario/{usuarioId}"
            arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
        ) { backStackEntry ->
            // 1. Obtenemos el ID que nos pasaron (sea del admin o del cliente)
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""

            val usuariosState by registroViewModel.uiState.collectAsState()

            // 2. Buscamos a ese usuario específico en la lista
            val usuarioEncontrado = usuariosState.usuarios.find { it.id == usuarioId }

            PerfilUsuarioScreen(
                usuarioActual = usuarioEncontrado,
                registroViewModel = registroViewModel,
                onVolverClick = { navController.popBackStack() },
                onCerrarSesion = {
                    // Aquí decidimos qué sesión cerrar dependiendo de quién sea
                    if (usuarioEncontrado?.rol?.name == "ADMIN") preferenciasManager.cerrarSesionAdmin()
                    else preferenciasManager.cerrarSesionUsuario()

                    navController.navigate(Rutas.PORTADA) { popUpTo(0) }
                }
            )
        }

        // Ruta 2: Detalle de producto
        composable(
            route = "${Rutas.DETALLE}/{productoId}",
            arguments = listOf(
                navArgument("productoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            DetalleProductoScreen(
                productoId = productoId,
                productoRepository = productoRepository,
                carritoRepository = carritoRepository,
                onVolverClick = { navController.popBackStack() }
            )
        }

        // Ruta 3: Carrito
        composable(route = Rutas.CARRITO) {
            CarritoScreen(
                navController = navController,
                viewModel = carritoViewModel,
                carritoRepository = carritoRepository,
                preferenciasManager = preferenciasManager,
                onVolverClick = { navController.popBackStack() },
                onProductoClick = { productoId -> navController.navigate("${Rutas.DETALLE}/$productoId") },
                onIrALogin = { navController.navigate(Rutas.LOGIN_USER) }
            )
        }

        // Ruta 4: Registro
        composable(route = Rutas.REGISTRO) {
            RegistroScreen(
                usuarioRepository = usuarioRepository,
                onVolverClick = { navController.popBackStack() },
                onRegistroExitoso = { navController.navigate(Rutas.HOME) { popUpTo(Rutas.HOME) { inclusive = true } } }
            )
        }

        // Ruta 5: Login Usuario
        composable(route = Rutas.LOGIN_USER) {
            LoginUsuarioScreen(
                usuarioRepository = usuarioRepository,
                onVolverClick = { navController.popBackStack() },
                preferenciasManager = preferenciasManager,
                onLoginExitoso = { navController.navigate(Rutas.HOME) { popUpTo(Rutas.HOME) { inclusive = true } } }
            )
        }

        // Login Admin
        composable(route = Rutas.LOGIN_ADMIN) {
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(usuarioRepository))
            LoginAdminScreen(
                viewModel = loginViewModel,
                onLoginExitoso = { username ->
                    preferenciasManager.guardarSesionAdmin(username)
                    navController.navigate(Rutas.PANEL_ADMIN) { popUpTo(Rutas.LOGIN_ADMIN) { inclusive = true } }
                },
                onVolverClick = { navController.popBackStack() }
            )
        }

        // Ruta 6: Panel Admin
        composable(route = Rutas.PANEL_ADMIN) {
            if (!preferenciasManager.estaAdminLogueado()) {
                LaunchedEffect(Unit) { navController.navigate(Rutas.LOGIN_ADMIN) { popUpTo(0) } }
                return@composable
            }

            val usuariosState by registroViewModel.uiState.collectAsState()
            val productosState by productoViewModel.uiState.collectAsState()
            val ordenesState by ordenViewModel.uiState.collectAsState()

            // 3. Buscamos al Admin completo
            val username = preferenciasManager.obtenerUsernameAdmin()
            val usuarioAdmin = usuariosState.usuarios.find { it.username == username }

            AdminPanelScreen(
                usuarios = usuariosState.usuarios,
                productos = productosState.productos,
                ordenes = ordenesState.ordenes,
                usernameAdmin = username ?: "Admin",

                // ✅ CORRECCIÓN: Pasamos el objeto usuarioAdmin y la acción de perfil
                usuarioAdmin = usuarioAdmin,

                // ✅ CORRECCIÓN: Navegamos al perfil pasando el ID del ADMIN
                onPerfilAdminClick = {
                    usuarioAdmin?.let { navController.navigate(Rutas.perfilUsuario(it.id)) }
                },

                onAgregarUsuario = { navController.navigate("formulario_usuario?usuarioId=") },
                onEditarUsuario = { usuario -> navController.navigate(Rutas.formularioEditarUsuario(usuario.id)) },
                onEliminarUsuario = { usuario -> registroViewModel.eliminarUsuario(usuario) },
                onAgregarProducto = { navController.navigate("formulario_producto?productoId=") },
                onEditarProducto = { producto -> navController.navigate(Rutas.formularioEditarProducto(producto.id)) },
                onEliminarProducto = { producto -> productoViewModel.eliminarProducto(producto) },

                // Corrección Orden (String ID)
                onVerDetalleOrden = { ordenId -> navController.navigate(Rutas.detalleOrden(ordenId)) },
                onCambiarEstadoOrden = { ordenId, nuevoEstado -> ordenViewModel.actualizarEstadoOrden(ordenId, nuevoEstado) }, // Asume que VM espera String o conviértelo si es Long

                onCerrarSesion = { preferenciasManager.cerrarSesionAdmin(); navController.navigate(Rutas.PORTADA) { popUpTo(0) } }
            )
        }

        // Ruta 7: Formulario Producto
        composable(
            route = Rutas.FORMULARIO_PRODUCTO,
            arguments = listOf(navArgument("productoId") { type = NavType.StringType; defaultValue = ""; nullable = true })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            val productos by productoViewModel.uiState.collectAsState()
            val productoEditar = if (productoId.isNotBlank()) productos.productos.find { it.id == productoId } else null

            FormularioProductoScreen(
                productoExistente = productoEditar,
                onGuardar = { producto ->
                    if (producto.id.isBlank()) productoViewModel.agregarProducto(producto)
                    else productoViewModel.actualizarProducto(producto)
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        // Ruta 8: Formulario Usuario
        composable(
            route = Rutas.FORMULARIO_USUARIO,
            arguments = listOf(navArgument("usuarioId") { type = NavType.StringType; defaultValue = ""; nullable = true })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val usuarios by registroViewModel.uiState.collectAsState()
            val usuarioEditar = if (usuarioId.isNotBlank()) usuarios.usuarios.find { it.id == usuarioId } else null

            FormularioUsuarioScreen(
                usuarioExistente = usuarioEditar,
                onGuardar = { usuario ->
                    if (usuario.id.isBlank()) registroViewModel.agregarUsuario(usuario) { navController.popBackStack() }
                    else {
                        registroViewModel.actualizarUsuario(usuario)
                        navController.popBackStack()
                    }
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        // Detalle Orden
        composable(
            route = Rutas.DETALLE_ORDEN,
            arguments = listOf(navArgument("ordenId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ordenId = backStackEntry.arguments?.getString("ordenId") ?: ""
            // Si el VM espera Long (como acordamos antes), conviértelo aquí.
            // Si ya lo pasaste a String, déjalo así. Asumiré String por el navArgument.
            DetalleOrdenScreen(ordenId = ordenId, viewModel = ordenViewModel, onVolverClick = { navController.popBackStack() })
        }
    }
}