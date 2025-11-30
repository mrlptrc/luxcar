package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posters_images",
    foreignKeys = [
        ForeignKey(
            entity = Poster::class,
            parentColumns = ["id"],
            childColumns = ["posterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["posterId"])]
)
data class PosterImage (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val posterId: Long,
    val image: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PosterImage

        if (id != other.id) return false
        if (posterId != other.posterId) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + posterId.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }
}