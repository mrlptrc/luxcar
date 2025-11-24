package com.example.luxcar.ui.screens

import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User

suspend fun registerUser(
    db: AppDatabase,
    nome: String,
    email: String,
    senha: String,
    confirmSenha: String
): Result<String> {
    if (senha != confirmSenha) {
        return Result.failure(Exception("Senhas não coincidem"))
    }

    return try {
        db.userDao().insert(User(nome = nome, email = email, senha = senha))
        Result.success("Cadastro realizado com sucesso")
    } catch (e: Exception) {
        Result.failure(e)
    }
}
