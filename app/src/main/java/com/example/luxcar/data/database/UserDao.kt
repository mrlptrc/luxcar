package com.example.luxcar.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.luxcar.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User) // cadastra usuario

    @Query("SELECT * FROM users WHERE email = :email AND senha = :senha")
    suspend fun login(email: String, senha: String): User? // procura se o email e a senha existe no banco e faz login
}