package com.example.levelUpKotlinProject.data.local.entity

import com.example.levelUpKotlinProject.domain.model.EstadoOrden
import com.example.levelUpKotlinProject.domain.model.Orden
import com.example.levelUpKotlinProject.domain.model.TipoCompra
import com.example.levelUpKotlinProject.domain.model.TipoCourier
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class OrdenEntityTest {

    @Test
    fun `Orden - conversion a entidad guarda los Enums como String`() {
        // GIVEN
        val orden = Orden(
            id = "500L",
            rut = "1-9",
            nombreCliente = "Cliente",
            fechaCreacion = Date(),
            estado = EstadoOrden.EN_PREPARACION, // Enum
            direccionEnvio = "Dir",
            metodoPago = TipoCompra.TARJETA_CREDITO, // Enum
            courier = TipoCourier.CHILEXPRESS, // Enum
            subtotal = 100.0,
            costoEnvio = 10.0,
            descuento = 0.0,
            total = 110.0,
            items = emptyList()
        )

        // WHEN
        val entity = orden.toEntity()

        // THEN
        assertEquals("500L", entity.id)
        assertEquals("EN_PREPARACION", entity.estado) // Verificamos que sea String
        assertEquals("TARJETA_CREDITO", entity.tipoCompra)
    }
}