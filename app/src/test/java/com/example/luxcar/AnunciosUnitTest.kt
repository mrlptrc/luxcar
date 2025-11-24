package com.example.luxcar

import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AnunciosUnitTest {
    private lateinit var carros: List<Car>
    private lateinit var anuncios: List<Poster>

    @Before
    fun setup() {
        carros = listOf(
            Car(id = 1, marca = "Toyota", modelo = "Corolla", ano = 2020, cor = "Branco", kilometragem = 30000.0, combustivel = "Flex", categoria = "Sedan", acessorios = listOf("Airbag")),
            Car(id = 2, marca = "Honda", modelo = "Civic", ano = 2019, cor = "Preto", kilometragem = 40000.0, combustivel = "Gasolina", categoria = "Sedan", acessorios = listOf("ABS")),
            Car(id = 3, marca = "Jeep", modelo = "Renegade", ano = 2021, cor = "Cinza", kilometragem = 20000.0, combustivel = "Diesel", categoria = "SUV", acessorios = listOf("4x4"))
        )

        anuncios = listOf(
            Poster(id = 1, titulo = "Corolla seminovo", descricao = "Ótimo estado", preco = 85000.0, imagem = byteArrayOf(), carId = 1),
            Poster(id = 2, titulo = "Civic 2019 completo", descricao = "Bem cuidado", preco = 90000.0, imagem = byteArrayOf(), carId = 2),
            Poster(id = 3, titulo = "Renegade top", descricao = "SUV completo", preco = 120000.0, imagem = byteArrayOf(), carId = 3)
        )
    }

    @Test
    fun filtroPorMarcaOuModelo_deveRetornarResultadosCorretos() {
        val query = "civic"

        val filtrados = anuncios.filter { poster ->
            val car = carros.find { it.id.toLong() == poster.carId }
            val textoBusca = "${poster.titulo} ${car?.marca} ${car?.modelo}".lowercase()
            textoBusca.contains(query.lowercase())
        }

        assertEquals(1, filtrados.size)
        assertEquals("Civic 2019 completo", filtrados.first().titulo)
    }

    @Test
    fun filtroVazio_deveRetornarTodosAnuncios() {
        val query = ""

        val filtrados = anuncios.filter { poster ->
            val car = carros.find { it.id.toLong() == poster.carId }
            val textoBusca = "${poster.titulo} ${car?.marca} ${car?.modelo}".lowercase()
            textoBusca.contains(query.lowercase())
        }

        assertEquals(anuncios.size, filtrados.size)
    }

    @Test
    fun removerAnuncio_deveAtualizarListaCorretamente() {
        val anuncioRemovido = anuncios.first()
        val novaLista = anuncios.filter { it.id != anuncioRemovido.id }

        assertEquals(anuncios.size - 1, novaLista.size)
        assertFalse(novaLista.any { it.id == anuncioRemovido.id })
    }

    @Test
    fun atualizarAnuncio_deveRefletirAlteracoes() {
        val anuncioAntigo = anuncios.first()
        val atualizado = anuncioAntigo.copy(preco = 88000.0, titulo = "Corolla atualizado")

        val novaLista = anuncios.map { if (it.id == atualizado.id) atualizado else it }

        val anuncioFinal = novaLista.find { it.id == atualizado.id }
        assertNotNull(anuncioFinal)
        assertEquals(88000.0, anuncioFinal?.preco ?: 0.0, 0.0)
        assertEquals("Corolla atualizado", anuncioFinal?.titulo)
    }
}