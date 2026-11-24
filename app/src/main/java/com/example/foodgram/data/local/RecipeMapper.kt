package com.example.foodgram.data.local

import com.example.foodgram.model.Recipe

fun RecipeEntity.toRecipe(): Recipe {
    return Recipe(
        id = id,
        title = title,
        category = category,
        cookingTimeMinutes = cookingTimeMinutes,
        description = description,
        ingredients = ingredients,
        instructions = instructions,
        imageUrl = imageUrl,
        authorId = authorId,
        authorName = authorName
    )
}

fun Recipe.toRecipeEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        category = category,
        cookingTimeMinutes = cookingTimeMinutes,
        description = description,
        ingredients = ingredients,
        instructions = instructions,
        imageUrl = imageUrl,
        authorId = authorId,
        authorName = authorName
    )
}