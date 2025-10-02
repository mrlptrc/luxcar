package com.example.luxcar.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.luxcar.data.model.Poster

@Dao
interface PosterDao {
    @Insert
    suspend fun insert(poster: Poster)

    @Query("SELECT * FROM posters")
    suspend fun list(): List<Poster>;

    @Update
    suspend fun update(poster: Poster)

    @Delete
    suspend fun delete(poster: Poster)

    @Query("SELECT * FROM posters WHERE id = :carId LIMIT 1")
    fun getByCarId(carId: Int): Poster?
}