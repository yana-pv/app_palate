package com.example.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isDarkMode: Boolean = false,
    val language: String = "English",
    val isLoggedOut: Boolean = false,
    val cookedCount: Int = 0,
    val plannedCount: Int = 0,
    val ownRecipesCount: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: com.example.domain.repository.SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.isDarkMode().collect { isDark ->
                _uiState.value = _uiState.value.copy(isDarkMode = isDark)
            }
        }
        viewModelScope.launch {
            settingsRepository.getLanguage().collect { langCode ->
                val langDisplay = if (langCode == "ru") "Русский" else "English"
                _uiState.value = _uiState.value.copy(language = langDisplay)
            }
        }
    }

    private fun loadUserProfile() {
        val currentUser = authRepository.getCurrentUser()
        _uiState.value = _uiState.value.copy(user = currentUser)
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }

    fun setLanguage(lang: String, context: android.content.Context) {
        val langCode = when (lang) {
            "Russian", "Русский" -> "ru"
            else -> "en"
        }
        viewModelScope.launch {
            settingsRepository.setLanguage(langCode)
            com.example.utils.LocaleHelper.setLocale(context, langCode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }
    }
}
