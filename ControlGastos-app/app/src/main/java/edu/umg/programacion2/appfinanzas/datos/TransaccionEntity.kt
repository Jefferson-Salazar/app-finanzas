package edu.umg.programacion2.appfinanzas.datos

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.umg.programacion2.appfinanzas.modelo.Transaccion

// Define esta clase como una tabla llamada "transacciones" en la base de datos Room
@Entity(tableName = "transacciones")
data class TransaccionEntity(

    // Identificador único autoincrementable para cada fila
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val fecha: String,
    val categoria: String,
    val tipo: String, // "INGRESO" o "GASTO"
    val usuarioId: Int
) {
    // Convierte el registro de la base de datos (Entity) al modelo general del core
    fun aModelo(): Transaccion = Transaccion(
        id = id,
        descripcion = descripcion,
        monto = monto,
        fecha = fecha,
        categoria = categoria,
        tipo = tipo,
        usuarioId = usuarioId
    )

    companion object {
        // Convierte un objeto del core a una Entity para poder guardarla en SQLite
        fun desde(transaccion: Transaccion): TransaccionEntity = TransaccionEntity(
            id = transaccion.id,
            descripcion = transaccion.descripcion,
            monto = transaccion.monto,
            fecha = transaccion.fecha,
            categoria = transaccion.categoria,
            tipo = transaccion.tipo,
            usuarioId = transaccion.usuarioId
        )
    }
}