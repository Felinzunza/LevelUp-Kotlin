package com.example.levelUpKotlinProyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.levelUpKotlinProyect.data.local.AppDatabase
import com.example.levelUpKotlinProyect.data.local.PreferenciasManager
import com.example.levelUpKotlinProyect.data.local.ProductoInicializador
import com.example.levelUpKotlinProyect.data.repository.CarritoRepository
import com.example.levelUpKotlinProyect.data.repository.ProductoRepositoryImpl
import com.example.levelUpKotlinProyect.ui.navigation.NavGraph
import com.example.levelUpKotlinProyect.ui.viewmodel.ProductoViewModel
import com.example.levelUpKotlinProyect.ui.viewmodel.ProductoViewModelFactory

/**
 * MainActivity: Punto de entrada de la aplicación
 * 
 * Responsabilidades:
 * - Crear la base de datos
 * - Crear los repositorios
 * - Configurar el sistema de navegación
 * 

 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Crear base de datos
        val database = AppDatabase.getDatabase(applicationContext)
        
        // Inicializar productos de ejemplo (solo primera vez)
        ProductoInicializador.inicializarProductos(applicationContext)
        
        // Crear repositorios
        val productoRepository = ProductoRepositoryImpl(database.productoDao())
        val carritoRepository = CarritoRepository(database.carritoDao())
        
        // Crear PreferenciasManager para sesión admin
        val preferenciasManager = PreferenciasManager(applicationContext)

        setContent {
            MaterialTheme {
                Surface {
                    // Crear NavController para gestionar navegación
                    val navController = rememberNavController()
                    
                    // Crear ViewModel con Factory
                    val productoViewModel: ProductoViewModel = viewModel(
                        factory = ProductoViewModelFactory(productoRepository)
                    )
                    
                    // NavGraph: Define todas las pantallas y rutas
                    NavGraph(
                        navController = navController,
                        productoRepository = productoRepository,
                        carritoRepository = carritoRepository,
                        preferenciasManager = preferenciasManager,
                        productoViewModel = productoViewModel
                    )
                }
            }
        }
    }
}