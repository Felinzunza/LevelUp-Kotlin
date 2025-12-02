package com.example.levelUpKotlinProject.data.repository

import com.example.levelUpKotlinProject.data.local.dao.ProductoDao
import com.example.levelUpKotlinProject.data.local.entity.toEntity
import com.example.levelUpKotlinProject.data.local.entity.toProducto
import com.example.levelUpKotlinProject.data.remote.api.ProductoApiService
import com.example.levelUpKotlinProject.domain.model.Producto
import com.example.levelUpKotlinProject.domain.repository.RepositorioProductos

import android.util.Log
import com.example.levelUpKotlinProject.data.remote.dto.aDto
import com.example.levelUpKotlinProject.data.remote.dto.aModelo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
/*/**
 * Implementación del repositorio de productos con soporte para API REST y cache local
 *
 * Este repositorio implementa el patrón Repository con estrategia híbrida:
 * - Fuente primaria: API REST (Retrofit)
 * - Fuente secundaria: Base de datos local (Room)
 *
 * Estrategia de fallback implementada:
 * 1. Intenta obtener datos de la API
 * 2. Si la API falla (sin internet, timeout, error servidor), usa datos locales
 * 3. Siempre hay un plan B para garantizar funcionamiento offline
 *
 * Ventajas de esta arquitectura:
 * - La app funciona sin internet (usando cache)
 * - Datos siempre actualizados cuando hay conexión
 * - Experiencia de usuario consistente
 * - Fácil cambiar fuente de datos sin afectar ViewModels
 *
 * @property productoDao DAO para acceso a base de datos local
 * @property apiService Servicio para peticiones HTTP a la API
 *
 * @author Sting Parra Silva
 * @version 2.0
 */
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val apiService: ProductoApiService
) : RepositorioProductos {

    companion object {
        private const val TAG = "ProductoRepository"
    }

    /**
     * Obtiene la lista de productos con estrategia API-first + fallback local
     *
     * Flujo de ejecución:
     * 1. Realiza petición GET a la API
     * 2. Si la respuesta es exitosa (código 2xx):
     *    - Mapea ProductoDto a Producto
     *    - Emite la lista al Flow
     * 3. Si la API falla por cualquier motivo:
     *    - Captura la excepción
     *    - Obtiene datos de Room como fallback
     *    - Emite la lista local
     *
     * Excepciones manejadas:
     * - IOException: Problemas de red (sin internet, timeout)
     * - UnknownHostException: No se puede resolver el host
     * - Exception: Cualquier otro error inesperado
     *
     * @return Flow que emite lista de productos (de API o local)
     */
    override fun obtenerProductos(): Flow<List<Producto>> = flow {
        try {
            Log.d(TAG, "Intentando obtener productos desde API REST...")

            // Realizar petición HTTP GET a la API
            val respuesta = apiService.obtenerTodosLosProductos()

            // Verificar si la respuesta fue exitosa (código 200-299)
            if (respuesta.isSuccessful) {
                val cuerpoRespuesta = respuesta.body()

                if (cuerpoRespuesta != null) {
                    // Mapear lista de DTOs a lista de modelos de dominio
                    val listaProductos = cuerpoRespuesta.map { productoDto ->
                        productoDto.aModelo()
                    }

                    Log.d(
                        TAG,
                        "✓ Productos obtenidos de API exitosamente: ${listaProductos.size} items"
                    )

                    // Opcional: Guardar en cache local para futuras consultas offline
                    // Descomentar las siguientes líneas si quieres implementar cache
                    // val entidades = listaProductos.map { it.toEntity() }
                    // productoDao.eliminarTodosLosProductos()
                    // productoDao.insertarProductos(entidades)
                    // Log.d(TAG, "Cache local actualizado")

                    emit(listaProductos)

                } else {
                    // Respuesta exitosa pero sin datos (edge case)
                    Log.w(TAG, "⚠ Respuesta HTTP exitosa pero cuerpo vacío, usando datos locales")
                    usarDatosLocales(this)
                }

            } else {
                // Error HTTP (4xx, 5xx)
                Log.w(
                    TAG,
                    "⚠ Error HTTP ${respuesta.code()}: ${respuesta.message()}, usando datos locales"
                )
                usarDatosLocales(this)
            }

        } catch (excepcion: UnknownHostException) {
            // No se puede resolver el host (sin internet o URL incorrecta)
            Log.e(TAG, "✗ Sin conexión a internet o host inválido, usando datos locales")
            usarDatosLocales(this)

        } catch (excepcion: IOException) {
            // Error de red genérico (timeout, conexión cerrada, etc)
            Log.e(TAG, "✗ Error de red: ${excepcion.message}, usando datos locales")
            usarDatosLocales(this)

        } catch (excepcion: Exception) {
            // Cualquier otro error inesperado (parsing JSON, etc)
            Log.e(
                TAG,
                "✗ Error inesperado: ${excepcion.javaClass.simpleName} - ${excepcion.message}"
            )
            Log.e(TAG, "Usando datos locales como fallback")
            usarDatosLocales(this)
        }
    }

    /**
     * Función auxiliar para obtener datos desde la base de datos local
     *
     * Se usa como fallback cuando la API no está disponible.
     * Evita duplicación de código en los múltiples catch blocks.
     *
     * @param flowCollector Colector del Flow para emitir los datos
     */
    private suspend fun usarDatosLocales(
        flowCollector: kotlinx.coroutines.flow.FlowCollector<List<Producto>>
    ) {
        productoDao.obtenerTodosLosProductos().collect { listaEntidades ->
            val productosLocales = listaEntidades.map { entidad ->
                entidad.toProducto()
            }

            if (productosLocales.isEmpty()) {
                Log.w(TAG, "Base de datos local está vacía")
            } else {
                Log.d(TAG, "✓ Productos obtenidos de cache local: ${productosLocales.size} items")
            }

            flowCollector.emit(productosLocales)
        }
    }

    /**
     * Obtiene un producto específico por su ID
     *
     * Estrategia:
     * 1. Intenta obtener de la API usando el endpoint GET /products/{id}
     * 2. Si falla, busca en la base de datos local
     *
     * @param id Identificador único del producto
     * @return Producto encontrado o null si no existe
     */
    override suspend fun obtenerProductoPorId(id: Int): Producto? {
        return try {
            Log.d(TAG, "Buscando producto con ID: $id en API...")

            val respuesta = apiService.obtenerProductoPorId(id)

            if (respuesta.isSuccessful && respuesta.body() != null) {
                val productoDto = respuesta.body()!!
                val producto = productoDto.aModelo()

                Log.d(TAG, "✓ Producto encontrado en API: ${producto.nombre}")
                producto

            } else {
                // Producto no encontrado en API, buscar localmente
                Log.w(
                    TAG,
                    "⚠ Producto no encontrado en API (HTTP ${respuesta.code()}), buscando localmente..."
                )
                val entidad = productoDao.obtenerProductoPorId(id)
                entidad?.toProducto()
            }

        } catch (excepcion: Exception) {
            // Error de red, buscar en base de datos local
            Log.e(TAG, "✗ Error al buscar en API: ${excepcion.message}, buscando localmente...")
            val entidad = productoDao.obtenerProductoPorId(id)
            entidad?.toProducto()
        }
    }

    /**
     * Inserta una lista de productos en la base de datos local
     *
     * Esta función solo afecta la cache local, no envía datos a la API.
     * Útil para inicializar datos de ejemplo o sincronizar desde API.
     *
     * @param productos Lista de productos a insertar
     */
    override suspend fun insertarProductos(productos: List<Producto>) {
        val entidades = productos.map { it.toEntity() }
        productoDao.insertarProductos(entidades)
        Log.d(TAG, "✓ ${productos.size} productos insertados en cache local")
    }

    /**
     * Crea un nuevo producto tanto en la API como localmente
     *
     * Estrategia:
     * 1. Intenta crear el producto en la API (POST)
     * 2. Si es exitoso, también lo guarda localmente
     * 3. Si falla, solo lo guarda localmente
     *
     * Nota: FakeStoreAPI simula la creación pero no persiste datos.
     * Con JSON Server los datos sí se persisten realmente.
     *
     * @param producto Producto a crear
     * @return ID del producto insertado localmente
     */
    override suspend fun insertarProducto(producto: Producto): Long {
        return try {
            Log.d(TAG, "Creando producto: ${producto.nombre} en API...")

            // Convertir modelo de dominio a DTO para enviar a API
            val productoDto = producto.aDto()
            val respuesta = apiService.agregarProducto(productoDto)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto creado exitosamente en API")

                // Guardar también en base de datos local
                val idLocal = productoDao.insertarProducto(producto.toEntity())
                Log.d(TAG, "✓ Producto guardado en cache local con ID: $idLocal")
                idLocal

            } else {
                // Error en API, guardar solo localmente
                Log.w(
                    TAG,
                    "⚠ Error al crear en API (HTTP ${respuesta.code()}), guardando solo localmente"
                )
                productoDao.insertarProducto(producto.toEntity())
            }

        } catch (excepcion: Exception) {
            // Error de red, guardar solo localmente
            Log.e(TAG, "✗ Error de red al crear producto: ${excepcion.message}")
            Log.d(TAG, "Guardando producto solo en cache local")
            productoDao.insertarProducto(producto.toEntity())
        }
    }

    /**
     * Actualiza un producto existente en API y localmente
     *
     * Estrategia:
     * 1. Intenta actualizar en la API (PUT)
     * 2. Actualiza en base de datos local independientemente del resultado API
     *
     * @param producto Producto con datos actualizados
     */
    override suspend fun actualizarProducto(producto: Producto) {
        try {
            Log.d(TAG, "Actualizando producto ID: ${producto.id} - ${producto.nombre}")

            // Enviar actualización a la API
            val productoDto = producto.aDto()
            val respuesta = apiService.modificarProducto(producto.id, productoDto)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto actualizado exitosamente en API")
            } else {
                Log.w(TAG, "⚠ Error al actualizar en API (HTTP ${respuesta.code()})")
            }

        } catch (excepcion: Exception) {
            Log.e(TAG, "✗ Error al actualizar en API: ${excepcion.message}")
        } finally {
            // SIEMPRE actualizar en cache local (aunque falle API)
            productoDao.actualizarProducto(producto.toEntity())
            Log.d(TAG, "✓ Producto actualizado en cache local")
        }
    }

    /**
     * Elimina un producto de API y base de datos local
     *
     * Estrategia:
     * 1. Intenta eliminar de la API (DELETE)
     * 2. Elimina de base de datos local independientemente del resultado API
     *
     * @param producto Producto a eliminar
     */
    override suspend fun eliminarProducto(producto: Producto) {
        try {
            Log.d(TAG, "Eliminando producto ID: ${producto.id} - ${producto.nombre}")

            // Enviar solicitud de eliminación a la API
            val respuesta = apiService.borrarProducto(producto.id)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto eliminado exitosamente de API")
            } else {
                Log.w(TAG, "⚠ Error al eliminar de API (HTTP ${respuesta.code()})")
            }

        } catch (excepcion: Exception) {
            Log.e(TAG, "✗ Error al eliminar de API: ${excepcion.message}")
        } finally {
            // SIEMPRE eliminar de cache local (aunque falle API)
            productoDao.eliminarProducto(producto.toEntity())
            Log.d(TAG, "✓ Producto eliminado de cache local")
        }
    }

    /**
     * Elimina todos los productos de la base de datos local
     *
     * Esta operación NO afecta la API, solo limpia la cache local.
     * Útil para resetear la aplicación o forzar recarga desde API.
     */
    override suspend fun eliminarTodosLosProductos() {
        productoDao.eliminarTodosLosProductos()
        Log.d(TAG, "✓ Todos los productos eliminados de cache local")
    }
}*/

class ProductoRepository(
    private val productoDao: ProductoDao,
    private val apiService: ProductoApiService
) {
    companion object {
        private const val TAG = "ProductoRepository"
    }

    // LECTURA REACTIVA
    fun obtenerProductos(): Flow<List<Producto>> = flow {
        // Sincronización API -> Local en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.obtenerTodosLosProductos()
                if (response.isSuccessful && response.body() != null) {
                    val productosRemotos = response.body()!!.map { it.aModelo() }

                    // Insertamos usando los IDs del servidor.
                    // Al ser OnConflictStrategy.REPLACE (por defecto en muchos daos) o manual,
                    // esto sobrescribe lo local con la verdad del servidor.
                    productoDao.insertarProductos(productosRemotos.map { it.toEntity() })

                    Log.d(TAG, "✓ Sincronización exitosa: ${productosRemotos.size} productos")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sincronizando: ${e.message}")
            }
        }

        // Fuente de Verdad: Base de datos Local
        val flowLocal = productoDao.obtenerTodosLosProductos().map { list ->
            list.map { it.toProducto() }
        }
        emitAll(flowLocal)
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerProductoPorId(id)?.toProducto()
    }

    // --- CRUD ---

    /**
     *ya hice el cambio en el productapiservice y ahora le quite el id al agregarProducto
     */
    suspend fun insertarProducto(producto: Producto): Long {
        return try {
            Log.d(TAG, "Enviando producto a API...")
            val response = apiService.agregarProducto(producto.aDto()) //le saque del parametro el id

            if (response.isSuccessful && response.body() != null) {
                // 1. Obtenemos el producto YA CREADO por el servidor (con ID real)
                val productoCreado = response.body()!!.aModelo()
                Log.d(TAG, "✓ Producto creado en API con ID: ${productoCreado.id}")

                // 2. Guardamos en local ESE producto (con el ID del servidor)
                productoDao.insertarProducto(productoCreado.toEntity())

                productoCreado.id.toLong()
            } else {
                Log.e(TAG, "Error API (${response.code()}). Guardando localmente.")
                // Fallback: Si la API falla, guardamos el local (el ID será el que traiga o 0)
                // Nota: Esto podría causar desincronización si no se maneja cola de subida luego.
                productoDao.insertarProducto(producto.toEntity())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción red: ${e.message}")
            productoDao.insertarProducto(producto.toEntity())
        }
    }

    suspend fun actualizarProducto(producto: Producto) {
        // 1. Actualización Optimista (Local)
        productoDao.actualizarProducto(producto.toEntity())

        try {
            Log.d(TAG, "Actualizando ID ${producto.id} en API...")
            // Importante: producto.id debe ser el ID del servidor
            val response = apiService.modificarProducto(producto.id, producto.aDto())

            if (response.isSuccessful) {
                Log.d(TAG, "✓ Actualizado en servidor")
            } else {
                Log.e(TAG, "Error API Update: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error red Update: ${e.message}")
        }
    }

    suspend fun eliminarProducto(producto: Producto) {
        // 1. Borrado Optimista (Local)
        productoDao.eliminarProducto(producto.toEntity())

        try {
            Log.d(TAG, "Eliminando ID ${producto.id} en API...")
            val response = apiService.borrarProducto(producto.id)

            if (response.isSuccessful) {
                Log.d(TAG, "✓ Eliminado en servidor")
            } else {
                Log.e(TAG, "Error API Delete: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error red Delete: ${e.message}")
        }
    }
}