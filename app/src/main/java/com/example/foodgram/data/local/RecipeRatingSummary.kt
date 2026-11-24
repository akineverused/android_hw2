package com.example.foodgram.data.local

import androidx.room.ColumnInfo

data class RecipeRatingSummary(
    val recipeId: Int,

    @ColumnInfo(name = "averageRating")
    val averageRating: Double?,

    @ColumnInfo(name = "ratingCount")
    val ratingCount: Int
)