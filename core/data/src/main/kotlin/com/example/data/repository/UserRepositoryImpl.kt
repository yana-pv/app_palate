package com.example.data.repository

import androidx.core.net.toUri
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val supabase: SupabaseClient
) : UserRepository {

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString()
        )
    }

    override suspend fun uploadAvatar(bytes: ByteArray): Resource<String> {
        return try {
            val firebaseUser = auth.currentUser ?: return Resource.Error("User not logged in")
            val userId = firebaseUser.uid
            val bucket = supabase.storage.from("palate-images")
            val path = "avatars/$userId.jpg"

            bucket.upload(path, bytes) {
                upsert = true
            }

            val url = bucket.publicUrl(path)

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(url.toUri())
                .build()

            firebaseUser.updateProfile(profileUpdates).await()

            Resource.Success(url)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Upload failed")
        }
    }

    override suspend fun uploadRecipeImage(bytes: ByteArray, recipeId: String): Resource<String> {
        return try {
            val bucket = supabase.storage.from("palate-images")
            val path = "recipes/$recipeId/main.jpg"

            bucket.upload(path, bytes) {
                upsert = true
            }

            val url = bucket.publicUrl(path)
            Resource.Success(url)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Upload failed")
        }
    }
}
