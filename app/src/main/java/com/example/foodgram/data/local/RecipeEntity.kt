package com.example.foodgram.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: Int,

    val title: String,
    val category: String,
    val cookingTimeMinutes: Int,
    val description: String,

    val ingredients: List<String>,
    val instructions: List<String>,

    val imageUrl: String = "",
    val authorId: String = "demo_chef",
    val authorName: String = "Шеф-повар"
)