package com.example.foodgram.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeInteractionDao {
    @Query("SELECT * FROM recipe_interactions WHERE status = :status")
    fun getRecipesByStatus(status: String): Flow<List<RecipeInteractionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveInteraction(interaction: RecipeInteractionEntity)

    @Query("DELETE FROM recipe_interactions WHERE recipeId = :recipeId")
    suspend fun deleteInteraction(recipeId: Int)

    @Query("DELETE FROM recipe_interactions")
    suspend fun clearAllInteractions()
}