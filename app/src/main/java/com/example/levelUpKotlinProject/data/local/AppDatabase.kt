package com.example.levelUpKotlinProject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.levelUpKotlinProject.data.local.dao.CarritoDao
import com.example.levelUpKotlinProject.data.local.dao.OrdenDao
import com.example.levelUpKotlinProject.data.local.dao.ProductoDao
import com.example.levelUpKotlinProject.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProject.data.local.entity.CarritoEntity
import com.example.levelUpKotlinProject.data.local.entity.ProductoEntity
import com.example.levelUpKotlinProject.data.local.entity.UsuarioEntity
import com.example.levelUpKotlinProject.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProject.data.local.entity.DetalleOrdenEntity
import androidx.room.TypeConverters // IMPORTAR

/**
 * Database principal de la app
 * Ahora incluye productos y carrito
 * Singleton para una única instancia en toda la app
 *
 */
@Database(
    // 1. LISTA DE ENTIDADES: Incluir todas las entidades
    entities = [
        UsuarioEntity::class,
        ProductoEntity::class,
        CarritoEntity::class,
        OrdenEntity::class,
        DetalleOrdenEntity::class
    ],
    // 2. VERSIÓN: Incrementar la versión después de añadir/modificar tablas
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class) // APLICACIÓN DEL CONVERTER
abstract class AppDatabase : RoomDatabase() {

    // --- DAOs (Abstract Functions) ---

    /**
     * Provee acceso al DAO de carrito
     */
    abstract fun carritoDao(): CarritoDao

    /**
     * Provee acceso al DAO de productos
     */
    abstract fun productoDao(): ProductoDao

    abstract fun usuarioDao(): UsuarioDao
    abstract fun ordenDao(): OrdenDao

    // --- Singleton Companion Object ---

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene instancia única de la base de datos
         * Thread-safe con synchronized
         */
        fun getDatabase(context: Context): AppDatabase {
            // Usa el patrón singleton para asegurar una única instancia
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labx_database" // Nombre de la base de datos
                )
                    .fallbackToDestructiveMigration() // Borra BD si cambia versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}