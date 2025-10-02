package com.example.luxcar.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegisterScreen (
    navToLogin: () -> Unit,
    db: AppDatabase
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmSenha by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cadastro", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation()
        )
        TextField(
            value = confirmSenha,
            onValueChange = { confirmSenha = it },
            label = { Text("Confirme a senha") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (senha != confirmSenha) {
                Toast.makeText(
                    context,
                    "Senhas não coincidem",
                    Toast.LENGTH_SHORT
                ).show()
                return@Button
            }

            // registra no banco
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    db.userDao().insert(User(nome = nome, email = email, senha = senha))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Cadastro realizado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        navToLogin()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            context,
                            "Erro ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }) {
            Text("Salvar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = navToLogin) {
            Text("Voltar ao login")
        }
    }
}