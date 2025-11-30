package com.example.luxcar.data.repository

import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.utils.PasswordHasher

class AuthRepository(private val db: AppDatabase) {

    suspend fun login(email: String, senha: String): Boolean {
        val user = db.userDao().getUserByEmail(email) ?: return false
        return PasswordHasher.checkPassword(senha, user.senha)
    }
}
