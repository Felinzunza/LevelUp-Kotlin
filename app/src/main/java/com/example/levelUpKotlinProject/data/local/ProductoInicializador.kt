package com.example.levelUpKotlinProject.data.local

import android.content.Context
import com.example.levelUpKotlinProject.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Carga productos de ejemplo en la BD
 */
object ProductoInicializador {

    suspend fun inicializarProductos(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val productoDao = database.productoDao()

        // CRÍTICO: Ejecutar en hilo IO
        withContext(Dispatchers.IO) {
            val productosExistentes = productoDao.obtenerProductoPorId(1)
            if (productosExistentes == null) {
                val productosDeEjemplo = listOf(
                    Producto(
                        id = 1,
                        nombre = "Mouse Gamer RGB",
                        descripcion = "Mouse óptico profesional.",
                        precio = 25000.0,
                        imagenUrl = "mouse_gamer",
                        categoria = "Periféricos",
                        stock = 15
                    ),
                    // ... (Puedes agregar el resto de tus productos aquí si lo deseas)
                    Producto(
                        id = 2,
                        nombre = "Teclado Mecánico",
                        descripcion = "Teclado mecánico switches azules.",
                        precio = 45000.0,
                        imagenUrl = "teclado_mecanico",
                        categoria = "Periféricos",
                        stock = 8
                    )
                )

                productoDao.insertarProductos(productosDeEjemplo.map { it.toEntity() })
            }
        }
    }
}

private fun Producto.toEntity() = com.example.levelUpKotlinProject.data.local.entity.ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)