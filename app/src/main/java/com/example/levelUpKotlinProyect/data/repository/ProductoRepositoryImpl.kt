package com.example.levelUpKotlinProyect.data.repository

import com.example.levelUpKotlinProyect.data.local.dao.ProductoDao
import com.example.levelUpKotlinProyect.data.local.entity.toEntity
import com.example.levelUpKotlinProyect.data.local.entity.toProducto
import com.example.levelUpKotlinProyect.domain.model.Producto
import com.example.levelUpKotlinProyect.domain.repository.RepositorioProductos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de productos
 * Traduce entre entidades Room y modelos del dominio
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
class ProductoRepositoryImpl(
    private val productoDao: ProductoDao
) : RepositorioProductos {
    
    override fun obtenerProductos(): Flow<List<Producto>> {
        // Convierte Flow<List<ProductoEntity>> a Flow<List<Producto>>
        return productoDao.obtenerTodosLosProductos()
            .map { entities -> 
                entities.map { it.toProducto() }
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
        return productoDao.insertarProducto(producto.toEntity())
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
