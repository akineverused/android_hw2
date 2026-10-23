package com.example.foodgram.model

data class Recipe(
    val id: Int,
    val title: String,
    val category: String,
    val cookingTimeMinutes: Int,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val imageUrl: String = ""
)