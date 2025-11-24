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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luxcar.R
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen(
    navToLogin: () -> Unit,
    db: AppDatabase
) {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmSenha by remember { mutableStateOf("") }
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
            "Cadastro",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontScale
            )
        )

        Spacer(Modifier.height(16.dp))

        TextField(value = nome, onValueChange = { nome = it },
            label = { Text("Nome", fontSize = 16.sp * fontScale) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(value = email, onValueChange = { email = it },
            label = { Text("Email", fontSize = 16.sp * fontScale) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(value = senha, onValueChange = { senha = it },
            label = { Text("Senha", fontSize = 16.sp * fontScale) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextField(value = confirmSenha, onValueChange = { confirmSenha = it },
            label = { Text("Confirme a senha", fontSize = 16.sp * fontScale) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (senha != confirmSenha) {
                    Toast.makeText(context, "Senhas não coincidem", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        db.userDao().insert(User(nome = nome, email = email, senha = senha))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
                            navToLogin()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Text("Salvar", fontSize = 16.sp * fontScale)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = navToLogin) {
            Text("Voltar ao login", fontSize = 14.sp * fontScale)
        }
    }
}
