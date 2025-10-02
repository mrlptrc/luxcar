// Poster.kt
package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posters")
data class Poster(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descricao: String,
    val preco: Double,
    val imagem: ByteArray,
    val carId: Int // referência para o carro
)
