package com.example.domain.repository

import com.example.domain.model.User
import com.example.utils.Resource


interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Resource<User>
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun logout()
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Boolean
}