package com.example.levelUpKotlinProyect.ui.navigation

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
import com.example.levelUpKotlinProyect.data.local.PreferenciasManager
import com.example.levelUpKotlinProyect.data.repository.CarritoRepository
import com.example.levelUpKotlinProyect.data.repository.ProductoRepositoryImpl
import com.example.levelUpKotlinProyect.ui.screen.AdminPanelScreen
import com.example.levelUpKotlinProyect.ui.screen.CarritoScreen
import com.example.levelUpKotlinProyect.ui.screen.DetalleProductoScreen
import com.example.levelUpKotlinProyect.ui.screen.FormularioProductoScreen
import com.example.levelUpKotlinProyect.ui.screen.HomeScreen
import com.example.levelUpKotlinProyect.ui.screen.LoginAdminScreen
import com.example.levelUpKotlinProyect.ui.screen.PortadaScreen
import com.example.levelUpKotlinProyect.ui.screen.RegistroScreen
import com.example.levelUpKotlinProyect.ui.viewmodel.ProductoViewModel

/**
 * NavGraph: Define todas las rutas de navegación de la app
 * 
 * Piensa en esto como un mapa de carreteras:
 * - Cada pantalla es una ciudad
 * - Las rutas son las carreteras que las conectan
 * - NavController es el GPS que te lleva de una a otra
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    productoRepository: ProductoRepositoryImpl,
    carritoRepository: CarritoRepository,
    preferenciasManager: PreferenciasManager,
    productoViewModel: ProductoViewModel,
    modifier: Modifier = Modifier
) {
    // NavHost es el contenedor de todas las pantallas
    // startDestination: la primera pantalla que se ve al abrir la app
    NavHost(
        navController = navController,
        startDestination = Rutas.PORTADA,
        modifier = modifier
    ) {
        
        // Ruta 0: Pantalla de Portada/Bienvenida
        composable(route = Rutas.PORTADA) {
            PortadaScreen(
                onEntrarClick = {
                    navController.navigate(Rutas.HOME) {
                        // Eliminar portada del backstack
                        popUpTo(Rutas.PORTADA) { inclusive = true }
                    }
                },
                onAdminClick = {
                    navController.navigate(Rutas.LOGIN_ADMIN)
                }
            )
        }
        
        // Ruta 1: Pantalla principal (Home)
        composable(route = Rutas.HOME) {
            HomeScreen(
                productoRepository = productoRepository,
                carritoRepository = carritoRepository,
                onProductoClick = { productoId ->
                    // Cuando tocas un producto, navega a detalle
                    navController.navigate("${Rutas.DETALLE}/$productoId")
                },
                onCarritoClick = {
                    // Ir al carrito
                    navController.navigate(Rutas.CARRITO)
                },
                onRegistroClick = {
                    // Ir a registro
                    navController.navigate(Rutas.REGISTRO)
                },
                onVolverPortada = {
                    // Volver a portada
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }
        
        // Ruta 2: Detalle de producto (recibe un ID como parámetro)
        composable(
            route = "detalle/{productoId}",
            arguments = listOf(
                // El ID viene como String en la URL, lo convertimos a Int
                navArgument("productoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Extraer el ID del producto de la URL
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            
            DetalleProductoScreen(
                productoId = productoId,
                productoRepository = productoRepository,
                carritoRepository = carritoRepository,
                onVolverClick = {
                    // Volver a Home
                    navController.popBackStack()
                }
            )
        }
        
        // Ruta 3: Carrito completo
        composable(route = Rutas.CARRITO) {
            CarritoScreen(
                carritoRepository = carritoRepository,
                onVolverClick = {
                    navController.popBackStack()
                },
                onProductoClick = { productoId ->
                    navController.navigate("${Rutas.DETALLE}/$productoId")
                }
            )
        }
        
        // Ruta 4: Formulario de registro
        composable(route = Rutas.REGISTRO) {
            RegistroScreen(
                onVolverClick = {
                    navController.popBackStack()
                },
                onRegistroExitoso = {
                    // Después de registrarse, volver a Home
                    navController.navigate(Rutas.HOME) {
                        // Limpiar el back stack para que no pueda volver atrás
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
            // Verificar si está logueado
            if (!preferenciasManager.estaAdminLogueado()) {
                LaunchedEffect(Unit) {
                    navController.navigate(Rutas.LOGIN_ADMIN) {
                        popUpTo(0)
                    }
                }
                return@composable
            }
            
            val productos by productoViewModel.uiState.collectAsState()
            
            AdminPanelScreen(
                productos = productos.productos,
                usernameAdmin = preferenciasManager.obtenerUsernameAdmin() ?: "Admin",
                onAgregarProducto = {
                    navController.navigate("formulario_producto?productoId=-1")
                },
                onEditarProducto = { producto ->
                    navController.navigate(Rutas.formularioEditar(producto.id))
                },
                onEliminarProducto = { producto ->
                    productoViewModel.eliminarProducto(producto)
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
    }
}
