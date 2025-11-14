package com.example.levelUpKotlinProject.domain.model

enum class TipoCompra(val displayString: String) {
    TARJETA_CREDITO("Tarjeta de Crédito"),
    TARJETA_DEBITO("Tarjeta de Débito"),
    TRANSFERENCIA("Transferencia Bancaria"),
    EFECTIVO_PUNTO_RETIRO("Efectivo / Punto de Retiro")
}