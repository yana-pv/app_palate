package com.example.domain.repository

import com.example.domain.model.User
import com.example.utils.Resource

interface UserRepository {
    suspend fun uploadAvatar(bytes: ByteArray): Resource<String>
    fun getCurrentUser(): User?
}
