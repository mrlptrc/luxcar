package com.example.luxcar

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.Car
import com.example.luxcar.data.model.Poster
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosterCarDaoIntegrationTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun deveInserirEConsultarPosterComCarroRelacionado() = runBlocking {
        val car = Car(
            id = 1,
            marca = "Toyota",
            modelo = "Corolla",
            ano = 2020,
            cor = "Branco",
            placa = "ASEAES",
            cambio = "Automático",
            kilometragem = 30000.0,
            combustivel = "Flex",
            categoria = "Sedan",
            acessorios = listOf("Airbag")
        )

        db.carDao().insertCar(car)

        val poster = Poster(
            id = 1,
            titulo = "Corolla seminovo",
            descricao = "Ótimo estado",
            preco = 85000.0,
            imagem = byteArrayOf(),
            carId = 1
        )

        db.posterDao().insert(poster)

        val posters = db.posterDao().list()
        val cars = db.carDao().getAllCarsList()

        Assert.assertEquals(1, posters.size)
        Assert.assertEquals(1, cars.size)
        Assert.assertEquals("Toyota", cars.first().marca)
        Assert.assertEquals(1, posters.first().carId)
    }

    @Test
    fun deveAtualizarPosterNoBanco() = runBlocking {

        val car = Car(
            id = 1,
            marca = "Toyota",
            modelo = "Corolla",
            ano = 2020,
            placa = "ASEAES",
            cambio = "Automático",
            cor = "Branco",
            kilometragem = 30000.0,
            combustivel = "Flex",
            categoria = "Sedan",
            acessorios = listOf("Airbag")
        )
        db.carDao().insertCar(car)

        val poster = Poster(1, "Antigo", "desc", 100.0, byteArrayOf(), 1)
        db.posterDao().insert(poster)

        val atualizado = poster.copy(titulo = "Atualizado", preco = 200.0)
        db.posterDao().update(atualizado)

        val result = db.posterDao().list().first()
        Assert.assertEquals("Atualizado", result.titulo)
        Assert.assertEquals(200.0, result.preco, 0.0)
    }


    @Test
    fun deveExcluirPosterEManterBancoConsistente() = runBlocking {

        val car = Car(
            id = 1,
            marca = "Toyota",
            modelo = "Corolla",
            ano = 2020,
            placa = "ASEAES",
            cambio = "Automático",
            cor = "Branco",
            kilometragem = 30000.0,
            combustivel = "Flex",
            categoria = "Sedan",
            acessorios = listOf("Airbag")
        )
        db.carDao().insertCar(car)

        val poster = Poster(1, "Teste", "desc", 100.0, byteArrayOf(), 1)
        db.posterDao().insert(poster)

        db.posterDao().delete(poster)

        val result = db.posterDao().list()
        Assert.assertTrue(result.isEmpty())
    }

}