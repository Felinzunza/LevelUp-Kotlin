package com.example.levelUpKotlinProject.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class UsuarioTest {

    @Test
    fun `Usuario - creación con valores por defecto es correcta`() {
        // GIVEN: Un usuario creado solo con los datos obligatorios
        val usuario = Usuario(
            rut = "12.345.678-9",
            nombre = "Juan",
            apellido = "Pérez",
            fechaNacimiento = Date(),
            username = "juanp",
            email = "juan@test.com",
            password = "123",
            telefono = null,
            direccion = "Calle 1",
            comuna = "Centro",
            region = "RM",
            fechaRegistro = Date()
        )

        // THEN: Verificamos los valores por defecto
        assertEquals("", usuario.id) // ID debe ser vacío por defecto (String)
        assertEquals(Rol.USUARIO, usuario.rol) // Rol debe ser USUARIO por defecto
    }

    @Test
    fun `Usuario - integridad de datos al crear Admin`() {
        // GIVEN: Un usuario creado con rol ADMIN y ID específico
        val admin = Usuario(
            id = "admin_01",
            rut = "99.999.999-K",
            nombre = "Admin",
            apellido = "Sistema",
            fechaNacimiento = Date(),
            username = "admin",
            email = "admin@test.com",
            password = "pass",
            telefono = "123",
            direccion = "Oficina",
            comuna = "Centro",
            region = "RM",
            fechaRegistro = Date(),
            rol = Rol.ADMIN
        )

        // THEN
        assertEquals("admin_01", admin.id)
        assertEquals(Rol.ADMIN, admin.rol)
        assertEquals("Admin", admin.nombre)
    }
}