package com.example.data.di

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.RecipeRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: com.example.data.repository.UserRepositoryImpl
    ): com.example.domain.repository.UserRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: com.example.data.repository.SettingsRepositoryImpl
    ): com.example.domain.repository.SettingsRepository

    @Binds
    @Singleton
    abstract fun bindUserRecipeRepository(
        impl: com.example.data.repository.UserRecipeRepositoryImpl
    ): com.example.domain.repository.UserRecipeRepository
}
