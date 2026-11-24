package com.example.foodgram.data.repository

import com.example.foodgram.data.local.RecipeDao
import com.example.foodgram.data.local.RecipeInteractionDao
import com.example.foodgram.data.local.RecipeInteractionEntity
import com.example.foodgram.data.local.toRecipe
import com.example.foodgram.data.local.toRecipeEntity
import com.example.foodgram.data.local.RecipeRatingDao
import com.example.foodgram.data.local.RecipeRatingEntity
import com.example.foodgram.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val interactionDao: RecipeInteractionDao,
    private val ratingDao: RecipeRatingDao
) {

    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { recipes ->
            recipes.map { it.toRecipe() }
        }
    }

    fun getSavedRecipes(): Flow<List<Recipe>> {
        return recipeDao.getRecipesByStatus("SAVED").map { recipes ->
            recipes.map { it.toRecipe() }
        }
    }

    fun getSkippedRecipes(): Flow<List<Recipe>> {
        return recipeDao.getRecipesByStatus("SKIPPED").map { recipes ->
            recipes.map { it.toRecipe() }
        }
    }

    suspend fun seedInitialRecipes(recipes: List<Recipe>) {
        recipeDao.seedRecipesIfEmpty(
            recipes.map { it.toRecipeEntity() }
        )
    }

    suspend fun saveRecipe(recipe: Recipe) {
        interactionDao.saveInteraction(
            RecipeInteractionEntity(
                recipeId = recipe.id,
                status = "SAVED"
            )
        )
    }

    suspend fun skipRecipe(recipe: Recipe) {
        interactionDao.saveInteraction(
            RecipeInteractionEntity(
                recipeId = recipe.id,
                status = "SKIPPED"
            )
        )
    }

    suspend fun removeInteraction(recipeId: Int) {
        interactionDao.deleteInteraction(recipeId)
    }

    suspend fun clearAllInteractions() {
        interactionDao.clearAllInteractions()
    }

    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toRecipeEntity())
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe.toRecipeEntity())
    }

    suspend fun deleteRecipe(recipeId: Int) {
        recipeDao.deleteRecipeById(recipeId)
    }

    suspend fun getRecipeById(id: Int): Recipe? {
        return recipeDao.getRecipeByIdOnce(id)?.toRecipe()
    }

    suspend fun saveRating(
        recipeId: Int,
        userId: String,
        rating: Int
    ) {
        require(rating in 1..5) {
            "Rating must be between 1 and 5"
        }

        ratingDao.saveRating(
            RecipeRatingEntity(
                recipeId = recipeId,
                userId = userId,
                rating = rating
            )
        )
    }

    fun getAverageRating(recipeId: Int): Flow<Double?> {
        return ratingDao.getAverageRating(recipeId)
    }

    fun getRatingCount(recipeId: Int): Flow<Int> {
        return ratingDao.getRatingCount(recipeId)
    }

    fun getUserRating(
        recipeId: Int,
        userId: String
    ): Flow<Int?> {
        return ratingDao.getUserRating(recipeId, userId)
            .map { it?.rating }
    }

    fun getAllRatingSummaries(): Flow<Map<Int, Pair<Double?, Int>>> {
        return ratingDao.getAllRatingSummaries().map { summaries ->
            summaries.associate { summary ->
                summary.recipeId to (summary.averageRating to summary.ratingCount)
            }
        }
    }
}