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
            _registerState.value = RegisterUiState.Error(com.example.auth.R.string.error_name_required)
            return
        }
        if (!validateEmail(email)) {
            _registerState.value = RegisterUiState.Error(com.example.auth.R.string.error_email_invalid)
            return
        }
        if (!validatePassword(password)) {
            _registerState.value = RegisterUiState.Error(com.example.auth.R.string.error_password_min)
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            when (val result = authRepository.register(name, email, password)) {
                is Resource.Success -> {
                    _registerState.value = RegisterUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _registerState.value = RegisterUiState.Error(mapErrorToResource(result.message))
                }
                else -> {}
            }
        }
    }

    fun login(email: String, password: String) {
        if (!validateEmail(email)) {
            _loginState.value = LoginUiState.Error(com.example.auth.R.string.error_email_invalid)
            return
        }
        if (!validatePassword(password)) {
            _loginState.value = LoginUiState.Error(com.example.auth.R.string.error_password_min)
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    _loginState.value = LoginUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _loginState.value = LoginUiState.Error(mapErrorToResource(result.message))
                }
                else -> {}
            }
        }
    }

    private fun mapErrorToResource(message: String?): Int {
        val error = message?.lowercase() ?: ""
        return when {
            error.contains("network") || error.contains("unable to resolve host") || error.contains("timeout") -> 
                com.example.auth.R.string.error_no_network
            error.contains("password") || error.contains("credential") || 
            error.contains("no user") || error.contains("identifier") ||
            error.contains("invalid") -> 
                com.example.auth.R.string.error_invalid_credentials
            error.contains("blocked") || error.contains("unusual activity") ->
                com.example.auth.R.string.error_unknown
            else -> com.example.auth.R.string.error_unknown
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
    data class Error(val messageRes: Int) : RegisterUiState()
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val messageRes: Int) : LoginUiState()
}