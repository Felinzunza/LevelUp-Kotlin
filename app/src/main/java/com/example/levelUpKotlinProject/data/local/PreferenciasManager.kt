package com.example.levelUpKotlinProject.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * PreferenciasManager: Gestiona datos persistentes simples
 * 
 * SharedPreferences:
 * - Almacena pares clave-valor (como un Map)
 * - Persiste entre sesiones de la app
 * - Solo para datos simples (no objetos complejos)
 * 
 * Uso típico:
 * - Sesión de usuario
 * - Configuraciones
 * - Preferencias de UI (tema oscuro, etc.)
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
class PreferenciasManager(context: Context) {
    
    // Obtener SharedPreferences del sistema
    private val prefs: SharedPreferences = context.getSharedPreferences(
        NOMBRE_ARCHIVO,
        Context.MODE_PRIVATE  // Solo esta app puede leer
    )
    
    companion object {
        private const val NOMBRE_ARCHIVO = "stingcommerce_prefs"
        
        // Claves (constantes para evitar typos)
        private const val KEY_ADMIN_LOGUEADO = "admin_logueado"
        private const val KEY_USERNAME_ADMIN = "username_admin"
        
        // Credenciales por defecto (en app real, estarían en BD segura)
        const val ADMIN_USERNAME = "admin"
        const val ADMIN_PASSWORD = "admin123"
    }
    
    /**
     * Guarda sesión de admin
     */
    fun guardarSesionAdmin(username: String) {
        prefs.edit().apply {
            putBoolean(KEY_ADMIN_LOGUEADO, true)
            putString(KEY_USERNAME_ADMIN, username)
            apply()  // Guarda en background
        }
    }
    
    /**
     * Verifica si hay un admin logueado
     */
    fun estaAdminLogueado(): Boolean {
        return prefs.getBoolean(KEY_ADMIN_LOGUEADO, false)
    }
    
    /**
     * Obtiene username del admin logueado
     */
    fun obtenerUsernameAdmin(): String? {
        return prefs.getString(KEY_USERNAME_ADMIN, null)
    }
    
    /**
     * Cierra sesión de admin
     */
    fun cerrarSesionAdmin() {
        prefs.edit().apply {
            remove(KEY_ADMIN_LOGUEADO)
            remove(KEY_USERNAME_ADMIN)
            apply()
        }
    }
    
    /**
     * Valida credenciales de admin
     * En app real: Consulta a backend con hash de password
     */
    fun validarCredencialesAdmin(username: String, password: String): Boolean {
        return username == ADMIN_USERNAME && password == ADMIN_PASSWORD
    }
}
