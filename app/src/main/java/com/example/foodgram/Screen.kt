
package com.example.foodgram

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object AddRecipe : Screen("add_recipe")
    object Profile : Screen("profile")

    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String): String {
            return "user_profile/$userId"
        }
    }
}
