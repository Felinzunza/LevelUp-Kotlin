package com.example.levelUpKotlinProject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.example.levelUpKotlinProject.ui.screen.LoginAdminScreen
import com.example.levelUpKotlinProject.ui.screen.PortadaScreen
import com.example.levelUpKotlinProject.ui.screen.OpcionesAccesoScreen
import com.example.levelUpKotlinProject.ui.screen.LoginUsuarioScreen
import com.example.levelUpKotlinProject.ui.screen.RegistroScreen
import com.example.levelUpKotlinProject.ui.viewmodel.CarritoViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.OrdenViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.ProductoViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.RegistroViewModel

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
                navArgument("productoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0

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

        // NUEVA RUTA: Login de Usuario
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

        // Ruta 5: Login Admin
        composable(route = Rutas.LOGIN_ADMIN) {
            LoginAdminScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.PANEL_ADMIN) {
                        popUpTo(Rutas.LOGIN_ADMIN) { inclusive = true }
                    }
                },
                onVolverClick = {
                    navController.popBackStack()
                },
                onValidarCredenciales = preferenciasManager::validarCredencialesAdmin,
                onGuardarSesion = preferenciasManager::guardarSesionAdmin
            )
        }

        // Ruta 6: Panel Admin
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
                    navController.navigate("formulario_usuario?usuarioId=-1")
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
                    ordenViewModel.actualizarEstado(ordenId, nuevoEstado)
                },

                onCerrarSesion = {
                    preferenciasManager.cerrarSesionAdmin()
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(0)
                    }
                }
            )
        }

        // Ruta 7: Formulario Producto (agregar o editar)
        composable(
            route = Rutas.FORMULARIO_PRODUCTO,
            arguments = listOf(
                navArgument("productoId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: -1
            val productos by productoViewModel.uiState.collectAsState()
            val productoEditar = if (productoId != -1) {
                productos.productos.find { it.id == productoId }
            } else null

            FormularioProductoScreen(
                productoExistente = productoEditar,
                onGuardar = { producto ->
                    if (producto.id == 0) {
                        productoViewModel.agregarProducto(producto)
                    } else {
                        productoViewModel.actualizarProducto(producto)
                    }
                    navController.popBackStack()
                },
                onCancelar = {
                    navController.popBackStack()
                }
            )
        }


        // ... (Despues de la Ruta 7: Formulario Producto)

// Ruta 8: Formulario Usuario (agregar o editar)
        composable(
            route = Rutas.FORMULARIO_USUARIO, // Asumiremos que es "formulario_usuario?usuarioId={usuarioId}"
            arguments = listOf(
                navArgument("usuarioId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: -1

            // Recoge la lista de usuarios del ViewModel
            val usuarios by registroViewModel.uiState.collectAsState()

            // Lógica para encontrar el usuario a editar (si existe)
            val usuarioEditar = if (usuarioId != -1) {
                usuarios.usuarios.find { it.id == usuarioId }
            } else null

            val onRegistroExitoso: () -> Unit

            FormularioUsuarioScreen(
                usuarioExistente = usuarioEditar,
                onGuardar = { usuario ->
                    if (usuario.id == 0) {
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

        composable(
            route = Rutas.DETALLE_ORDEN,
            arguments = listOf(navArgument("ordenId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ordenId = backStackEntry.arguments?.getLong("ordenId") ?: -1L

            DetalleOrdenScreen(
                ordenId = ordenId,
                viewModel = ordenViewModel, // Pasamos el ViewModel compartido
                onVolverClick = { navController.popBackStack() }
            )
        }




    }
}
