package com.example.levelUpKotlinProyect.data.local.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.example.levelUpKotlinProyect.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProyect.data.local.entity.DetalleOrdenEntity

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