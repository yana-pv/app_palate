package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.RecipeDao
import com.example.data.local.dao.UserRecipeDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.CookedEntity
import com.example.data.local.entity.RecipeEntity
import com.example.data.local.entity.RecipePreviewEntity
import com.example.data.local.entity.UserRecipeEntity
import com.example.data.local.entity.WantToCookEntity

@Database(
    entities = [
        CategoryEntity::class,
        RecipePreviewEntity::class,
        RecipeEntity::class,

        WantToCookEntity::class,
        CookedEntity::class,
        UserRecipeEntity::class

    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    abstract fun userRecipeDao(): UserRecipeDao
}
