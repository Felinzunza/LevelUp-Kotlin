package com.example.levelUpKotlinProject.data.repository

import com.example.levelUpKotlinProject.data.local.entity.ProductoEntity
import com.example.levelUpKotlinProject.data.remote.dto.ProductoDto
import com.example.levelUpKotlinProject.domain.model.Producto
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios simples para ProductoRepositoryImpl - Optimizados para educación
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
class ProductoRepositoryImplTest {

    @Test
    fun `ProductoEntity - creación básica funciona correctamente`() {
        // Test de entidad básica
        val productoEntity = ProductoEntity(
            id = "1",
            nombre = "Mouse Gamer",
            descripcion = "Mouse RGB inalámbrico",
            precio = 25000.0,
            imagenUrl = "https://ejemplo.com/mouse.jpg",
            categoria = "Periféricos",
            stock = 50
        )

        // Verificar valores
        assertEquals("1", productoEntity.id)
        assertEquals("Mouse Gamer", productoEntity.nombre)
        assertEquals("Mouse RGB inalámbrico", productoEntity.descripcion)
        assertEquals(25000.0, productoEntity.precio, 0.01)
        assertEquals("https://ejemplo.com/mouse.jpg", productoEntity.imagenUrl)
        assertEquals("Periféricos", productoEntity.categoria)
        assertEquals(50, productoEntity.stock)
    }

    @Test
    fun `ProductoDto - creación básica funciona correctamente`() {
        // Test de DTO básico
        val productoDto = ProductoDto(
            id = "1",
            titulo = "Mouse Gamer",
            descripcion = "Mouse RGB inalámbrico",
            precio = 25000.0,
            urlImagen = "https://ejemplo.com/mouse.jpg",
            categoria = "Periféricos"
        )

        // Verificar valores
        assertEquals("1", productoDto.id)
        assertEquals("Mouse Gamer", productoDto.titulo)
        assertEquals("Mouse RGB inalámbrico", productoDto.descripcion)
        assertEquals(25000.0, productoDto.precio, 0.01)
        assertEquals("https://ejemplo.com/mouse.jpg", productoDto.urlImagen)
        assertEquals("Periféricos", productoDto.categoria)
    }

    @Test
    fun `ProductoDomain - creación básica funciona correctamente`() {
        // Test de modelo de dominio básico
        val producto = Producto(
            id = "1",
            nombre = "Mouse Gamer",
            descripcion = "Mouse RGB inalámbrico",
            precio = 25000.0,
            imagenUrl = "https://ejemplo.com/mouse.jpg",
            categoria = "Periféricos",
            stock = 50
        )

        // Verificar valores
        assertEquals("1", producto.id)
        assertEquals("Mouse Gamer", producto.nombre)
        assertEquals("Mouse RGB inalámbrico", producto.descripcion)
        assertEquals(25000.0, producto.precio, 0.01)
        assertEquals("https://ejemplo.com/mouse.jpg", producto.imagenUrl)
        assertEquals("Periféricos", producto.categoria)
        assertEquals(50, producto.stock)
    }

    @Test
    fun `ProductoEntity - equals funciona correctamente`() {
        val productoEntity1 = ProductoEntity(
            id = "1",
            nombre = "Producto 1",
            descripcion = "Descripción 1",
            precio = 100.0,
            imagenUrl = "url1",
            categoria = "cat1",
            stock = 10
        )

        val productoEntity2 = ProductoEntity(
            id = "1",
            nombre = "Producto 1",
            descripcion = "Descripción 1",
            precio = 100.0,
            imagenUrl = "url1",
            categoria = "cat1",
            stock = 10
        )

        assertEquals(productoEntity1, productoEntity2)
    }

    @Test
    fun `ProductoDomain - equals funciona correctamente`() {
        val producto1 = Producto(
            id = "1",
            nombre = "Producto 1",
            descripcion = "Descripción 1",
            precio = 100.0,
            imagenUrl = "url1",
            categoria = "cat1",
            stock = 10
        )

        val producto2 = Producto(
            id = "1",
            nombre = "Producto 1",
            descripcion = "Descripción 1",
            precio = 100.0,
            imagenUrl = "url1",
            categoria = "cat1",
            stock = 10
        )

        assertEquals(producto1, producto2)
    }

    @Test
    fun `ProductoDomain - copia funciona correctamente`() {
        val productoOriginal = Producto(
            id = "1",
            nombre = "Original",
            descripcion = "Descripción original",
            precio = 100.0,
            imagenUrl = "url_original",
            categoria = "categoria_original",
            stock = 10
        )

        // Usar data class copy
        val productoModificado = productoOriginal.copy(
            nombre = "Modificado",
            precio = 150.0
        )

        // Verificar que el original no cambió
        assertEquals("Original", productoOriginal.nombre)
        assertEquals(100.0, productoOriginal.precio, 0.01)

        // Verificar que la copia tiene los valores nuevos
        assertEquals("Modificado", productoModificado.nombre)
        assertEquals(150.0, productoModificado.precio, 0.01)
        assertEquals("Descripción original", productoModificado.descripcion)
    }

    @Test
    fun `transformación de datos - manejo de precios edge cases funciona correctamente`() {
        // Test con precio cero
        val productoGratis = Producto(
            id = "1",
            nombre = "Gratis",
            descripcion = "Producto gratuito",
            precio = 0.0,
            imagenUrl = "url",
            categoria = "cat",
            stock = 0
        )

        assertEquals(0.0, productoGratis.precio, 0.01)
        assertEquals(0, productoGratis.stock)

        // Test con precio grande
        val productoCaro = Producto(
            id = "2",
            nombre = "Caro",
            descripcion = "Producto muy caro",
            precio = 999999.99,
            imagenUrl = "url",
            categoria = "cat",
            stock = 1000
        )

        assertEquals(999999.99, productoCaro.precio, 0.01)
        assertEquals(1000, productoCaro.stock)
    }
}
