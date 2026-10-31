package edu.umg.programacion2.appfinanzas.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Define que esta interfaz es un DAO para la base de datos
@Dao
interface TransaccionDao {

    // Guarda una nueva transacción
    @Insert
    suspend fun insertar(transaccion: TransaccionEntity): Long

    // Obtiene todas las transacciones de un usuario
    @Query("SELECT * FROM transacciones WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun listar(usuarioId: Int): List<TransaccionEntity>

    // Busca una transacción por su ID
    @Query("SELECT * FROM transacciones WHERE id = :id")
    suspend fun buscarPorId(id: Int): TransaccionEntity?

    // Actualiza los datos de una transacción
    @Update
    suspend fun actualizar(transaccion: TransaccionEntity): Int

    // Elimina una transacción por su ID
    @Query("DELETE FROM transacciones WHERE id = :id")
    suspend fun eliminar(id: Int): Int
}