package com.example.foodgram.ui.theme.main

import com.example.foodgram.model.Recipe

data class MainUiState(
    val allRecipes: List<Recipe> = emptyList(),
    val availableRecipes: List<Recipe> = emptyList(),
    val savedRecipes: List<Recipe> = emptyList(),
    val myRecipes: List<Recipe> = emptyList(),
    val skippedRecipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Все",
    val selectedRecipeForDetails: Recipe? = null
)