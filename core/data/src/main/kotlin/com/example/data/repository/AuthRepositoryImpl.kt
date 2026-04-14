package com.example.data.repository

import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Resource<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Ошибка создания пользователя")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = email
            )
            firestore.collection("users").document(firebaseUser.uid).set(user).await()

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка регистрации")
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Resource.Error("Ошибка входа")

            val document = firestore.collection("users").document(firebaseUser.uid).get().await()
            val name = document.getString("name") ?: firebaseUser.displayName ?: ""

            val user = User(
                id = firebaseUser.uid,
                name = name,
                email = firebaseUser.email ?: email
            )
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Ошибка входа")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: ""
        )
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}