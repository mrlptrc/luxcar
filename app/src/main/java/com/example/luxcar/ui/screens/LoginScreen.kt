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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                .size(140.dp)
                .padding(bottom = 32.dp)
        )

        // título
        Text(
            stringResource(R.string.login_title),
            fontSize = (28.sp.value * fontScale).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            stringResource(R.string.login_subtitle),
            fontSize = (14.sp.value * fontScale).sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        // campos
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
            )
        )

        Spacer(Modifier.height(16.dp))

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
            )
        )

        Spacer(Modifier.height(24.dp))

        // botão login
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = db.userDao().login(email, senha)
                    withContext(Dispatchers.Main) {
                        if (user != null) navToMain()
                        else Toast.makeText(
                            context,
                            context.getString(R.string.login_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                stringResource(R.string.login_button),
                fontSize = (16.sp.value * fontScale).sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        // esqueci a senha
        TextButton(onClick = {
            Toast.makeText(
                context,
                context.getString(R.string.forgot_password_message),
                Toast.LENGTH_LONG
            ).show()
        }) {
            Text(
                stringResource(R.string.forgot_password),
                fontSize = (13.sp.value * fontScale).sp,
                color = Color(0xFF757575)
            )
        }

        Spacer(Modifier.height(8.dp))

        // link cadastro
        TextButton(onClick = navToRegister) {
            Text(
                stringResource(R.string.register_link),
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
                )
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
                )
            ) {
                Text(
                    stringResource(R.string.font_decrease),
                    fontSize = (14.sp.value * fontScale).sp
                )
            }
        }
    }
}