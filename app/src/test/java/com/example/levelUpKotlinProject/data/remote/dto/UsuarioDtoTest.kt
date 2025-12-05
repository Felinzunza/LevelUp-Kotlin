package com.example.levelUpKotlinProject.data.remote.dto

import com.example.levelUpKotlinProject.domain.model.Rol
import org.junit.Assert.*
import org.junit.Test

class UsuarioDtoTest {

    @Test
    fun `UsuarioDto - conversion a modelo es correcta`() {
        // GIVEN: Un DTO tal como llega de la API (ID String)
        val dto = UsuarioDto(
            id = "U100",
            rut = "12.345.678-9",
            nombre = "Juan",
            apellido = "Perez",
            username = "juanp",
            email = "juan@mail.com",
            password = "123",
            telefono = "999",
            direccion = "Calle 1",
            comuna = "Centro",
            region = "RM",
            fechaNacimiento = "1990-01-01",
            fechaRegistro = "2024-01-01",
            rol = "ADMIN"
        )

        // WHEN: Convertimos
        val modelo = dto.aModelo()

        // THEN: Verificamos que los datos clave se mantengan
        assertEquals("U100", modelo.id)
        assertEquals("Juan", modelo.nombre)
        assertEquals(Rol.ADMIN, modelo.rol)
        assertNotNull(modelo.fechaNacimiento) // Verifica que la fecha se parseó (no es null)
    }

    @Test
    fun `UsuarioDto - valores nulos se manejan con defaults`() {
        // GIVEN: DTO vacío o con nulos
        val dtoVacio = UsuarioDto(
            id = null,
            rut = "",
            nombre = "",
            apellido = "",
            username = "",
            email = "",
            password = "",
            telefono = null,
            direccion = "",
            comuna = "",
            region = "",
            fechaNacimiento = "",
            fechaRegistro = "",
            rol = ""
        )

        // WHEN
        val modelo = dtoVacio.aModelo()

        // THEN
        assertEquals("", modelo.id) // ID vacío por defecto
        assertEquals(Rol.USUARIO, modelo.rol) // Rol por defecto
    }
}