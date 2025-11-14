package com.example.levelUpKotlinProject.domain.model

enum class EstadoOrden(val displayString: String) {
    EN_PREPARACION("En preparacion"),
    EN_TRANSITO("En transito"),
    ENTREGADO("Entregado"),
    RECHAZADO("Rechazado"),
}