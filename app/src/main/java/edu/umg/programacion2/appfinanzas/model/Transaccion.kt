package edu.umg.programacion2.appfinanzas.model

data class Transaccion(
    val id: Int,
    val usuarioId: Int,
    val tipo: String,
    val categoria: String,
    val monto: Double,
    val descripcion: String,
    val fecha: String
)