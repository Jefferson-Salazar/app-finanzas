package edu.umg.programacion2.appfinanzas.datos

import edu.umg.programacion2.appfinanzas.modelo.Transaccion

// Repositorio que conecta la lógica del core con el DAO de Room para manejar los datos
class TransaccionRepositoryRoom(private val dao: TransaccionDao) : TransaccionRepository {

    // Inserta una nueva transacción convirtiéndola a Entity y devuelve el objeto con su nuevo ID
    override suspend fun agregar(transaccion: Transaccion): Transaccion {
        val idNuevo = dao.insertar(TransaccionEntity.desde(transaccion.copy(id = 0)))
        return transaccion.copy(id = idNuevo.toInt())
    }

    // Obtiene la lista de transacciones de un usuario y las convierte de Entity a modelos del core
    override suspend fun listar(usuarioId: Int): List<Transaccion> {
        return dao.listar(usuarioId).map { it.aModelo() }
    }

    // Busca una transacción específica por ID y la convierte a modelo si existe
    override suspend fun buscarPorId(id: Int): Transaccion? {
        return dao.buscarPorId(id)?.aModelo()
    }

    // Actualiza un registro existente y devuelve verdadero si la operación afectó filas
    override suspend fun actualizar(transaccion: Transaccion): Boolean {
        return dao.actualizar(TransaccionEntity.desde(transaccion)) > 0
    }

    // Elimina un registro por ID y devuelve verdadero si se borró con éxito
    override suspend fun eliminar(id: Int): Boolean {
        return dao.eliminar(id) > 0
    }
}