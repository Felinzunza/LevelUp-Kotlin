package com.example.levelUpKotlinProject.ui.viewmodel

import com.example.levelUpKotlinProject.ui.state.ProductoUiState
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios simples para ProductoViewModel - Optimizados para educación
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
class ProductoViewModelTest {

    @Test
    fun `ProductoUiState - creación básica funciona correctamente`() {
        // Test del estado UI básico
        val uiState = ProductoUiState()
        
        // Verificar valores por defecto
        assertFalse(uiState.estaCargando)
        assertTrue(uiState.productos.isEmpty())
        assertNull(uiState.error)
        assertFalse(uiState.hayProductos)
    }

    @Test
    fun `ProductoUiState - con productos funciona correctamente`() {
        // Test con productos
        val productos = listOf(
            com.example.levelUpKotlinProject.domain.model.Producto(
                id = 1,
                nombre = "Mouse",
                descripcion = "Mouse gamer",
                precio = 25000.0,
                imagenUrl = "url",
                categoria = "Periféricos",
                stock = 10
            )
        )
        
        val uiState = ProductoUiState(
            productos = productos,
            estaCargando = false
        )
        
        // Verificar estado con productos
        assertEquals(1, uiState.productos.size)
        assertEquals("Mouse", uiState.productos.first().nombre)
        assertTrue(uiState.hayProductos)
        assertFalse(uiState.estaCargando)
    }

    @Test
    fun `ProductoUiState - estado de carga funciona correctamente`() {
        // Test estado de carga
        val uiState = ProductoUiState(
            estaCargando = true,
            productos = emptyList()
        )
        
        // Verificar estado de carga
        assertTrue(uiState.estaCargando)
        assertTrue(uiState.productos.isEmpty())
        assertFalse(uiState.hayProductos)
    }

    @Test
    fun `ProductoUiState - con error funciona correctamente`() {
        // Test estado con error
        val errorMessage = "Error de conexión"
        val uiState = ProductoUiState(
            error = errorMessage,
            productos = emptyList()
        )
        
        // Verificar estado con error
        assertEquals(errorMessage, uiState.error)
        assertTrue(uiState.productos.isEmpty())
        assertFalse(uiState.hayProductos)
        assertFalse(uiState.estaCargando)
    }

    @Test
    fun `ProductoUiState - copia funciona correctamente`() {
        // Test de copia de estado
        val uiStateOriginal = ProductoUiState(
            productos = listOf(
                com.example.levelUpKotlinProject.domain.model.Producto(
                    id = 1,
                    nombre = "Mouse Original",
                    descripcion = "Descripción original",
                    precio = 25000.0,
                    imagenUrl = "url_original",
                    categoria = "Periféricos",
                    stock = 10
                )
            ),
            estaCargando = false
        )
        
        // Usar data class copy
        val uiStateModificado = uiStateOriginal.copy(
            estaCargando = true,
            error = "Error de prueba"
        )
        
        // Verificar que el original no cambió
        assertFalse(uiStateOriginal.estaCargando)
        assertNull(uiStateOriginal.error)
        
        // Verificar que la copia tiene los valores nuevos
        assertTrue(uiStateModificado.estaCargando)
        assertEquals("Error de prueba", uiStateModificado.error)
        assertEquals("Mouse Original", uiStateModificado.productos.first().nombre)
    }
}
