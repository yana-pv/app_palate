package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CookedEntity
import com.example.data.local.entity.UserRecipeEntity
import com.example.data.local.entity.WantToCookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRecipeDao {

    // ========== Want To Cook ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWantToCook(item: WantToCookEntity)

    @Query("DELETE FROM want_to_cook WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun deleteWantToCook(userId: String, recipeId: String)

    @Query("SELECT * FROM want_to_cook WHERE userId = :userId ORDER BY addedAt DESC")
    fun getAllWantToCook(userId: String): Flow<List<WantToCookEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM want_to_cook WHERE userId = :userId AND recipeId = :recipeId)")
    suspend fun isInWantToCook(userId: String, recipeId: String): Boolean

    // ========== Cooked ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCooked(item: CookedEntity)

    @Update
    suspend fun updateCooked(item: CookedEntity)

    @Query("DELETE FROM cooked WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun deleteCooked(userId: String, recipeId: String)

    @Query("SELECT * FROM cooked WHERE userId = :userId ORDER BY cookedAt DESC")
    fun getAllCooked(userId: String): Flow<List<CookedEntity>>

    @Query("SELECT * FROM cooked WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun getCookedById(userId: String, recipeId: String): CookedEntity?

    // ========== User Recipes ==========
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRecipe(item: UserRecipeEntity)

    @Update
    suspend fun updateUserRecipe(item: UserRecipeEntity)

    @Query("DELETE FROM user_recipes WHERE userId = :userId AND id = :id")
    suspend fun deleteUserRecipe(userId: String, id: String)

    @Query("SELECT * FROM user_recipes WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllUserRecipes(userId: String): Flow<List<UserRecipeEntity>>

    @Query("SELECT * FROM user_recipes WHERE userId = :userId AND id = :id")
    suspend fun getUserRecipeById(userId: String, id: String): UserRecipeEntity?

    @Query("SELECT COUNT(*) FROM cooked WHERE userId = :userId")
    fun getCookedCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM want_to_cook WHERE userId = :userId")
    fun getWantToCookCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_recipes WHERE userId = :userId")
    fun getUserRecipesCount(userId: String): Flow<Int>
}