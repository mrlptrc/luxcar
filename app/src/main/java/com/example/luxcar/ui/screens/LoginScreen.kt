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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luxcar.R
import com.example.luxcar.presentation.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navToRegister: () -> Unit,
    navToMain: () -> Unit
) {
    val state by viewModel.uiState
    val context = LocalContext.current
    val Orange = Color(0xFFFF9800)

    val forgotPasswordMessage = stringResource(R.string.forgot_password_message)
    val forgotPasswordText = stringResource(R.string.forgot_password)
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) navToMain()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.normalgroup),
            contentDescription = stringResource(R.string.logo_description),
            modifier = Modifier
                .size(140.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = stringResource(R.string.login_title),
            fontSize = 28.sp * state.fontScale,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            fontSize = 14.sp * state.fontScale,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "email_input" }, // <- testTag adicionado
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange
            )
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.senha,
            onValueChange = viewModel::onSenhaChange,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "password_input" }, // <- testTag adicionado
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange,
                focusedLabelColor = Orange
            )
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .semantics { testTag = "login_button" }, // <- testTag adicionado
            enabled = !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    stringResource(R.string.login_button),
                    fontSize = 16.sp * state.fontScale,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                Toast.makeText(
                    context,
                    forgotPasswordMessage,
                    Toast.LENGTH_LONG
                ).show()
            },
            enabled = !state.isLoading,
            modifier = Modifier.semantics { testTag = "forgot_password_button" } // <- testTag adicionado
        ) {
            Text(
                forgotPasswordText,
                fontSize = 13.sp * state.fontScale,
                color = Color(0xFF757575)
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = navToRegister,
            enabled = !state.isLoading,
            modifier = Modifier.semantics { testTag = "register_button" } // <- testTag adicionado
        ) {
            Text(
                stringResource(R.string.register),
                fontSize = 14.sp * state.fontScale,
                color = Orange
            )
        }

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

            OutlinedButton(
                onClick = { viewModel.increaseFont() },
                shape = RoundedCornerShape(8.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Orange),
                modifier = Modifier.semantics { testTag = "increase_font_button" } // <- testTag adicionado
            ) {
                Text(
                    stringResource(R.string.font_increase),
                    fontSize = 14.sp * state.fontScale
                )
            }

            OutlinedButton(
                onClick = { viewModel.decreaseFont() },
                shape = RoundedCornerShape(8.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Orange),
                modifier = Modifier.semantics { testTag = "decrease_font_button" } // <- testTag adicionado
            ) {
                Text(
                    stringResource(R.string.font_decrease),
                    fontSize = 14.sp * state.fontScale
                )
            }
        }
    }
}
