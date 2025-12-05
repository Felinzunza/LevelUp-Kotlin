package com.example.levelUpKotlinProject.data.remote.dto


import com.example.levelUpKotlinProject.domain.model.EstadoOrden
import com.example.levelUpKotlinProject.domain.model.ItemOrden
import com.example.levelUpKotlinProject.domain.model.Orden
import com.example.levelUpKotlinProject.domain.model.TipoCompra
import com.example.levelUpKotlinProject.domain.model.TipoCourier
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale


data class OrdenDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("rut")
    val rut: String,

    @SerializedName("clientName")
    val nombreCliente: String,

    @SerializedName("date")
    val fechaCreacion: String, // String para la fecha

    @SerializedName("status")
    val estado: String,

    @SerializedName("shippingAddress")
    val direccionEnvio: String,

    @SerializedName("paymentMethod")
    val metodoPago: String,

    @SerializedName("courier")
    val courier: String,

    @SerializedName("subtotal")
    val subtotal: Double,

    @SerializedName("shippingCost")
    val costoEnvio: Double,

    @SerializedName("discount")
    val descuento: Double,

    @SerializedName("total")
    val total: Double,

    @SerializedName("items") val
    items: List<ItemOrdenDto>
)

/**
 * Representación de un ítem dentro de la orden
 */
data class ItemOrdenDto(



    @SerializedName("productId")
    val productoId: String,

    @SerializedName("orderId")
    val ordenId: String? = null,

    @SerializedName("productName")
    val nombreProducto: String,

    @SerializedName("imageUrl")
    val imagenUrl: String?,

    // Mapeamos 'price' del JSON a tu variable 'precioUnitarioFijo'
    @SerializedName("price")
    val precioUnitarioFijo: Double,

    @SerializedName("quantity")
    val cantidad: Int
)


// --- MAPPERS CORREGIDOS ---

fun ItemOrdenDto.aModelo(): ItemOrden {
    return ItemOrden(
        productoId = productoId,

        ordenId = ordenId ?: "",

        nombreProducto = nombreProducto,

        imagenUrl = imagenUrl,

        precioUnitarioFijo = precioUnitarioFijo,

        cantidad = cantidad
    )
}

fun OrdenDto.aModelo(): Orden {
    val fecha = try {
        // Usamos SimpleDateFormat para evitar crash en Android < 8.0
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.parse(fechaCreacion) ?: Date()
    } catch (e: Exception) {
        Date()
    }

    return Orden(
        id = id ?: "",
        rut = rut,
        nombreCliente = nombreCliente,
        fechaCreacion = fecha,
        // Convertimos Strings del JSON a Enums del Modelo con seguridad
        estado = try { EstadoOrden.valueOf(estado) } catch (e: Exception) { EstadoOrden.EN_PREPARACION },
        direccionEnvio = direccionEnvio,
        // Asegúrate de tener un valor por defecto válido en tu Enum (ej: DEBITO, EFECTIVO)
        metodoPago = try { TipoCompra.valueOf(metodoPago) } catch (e: Exception) { TipoCompra.TARJETA_DEBITO },
        // Asegúrate de tener un valor por defecto válido (ej: CHILEXPRESS)
        courier = try { TipoCourier.valueOf(courier) } catch (e: Exception) { TipoCourier.CHILEXPRESS },
        subtotal = subtotal,
        costoEnvio = costoEnvio,
        descuento = descuento,
        total = total,
        items = items.map { it.aModelo() }
    )
}

//metodo agregado ahora
fun Orden.aDto(): OrdenDto {
    val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return OrdenDto(
        // Si el ID está vacío (nueva orden), enviamos null para que el server genere uno
        id = if (id.isBlank()) null else id,
        rut = rut,
        nombreCliente = nombreCliente,
        fechaCreacion = f.format(fechaCreacion),
        estado = estado.name,
        direccionEnvio = direccionEnvio,
        metodoPago = metodoPago.name,
        courier = courier.name,
        subtotal = subtotal,
        costoEnvio = costoEnvio,
        descuento = descuento,
        total = total,
        items = items.map {
            ItemOrdenDto(
                productoId = it.productoId,
                ordenId = it.ordenId.ifBlank { null },
                nombreProducto = it.nombreProducto, // Mapeamos 'nombre' del modelo a 'nombreProducto' del DTO
                imagenUrl = it.imagenUrl,
                precioUnitarioFijo = it.precioUnitarioFijo,
                cantidad = it.cantidad
            )
        }
    )
}





