package com.example.levelUpKotlinProject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.levelUpKotlinProject.domain.model.EstadoOrden
import com.example.levelUpKotlinProject.domain.model.Orden
import com.example.levelUpKotlinProject.domain.model.TipoCompra
import com.example.levelUpKotlinProject.domain.model.TipoCourier
import java.util.Date


@Entity(tableName = "ordenes")
data class OrdenEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String, // String

    // DATOS DEL CLIENTE
    val rutCliente: String,
    val nombreCliente: String, //

    // TIEMPO Y LOGÍSTICA
    val fechaCreacion: Long,
    val direccion: String,
    val tipoCourier: String,
    val estado: String,

    // PAGO Y FINANZAS
    val tipoCompra: String,
    val subtotal: Double,
    val descuento: Double,
    val costoEnvio: Double,
    val totalPagar: Double
)

fun OrdenEntity.toOrden() = Orden(
    id = id,
    rut = rutCliente, // 👈 Se asume que el campo 'rut' del Modelo viene de 'rutCliente' de la Entidad
    nombreCliente = nombreCliente, //
    fechaCreacion = Date(fechaCreacion),
    direccionEnvio = direccion,
    metodoPago = TipoCompra.valueOf(tipoCompra),
    courier = TipoCourier.valueOf(tipoCourier),
    estado = EstadoOrden.valueOf(estado),
    subtotal = subtotal,
    costoEnvio = costoEnvio,
    descuento = descuento,
    // CORRECCIÓN CRÍTICA: Asignar el campo de la Entidad al campo del Modelo
    total = totalPagar,
    items = emptyList()
)

fun Orden.toEntity() = OrdenEntity(
    id = id,
    rutCliente = rut, // 👈 Se asume que el Modelo usa 'rut' y la Entidad 'rutCliente'
    nombreCliente=nombreCliente,
    // CONVERSIÓN CRÍTICA: Date a Long (timestamp)
    fechaCreacion = fechaCreacion.time,

    direccion = direccionEnvio, // 👈 Asigna el nombre del Modelo al de la Entidad

    // CONVERSIÓN CRÍTICA: Enum a String (usando el nombre del Enum)
    tipoCourier = courier.name,
    estado = estado.name,

    // CONVERSIÓN CRÍTICA: Enum a String (usando el nombre del Enum)
    tipoCompra = metodoPago.name,

    subtotal = subtotal,
    descuento = descuento,
    costoEnvio = costoEnvio,
    // Asignamos el total del Modelo al campo de la Entidad
    totalPagar = total
)



