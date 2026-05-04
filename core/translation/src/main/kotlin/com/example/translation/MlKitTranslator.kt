package com.example.translation

import com.example.domain.translation.Translator
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlKitTranslator @Inject constructor() : Translator {

    private val mutex = Mutex()
    private var currentTranslator: com.google.mlkit.nl.translate.Translator? = null
    private var currentLangCode: String? = null

    private suspend fun getTranslator(targetLanguage: String): com.google.mlkit.nl.translate.Translator? = withContext(Dispatchers.IO) {
        val targetLang = when (targetLanguage.lowercase()) {
            "ru" -> TranslateLanguage.RUSSIAN
            else -> return@withContext null
        }

        mutex.withLock {
            if (currentLangCode == targetLanguage && currentTranslator != null) {
                return@withLock currentTranslator
            }

            currentTranslator?.close()
            
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLang)
                .build()
                
            val translator = Translation.getClient(options)
            val conditions = DownloadConditions.Builder().build()
            
            return@withLock try {
                translator.downloadModelIfNeeded(conditions).await()
                currentTranslator = translator
                currentLangCode = targetLanguage
                translator
            } catch (e: Exception) {
                translator.close()
                null
            }
        }
    }

    override suspend fun translate(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        if (text.isBlank() || targetLanguage.lowercase() == "en") return@withContext text
        
        val translator = getTranslator(targetLanguage) ?: return@withContext text

        try {
            translator.translate(text).await()
        } catch (e: Exception) {
            text
        }
    }

    override suspend fun translateList(texts: List<String>, targetLanguage: String): List<String> = withContext(Dispatchers.IO) {
        if (texts.isEmpty() || targetLanguage.lowercase() == "en") return@withContext texts
        
        val translator = getTranslator(targetLanguage) ?: return@withContext texts

        try {
            texts.map { text ->
                if (text.isNotBlank()) {
                    translator.translate(text)
                } else {
                    null
                }
            }.map { task ->
                task?.await() ?: ""
            }
        } catch (e: Exception) {
            texts
        }
    }
}
