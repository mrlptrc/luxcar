package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nome: String,
    val email: String,
    val senha: String
)