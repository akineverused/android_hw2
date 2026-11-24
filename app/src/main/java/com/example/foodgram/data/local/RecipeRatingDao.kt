package com.example.foodgram.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRating(rating: RecipeRatingEntity)

    @Query("""
        SELECT * FROM recipe_ratings
        WHERE recipeId = :recipeId
    """)
    fun getRatingsForRecipe(recipeId: Int): Flow<List<RecipeRatingEntity>>

    @Query("""
        SELECT * FROM recipe_ratings
        WHERE recipeId = :recipeId AND userId = :userId
        LIMIT 1
    """)
    fun getUserRating(
        recipeId: Int,
        userId: String
    ): Flow<RecipeRatingEntity?>

    @Query("""
        SELECT AVG(rating) FROM recipe_ratings
        WHERE recipeId = :recipeId
    """)
    fun getAverageRating(recipeId: Int): Flow<Double?>

    @Query("""
        SELECT COUNT(*) FROM recipe_ratings
        WHERE recipeId = :recipeId
    """)
    fun getRatingCount(recipeId: Int): Flow<Int>

    @Query("""
    SELECT
        recipeId,
        AVG(rating) AS averageRating,
        COUNT(*) AS ratingCount
    FROM recipe_ratings
    GROUP BY recipeId
""")
    fun getAllRatingSummaries(): Flow<List<RecipeRatingSummary>>
}