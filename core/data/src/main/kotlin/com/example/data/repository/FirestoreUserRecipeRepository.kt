package com.example.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.data.local.entity.UserRecipeEntity
import com.example.domain.model.Ingredient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserRecipeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ========== Want To Cook ==========
    suspend fun addToWantToCook(
        userId: String,
        recipeId: String,
        name: String,
        imageUrl: String,
        category: String,
        source: String
    ) {
        val data = mapOf(
            "recipeId" to recipeId,
            "name" to name,
            "imageUrl" to imageUrl,
            "category" to category,
            "source" to source,
            "status" to "wantToCook",
            "dateAdded" to System.currentTimeMillis()
        )
        firestore.collection("users/$userId/userRecipes")
            .document(recipeId)
            .set(data)
            .await()
    }

    suspend fun removeFromWantToCook(userId: String, recipeId: String) {
        firestore.collection("users/$userId/userRecipes")
            .document(recipeId)
            .delete()
            .await()
    }

    suspend fun isInWantToCook(userId: String, recipeId: String): Boolean {
        val snapshot = firestore.collection("users/$userId/userRecipes")
            .document(recipeId)
            .get()
            .await()
        return snapshot.exists() && snapshot.getString("status") == "wantToCook"
    }

    // ========== Cooked ==========
    suspend fun addToCooked(
        userId: String,
        recipeId: String,
        name: String,
        imageUrl: String,
        category: String,
        rating: Int,
        note: String
    ) {
        val data = mapOf(
            "recipeId" to recipeId,
            "status" to "cooked",
            "name" to name,
            "imageUrl" to imageUrl,
            "category" to category,
            "rating" to rating,
            "note" to note,
            "dateCooked" to System.currentTimeMillis()
        )
        firestore.collection("users/$userId/userRecipes")
            .document(recipeId)
            .set(data)
            .await()
    }

    suspend fun updateOrCreateCooked(
        userId: String,
        recipeId: String,
        name: String,
        imageUrl: String,
        category: String,
        rating: Int,
        note: String
    ) {
        val docRef = firestore.collection("users/$userId/userRecipes").document(recipeId)
        val data = mapOf(
            "recipeId" to recipeId,
            "status" to "cooked",
            "name" to name,
            "imageUrl" to imageUrl,
            "category" to category,
            "rating" to rating,
            "note" to note,
            "dateCooked" to System.currentTimeMillis()
        )
        docRef.set(data).await()
    }

    suspend fun removeFromCooked(userId: String, recipeId: String) {
        firestore.collection("users/$userId/userRecipes")
            .document(recipeId)
            .delete()
            .await()
    }

    // ========== Custom Recipes ==========
    suspend fun saveCustomRecipe(userId: String, recipe: UserRecipeEntity) {
        val ingredientsList = recipe.ingredients.map { ingredient ->
            mapOf(
                "name" to ingredient.name,
                "amount" to ingredient.amount,
                "unit" to ingredient.unit
            )
        }

        val recipeData = mapOf(
            "name" to recipe.name,
            "cuisine" to recipe.category,
            "category" to recipe.category,
            "imageUrl" to recipe.imagePath,
            "instructions" to recipe.instructions,
            "ingredients" to ingredientsList,
            "createdAt" to recipe.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("users/$userId/customRecipes")
            .document(recipe.id)
            .set(recipeData)
            .await()
    }

    suspend fun updateCustomRecipe(userId: String, recipe: UserRecipeEntity) {
        val ingredientsList = recipe.ingredients.map { ingredient ->
            mapOf(
                "name" to ingredient.name,
                "amount" to ingredient.amount,
                "unit" to ingredient.unit
            )
        }

        val recipeData = mapOf(
            "name" to recipe.name,
            "cuisine" to recipe.category,
            "category" to recipe.category,
            "imageUrl" to recipe.imagePath,
            "instructions" to recipe.instructions,
            "ingredients" to ingredientsList,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("users/$userId/customRecipes")
            .document(recipe.id)
            .update(recipeData)
            .await()
    }

    suspend fun deleteCustomRecipe(userId: String, recipeId: String) {
        firestore.collection("users/$userId/customRecipes")
            .document(recipeId)
            .delete()
            .await()
    }

    suspend fun getCustomRecipe(userId: String, recipeId: String): UserRecipeEntity? {
        val snapshot = firestore.collection("users/$userId/customRecipes")
            .document(recipeId)
            .get()
            .await()

        if (!snapshot.exists()) return null

        val ingredientsList = snapshot.get("ingredients") as? List<Map<String, String>> ?: emptyList()
        val ingredients = ingredientsList.mapNotNull { ingredientMap ->
            val name = ingredientMap["name"] ?: return@mapNotNull null
            val amount = ingredientMap["amount"] ?: ""
            val unit = ingredientMap["unit"] ?: ""
            Ingredient(name, amount, unit)
        }

        return UserRecipeEntity(
            userId = userId,
            id = snapshot.id,
            name = snapshot.getString("name") ?: "",
            imagePath = snapshot.getString("imageUrl"),
            category = snapshot.getString("category") ?: "",
            cookingTime = 0,
            ingredients = ingredients,
            instructions = snapshot.getString("instructions") ?: "",
            createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
        )
    }

    // ========== Get All Recipes with Status ==========
    suspend fun getAllUserRecipesWithStatus(userId: String): List<Map<String, Any>> {
        val snapshot = firestore.collection("users/$userId/userRecipes").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.data?.toMutableMap()?.apply {
                put("recipeId", doc.id)
            }
        }
    }

    suspend fun getAllCustomRecipes(userId: String): List<UserRecipeEntity> {
        val snapshot = firestore.collection("users/$userId/customRecipes").get().await()
        return snapshot.documents.mapNotNull { document ->
            val ingredientsList = document.get("ingredients") as? List<Map<String, String>> ?: emptyList()
            val ingredients = ingredientsList.mapNotNull { ingredientMap ->
                val name = ingredientMap["name"] ?: return@mapNotNull null
                val amount = ingredientMap["amount"] ?: ""
                val unit = ingredientMap["unit"] ?: ""
                Ingredient(name, amount, unit)
            }

            UserRecipeEntity(
                userId = userId,
                id = document.id,
                name = document.getString("name") ?: "",
                imagePath = document.getString("imageUrl"),
                category = document.getString("category") ?: "",
                cookingTime = 0,
                ingredients = ingredients,
                instructions = document.getString("instructions") ?: "",
                createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
            )
        }
    }
}


