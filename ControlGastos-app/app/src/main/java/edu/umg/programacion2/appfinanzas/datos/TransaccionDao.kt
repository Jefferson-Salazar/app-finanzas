package edu.umg.programacion2.appfinanzas.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DAO que define las operaciones de base de datos para las transacciones usando Room.
 */
@Dao
interface TransaccionDao {

    @Insert
    suspend fun insertar(transaccion: TransaccionEntity): Long

    @Query("SELECT * FROM transacciones WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun listar(usuarioId: Int): List<TransaccionEntity>

    @Query("SELECT * FROM transacciones WHERE id = :id")
    suspend fun buscarPorId(id: Int): TransaccionEntity?

    @Update
    suspend fun actualizar(transaccion: TransaccionEntity): Int

    @Query("DELETE FROM transacciones WHERE id = :id")
    suspend fun eliminar(id: Int): Int
}