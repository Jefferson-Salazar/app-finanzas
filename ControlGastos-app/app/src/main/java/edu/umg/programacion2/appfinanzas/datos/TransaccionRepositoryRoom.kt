package edu.umg.programacion2.appfinanzas.datos

import edu.umg.programacion2.appfinanzas.modelo.Transaccion

class TransaccionRepositoryRoom(private val dao: TransaccionDao) : TransaccionRepository {

    override suspend fun agregar(transaccion: Transaccion): Transaccion {
        // Forzamos id = 0 para que SQLite genere el ID automáticamente por autoGenerate
        val idNuevo = dao.insertar(TransaccionEntity.desde(transaccion.copy(id = 0)))
        return transaccion.copy(id = idNuevo.toInt())
    }

    override suspend fun listar(usuarioId: Int): List<Transaccion> {
        // Convierte cada TransaccionEntity de la base de datos al modelo puro del core
        return dao.listar(usuarioId).map { it.aModelo() }
    }

    override suspend fun buscarPorId(id: Int): Transaccion? {
        return dao.buscarPorId(id)?.aModelo()
    }

    override suspend fun actualizar(transaccion: Transaccion): Boolean {
        // Retorna true si se actualizó al menos una fila
        return dao.actualizar(TransaccionEntity.desde(transaccion)) > 0
    }

    override suspend fun eliminar(id: Int): Boolean {
        // Retorna true si se eliminó al menos una fila
        return dao.eliminar(id) > 0
    }
}