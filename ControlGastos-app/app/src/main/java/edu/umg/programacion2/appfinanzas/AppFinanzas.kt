package edu.umg.programacion2.appfinanzas

import android.app.Application

// Clase base global de la aplicación: Se ejecuta automáticamente al arrancar la app antes que cualquier actividad
class AppFinanzas : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializa el contenedor global de dependencias (como el repositorio y la base de datos)
        Dependencias.iniciar(this)
    }
}