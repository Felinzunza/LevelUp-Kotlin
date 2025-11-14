package com.example.levelUpKotlinProject.data.local.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.toItemOrden
import com.example.levelUpKotlinProject.data.local.entity.toOrden
import com.example.levelUpKotlinProject.domain.model.Orden

/**
 * Clase contenedora que le dice a Room cómo relacionar la Orden principal con sus detalles.
 * Es el resultado que devuelve el DAO cuando se usa @Transaction.
 */
data class OrdenConDetalles(

    // 1. Entidad principal (los campos de la tabla 'ordenes')
    @Embedded
    val orden: OrdenEntity,

    // 2. La lista de ítems relacionados (los campos de la tabla 'detalle_orden')
    @Relation(
        // Columna en la entidad principal (OrdenEntity)
        parentColumn = "id",
        // Columna en la entidad secundaria (DetalleOrdenEntity) que se relaciona
        entityColumn = "ordenId"
    )
    val detalles: List<DetalleOrdenEntity>
)

fun OrdenConDetalles.toOrden(): Orden {
    // 1. Mapear la cabecera (OrdenEntity a Orden)
    // Asume que OrdenEntity tiene su propio toOrden() que no maneja la lista de items
    val ordenPura = this.orden.toOrden()

    // 2. Mapear la lista de detalles (usando la función que mapea el ítem)
    val itemsDominio = this.detalles.map { it.toItemOrden() }

    // 3. Devolver el modelo Orden completo con la lista de ítems
    // Usa .copy para añadir la lista de ítems al modelo Orden principal
    return ordenPura.copy(items = itemsDominio)
}