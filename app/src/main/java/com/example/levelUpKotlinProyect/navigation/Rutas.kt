package com.example.levelUpKotlinProyect.ui.navigation

/**
 * Rutas: Nombres de las pantallas para navegación
 * 
 * ¿Por qué usar constantes?
 * - Evita errores de tipeo ("home" vs "Home" vs "HOME")
 * - Fácil de cambiar en un solo lugar
 * - Autocompletado del IDE
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
object Rutas {
    const val PORTADA = "portada"
    const val HOME = "home"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    const val REGISTRO = "registro"
    
    // Rutas de administración
    const val LOGIN_ADMIN = "login_admin"
    const val PANEL_ADMIN = "panel_admin"
    const val FORMULARIO_PRODUCTO = "formulario_producto?productoId={productoId}"
    
    // Funciones helper para pasar argumentos
    fun detalleConId(id: Int) = "detalle/$id"
    fun formularioEditar(id: Int) = "formulario_producto?productoId=$id"
}
