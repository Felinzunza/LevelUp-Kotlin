package com.example.levelUpKotlinProject.domain.validator


object ValidadorProducto {

    fun validarNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre es obligatorio"
            nombre.length < 2 -> "El nombre es muy corto"
            else -> null
        }
    }

    fun validarDescripcion(descripcion: String): String? {
        return if (descripcion.isBlank()) "La descripción es obligatoria" else null
    }

    fun validarPrecio(precio: String): String? {
        val numero = precio.toIntOrNull()
        return when {
            precio.isBlank() -> "El precio es obligatorio"
            numero == null -> "Ingresa un número válido"
            numero <= 0 -> "El precio debe ser mayor a 0"
            else -> null
        }
    }

    fun validarStock(stock: String): String? {
        val numero = stock.toIntOrNull()
        return when {
            stock.isBlank() -> "El stock es obligatorio"
            numero == null -> "Ingresa un número válido"
            numero < 0 -> "El stock no puede ser negativo"
            else -> null
        }
    }

    fun validarCategoria(categoria: String): String? {
        return if (categoria.isBlank()) "La categoría es obligatoria" else null
    }

    fun validarImagenUrl(url: String): String? {
        return when {
            url.isBlank() -> "El ID de imagen es obligatorio"
            url.contains(" ") -> "No puede contener espacios"
            url.any { it.isUpperCase() } -> "Debe ser minúsculas (formato drawable)"
            else -> null
        }
    }
}