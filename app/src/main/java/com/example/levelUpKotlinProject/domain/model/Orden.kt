package com.example.levelUpKotlinProject.domain.model
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Orden(
    val id: Long,
    val rut: String,
    val fechaCreacion: Date,
    val estado: EstadoOrden,
    val direccionEnvio: String,
    val metodoPago: TipoCompra,
    val courier: TipoCourier,
    val subtotal: Double,
    val costoEnvio: Double,
    val descuento: Double,
    val total: Double, // Siempre debe ser el valor guardado
    val items: List<ItemOrden>
) {
    fun fechaCreacionFormateada(): String {
        val fechaformateada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
        return fechaformateada.format(fechaCreacion)
    }

    fun totalFormateado(): String {
        val totalEntero = total.toInt()
        return "$$${totalEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"

    }
    val estadoDisplay:String
        get()=estado.displayString

    val metodoPagoDisplay: String
        get() = metodoPago.displayString

    val courierDisplay: String
        get() = courier.displayString

}