package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "cars")
data class Car (
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val marca: String,
    val modelo: String,
    val cor: String,
    val ano: Int,
    val kilometragem: Double,
    val combustivel: String,
    val categoria: String,
    val acessorios: List<String>
)

class Converters{
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toList(data: String): List<String> =
            if(data.isEmpty()) emptyList() else data.split(",")
}