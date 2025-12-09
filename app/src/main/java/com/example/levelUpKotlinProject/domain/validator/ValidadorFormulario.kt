package com.example.levelUpKotlinProject.domain.validator

import com.example.levelUpKotlinProject.domain.model.ErroresFormulario
import com.example.levelUpKotlinProject.domain.model.FormularioRegUsuario


object ValidadorFormulario {
    
    // Regex para email: algo@algo.com
    private val emailRegex = Regex(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )
    
    // Regex para teléfono: 9 dígitos (formato español)
    private val telefonoRegex = Regex(
        "^[0-9]{9}$"
    )
    
    // Regex para contraseña: mínimo 8 caracteres, 1 mayúscula, 1 número
    private val passwordRegex = Regex(
        "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"
    )
    
    /**
     * Valida todo el formulario y devuelve los errores
     */
    fun validarFormulario(formulario: FormularioRegUsuario): ErroresFormulario {
        return ErroresFormulario(
            nombreCompletoError = validarNombreCompleto(formulario.nombreCompleto),
            emailError = validarEmail(formulario.email),
            telefonoError = validarTelefono(formulario.telefono),
            direccionError = validarDireccion(formulario.direccion),
            passwordError = validarPassword(formulario.password),
            confirmarPasswordError = validarConfirmarPassword(
                formulario.password,
                formulario.confirmarPassword
            ),
            terminosError = validarTerminos(formulario.aceptaTerminos)
        )
    }
    
    fun validarNombreCompleto(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            !nombre.contains(" ") -> "Ingresa tu nombre completo"
            else -> null
        }
    }
    
    fun validarEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es obligatorio"
            !emailRegex.matches(email) -> "Email inválido (ejemplo: usuario@mail.com)"
            else -> null
        }
    }
    
    fun validarTelefono(telefono: String): String? {
        return when {
            telefono.isBlank() -> "El teléfono es obligatorio"
            !telefonoRegex.matches(telefono) -> "Teléfono inválido (debe tener 9 dígitos)"
            else -> null
        }
    }
    
    fun validarDireccion(direccion: String): String? {
        return when {
            direccion.isBlank() -> "La dirección es obligatoria"
            direccion.length < 10 -> "La dirección debe ser más completa"
            else -> null
        }
    }
    
    fun validarPassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            !passwordRegex.matches(password) -> 
                "Contraseña débil (mínimo 8 caracteres, 1 mayúscula y 1 número)"
            else -> null
        }
    }
    
    fun validarConfirmarPassword(password: String, confirmar: String): String? {
        return when {
            confirmar.isBlank() -> "Confirma tu contraseña"
            confirmar != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }
    
    fun validarTerminos(acepta: Boolean): String? {
        return if (!acepta) {
            "Debes aceptar los términos y condiciones"
        } else {
            null
        }
    }
}
