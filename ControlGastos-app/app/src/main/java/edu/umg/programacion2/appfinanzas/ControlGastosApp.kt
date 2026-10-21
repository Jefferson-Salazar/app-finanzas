package edu.umg.programacion2.appfinanzas

import android.app.Application

class ControlGastosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Dependencias.iniciar(this)
    }
}