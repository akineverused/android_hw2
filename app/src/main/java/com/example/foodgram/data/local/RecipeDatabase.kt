package com.example.foodgram.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RecipeEntity::class,
        RecipeInteractionEntity::class,
        RecipeRatingEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(RecipeConverters::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    abstract fun recipeInteractionDao(): RecipeInteractionDao

    abstract fun recipeRatingDao(): RecipeRatingDao
}