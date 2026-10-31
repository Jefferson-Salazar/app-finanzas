package edu.umg.programacion2.appfinanzas.datos

import androidx.room.Database
import androidx.room.RoomDatabase

// Configura la base de datos Room y define la tabla (entidad) que utiliza
@Database(entities = [TransaccionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Proporciona el acceso a los métodos de la base de datos (DAO)
    abstract fun transaccionDao(): TransaccionDao
}