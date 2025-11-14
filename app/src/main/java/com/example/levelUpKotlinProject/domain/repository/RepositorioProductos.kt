package com.example.levelUpKotlinProject.domain.repository

import com.example.levelUpKotlinProject.domain.model.Producto
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de productos
 * Define las operaciones sin implementación concreta
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
interface RepositorioProductos {
    
    /**
     * Obtiene todos los productos como Flow
     * Se actualiza automáticamente cuando cambia la base de datos
     */
    fun obtenerProductos(): Flow<List<Producto>>
    
    /**
     * Obtiene un producto por su ID
     */
    suspend fun obtenerProductoPorId(id: Int): Producto?
    
    /**
     * Inserta varios productos en la base de datos
     * Útil para cargar datos iniciales
     */
    suspend fun insertarProductos(productos: List<Producto>)
    
    /**
     * Inserta un solo producto
     * Retorna el ID asignado
     */
    suspend fun insertarProducto(producto: Producto): Long
    
    /**
     * Actualiza un producto existente
     */
    suspend fun actualizarProducto(producto: Producto)
    
    /**
     * Elimina un producto específico
     */
    suspend fun eliminarProducto(producto: Producto)
    
    /**
     * Elimina todos los productos
     */
    suspend fun eliminarTodosLosProductos()
}
