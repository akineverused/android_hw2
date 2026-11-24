
package com.example.foodgram.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS recipe_interactions (
                recipeId INTEGER NOT NULL,
                status TEXT NOT NULL,
                PRIMARY KEY(recipeId),
                FOREIGN KEY(recipeId)
                    REFERENCES recipes(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_recipe_interactions_recipeId
            ON recipe_interactions(recipeId)
            """.trimIndent()
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS recipe_ratings (
                recipeId INTEGER NOT NULL,
                userId TEXT NOT NULL,
                rating INTEGER NOT NULL,
                PRIMARY KEY(recipeId, userId),
                FOREIGN KEY(recipeId)
                    REFERENCES recipes(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_recipe_ratings_recipeId
            ON recipe_ratings(recipeId)
            """.trimIndent()
        )
    }
}

object RecipeDatabaseProvider {

    @Volatile
    private var INSTANCE: RecipeDatabase? = null

    fun getDatabase(context: Context): RecipeDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                RecipeDatabase::class.java,
                "foodgram_database"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
                )
                .build()

            INSTANCE = instance

            instance
        }
    }
}