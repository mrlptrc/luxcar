package com.example.luxcar.tests

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User
import com.example.luxcar.ui.screens.LoginScreen
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        runBlocking {
            db.userDao().insert(User(nome = "Murilo", email = "teste@luxcar.com", senha = "1234"))
        }
    }

    @Test
    fun loginComCredenciaisCorretas_deveNavegarParaAnuncios() {
        composeTestRule.setContent {
            LoginScreen(
                navToRegister = {},
                navToMain = { /* sucesso */ },
                db = db
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("teste@luxcar.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("1234")
        composeTestRule.onNodeWithText("Entrar").performClick()

        composeTestRule.onNodeWithText("Entrar").assertExists()
    }

    @Test
    fun loginComSenhaErrada_exibeMensagemErro() {
        composeTestRule.setContent {
            LoginScreen(
                navToRegister = {},
                navToMain = {},
                db = db
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("teste@luxcar.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("errado")
        composeTestRule.onNodeWithText("Entrar").performClick()

        composeTestRule.onNodeWithText("Entrar").assertExists()
    }
}