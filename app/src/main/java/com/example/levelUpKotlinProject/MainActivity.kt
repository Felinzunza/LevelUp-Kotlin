package com.example.levelUpKotlinProject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.levelUpKotlinProject.data.local.AppDatabase
import com.example.levelUpKotlinProject.data.local.PreferenciasManager
import com.example.levelUpKotlinProject.data.local.ProductoInicializador
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository // AÑADIDO
import com.example.levelUpKotlinProject.ui.navigation.NavGraph
import com.example.levelUpKotlinProject.ui.theme.LevelUpKotlinProjectTheme
import com.example.levelUpKotlinProject.ui.viewmodel.CarritoViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.CarritoViewModelFactory
import com.example.levelUpKotlinProject.ui.viewmodel.OrdenViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.OrdenesViewModelFactory
import com.example.levelUpKotlinProject.ui.viewmodel.ProductoViewModel
import com.example.levelUpKotlinProject.ui.viewmodel.ProductoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear base de datos
        val database = AppDatabase.getDatabase(applicationContext)

        // Inicializar productos de ejemplo (solo primera vez)
        ProductoInicializador.inicializarProductos(applicationContext)

        // Crear repositorios
        val productoRepository = ProductoRepository(database.productoDao())
        val ordenRepository = OrdenRepository(database.ordenDao(), database.carritoDao())
        val carritoRepository = CarritoRepository(database.carritoDao())
        val usuarioRepository = UsuarioRepository(database.usuarioDao()) // REQUERIDO

        // Crear PreferenciasManager para sesión admin
        val preferenciasManager = PreferenciasManager(applicationContext)

        setContent {
            LevelUpKotlinProjectTheme {
                Surface {
                    // Crear NavController para gestionar navegación
                    val navController = rememberNavController()

                    // Crear ViewModel con Factory
                    val productoViewModel: ProductoViewModel = viewModel(
                        factory = ProductoViewModelFactory(productoRepository)
                    )

                    val ordenViewModel: OrdenViewModel = viewModel(
                        factory = OrdenesViewModelFactory(ordenRepository)
                    )

                    val carritoViewModel: CarritoViewModel = viewModel(
                        factory = CarritoViewModelFactory(
                            application,
                            carritoRepository,
                            ordenRepository
                        )
                    )

                    // NavGraph: Define todas las pantallas y rutas
                    NavGraph(
                        navController = navController,
                        usuarioRepository = usuarioRepository, // PASADO
                        productoRepository = productoRepository,
                        carritoRepository = carritoRepository,
                        ordenRepository = ordenRepository,
                        productoViewModel = productoViewModel,
                        ordenViewModel = ordenViewModel,
                        carritoViewModel = carritoViewModel,
                        preferenciasManager = preferenciasManager,
                    )
                }
            }
        }
    }
}