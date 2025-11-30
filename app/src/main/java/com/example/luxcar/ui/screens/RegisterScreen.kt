package com.example.luxcar.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luxcar.R
import com.example.luxcar.data.database.AppDatabase
import com.example.luxcar.data.model.User
import com.example.luxcar.utils.PasswordHasher
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
    var isLoading by remember { mutableStateOf(false) }
    val Orange = Color(0xFFFF9800)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // logo
        Image(
            painter = painterResource(id = R.drawable.normalgroup),
            contentDescription = stringResource(id = R.string.logo_description),
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 32.dp)
        )

        // título
        Text(
            stringResource(R.string.register_title),
            fontSize = (28.sp.value * fontScale).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            stringResource(R.string.register_subtitle),
            fontSize = (14.sp.value * fontScale).sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // campo nome
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = {
                Text(
                    stringResource(R.string.name),
                    fontSize = (14.sp.value * fontScale).sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange
            ),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        // campo email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    stringResource(R.string.email),
                    fontSize = (14.sp.value * fontScale).sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange
            ),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        // campo senha
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = {
                Text(
                    stringResource(R.string.password),
                    fontSize = (14.sp.value * fontScale).sp
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange
            ),
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        // campo confirmar senha
        OutlinedTextField(
            value = confirmSenha,
            onValueChange = { confirmSenha = it },
            label = {
                Text(
                    stringResource(R.string.confirm_password),
                    fontSize = (14.sp.value * fontScale).sp
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange
            ),
            enabled = !isLoading
        )

        Spacer(Modifier.height(24.dp))

        // botão cadastrar
        Button(
            onClick = {
                // ✅ VALIDAÇÕES
                if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                    Toast.makeText(
                        context,
                        "Preencha todos os campos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                if (senha != confirmSenha) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.password_mismatch),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                // ✅ VALIDA SENHA FORTE
                val passwordError = PasswordHasher.getPasswordErrorMessage(senha)
                if (passwordError != null) {
                    Toast.makeText(context, passwordError, Toast.LENGTH_LONG).show()
                    return@Button
                }

                isLoading = true

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // ✅ VERIFICA SE EMAIL JÁ EXISTE
                        val existingUser = db.userDao().getUserByEmail(email)
                        if (existingUser != null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Email já cadastrado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isLoading = false
                            }
                            return@launch
                        }

                        // ✅ CRIA HASH DA SENHA
                        val hashedPassword = PasswordHasher.hashPassword(senha)

                        // ✅ INSERE USUÁRIO COM SENHA HASHEADA
                        db.userDao().insert(
                            User(
                                nome = nome,
                                email = email,
                                senha = hashedPassword
                            )
                        )

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.register_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            navToLogin()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Erro: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    stringResource(R.string.register_button),
                    fontSize = (16.sp.value * fontScale).sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // link voltar
        TextButton(
            onClick = navToLogin,
            enabled = !isLoading
        ) {
            Text(
                stringResource(R.string.back_to_login),
                fontSize = (14.sp.value * fontScale).sp,
                color = Orange
            )
        }

        Spacer(Modifier.height(32.dp))

        // controles de fonte
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { fontScale += 0.1f },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Orange
                ),
                enabled = !isLoading
            ) {
                Text(
                    stringResource(R.string.font_increase),
                    fontSize = (14.sp.value * fontScale).sp
                )
            }

            OutlinedButton(
                onClick = { fontScale = (fontScale - 0.1f).coerceAtLeast(0.8f) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Orange
                ),
                enabled = !isLoading
            ) {
                Text(
                    stringResource(R.string.font_decrease),
                    fontSize = (14.sp.value * fontScale).sp
                )
            }
        }
    }
}