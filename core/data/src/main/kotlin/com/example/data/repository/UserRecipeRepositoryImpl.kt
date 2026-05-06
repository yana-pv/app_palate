package com.example.data.repository

import com.example.data.local.dao.UserRecipeDao
import com.example.data.local.entity.*
import com.example.domain.model.CookedRecipe
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.UserRecipeRepository
import com.example.domain.usecase.GetRecipeByIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRecipeRepositoryImpl @Inject constructor(
    private val dao: UserRecipeDao,
    private val firestoreRepo: FirestoreUserRecipeRepository,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase  // ← добавить
) : UserRecipeRepository {

    // ========== Want To Cook ==========
    override suspend fun addToWantToCook(userId: String, recipe: Recipe) {
        try {
            firestoreRepo.addToWantToCook(userId, recipe.id, "api")
            dao.insertWantToCook(recipe.toWantToCookEntity(userId))
        } catch (e: Exception) {
            // логируем
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
        firestoreRepo.addToCooked(userId, recipe.id, rating, note)
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
        if (firestoreRepo.isInWantToCook(userId, recipe.id)) {
            firestoreRepo.removeFromWantToCook(userId, recipe.id)
            dao.deleteWantToCook(userId, recipe.id)
        }
    }

    override suspend fun updateCooked(userId: String, recipeId: String, rating: Int, note: String, photoPath: String?) {
        firestoreRepo.updateOrCreateCooked(userId, recipeId, rating, note)
        val existing = dao.getCookedById(userId, recipeId) ?: return
        dao.updateCooked(existing.copy(userRating = rating, userNote = note))
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
        firestoreRepo.addToCooked(userId, recipe.id, rating, note)
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
        val statusMap = firestoreRepo.getUserRecipesStatus(userId)

        val customRecipes = firestoreRepo.getAllCustomRecipes(userId)

        dao.clearAllUserData(userId)

        customRecipes.forEach { recipe ->
            dao.insertUserRecipe(recipe)
        }

        // Обрабатываем wantToCook и cooked
        statusMap.forEach { (recipeId, status) ->
            if (status == "wantToCook") {
                // Получаем рецепт из API
                val recipe = getRecipeByIdUseCase(recipeId)
                recipe?.let {
                    dao.insertWantToCook(it.toWantToCookEntity(userId))
                }
            } else if (status == "cooked") {
                // Получаем cooked данные из Firestore
                val cookedData = firestoreRepo.getCookedData(userId, recipeId)
                cookedData?.let {
                    dao.insertCooked(
                        CookedEntity(
                            userId = userId,
                            recipeId = recipeId,
                            name = cookedData.name,
                            imageUrl = cookedData.imageUrl,
                            category = cookedData.category,
                            userRating = cookedData.rating,
                            userNote = cookedData.note,
                            cookedAt = cookedData.dateCooked
                        )
                    )
                }
            }
        }
    }
}