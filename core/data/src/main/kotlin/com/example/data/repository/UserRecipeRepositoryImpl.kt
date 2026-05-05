package com.example.data.repository

import com.example.data.local.dao.UserRecipeDao
import com.example.data.local.entity.CookedEntity
import com.example.data.local.entity.WantToCookEntity
import com.example.data.local.entity.toDomain
import com.example.data.local.entity.toEntity
import com.example.domain.model.CookedRecipe
import com.example.domain.model.Recipe
import com.example.domain.model.UserRecipe
import com.example.domain.repository.UserRecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRecipeRepositoryImpl @Inject constructor(
    private val dao: UserRecipeDao
) : UserRecipeRepository {

    //  Want To Cook
    override suspend fun addToWantToCook(userId: String, recipe: Recipe) {
        dao.insertWantToCook(
            WantToCookEntity(
                userId = userId,
                recipeId = recipe.id,
                name = recipe.name,
                imageUrl = recipe.imageUrl,
                category = recipe.category
            )
        )
    }

    override suspend fun removeFromWantToCook(userId: String, recipeId: String) {
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
        return dao.isInWantToCook(userId, recipeId)
    }

    // Cooked
    override suspend fun addToCooked(
        userId: String,
        recipe: Recipe,
        rating: Int,
        note: String,
        photoPath: String?
    ) {
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
        if (dao.isInWantToCook(userId, recipe.id)) {
            dao.deleteWantToCook(userId, recipe.id)
        }
    }

    override suspend fun updateCooked(
        userId: String,
        recipeId: String,
        rating: Int,
        note: String,
        photoPath: String?
    ) {
        val existing = dao.getCookedById(userId, recipeId) ?: return
        dao.updateCooked(
            existing.copy(
                userRating = rating,
                userNote = note,
                userPhotoPath = photoPath
            )
        )
    }

    override suspend fun removeFromCooked(userId: String, recipeId: String) {
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

    //  User Recipes
    override suspend fun saveUserRecipe(userId: String, recipe: UserRecipe) {
        dao.insertUserRecipe(recipe.toEntity(userId))
    }

    override suspend fun updateUserRecipe(userId: String, recipe: UserRecipe) {
        dao.updateUserRecipe(recipe.toEntity(userId))
    }

    override suspend fun deleteUserRecipe(userId: String, recipeId: String) {
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

    override suspend fun addUserRecipeToCooked(userId: String, recipe: UserRecipe, rating: Int, note: String, photoPath: String?) {
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
        if (dao.isInWantToCook(userId, recipe.id)) {
            dao.deleteWantToCook(userId, recipe.id)
        }
    }

    override fun getCookedCount(userId: String): Flow<Int> {
        return dao.getCookedCount(userId)
    }

    override fun getWantToCookCount(userId: String): Flow<Int> {
        return dao.getWantToCookCount(userId)
    }

    override fun getUserRecipesCount(userId: String): Flow<Int> {
        return dao.getUserRecipesCount(userId)
    }
}