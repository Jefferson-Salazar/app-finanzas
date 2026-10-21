package edu.umg.programacion2.appfinanzas.modelo

data class Transaccion(
    val id: Int,
    val usuarioId: Int,
    val tipo: String,
    val categoria: String,
    val monto: Double,
    val descripcion: String,
    val fecha: String
)