package com.example.data.di

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.FirestoreUserRecipeRepository
import com.example.data.repository.RecipeRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.RecipeRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
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
    abstract fun bindShoppingListRepository(
        impl: com.example.data.repository.ShoppingListRepositoryImpl
    ): com.example.domain.repository.ShoppingListRepository

    @Binds
    @Singleton
    abstract fun bindUserRecipeRepository(
        impl: com.example.data.repository.UserRecipeRepositoryImpl
    ): com.example.domain.repository.UserRecipeRepository

    @Binds
    @Singleton
    abstract fun bindMealPlanRepository(
        impl: com.example.data.repository.MealPlanRepositoryImpl
    ): com.example.domain.repository.MealPlanRepository
}

@Module
@InstallIn(SingletonComponent::class)
class FirestoreModule {

    @Provides
    @Singleton
    fun provideFirestoreUserRecipeRepository(
        firestore: FirebaseFirestore
    ): FirestoreUserRecipeRepository {
        return FirestoreUserRecipeRepository(firestore)
    }
}