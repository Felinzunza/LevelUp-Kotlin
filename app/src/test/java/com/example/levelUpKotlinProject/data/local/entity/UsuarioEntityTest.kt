
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import com.example.levelUpKotlinProject.data.local.entity.toEntity
import com.example.levelUpKotlinProject.data.local.entity.toUsuario
import com.example.levelUpKotlinProject.domain.model.Rol
import com.example.levelUpKotlinProject.domain.model.Usuario
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class UsuarioEntityTest {

    @Test
    fun `UsuarioEntity - conversion a dominio es correcta`() {
        // GIVEN: Una entidad de base de datos
        val entity = UsuarioEntity(
            id = "local_1",
            rut = "1-9",
            nombre = "Maria",
            apellido = "Gomez",
            username = "mary",
            email = "m@m.cl",
            password = "123",
            telefono = "123",
            direccion = "Av",
            comuna = "Concepcion",
            region = "Bio bio",
            fechaNacimiento = 1000L, // Timestamp
            fechaRegistro = 2000L,
            rol = "USUARIO"
        )

        // WHEN
        val modelo = entity.toUsuario()

        // THEN
        assertEquals("local_1", modelo.id)
        assertEquals("Maria", modelo.nombre)
        assertEquals(Rol.USUARIO, modelo.rol)
        // Verificamos que el Long se convirtió a Date
        assertEquals(1000L, modelo.fechaNacimiento.time)
    }

    @Test
    fun `UsuarioModelo - conversion a entidad mantiene los datos`() {
        // GIVEN: Un modelo de dominio
        val modelo = Usuario(
            id = "xyz",
            rut = "2-2",
            nombre = "Test",
            apellido = "Test",
            username = "t",
            email = "t@t.t",
            password = "1",
            telefono = "1",
            direccion = "d",
            comuna = "c",
            region = "r",
            fechaNacimiento = Date(),
            fechaRegistro = Date(),
            rol = Rol.ADMIN
        )

        // WHEN
        val entity = modelo.toEntity()

        // THEN
        assertEquals("xyz", entity.id)
        assertEquals("ADMIN", entity.rol) // El Enum debe pasar a String
    }
}