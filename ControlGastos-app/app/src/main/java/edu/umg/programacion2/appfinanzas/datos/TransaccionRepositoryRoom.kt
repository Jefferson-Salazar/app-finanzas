package edu.umg.programacion2.appfinanzas.datos

import edu.umg.programacion2.appfinanzas.modelo.Transaccion

class TransaccionRepositoryRoom(private val dao: TransaccionDao) : TransaccionRepository {

    override suspend fun agregar(transaccion: Transaccion): Transaccion {

        val idNuevo = dao.insertar(TransaccionEntity.desde(transaccion.copy(id = 0)))
        return transaccion.copy(id = idNuevo.toInt())
    }

    override suspend fun listar(usuarioId: Int): List<Transaccion> {

        return dao.listar(usuarioId).map { it.aModelo() }
    }

    override suspend fun buscarPorId(id: Int): Transaccion? {
        return dao.buscarPorId(id)?.aModelo()
    }

    override suspend fun actualizar(transaccion: Transaccion): Boolean {

        return dao.actualizar(TransaccionEntity.desde(transaccion)) > 0
    }

    override suspend fun eliminar(id: Int): Boolean {

        return dao.eliminar(id) > 0
    }
}