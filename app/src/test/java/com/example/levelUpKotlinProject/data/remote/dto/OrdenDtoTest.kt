package com.example.levelUpKotlinProject.data.remote.dto

import org.junit.Assert.*
import org.junit.Test

class OrdenDtoTest {

    @Test
    fun `OrdenDto - mapeo de IDs mixtos funciona (Orden String, Producto String)`() {
        // GIVEN: Un ItemDto como viene del servidor (Producto ID es texto)
        val itemDto = ItemOrdenDto(
            productoId = "prod_abc", // String
            ordenId = "1001L",         // String
            nombreProducto = "Teclado",
            imagenUrl = "img",
            precioUnitarioFijo = 5000.0,
            cantidad = 2
        )

        // WHEN
        val itemModelo = itemDto.aModelo()

        // THEN
        assertEquals("prod_abc", itemModelo.productoId) // Debe ser String
        assertEquals("1001L", itemModelo.ordenId) // Debe ser String
        assertEquals(10000.0, itemModelo.subtotal, 0.01) // 5000 * 2
    }
}