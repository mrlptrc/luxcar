package com.example.luxcar.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.luxcar.data.model.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Insert
    suspend fun insertCar(car: Car): Long

    @Delete
    suspend fun deleteCar(car: Car): Int

    @Update
    suspend fun updateCar(car: Car)

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    suspend fun getCarById(id: Long): Car?

    @Query("SELECT * FROM cars ORDER BY id DESC")
    fun getAllCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars ORDER BY id DESC")
    suspend fun getAllCarsList(): List<Car>

    @Query("SELECT * FROM cars WHERE marca LIKE '%' || :marca || '%'")
    suspend fun getCarsByMarca(marca: String): List<Car>

    @Query("SELECT * FROM cars WHERE ano = :ano")
    suspend fun getCarsByAno(ano: Int): List<Car>

    @Query("DELETE FROM cars WHERE id = :carId")
    suspend fun deleteById(carId: Long)

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun getCarCount(): Int
}