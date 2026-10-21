package edu.umg.programacion2.appfinanzas.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.umg.programacion2.appfinanzas.modelo.Transaccion

@Entity(tableName = "transacciones")
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val fecha: String,
    val categoria: String,
    val tipo: String,
    val usuarioId: Int
) {
    //Convierte la fila de la base de datos (Entity) al modelo del core
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
        //Convierte un objeto del modelo del core a una Entity para guardarla en SQLite
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