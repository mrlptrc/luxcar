package com.example.luxcar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posters",
    foreignKeys = [
        ForeignKey(
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["carId"])]
)
data class Poster(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val titulo: String,
    val descricao: String,
    val preco: Double,
    val imagem: ByteArray,
    val carId: Long,
    val emNegociacao: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Poster

        if (id != other.id) return false
        if (titulo != other.titulo) return false
        if (descricao != other.descricao) return false
        if (preco != other.preco) return false
        if (!imagem.contentEquals(other.imagem)) return false
        if (carId != other.carId) return false
        if (emNegociacao != other.emNegociacao) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + titulo.hashCode()
        result = 31 * result + descricao.hashCode()
        result = 31 * result + preco.hashCode()
        result = 31 * result + imagem.contentHashCode()
        result = 31 * result + carId.hashCode()
        result = 31 * result + emNegociacao.hashCode()
        return result
    }
}