package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.RecipeDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecipeEntity
import com.example.data.local.entity.RecipePreviewEntity

@Database(
    entities = [
        CategoryEntity::class,
        RecipePreviewEntity::class,
        RecipeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
