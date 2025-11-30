package com.example.luxcar.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() }
    }
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.length < 6 -> "A senha deve ter pelo menos 6 caracteres"
            !password.any { it.isDigit() } -> "A senha deve conter pelo menos 1 número"
            !password.any { it.isLetter() } -> "A senha deve conter pelo menos 1 letra"
            else -> null
        }
    }
}