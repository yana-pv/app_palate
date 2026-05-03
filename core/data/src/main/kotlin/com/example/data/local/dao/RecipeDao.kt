package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecipeEntity
import com.example.data.local.entity.RecipePreviewEntity
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>

    @Query("SELECT * FROM categories")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM recipe_previews WHERE id IN (:ids)")
    suspend fun getPreviewsByIds(ids: List<String>): List<RecipePreviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipePreviews(previews: List<RecipePreviewEntity>): List<Long>

    @Update
    suspend fun updateRecipePreview(preview: RecipePreviewEntity): Int

    @Query("SELECT * FROM recipe_previews WHERE categoryName = :categoryName")
    fun getRecipesByCategory(categoryName: String): Flow<List<RecipePreviewEntity>>

    @Query("SELECT * FROM recipe_previews WHERE cuisine = :cuisine")
    fun getRecipesByCuisine(cuisine: String?): Flow<List<RecipePreviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?
    
    @Query("DELETE FROM categories")
    suspend fun clearCategories(): Int
    
    @Query("DELETE FROM recipe_previews WHERE categoryName = :categoryName")
    suspend fun clearPreviewsByCategory(categoryName: String): Int

    @Query("DELETE FROM recipe_previews WHERE cuisine = :cuisine")
    suspend fun clearPreviewsByCuisine(cuisine: String?): Int
}
