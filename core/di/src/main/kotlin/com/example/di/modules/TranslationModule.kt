package com.example.di.modules

import com.example.domain.translation.Translator
import com.example.translation.MlKitTranslator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationModule {

    @Binds
    @Singleton
    abstract fun bindTranslator(mlKitTranslator: MlKitTranslator): Translator
}
