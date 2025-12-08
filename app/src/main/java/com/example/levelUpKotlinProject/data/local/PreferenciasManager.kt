package com.example.levelUpKotlinProject.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * PreferenciasManager: Gestiona datos persistentes simples (Sesiones)
 */
class PreferenciasManager(context: Context) {

    // Obtener SharedPreferences del sistema
    private val prefs: SharedPreferences = context.getSharedPreferences(
        NOMBRE_ARCHIVO,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val NOMBRE_ARCHIVO = "stingcommerce_prefs"

        // ADMIN KEYS
        private const val KEY_ADMIN_LOGUEADO = "admin_logueado"
        private const val KEY_USERNAME_ADMIN = "username_admin"

        // USUARIO KEYS
        private const val KEY_USUARIO_LOGUEADO = "usuario_logueado"

        private const val KEY_ID_USUARIO = "id_usuario"
        private const val KEY_EMAIL_USUARIO = "email_usuario"
        private const val KEY_NOMBRE_USUARIO = "nombre_usuario"
        private const val KEY_RUT_USUARIO = "rut_usuario"

        // Credenciales Admin
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASSWORD = "admin123"
    }

    // --- GESTIÓN DE SESIÓN DE ADMIN ---

    fun guardarSesionAdmin(username: String) {
        prefs.edit().apply {
            putBoolean(KEY_ADMIN_LOGUEADO, true)
            putString(KEY_USERNAME_ADMIN, username)
            apply()
        }
    }

    fun estaAdminLogueado(): Boolean {
        return prefs.getBoolean(KEY_ADMIN_LOGUEADO, false)
    }

    fun obtenerUsernameAdmin(): String? {
        return prefs.getString(KEY_USERNAME_ADMIN, null)
    }

    fun cerrarSesionAdmin() {
        prefs.edit().apply {
            remove(KEY_ADMIN_LOGUEADO)
            remove(KEY_USERNAME_ADMIN)
            apply()
        }
    }

    fun validarCredencialesAdmin(username: String, password: String): Boolean {
        return username == ADMIN_USERNAME && password == ADMIN_PASSWORD
    }

    // --- GESTIÓN DE SESIÓN DE USUARIO (CLIENTE) ---

    // Esta función debe estar FUERA de las demás, al nivel de la clase
    // 1. AÑADE el parámetro 'id' aquí
    fun guardarSesionUsuario(id: String, email: String, nombre: String, rut: String) {
        prefs.edit().apply {
            putBoolean(KEY_USUARIO_LOGUEADO, true)
            putString(KEY_ID_USUARIO, id)         // ✅ Guardamos el ID
            putString(KEY_EMAIL_USUARIO, email)
            putString(KEY_NOMBRE_USUARIO, nombre) // ✅ Guardamos el Nombre real para mostrarlo
            putString(KEY_RUT_USUARIO, rut)
            apply()
        }
    }

    fun estaUsuarioLogueado(): Boolean {
        return prefs.getBoolean(KEY_USUARIO_LOGUEADO, false)
    }

    fun obtenerEmailUsuario(): String? {
        return prefs.getString(KEY_EMAIL_USUARIO, null)
    }

    fun obtenerNombreUsuario(): String? {
        return prefs.getString(KEY_NOMBRE_USUARIO, "Usuario")
    }

    fun obtenerRutUsuario(): String {
        return prefs.getString(KEY_RUT_USUARIO, "") ?: ""
    }

    // 2. AÑADE esta función nueva para recuperar el ID
    fun obtenerIdUsuario(): String? {
        return prefs.getString(KEY_ID_USUARIO, null)
    }

    fun cerrarSesionUsuario() {
        prefs.edit().apply {
            remove(KEY_USUARIO_LOGUEADO)
            remove(KEY_ID_USUARIO) // ✅ Borramos ID
            remove(KEY_EMAIL_USUARIO)
            remove(KEY_NOMBRE_USUARIO)
            remove(KEY_RUT_USUARIO)
            apply()
        }
    }
}