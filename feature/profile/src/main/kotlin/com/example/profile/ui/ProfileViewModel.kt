package com.example.profile.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.UserRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.UserRecipeRepository
import com.example.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isDarkMode: Boolean = false,
    val language: String = "English",
    val isLoggedOut: Boolean = false,
    val cookedCount: Int = 0,
    val plannedCount: Int = 0,
    val ownRecipesCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,

    private val userRecipeRepository: UserRecipeRepository
) : ViewModel() {

    private val userId: String
        get() = userRepository.getCurrentUser()?.id ?: ""
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        observeSettings()
        loadStatistics()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.isDarkMode().collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
        viewModelScope.launch {
            settingsRepository.getLanguage().collect { langCode ->
                val langDisplay = if (langCode == "ru") "Русский" else "English"
                _uiState.update { it.copy(language = langDisplay) }
            }
        }
    }

    private fun loadUserProfile() {
        val currentUser = userRepository.getCurrentUser()
        _uiState.update { it.copy(user = currentUser) }
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
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    fun uploadAvatar(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val bytes = withContext(Dispatchers.IO) {
                try {
                    contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    null
                }
            }

            if (bytes == null) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to read image") }
                return@launch
            }

            val result = withContext(Dispatchers.IO) {
                userRepository.uploadAvatar(bytes)
            }

            _uiState.update { state ->
                when (result) {
                    is Resource.Success -> {
                        val freshUrl = "${result.data}?t=${System.currentTimeMillis()}"
                        state.copy(
                            isLoading = false,
                            user = state.user?.copy(avatarUrl = freshUrl)
                        )
                    }
                    is Resource.Error -> {
                        state.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    else -> state.copy(isLoading = false)
                }
            }
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            userRecipeRepository.getCookedCount(userId).collect { cookedCount ->
                _uiState.update { it.copy(cookedCount = cookedCount) }
            }
        }
        viewModelScope.launch {
            userRecipeRepository.getWantToCookCount(userId).collect { plannedCount ->
                _uiState.update { it.copy(plannedCount = plannedCount) }
            }
        }
        viewModelScope.launch {
            userRecipeRepository.getUserRecipesCount(userId).collect { ownRecipesCount ->
                _uiState.update { it.copy(ownRecipesCount = ownRecipesCount) }
            }
        }
    }
}
