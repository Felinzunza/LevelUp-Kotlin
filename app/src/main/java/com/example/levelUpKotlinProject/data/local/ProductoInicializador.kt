package com.example.levelUpKotlinProject.data.local

import android.content.Context
import com.example.levelUpKotlinProject.domain.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ProductoInicializador: Carga productos de ejemplo en la BD
 * 
 * Se ejecuta la primera vez que se abre la app
 * Permite tener datos de prueba sin conectarse a una API
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
object ProductoInicializador {
    
    /**
     * Inserta productos de ejemplo si la base de datos está vacía
     */
    fun inicializarProductos(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val productoDao = database.productoDao()
        
        // Ejecutar en background (no bloquear la UI)
        CoroutineScope(Dispatchers.IO).launch {
            // Solo insertar si no hay productos
            val productosExistentes = productoDao.obtenerProductoPorId(1)
            if (productosExistentes == null) {
                val productosDeEjemplo = listOf(
                    Producto(
                        id = 1,
                        nombre = "Mouse Gamer RGB",
                        descripcion = "Mouse óptico profesional con 6 botones programables, sensor de 6400 DPI y retroiluminación RGB personalizable. Ideal para gaming y trabajo.",
                        precio = 25000.0,
                        imagenUrl = "mouse_gamer", // Nombre del archivo en drawable/
                        categoria = "Periféricos",
                        stock = 15
                    ),
                    Producto(
                        id = 2,
                        nombre = "Teclado Mecánico",
                        descripcion = "Teclado mecánico con switches azules, retroiluminación RGB por tecla, estructura de aluminio y reposamuñecas magnético.",
                        precio = 45000.0,
                        imagenUrl = "teclado_mecanico", // Nombre del archivo en drawable/
                        categoria = "Periféricos",
                        stock = 8
                    ),
                    Producto(
                        id = 3,
                        nombre = "Audífonos RGB",
                        descripcion = "Audífonos gaming over-ear con sonido envolvente 7.1, micrófono cancelación de ruido y almohadillas de espuma viscoelástica.",
                        precio = 35000.0,
                        imagenUrl = "audifonos_rgb", // Nombre del archivo en drawable/
                        categoria = "Audio",
                        stock = 12
                    ),
                    Producto(
                        id = 4,
                        nombre = "Webcam Full HD",
                        descripcion = "Cámara web 1080p 60fps con enfoque automático, micrófono estéreo integrado y corrección de luz baja.",
                        precio = 55000.0,
                        imagenUrl = "webcam_hd", // Nombre del archivo en drawable/
                        categoria = "Video",
                        stock = 5
                    ),
                    Producto(
                        id = 5,
                        nombre = "Monitor 24\" 144Hz",
                        descripcion = "Monitor gaming IPS de 24 pulgadas, tasa de refresco 144Hz, tiempo de respuesta 1ms, compatible con FreeSync.",
                        precio = 180000.0,
                        imagenUrl = "monitor_gaming", // Nombre del archivo en drawable/
                        categoria = "Monitores",
                        stock = 3
                    ),
                    Producto(
                        id = 6,
                        nombre = "SSD NVMe 1TB",
                        descripcion = "Disco sólido NVMe Gen4 de 1TB, velocidades de lectura hasta 7000 MB/s, ideal para gaming y creación de contenido.",
                        precio = 85000.0,
                        imagenUrl = "ssd_nvme", // Nombre del archivo en drawable/
                        categoria = "Almacenamiento",
                        stock = 20
                    ),
                    Producto(
                        id = 7,
                        nombre = "Silla Gamer Ergonómica",
                        descripcion = "Silla ergonómica con soporte lumbar ajustable, reposabrazos 4D, reclinación 180°, base de aluminio y ruedas de goma.",
                        precio = 120000.0,
                        imagenUrl = "silla_gamer", // Nombre del archivo en drawable/
                        categoria = "Mobiliario",
                        stock = 6
                    ),
                    Producto(
                        id = 8,
                        nombre = "Mousepad XXL",
                        descripcion = "Alfombrilla de escritorio tamaño XXL (90x40cm), superficie de tela suave, base antideslizante de goma natural.",
                        precio = 12000.0,
                        imagenUrl = "mousepad_xxl", // Nombre del archivo en drawable/
                        categoria = "Accesorios",
                        stock = 25
                    )
                )
                
                // Insertar en la base de datos
                productoDao.insertarProductos(productosDeEjemplo.map { it.toEntity() })
            }
        }
    }
}

// Extension function para convertir Producto a ProductoEntity
private fun Producto.toEntity() = com.example.levelUpKotlinProject.data.local.entity.ProductoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    imagenUrl = imagenUrl,
    categoria = categoria,
    stock = stock
)
