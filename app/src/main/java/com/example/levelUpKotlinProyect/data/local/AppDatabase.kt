package com.example.levelUpKotlinProyect.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.levelUpKotlinProyect.data.local.dao.CarritoDao
import com.example.levelUpKotlinProyect.data.local.dao.OrdenDao
import com.example.levelUpKotlinProyect.data.local.dao.ProductoDao
import com.example.levelUpKotlinProyect.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProyect.data.local.entity.CarritoEntity
import com.example.levelUpKotlinProyect.data.local.entity.ProductoEntity
import com.example.levelUpKotlinProyect.data.local.entity.UsuarioEntity
import com.example.levelUpKotlinProyect.data.local.entity.OrdenEntity
import com.example.levelUpKotlinProyect.data.local.entity.DetalleOrdenEntity

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
        DetalleOrdenEntity::class // Necesaria para la relación
    ],
    // 2. VERSIÓN: Incrementar la versión después de añadir/modificar tablas
    version = 3, // 👈 Se incrementa a 3
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene instancia única de la base de datos
         * Thread-safe con synchronized
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labx_database" // Renombrado para reflejar ambas tablas
                )
                    .fallbackToDestructiveMigration() // Borra BD si cambia versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}