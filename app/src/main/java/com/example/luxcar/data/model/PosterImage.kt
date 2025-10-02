package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "posters_images",
    foreignKeys = [ForeignKey(
        entity = Poster::class,
        parentColumns = ["id"],
        childColumns = ["posterId"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class PosterImage (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val posterId: Int,
    val image: ByteArray
)