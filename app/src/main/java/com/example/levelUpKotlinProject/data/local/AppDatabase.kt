package com.example.levelUpKotlinProject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.levelUpKotlinProject.data.local.dao.CarritoDao
import com.example.levelUpKotlinProject.data.local.dao.OrdenDao
import com.example.levelUpKotlinProject.data.local.dao.ProductoDao
import com.example.levelUpKotlinProject.data.local.dao.UsuarioDao
import com.example.levelUpKotlinProject.data.local.entity.*

@Database(
    entities = [
        UsuarioEntity::class,
        ProductoEntity::class,
        CarritoEntity::class,
        OrdenEntity::class,
        DetalleOrdenEntity::class
    ],
    version = 3, // Asegúrate de que coincida con tu versión actual
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carritoDao(): CarritoDao
    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ordenDao(): OrdenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "labx_database"
                )
                    .fallbackToDestructiveMigration() // Importante para evitar crashes por cambios de tabla
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}