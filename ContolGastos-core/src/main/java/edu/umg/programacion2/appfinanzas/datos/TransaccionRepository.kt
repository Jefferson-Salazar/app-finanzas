package edu.umg.programacion2.appfinanzas.datos

import edu.umg.programacion2.appfinanzas.modelo.Transaccion

interface TransaccionRepository {

    suspend fun agregar(transaccion: Transaccion): Transaccion

    suspend fun listar(usuarioId: Int): List<Transaccion>

    suspend fun buscarPorId(id: Int): Transaccion?

    suspend fun actualizar(transaccion: Transaccion): Boolean

    suspend fun eliminar(id: Int): Boolean
}