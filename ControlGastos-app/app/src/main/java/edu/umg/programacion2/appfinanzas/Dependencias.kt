package edu.umg.programacion2.appfinanzas

import android.content.Context
import androidx.room.Room
import edu.umg.programacion2.appfinanzas.datos.AppDatabase
import edu.umg.programacion2.appfinanzas.datos.TransaccionRepositoryRoom

/**
- Contenedor Singleton de dependencias compartidas para toda la aplicación.
- Garantiza que exista una única instancia conectada a la base de datos de Room.
 */
object Dependencias {

    // Instancia única global de la base de datos
    lateinit var database: AppDatabase
        private set

    // Instancia única global del repositorio de transacciones
    lateinit var repositorio: TransaccionRepositoryRoom
        private set

    // Inicializa las dependencias globales utilizando el contexto de la aplicación
    fun iniciar(context: Context) {
        // Construye la base de datos local de Room y asigna el nombre de archivo "gastos.db"
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "gastos.db"
        ).build()

        // Inicializa el repositorio conectándolo con el DAO de la base de datos
        repositorio = TransaccionRepositoryRoom(database.transaccionDao())
    }
}