package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car (
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val marca: String,
    val modelo: String,
    val cor: String,
    val ano: Int,
    val kilometragem: Double,
)