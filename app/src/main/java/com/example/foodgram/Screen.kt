package com.example.foodgram

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object AddRecipe : Screen("add_recipe")
    object Profile : Screen("profile")
}
