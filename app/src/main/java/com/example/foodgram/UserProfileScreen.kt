
package com.example.foodgram

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodgram.ui.theme.components.DeleteSwipeDirection
import com.example.foodgram.ui.theme.components.ProfileRecipeCard
import com.example.foodgram.ui.theme.components.RecipeDetailBottomSheet
import com.example.foodgram.ui.theme.components.SwipeToDeleteRecipeCard
import com.example.foodgram.ui.theme.main.MainViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val userRecipes = uiState.allRecipes.filter { recipe ->
        recipe.authorId == userId
    }

    val userName = userRecipes.firstOrNull()?.authorName ?: userId

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = userName,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "@$userId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Рецептов: ${userRecipes.size}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (userRecipes.isEmpty()) {
                Text(
                    text = "У этого пользователя пока нет рецептов.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = userRecipes,
                        key = { recipe -> recipe.id }
                    ) { recipe ->
                        val isSaved = uiState.savedRecipes.any {
                            it.id == recipe.id
                        }

                        SwipeToDeleteRecipeCard(
                            direction = if (isSaved) {
                                DeleteSwipeDirection.LEFT
                            } else {
                                DeleteSwipeDirection.RIGHT
                            },
                            onDeleteRequest = {
                                if (isSaved) {
                                    viewModel.removeSavedRecipe(recipe)
                                } else {
                                    viewModel.onSaveRecipe(recipe)
                                }
                            },
                            actionColor = if (isSaved) {
                                Color(0xFFD32F2F)
                            } else {
                                Color(0xFF2E7D32)
                            },
                            actionIcon = Icons.Default.Bookmark,
                            actionDescription = if (isSaved) {
                                "Убрать из сохранённых"
                            } else {
                                "Сохранить рецепт"
                            },
                            content = {
                                ProfileRecipeCard(
                                    recipe = recipe,
                                    isSaved = isSaved,
                                    onClick = {
                                        viewModel.selectRecipeForDetails(recipe)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        uiState.selectedRecipeForDetails?.let { recipe ->
            RecipeDetailBottomSheet(
                recipe = recipe,
                onDismiss = {
                    viewModel.selectRecipeForDetails(null)
                }
            )
        }
    }
}
