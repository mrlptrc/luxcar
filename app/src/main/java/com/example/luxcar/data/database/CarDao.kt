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

    // insere o objeto carro para criar novo carro
    @Insert
    suspend fun insertCar(car: Car): Long

    // deleta o obj carro
    @Delete
    suspend fun deleteCar(car: Car): Int

    // atualiza o obj carro
    @Update
    suspend fun updateCar(car: Car): Int

    // busca carro pelo id dele
    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCarById(id: Int): Car?

    // flow cars permite busca automaticas da lista de carros
    @Query("SELECT * FROM cars")
    fun getAllCars(): Flow<List<Car>>
}