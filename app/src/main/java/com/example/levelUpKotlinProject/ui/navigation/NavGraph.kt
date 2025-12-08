package com.example.levelUpKotlinProject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.levelUpKotlinProject.ui.screen.AdminPanelScreen
import com.example.levelUpKotlinProject.ui.screen.CarritoScreen
import com.example.levelUpKotlinProject.ui.screen.DetalleOrdenScreen
import com.example.levelUpKotlinProject.ui.screen.DetalleProductoScreen
import com.example.levelUpKotlinProject.ui.screen.FormularioProductoScreen
import com.example.levelUpKotlinProject.ui.screen.FormularioUsuarioScreen
import com.example.levelUpKotlinProject.ui.screen.HomeScreen
import com.example.levelUpKotlinProject.ui.screen.PerfilUsuarioScreen
import com.example.levelUpKotlinProject.ui.screen.LoginAdminScreen
import com.example.levelUpKotlinProject.ui.screen.PortadaScreen
import com.example.levelUpKotlinProject.ui.screen.OpcionesAccesoScreen
import com.example.levelUpKotlinProject.ui.screen.LoginUsuarioScreen
import com.example.levelUpKotlinProject.ui.screen.RegistroScreen
import com.example.levelUpKotlinProject.ui.viewmodel.CarritoViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.OrdenViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.ProductoViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.LoginViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.LoginViewModel.LoginViewModelFactory



/**
 * NavGraph: Define todas las rutas de navegación de la app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    usuarioRepository: UsuarioRepository, // REQUERIDO
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

        // Ruta 0: Pantalla de Portada/Bienvenida
        composable(route = Rutas.PORTADA) {
            PortadaScreen(
                onEntrarClick = {
                    navController.navigate(Rutas.OPCIONES_ACCESO)
                },
                onAdminClick = {
                    navController.navigate(Rutas.LOGIN_ADMIN)
                }
            )
        }

        // NUEVA RUTA: Opciones de Acceso (Login/Registro/Invitado)
        composable(route = Rutas.OPCIONES_ACCESO) {
            OpcionesAccesoScreen(
                onLoginClick = {
                    navController.navigate(Rutas.LOGIN_USER)
                },
                onRegistroClick = {
                    navController.navigate(Rutas.REGISTRO)
                },
                onInvitadoClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.PORTADA) { inclusive = true }
                    }
                },
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta 1: Pantalla principal (Home)
        composable(route = Rutas.HOME) {

            val estaLogueado = preferenciasManager.estaUsuarioLogueado()
            val nombreUsuario = if (estaLogueado) preferenciasManager.obtenerNombreUsuario() else null

            HomeScreen(
                productoRepository = productoRepository,
                carritoRepository = carritoRepository,
                onProductoClick = { productoId ->
                    navController.navigate("${Rutas.DETALLE}/$productoId")
                },
                onPerfilClick = {
                    navController.navigate(Rutas.PERFIL_USUARIO)
                },
                onCarritoClick = {
                    navController.navigate(Rutas.CARRITO)
                },
                onRegistroClick = {
                    navController.navigate(Rutas.REGISTRO)
                },
                onVolverPortada = {
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                },

                estaLogueado = estaLogueado,
                nombreUsuario = nombreUsuario, // 👈 Pasamos el nombre aquí

                onCerrarSesion = {
                    // 1. Borrar datos de sesión
                    preferenciasManager.cerrarSesionUsuario()

                    // 2. Recargar el Home o ir a Portada (para refrescar la UI)
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(0) // Limpia toda la pila de navegación
                    }
                },
                onIniciarSesionClick = {
                    navController.navigate(Rutas.LOGIN_USER)
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
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta 3: Carrito completo
        composable(route = Rutas.CARRITO) {
            CarritoScreen(
                navController = navController,
                viewModel = carritoViewModel,
                carritoRepository = carritoRepository,
                preferenciasManager = preferenciasManager,
                onVolverClick = {
                    navController.popBackStack()
                },
                onProductoClick = { productoId ->
                    navController.navigate("${Rutas.DETALLE}/$productoId")
                },
                onIrALogin = {
                    navController.navigate(Rutas.LOGIN_USER)
                }

            )
        }

        //Ruta 4: Perfil de Usuario
        composable(route = Rutas.PERFIL_USUARIO) {
            // Obtenemos el email del usuario logueado
            val emailUsuario = preferenciasManager.obtenerEmailUsuario()

            // Obtenemos la lista de usuarios del ViewModel compartido
            val usuariosState by registroViewModel.uiState.collectAsState()

            // Buscamos al usuario actual
            val usuarioActual = usuariosState.usuarios.find { it.email == emailUsuario }

            PerfilUsuarioScreen(
                usuarioActual = usuarioActual,
                registroViewModel = registroViewModel,
                onVolverClick = { navController.popBackStack() },
                onCerrarSesion = {
                    preferenciasManager.cerrarSesionUsuario()
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(0)
                    }
                }
            )
        }

        // Ruta 4: Formulario de registro (REGISTRO)
        composable(route = Rutas.REGISTRO) {
            RegistroScreen(
                usuarioRepository = usuarioRepository, // AÑADIDO
                onVolverClick = {
                    navController.popBackStack()
                },
                onRegistroExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        // Ruta 5: Login de Usuario
        composable(route = Rutas.LOGIN_USER) {
            LoginUsuarioScreen(
                usuarioRepository = usuarioRepository,
                onVolverClick = {
                    navController.popBackStack()
                },
                preferenciasManager = preferenciasManager, // 👈 Nuevo parámetro
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        // Ruta 6: Login Admin (CORREGIDA PARA USAR DB)
        composable(route = Rutas.LOGIN_ADMIN) {

            // 1. Instanciamos el ViewModel usando el repositorio real
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(usuarioRepository)
            )

            LoginAdminScreen(
                viewModel = loginViewModel, // Pasamos el VM conectado a BD
                onLoginExitoso = { username ->
                    // 2. Guardamos sesión en preferencias SOLO si el login en BD fue exitoso
                    preferenciasManager.guardarSesionAdmin(username)

                    // 3. Navegamos
                    navController.navigate(Rutas.PANEL_ADMIN) {
                        popUpTo(Rutas.LOGIN_ADMIN) { inclusive = true }
                    }
                },
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // Ruta 7: Panel Admin
        composable(route = Rutas.PANEL_ADMIN) {
            if (!preferenciasManager.estaAdminLogueado()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Rutas.LOGIN_ADMIN) {
                        popUpTo(0)
                    }
                }
                return@composable
            }
            // Inyección de dependencias
            val usuariosState by registroViewModel.uiState.collectAsState()
            val productosState by productoViewModel.uiState.collectAsState()
            val ordenesState by ordenViewModel.uiState.collectAsState()

            AdminPanelScreen(
                //cambio
                usuarios = usuariosState.usuarios,
                productos = productosState.productos,
                ordenes = ordenesState.ordenes,
                usernameAdmin = preferenciasManager.obtenerUsernameAdmin() ?: "Admin",

                //SECCIÓN DE USUARIOS
                onAgregarUsuario = {
                    navController.navigate("formulario_usuario?usuarioId=")//aqui hice un cambio pero igual me crasheo
                },
                onEditarUsuario = { usuario ->
                    navController.navigate(Rutas.formularioEditarUsuario(usuario.id))
                },
                onEliminarUsuario = { usuario ->
                    registroViewModel.eliminarUsuario(usuario)
                },


                //SECCIÓN DE PRODUCTOS
                onAgregarProducto = {
                    navController.navigate("formulario_producto?productoId=-1")
                },
                onEditarProducto = { producto ->
                    navController.navigate(Rutas.formularioEditarProducto(producto.id))
                },
                onEliminarProducto = { producto ->
                    productoViewModel.eliminarProducto(producto)
                },

                //SECCIÓN DE ORDENES
                onVerDetalleOrden = { ordenId ->
                    navController.navigate(Rutas.detalleOrden(ordenId))
                },
                onCambiarEstadoOrden = { ordenId, nuevoEstado ->
                    ordenViewModel.actualizarEstadoOrden(ordenId, nuevoEstado)
                },

                onCerrarSesion = {
                    preferenciasManager.cerrarSesionAdmin()
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(0)
                    }
                }
            )
        }

        // Ruta 8: Formulario Producto (ID STRING)
        composable(
            route = Rutas.FORMULARIO_PRODUCTO, // Asegúrate que en Rutas.kt sea "formulario_producto?productoId={productoId}"
            arguments = listOf(
                navArgument("productoId") {
                    type = NavType.StringType // CAMBIO CRÍTICO
                    defaultValue = "" // Vacío significa nuevo
                    nullable = true
                }
            )
        ) { backStackEntry ->
            // Obtenemos String
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""

            val productos by productoViewModel.uiState.collectAsState()

            // Buscamos comparando String con String
            val productoEditar = if (productoId.isNotBlank()) {
                productos.productos.find { it.id == productoId }
            } else null

            FormularioProductoScreen(
                productoExistente = productoEditar,
                onGuardar = { producto ->
                    // Si el ID está vacío, es nuevo -> Agregar
                    if (producto.id.isBlank()) {
                        productoViewModel.agregarProducto(producto)
                    } else {
                        // Si tiene ID, es existente -> Actualizar
                        productoViewModel.actualizarProducto(producto)
                    }
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }


        // Ruta 9: Formulario Usuario (agregar o editar)
        composable(
            route = Rutas.FORMULARIO_USUARIO, // Asumiremos que es "formulario_usuario?usuarioId={usuarioId}"
            arguments = listOf(
                navArgument("usuarioId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->



            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""

            // Recoge la lista de usuarios del ViewModel
            val usuarios by registroViewModel.uiState.collectAsState()
            val usuarioEditar = if (usuarioId.isNotBlank()) usuarios.usuarios.find {
                                    it.id == usuarioId
                                } else null

            FormularioUsuarioScreen(
                usuarioExistente = usuarioEditar,
                onGuardar = { usuario ->
                    if (usuario.id == "") {
                        // CASO AGREGAR: Ahora requiere el bloque { } al final (el onSuccess)
                        registroViewModel.agregarUsuario(usuario) {
                            // Esto se ejecuta cuando la base de datos termina
                            navController.popBackStack()
                        }
                    } else {
                        // CASO ACTUALIZAR: Sigue igual (asumiendo que no cambiaste esa función)
                        registroViewModel.actualizarUsuario(usuario)
                        navController.popBackStack()
                    }
                },
                onCancelar = {
                    navController.popBackStack()
                }
            )
        }

        //Ruta 10: Detalle de orden
        composable(
            route = Rutas.DETALLE_ORDEN,
            arguments = listOf(navArgument("ordenId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ordenId = backStackEntry.arguments?.getString("ordenId") ?: ""

            DetalleOrdenScreen(
                ordenId = ordenId,
                viewModel = ordenViewModel, // Pasamos el ViewModel compartido
                onVolverClick = { navController.popBackStack() }
            )
        }




    }
}
