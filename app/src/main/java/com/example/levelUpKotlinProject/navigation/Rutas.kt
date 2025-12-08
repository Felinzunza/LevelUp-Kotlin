package com.example.levelUpKotlinProject.ui.navigation

/**
 * Rutas: Nombres de las pantallas para navegación
 * * ¿Por qué usar constantes?
 * - Evita errores de tipeo ("home" vs "Home" vs "HOME")
 * - Fácil de cambiar en un solo lugar
 * - Autocompletado del IDE
 */
object Rutas {
    const val PORTADA = "portada"
    const val HOME = "home"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    const val REGISTRO = "registro"

    //cambio
    const val USUARIO = "usuario"

    // ✅ CAMBIO: La ruta base ahora es solo el nombre
    const val PERFIL_BASE = "perfil_usuario"
    // ✅ CAMBIO: Ruta completa con argumento para la navegación
    const val PERFIL_USUARIO = "$PERFIL_BASE/{usuarioId}"


    const val OPCIONES_ACCESO = "opciones_acceso" // AÑADIDO
    const val LOGIN_USER = "login_user" // AÑADIDO

    const val COMPRA_ACEPTADA = "compra_aceptada" // AÑADIDO: Nueva ruta


    // Rutas de administración
    const val LOGIN_ADMIN = "login_admin"
    const val PANEL_ADMIN = "panel_admin"
    const val FORMULARIO_PRODUCTO = "formulario_producto?productoId={productoId}"

    const val FORMULARIO_USUARIO = "formulario_usuario?usuarioId={usuarioId}"

    const val DETALLE_ORDEN = "detalle_orden/{ordenId}"




    // Funciones helper para pasar argumentos
    fun detalleOrden(ordenId: String) = "detalle_orden/$ordenId"

    fun formularioEditarProducto(id: String) = "formulario_producto?productoId=$id"
    fun formularioEditarUsuario(id: String) = "formulario_usuario?usuarioId=$id"

    // ✅ NUEVO HELPER: Para navegar al perfil de alguien específico
    fun perfilUsuario(id: String) = "$PERFIL_BASE/$id"



}