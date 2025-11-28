package com.example.levelUpKotlinProject.domain.model

object DatosChile {
    val regionesYComunas = mapOf(
        "Arica y Parinacota" to listOf("Arica", "Camarones", "Putre", "General Lagos"),
        "Tarapacá" to listOf("Iquique", "Alto Hospicio", "Pozo Almonte", "Camiña", "Colchane", "Huara", "Pica"),
        "Antofagasta" to listOf("Antofagasta", "Mejillones", "Sierra Gorda", "Taltal", "Calama", "Ollagüe", "San Pedro de Atacama", "Tocopilla", "María Elena"),
        "Atacama" to listOf("Copiapó", "Caldera", "Tierra Amarilla", "Chañaral", "Diego de Almagro", "Vallenar", "Alto del Carmen", "Freirina", "Huasco"),
        "Coquimbo" to listOf("La Serena", "Coquimbo", "Andacollo", "La Higuera", "Paiguano", "Vicuña", "Illapel", "Canela", "Los Vilos", "Salamanca", "Ovalle", "Combarbalá", "Monte Patria", "Punitaqui", "Río Hurtado"),
        "Valparaíso" to listOf("Valparaíso", "Casablanca", "Concón", "Juan Fernández", "Puchuncaví", "Quintero", "Viña del Mar", "Isla de Pascua", "Los Andes", "Calle Larga", "Rinconada", "San Esteban", "La Ligua", "Cabildo", "Papudo", "Petorca", "Zapallar", "Quillota", "Calera", "Hijuelas", "La Cruz", "Nogales", "San Antonio", "Algarrobo", "Cartagena", "El Quisco", "El Tabo", "Santo Domingo", "San Felipe", "Catemu", "Llaillay", "Panquehue", "Putaendo", "Santa María", "Quilpué", "Limache", "Olmué", "Villa Alemana"),
        "Metropolitana" to listOf("Cerrillos", "Cerro Navia", "Conchalí", "El Bosque", "Estación Central", "Huechuraba", "Independencia", "La Cisterna", "La Florida", "La Granja", "La Pintana", "La Reina", "Las Condes", "Lo Barnechea", "Lo Espejo", "Lo Prado", "Macul", "Maipú", "Ñuñoa", "Pedro Aguirre Cerda", "Peñalolén", "Providencia", "Pudahuel", "Quilicura", "Quinta Normal", "Recoleta", "Renca", "San Joaquín", "San Miguel", "San Ramón", "Santiago", "Vitacura", "Puente Alto", "Pirque", "San José de Maipo", "Colina", "Lampa", "Tiltil", "San Bernardo", "Buin", "Calera de Tango", "Paine", "Melipilla", "Alhué", "Curacaví", "María Pinto", "San Pedro", "Talagante", "El Monte", "Isla de Maipo", "Padre Hurtado", "Peñaflor"),
        "O'Higgins" to listOf("Rancagua", "Machalí", "San Fernando", "Santa Cruz", "Pichilemu"), // (Resumido para el ejemplo)
        "Maule" to listOf("Talca", "Curicó", "Linares", "Constitución", "Cauquenes"),
        "Ñuble" to listOf("Chillán", "San Carlos", "Cobquecura", "Bulnes"),
        "Biobío" to listOf("Concepción", "Talcahuano", "Los Ángeles", "San Pedro de la Paz", "Coronel", "Chiguayante"),
        "Araucanía" to listOf("Temuco", "Padre Las Casas", "Villarrica", "Pucón", "Angol"),
        "Los Ríos" to listOf("Valdivia", "Corral", "La Unión", "Río Bueno", "Panguipulli"),
        "Los Lagos" to listOf("Puerto Montt", "Puerto Varas", "Osorno", "Castro", "Ancud", "Chaitén"),
        "Aysén" to listOf("Coyhaique", "Aysén", "Chile Chico", "Cochrane"),
        "Magallanes" to listOf("Punta Arenas", "Puerto Natales", "Porvenir", "Cabo de Hornos")
    )

    val listaRegiones = regionesYComunas.keys.toList().sorted() // Orden alfabético opcional
}