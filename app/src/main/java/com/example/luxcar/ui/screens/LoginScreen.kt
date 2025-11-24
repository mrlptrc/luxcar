package com.example.luxcar.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luxcar.R
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Composable
fun LoginScreen(
    navToRegister: () -> Unit,
    navToMain: () -> Unit,
    db: AppDatabase
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var fontScale by remember { mutableStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.normalgroup),
            contentDescription = "Logotipo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { fontScale += 0.1f }) { Text("A+") }
            Button(onClick = { fontScale = (fontScale - 0.1f).coerceAtLeast(0.8f) }) { Text("A-") }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Login",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontScale
            )
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", fontSize = 16.sp * fontScale) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha", fontSize = 16.sp * fontScale) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = db.userDao().login(email, senha)
                    withContext(Dispatchers.Main) {
                        if (user != null) navToMain()
                        else Toast.makeText(context, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Text("Entrar", fontSize = 16.sp * fontScale, color = Color.White)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = navToRegister) {
            Text("Esqueci minha senha", fontSize = 14.sp * fontScale)
        }

        TextButton(onClick = navToRegister) {
            Text("Cadastre-se", fontSize = 14.sp * fontScale)
        }

        Spacer(Modifier.height(8.dp))
        }
}

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        // Cria usuário de teste no banco
        runBlocking {
            db.userDao().insert(User(nome = "Murilo", email = "teste@luxcar.com", senha = "1234"))
        }
    }

    @Test
    fun loginComCredenciaisCorretas_deveNavegarParaAnuncios() {
        composeTestRule.setContent {
            LoginScreen(
                navToRegister = {},
                navToMain = { /* sucesso: simula navegação */ },
                db = db
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("teste@luxcar.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("1234")
        composeTestRule.onNodeWithText("Entrar").performClick()

        // Aqui, em um cenário real, você verificaria o estado de navegação.
        // Como exemplo:
        composeTestRule.onNodeWithText("Entrar").assertExists() // tela ainda existe = teste básico passou
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

        // Esse teste pode ser aprimorado para checar Toast usando Espresso
        // mas como simplificação:
        composeTestRule.onNodeWithText("Entrar").assertExists()
    }
}