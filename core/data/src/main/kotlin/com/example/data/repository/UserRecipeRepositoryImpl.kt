package com.example.data.repository

import com.example.data.local.dao.UserRecipeDao
import com.example.data.local.entity.*
import com.example.domain.model.CookedRecipe
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.UserRecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRecipeRepositoryImpl @Inject constructor(
    private val dao: UserRecipeDao,
    private val firestoreRepo: FirestoreUserRecipeRepository
) : UserRecipeRepository {

    // ========== Want To Cook ==========
    override suspend fun addToWantToCook(userId: String, recipe: Recipe) {
        try {
            firestoreRepo.addToWantToCook(
                userId = userId,
                recipeId = recipe.id,
                name = recipe.name,
                imageUrl = recipe.imageUrl,
                category = recipe.category,
                source = "api"
            )
            dao.insertWantToCook(recipe.toWantToCookEntity(userId))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun removeFromWantToCook(userId: String, recipeId: String) {
        firestoreRepo.removeFromWantToCook(userId, recipeId)
        dao.deleteWantToCook(userId, recipeId)
    }

    override fun getWantToCookRecipes(userId: String): Flow<List<Recipe>> {
        return dao.getAllWantToCook(userId).map { entities ->
            entities.map { entity ->
                Recipe(
                    id = entity.recipeId,
                    name = entity.name,
                    cuisine = "",
                    imageUrl = entity.imageUrl,
                    category = entity.category,
                    ingredients = emptyList(),
                    instructions = emptyList()
                )
            }
        }
    }

    override suspend fun isInWantToCook(userId: String, recipeId: String): Boolean {
        return firestoreRepo.isInWantToCook(userId, recipeId)
    }

    override fun getWantToCookCount(userId: String): Flow<Int>  {
        return dao.getWantToCookCount(userId)
    }

    // ========== Cooked ==========
    override suspend fun addToCooked(userId: String, recipe: Recipe, rating: Int, note: String, photoPath: String?) {
        firestoreRepo.addToCooked(
            userId = userId,
            recipeId = recipe.id,
            name = recipe.name,
            imageUrl = recipe.imageUrl,
            category = recipe.category,
            rating = rating,
            note = note
        )
        dao.insertCooked(
            CookedEntity(
                userId = userId,
                recipeId = recipe.id,
                name = recipe.name,
                imageUrl = recipe.imageUrl,
                category = recipe.category,
                userRating = rating,
                userNote = note,
                userPhotoPath = photoPath
            )
        )
        dao.deleteWantToCook(userId, recipe.id)
    }

    override suspend fun updateCooked(userId: String, recipeId: String, rating: Int, note: String, photoPath: String?) {
        val existing = dao.getCookedById(userId, recipeId) ?: return
        firestoreRepo.updateOrCreateCooked(
            userId = userId,
            recipeId = recipeId,
            name = existing.name,
            imageUrl = existing.imageUrl,
            category = existing.category,
            rating = rating,
            note = note
        )
        dao.updateCooked(existing.copy(userRating = rating, userNote = note, userPhotoPath = photoPath))
    }

    override suspend fun removeFromCooked(userId: String, recipeId: String) {
        firestoreRepo.removeFromCooked(userId, recipeId)
        dao.deleteCooked(userId, recipeId)
    }

    override fun getCookedRecipes(userId: String): Flow<List<CookedRecipe>> {
        return dao.getAllCooked(userId).map { entities ->
            entities.map { entity ->
                CookedRecipe(
                    recipeId = entity.recipeId,
                    name = entity.name,
                    imageUrl = entity.imageUrl,
                    category = entity.category,
                    userRating = entity.userRating,
                    userNote = entity.userNote,
                    userPhotoPath = entity.userPhotoPath,
                    cookedAt = entity.cookedAt
                )
            }
        }
    }

    override suspend fun getCookedRecipeById(userId: String, recipeId: String): CookedRecipe? {
        return dao.getCookedById(userId, recipeId)?.toDomain()
    }

    override fun getCookedCount(userId: String): Flow<Int>  {
        return dao.getCookedCount(userId)
    }

    // ========== User Recipes ==========
    override suspend fun addUserRecipeToCooked(userId: String, recipe: UserRecipe, rating: Int, note: String, photoPath: String?) {
        firestoreRepo.addToCooked(
            userId = userId,
            recipeId = recipe.id,
            name = recipe.name,
            imageUrl = recipe.imagePath ?: "",
            category = recipe.category,
            rating = rating,
            note = note
        )
        dao.insertCooked(
            CookedEntity(
                userId = userId,
                recipeId = recipe.id,
                name = recipe.name,
                imageUrl = recipe.imagePath ?: "",
                category = recipe.category,
                userRating = rating,
                userNote = note,
                userPhotoPath = photoPath
            )
        )
    }

    override suspend fun saveUserRecipe(userId: String, recipe: UserRecipe) {
        val entity = recipe.toEntity(userId)
        firestoreRepo.saveCustomRecipe(userId, entity)
        dao.insertUserRecipe(entity)
    }

    override suspend fun updateUserRecipe(userId: String, recipe: UserRecipe) {
        val entity = recipe.toEntity(userId)
        firestoreRepo.updateCustomRecipe(userId, entity)
        dao.updateUserRecipe(entity)
    }

    override suspend fun deleteUserRecipe(userId: String, recipeId: String) {
        firestoreRepo.deleteCustomRecipe(userId, recipeId)
        dao.deleteUserRecipe(userId, recipeId)
    }

    override fun getUserRecipes(userId: String): Flow<List<UserRecipe>> {
        return dao.getAllUserRecipes(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getUserRecipeById(userId: String, id: String): UserRecipe? {
        return dao.getUserRecipeById(userId, id)?.toDomain()
    }

    override fun getUserRecipesCount(userId: String): Flow<Int>  {
        return dao.getUserRecipesCount(userId)
    }

    // ========== Синхронизация ==========
    override suspend fun syncAllDataFromFirestore(userId: String) {
        try {
            val remoteRecipes = firestoreRepo.getAllUserRecipesWithStatus(userId)
            val customRecipes = firestoreRepo.getAllCustomRecipes(userId)

            val wantToCookEntities = mutableListOf<WantToCookEntity>()
            val cookedEntities = mutableListOf<CookedEntity>()

            remoteRecipes.forEach { data ->
                val recipeId = data["recipeId"] as? String ?: return@forEach
                val status = data["status"] as? String ?: return@forEach
                val name = data["name"] as? String ?: ""
                val imageUrl = data["imageUrl"] as? String ?: ""
                val category = data["category"] as? String ?: ""

                when (status) {
                    "wantToCook" -> {
                        wantToCookEntities.add(
                            WantToCookEntity(
                                userId = userId,
                                recipeId = recipeId,
                                name = name,
                                imageUrl = imageUrl,
                                category = category,
                                addedAt = (data["dateAdded"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        )
                    }
                    "cooked" -> {
                        cookedEntities.add(
                            CookedEntity(
                                userId = userId,
                                recipeId = recipeId,
                                name = name,
                                imageUrl = imageUrl,
                                category = category,
                                userRating = (data["rating"] as? Number)?.toInt() ?: 0,
                                userNote = data["note"] as? String ?: "",
                                cookedAt = (data["dateCooked"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
            }

            dao.refreshAllUserData(
                userId = userId,
                wantToCook = wantToCookEntities,
                cooked = cookedEntities,
                userRecipes = customRecipes
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
