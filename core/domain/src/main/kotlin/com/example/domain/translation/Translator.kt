package com.example.domain.translation

interface Translator {
    suspend fun translate(text: String, targetLanguage: String): String
    suspend fun translateList(texts: List<String>, targetLanguage: String): List<String>
}
