package com.example.levelUpKotlinProject.data.remote.dto

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios simples para ProductoDto - Optimizados para educación
 * 
 */
class ProductoDtoTest {

    @Test
    fun `ProductoDto - creación básica funciona correctamente`() {
        // Crear DTO básico
        val productoDto = ProductoDto(
            identificador = 1,
            titulo = "Mouse Gamer",
            descripcion = "Mouse RGB inalámbrico",
            precio = 25000.0,
            urlImagen = "https://ejemplo.com/mouse.jpg",
            categoria = "Periféricos"
        )

        // Verificar valores básicos
        assertEquals(1, productoDto.identificador)
        assertEquals("Mouse Gamer", productoDto.titulo)
        assertEquals("Mouse RGB inalámbrico", productoDto.descripcion)
        assertEquals(25000.0, productoDto.precio, 0.01) // Delta para Double
        assertEquals("https://ejemplo.com/mouse.jpg", productoDto.urlImagen)
        assertEquals("Periféricos", productoDto.categoria)
    }

    @Test
    fun `ProductoDto - conversión a modelo funciona correctamente`() {
        // Test de conversión a modelo de dominio
        val productoDto = ProductoDto(
            identificador = 1,
            titulo = "Mouse Gamer",
            descripcion = "Mouse RGB",
            precio = 25000.0,
            urlImagen = "https://ejemplo.com/mouse.jpg",
            categoria = "Periféricos"
        )

        // Convertir a modelo
        val producto = productoDto.aModelo()

        // Verificar conversión básica
        assertEquals(1, producto.id)
        assertEquals("Mouse Gamer", producto.nombre)
        assertEquals("Mouse RGB", producto.descripcion)
        assertEquals(25000.0, producto.precio, 0.01) // Delta para Double
        assertEquals("https://ejemplo.com/mouse.jpg", producto.imagenUrl)
        assertEquals("Periféricos", producto.categoria)
        assertEquals(10, producto.stock) // Stock por defecto
    }


    @Test
    fun `ProductoDto - equals funciona correctamente`() {
        val productoDto1 = ProductoDto(
            identificador = 1,
            titulo = "Producto 1",
            descripcion = "Descripción 1",
            precio = 100.0,
            urlImagen = "url1",
            categoria = "cat1"
        )

        val productoDto2 = ProductoDto(
            identificador = 1,
            titulo = "Producto 1",
            descripcion = "Descripción 1",
            precio = 100.0,
            urlImagen = "url1",
            categoria = "cat1"
        )

        assertEquals(productoDto1, productoDto2)
    }

    @Test
    fun `ProductoDto - manejo de precio cero funciona`() {
        val productoDto = ProductoDto(
            identificador = 2,
            titulo = "Producto gratis",
            descripcion = "Descripción",
            precio = 0.0,
            urlImagen = "url",
            categoria = "cat"
        )

        assertEquals(0.0, productoDto.precio, 0.01)
    }

    @Test
    fun `ProductoDto - copia funciona correctamente`() {
        val productoDtoOriginal = ProductoDto(
            identificador = 1,
            titulo = "Original",
            descripcion = "Descripción original",
            precio = 100.0,
            urlImagen = "url_original",
            categoria = "categoria_original"
        )

        // Usar data class copy
        val productoDtoModificado = productoDtoOriginal.copy(
            titulo = "Modificado",
            precio = 150.0
        )

        // Verificar que el original no cambió
        assertEquals("Original", productoDtoOriginal.titulo)
        assertEquals(100.0, productoDtoOriginal.precio, 0.01)

        // Verificar que la copia tiene los valores nuevos
        assertEquals("Modificado", productoDtoModificado.titulo)
        assertEquals(150.0, productoDtoModificado.precio, 0.01)
        assertEquals("Descripción original", productoDtoModificado.descripcion)
    }
}
