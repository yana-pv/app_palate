package com.example.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun validateName(name: String): Boolean = name.isNotBlank()

    fun validateEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun validatePassword(password: String): Boolean = password.length >= 6

    fun validatePasswordMatch(password: String, confirmPassword: String): Boolean =
        password == confirmPassword

    fun register(name: String, email: String, password: String) {
        if (!validateName(name)) {
            _registerState.value = RegisterUiState.Error("Введите имя")
            return
        }
        if (!validateEmail(email)) {
            _registerState.value = RegisterUiState.Error("Введите корректный email")
            return
        }
        if (!validatePassword(password)) {
            _registerState.value = RegisterUiState.Error("Пароль должен быть не менее 6 символов")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            when (val result = authRepository.register(name, email, password)) {
                is Resource.Success -> {
                    _registerState.value = RegisterUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _registerState.value = RegisterUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun login(email: String, password: String) {
        if (!validateEmail(email)) {
            _loginState.value = LoginUiState.Error("Введите корректный email")
            return
        }
        if (!validatePassword(password)) {
            _loginState.value = LoginUiState.Error("Пароль должен быть не менее 6 символов")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    _loginState.value = LoginUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _loginState.value = LoginUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: User) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}