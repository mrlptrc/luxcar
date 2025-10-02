package com.example.luxcar.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.luxcar.data.model.PosterImage

@Dao
interface PosterImageDao {
    @Insert
    suspend fun insert(image: PosterImage): Long

    @Insert
    suspend fun insertAll(images: List<PosterImage>)

    @Query("SELECT * FROM posters_images WHERE posterId = :posterId")
    suspend fun getByPosterId(posterId: Int): List<PosterImage>

    @Delete
    suspend fun delete(image: PosterImage)

    @Query("DELETE FROM posters_images WHERE posterId = :posterId")
    suspend fun deleteByPosterId(posterId: Int)
}