package com.example.auth.di

import com.example.auth.ui.AuthViewModel
import com.example.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthViewModel(
        authRepository: AuthRepository
    ): AuthViewModel {
        return AuthViewModel(authRepository)
    }
}