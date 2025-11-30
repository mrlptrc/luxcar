package com.example.luxcar.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.luxcar.data.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    suspend fun findByEmail(email: String): User? {
        return getUserByEmail(email) }

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Int)
}