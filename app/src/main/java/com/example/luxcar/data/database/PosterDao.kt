package com.example.luxcar.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.luxcar.data.model.Poster

/**
 * DAO para operações de Posters (anúncios)
 * ✅ CORRIGIDO: Query getByCarId agora busca corretamente por carId
 */
@Dao
interface PosterDao {

    /**
     * Insere um novo poster
     * @return ID do poster inserido
     */
    @Insert
    suspend fun insert(poster: Poster): Long

    /**
     * Lista todos os posters
     */
    @Query("SELECT * FROM posters ORDER BY id DESC")
    suspend fun list(): List<Poster>

    /**
     * Busca poster por ID
     */
    @Query("SELECT * FROM posters WHERE id = :posterId LIMIT 1")
    suspend fun getById(posterId: Long): Poster?

    @Query("SELECT * FROM posters WHERE carId = :carId LIMIT 1")
    suspend fun getByCarId(carId: Long): Poster?
    @Query("SELECT * FROM posters WHERE carId = :carId")
    suspend fun getAllByCarId(carId: Long): List<Poster>

    @Update
    suspend fun update(poster: Poster)

    @Delete
    suspend fun delete(poster: Poster)

    @Query("DELETE FROM posters WHERE id = :posterId")
    suspend fun deleteById(posterId: Long)


    @Query("SELECT * FROM posters WHERE emNegociacao = 1")
    suspend fun getPostersEmNegociacao(): List<Poster>

    @Query("UPDATE posters SET emNegociacao = :status WHERE id = :posterId")
    suspend fun updateNegociacao(posterId: Long, status: Boolean)
}