package com.example.levelUpKotlinProject.domain.model

data class ItemOrden(
    // Identificadores
    val productoId: Int,
    val ordenId: Long = 0, // Referencia a la orden (opcional, pero útil)

    // Información Desnormalizada (Inmutable del momento de la compra)
    val nombre: String,
    val imagenUrl: String?,

    // Valores Fijos
    val precioUnitarioFijo: Double, // 👈 EL PRECIO COBRADO
    val cantidad: Int
) {
    // Propiedad calculada: subtotal del ítem (basado en el precio fijo)
    val subtotal: Double
        get() = precioUnitarioFijo * cantidad // Calcula usando el precio fijo.
}


