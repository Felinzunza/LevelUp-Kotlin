package com.example.levelUpKotlinProject.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type Converters para Room
 * Permite a Room almacenar y recuperar objetos que no son nativos de SQLite,
 * como java.util.Date (almacenado como Long/Timestamp).
 */
class Converters {

    /**
     * Convierte un Long (Timestamp) de SQLite a un objeto Date de Kotlin/Java.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Convierte un objeto Date de Kotlin/Java a un Long (Timestamp) para SQLite.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        // El método .time de Date devuelve el valor Long del timestamp.
        return date?.time
    }
}