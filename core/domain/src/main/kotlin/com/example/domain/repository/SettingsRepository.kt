package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    fun getLanguage(): Flow<String>
    suspend fun setLanguage(languageCode: String)
}
