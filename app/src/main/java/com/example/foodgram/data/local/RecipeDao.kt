package com.example.foodgram.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Int): Flow<RecipeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Transaction
    suspend fun seedRecipesIfEmpty(recipes: List<RecipeEntity>) {
        if (getRecipeCount() == 0) {
            insertRecipes(recipes)
        }
    }

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)

    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    suspend fun getRecipeByIdOnce(recipeId: Int): RecipeEntity?

    @Query("""
        SELECT recipes.*
        FROM recipes
        INNER JOIN recipe_interactions
            ON recipes.id = recipe_interactions.recipeId
        WHERE recipe_interactions.status = :status
        ORDER BY recipes.id DESC
    """)
    fun getRecipesByStatus(status: String): Flow<List<RecipeEntity>>
}