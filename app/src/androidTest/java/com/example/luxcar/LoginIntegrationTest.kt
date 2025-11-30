package com.example.luxcar

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginIntegrationTest {

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
    fun loginDeveFuncionarNoBanco() = runBlocking {
        val user = User(1, "Teste", "teste@luxcar.com", "1234")
        db.userDao().insert(user)

        val login = db.userDao().findByEmail("teste@luxcar.com")

        Assert.assertNotNull(login)
        Assert.assertEquals("1234", login?.senha)
    }
}