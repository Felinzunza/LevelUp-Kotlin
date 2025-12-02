package com.example.levelUpKotlinProject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.levelUpKotlinProject.data.local.AppDatabase
import com.example.levelUpKotlinProject.data.local.PreferenciasManager
import com.example.levelUpKotlinProject.data.local.ProductoInicializador
import com.example.levelUpKotlinProject.data.local.UsuarioInicializador
import com.example.levelUpKotlinProject.data.remote.RetrofitClient
import com.example.levelUpKotlinProject.data.remote.api.OrdenApiService
import com.example.levelUpKotlinProject.data.remote.api.ProductoApiService
import com.example.levelUpKotlinProject.data.remote.api.UsuarioApiService
import com.example.levelUpKotlinProject.data.repository.CarritoRepository
import com.example.levelUpKotlinProject.data.repository.OrdenRepository
import com.example.levelUpKotlinProject.data.repository.ProductoRepository
import com.example.levelUpKotlinProject.data.repository.UsuarioRepository
import com.example.levelUpKotlinProject.ui.navigation.NavGraph
import com.example.levelUpKotlinProject.ui.theme.LevelUpKotlinProjectTheme
import com.example.levelUpKotlinProject.ui.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Crear base de datos (Síncrono - Singleton rápido)
        val database = AppDatabase.getDatabase(applicationContext)

        // 2. Inicialización ASÍNCRONA de datos (Previene ANR y bloqueos)
        lifecycleScope.launch {
            // Ambas funciones son 'suspend' y usan Dispatchers.IO internamente
            ProductoInicializador.inicializarProductos(applicationContext)
            UsuarioInicializador.inicializarAdmin(applicationContext)

        }

        // 3. Crear instancia del servicio API
        val apiServiceProduct: ProductoApiService = RetrofitClient.crearServicio(ProductoApiService::class.java)
        val apiServiceUser: UsuarioApiService = RetrofitClient.crearServicio(UsuarioApiService::class.java)
        val apiServiceOrden: OrdenApiService = RetrofitClient.crearServicio(OrdenApiService::class.java)



        //4. Crear repositorio con API y DAO
        val productoRepository = ProductoRepository(
            productoDao = database.productoDao(),
            apiService = apiServiceProduct
        )
        val usuarioRepository = UsuarioRepository(
            usuarioDao = database.usuarioDao(),
            apiService = apiServiceUser
        )
        val ordenRepository = OrdenRepository(
            ordenDao = database.ordenDao(),
            carritoDao = database.carritoDao(),
            apiService = apiServiceOrden
        )
        val carritoRepository = CarritoRepository(database.carritoDao())

        // 5. Crear PreferenciasManager
        val preferenciasManager = PreferenciasManager(applicationContext)

        //6. UI
        setContent {
            LevelUpKotlinProjectTheme {
                Surface {
                    val navController = rememberNavController()

                    // Crear ViewModels
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

                    val registroViewModel: RegistroViewModel = viewModel(
                        factory = RegistroViewModelFactory(usuarioRepository)
                    )

                    // Configurar Navegación
                    NavGraph(
                        navController = navController,
                        usuarioRepository = usuarioRepository,
                        productoRepository = productoRepository,
                        carritoRepository = carritoRepository,
                        ordenRepository = ordenRepository,
                        productoViewModel = productoViewModel,
                        ordenViewModel = ordenViewModel,
                        carritoViewModel = carritoViewModel,
                        registroViewModel = registroViewModel,
                        preferenciasManager = preferenciasManager,
                    )
                }
            }
        }
    }
}