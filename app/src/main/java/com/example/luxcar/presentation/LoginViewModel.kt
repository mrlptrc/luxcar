package com.example.luxcar.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxcar.data.repository.AuthRepository
import com.example.luxcar.presentation.login.LoginUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.State

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(LoginUiState())
    val uiState: State<LoginUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onSenhaChange(value: String) {
        _uiState.value = _uiState.value.copy(senha = value)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun increaseFont() {
        val current = _uiState.value.fontScale
        if (current < 1.6f) {
            _uiState.value = _uiState.value.copy(fontScale = current + 0.1f)
        }
    }

    fun decreaseFont() {
        val current = _uiState.value.fontScale
        if (current > 0.7f) {
            _uiState.value = _uiState.value.copy(fontScale = current - 0.1f)
        }
    }

    fun login() {
        val email = _uiState.value.email
        val senha = _uiState.value.senha

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val success = withContext(Dispatchers.IO) {
                authRepository.login(email, senha)
            }

            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loginSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Credenciais inválidas"
                )
            }
        }
    }
}

