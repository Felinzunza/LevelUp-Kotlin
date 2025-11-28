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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.UnknownHostException
/**
 * Implementación del repositorio de productos
 * Traduce entre entidades Room y modelos del dominio
 **/
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val apiService: ProductoApiService
) : RepositorioProductos {

    companion object {
        private const val TAG = "ProductoRepository"
    }

    // Obtener productos desde la API REST
    override fun obtenerProductos(): Flow<List<Producto>> = flow {
        try {
            Log.d(TAG, "Intentando obtener productos desde API REST...")

            // Hacer peticion a la API
            val respuesta = apiService.obtenerTodosLosProductos()

            // Verificar si fue exitosa
            if (respuesta.isSuccessful) {
                val cuerpoRespuesta = respuesta.body()

                if (cuerpoRespuesta != null) {
                    // Mapear DTOs a modelos de dominio
                    val listaProductos = cuerpoRespuesta.map{ it.aModelo() }

                    Log.d(TAG, "✓ Productos obtenidos de API: ${listaProductos.size} items")
                    emit(listaProductos)

                } else {
                    // Respuesta exitosa pero sin datos
                    Log.w(TAG, "⚠ Respuesta vacía, usando datos locales")
                    usarDatosLocales(this)
                }

            } else {
                // Error HTTP (4xx, 5xx)
                Log.w(TAG, "⚠ Error HTTP ${respuesta.code()}, usando datos locales")
                usarDatosLocales(this)
            }

        } catch (excepcion: UnknownHostException) {
            // Sin internet o host invalido
            Log.e(TAG, "✗ Sin conexion a internet, usando datos locales")
            usarDatosLocales(this)

        } catch (excepcion: IOException) {
            // Error de red (timeout, etc)
            Log.e(TAG, "✗ Error de red, usando datos locales")
            usarDatosLocales(this)

        } catch (excepcion: Exception) {
            // Error inesperado
            Log.e(TAG, "✗ Error inesperado: ${excepcion.message}")
            usarDatosLocales(this)
        }
    }

    // Obtener productos desde la base de datos local
    private suspend fun usarDatosLocales(
        flowCollector: kotlinx.coroutines.flow.FlowCollector<List<Producto>>
    ) {
        productoDao.obtenerTodosLosProductos().collect { listaEntidades ->
            val productosLocales = listaEntidades.map { it.toProducto() }

            if (productosLocales.isEmpty()) {
                Log.w(TAG, "Base de datos local está vacía")
            } else {
                Log.d(TAG, "✓ Productos de cache local: ${productosLocales.size} items")
            }

            flowCollector.emit(productosLocales)
        }


    }


    override suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerProductoPorId(id)?.toProducto()
    }

    override suspend fun insertarProductos(productos: List<Producto>) {
        val entities = productos.map { it.toEntity() }
        productoDao.insertarProductos(entities)
    }



    override suspend fun insertarProducto(producto: Producto): Long {
        return try {
            Log.d(TAG, "Creando producto: ${producto.nombre} en API...")

            val productoDto = producto.aDto()
            val respuesta = apiService.agregarProducto(productoDto)

            if (respuesta.isSuccessful) {
                Log.d(TAG, "✓ Producto creado en API")
                val idLocal = productoDao.insertarProducto(producto.toEntity())
                Log.d(TAG, "✓ Producto guardado localmente con ID: $idLocal")
                idLocal
            } else {
                Log.w(TAG, "⚠ Error en API, guardando solo localmente")
                productoDao.insertarProducto(producto.toEntity())
            }

        } catch (excepcion: Exception) {
            Log.e(TAG, "✗ Error de red, guardando solo localmente")
            productoDao.insertarProducto(producto.toEntity())
        }
    }
    
    override suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto.toEntity())
    }
    
    override suspend fun eliminarProducto(producto: Producto) {
        productoDao.eliminarProducto(producto.toEntity())
    }
    
    override suspend fun eliminarTodosLosProductos() {
        productoDao.eliminarTodosLosProductos()
    }
}
