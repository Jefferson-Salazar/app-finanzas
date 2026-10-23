package edu.umg.programacion2.appfinanzas

import android.content.Context
import androidx.room.Room
import edu.umg.programacion2.appfinanzas.datos.AppDatabase

/**
 * Contenedor Singleton de dependencias compartidas para toda la aplicación.
 * Garantiza que exista una única instancia conectada a la base de datos de Room.
 */
object Dependencias {

    lateinit var database: AppDatabase
        private set

    lateinit var repositorio: `TransaccionRepositoryRoom.kt`
        private set

    fun iniciar(context: Context) {
        // Construye la base de datos local de Room (archivo gastos.db)
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "gastos.db"
        ).build()

        // Inicializa el repositorio pasándole el DAO correspondiente
        repositorio = `TransaccionRepositoryRoom.kt`(database.transaccionDao())
    }
}