package edu.umg.programacion2.appfinanzas.datos

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TransaccionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transaccionDao(): TransaccionDao
}