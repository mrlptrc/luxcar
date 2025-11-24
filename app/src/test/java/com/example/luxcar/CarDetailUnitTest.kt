package com.example.luxcar

import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CarDetailUnitTest {

    private lateinit var car: Car
    private lateinit var poster: Poster

    @Before
    fun setup() {
        car = Car(
            id = 10,
            marca = "Toyota",
            modelo = "Corolla",
            ano = 2022,
            cor = "Prata",
            kilometragem = 15000.0,
            combustivel = "Flex",
            categoria = "Sedan",
            acessorios = listOf("Airbag", "ABS", "Ar-condicionado")
        )

        poster = Poster(
            id = 1,
            titulo = "Corolla 2022 impecável",
            descricao = "Único dono, revisões feitas na concessionária",
            preco = 115000.0,
            imagem = byteArrayOf(),
            carId = car.id.toLong()
        )
    }

    @Test
    fun deveExibirInformacoesCorretasDoCarro() {
        assertEquals("Toyota", car.marca)
        assertEquals("Corolla", car.modelo)
        assertEquals(2022, car.ano)
        assertEquals("Prata", car.cor)
        assertEquals("Flex", car.combustivel)
        assertEquals("Sedan", car.categoria)
        assertTrue(car.acessorios.contains("Airbag"))
    }

    @Test
    fun deveExibirInformacoesCorretasDoAnuncio() {
        assertEquals("Corolla 2022 impecável", poster.titulo)
        assertEquals("Único dono, revisões feitas na concessionária", poster.descricao)
        assertEquals(115000.0, poster.preco, 0.0)
        assertEquals(car.id, poster.carId)
    }

    @Test
    fun deveFormatarPrecoCorretamente() {
        val precoFormatado = "R$ " + String.format("%,.2f", poster.preco).replace(",", ".")
        assertEquals("R$ 115000.00", precoFormatado)
    }

    @Test
    fun deveConterAcessoriosPrincipais() {
        val obrigatorios = listOf("Airbag", "ABS")
        val contemTodos = obrigatorios.all { it in car.acessorios }
        assertTrue("O carro deveria conter todos os acessórios obrigatórios", contemTodos)
    }

    @Test
    fun deveDetectarCarroSemDescricaoDetalhada() {
        val posterSemDescricao = poster.copy(descricao = "")
        val descricaoValida = posterSemDescricao.descricao.isNotBlank()
        assertFalse("A descrição não deveria estar vazia", descricaoValida)
    }

    @Test
    fun deveManterIntegridadeDeRelacionamentoEntreAnuncioECarro() {
        assertEquals(poster.carId, car.id)
        assertTrue("IDs deveriam corresponder", poster.carId.toInt() == car.id)
    }
}
