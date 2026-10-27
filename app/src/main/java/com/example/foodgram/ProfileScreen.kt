
package com.example.foodgram

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodgram.model.Recipe
import com.example.foodgram.ui.theme.components.DeleteSwipeDirection
import com.example.foodgram.ui.theme.components.ProfileRecipeCard
import com.example.foodgram.ui.theme.components.RecipeDetailBottomSheet
import com.example.foodgram.ui.theme.components.SwipeToDeleteRecipeCard
import com.example.foodgram.ui.theme.main.MainViewModel

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable {
        mutableStateOf(0)
    }

    var recipePendingDeletion by remember {
        mutableStateOf<Recipe?>(null)
    }

    val recipes = if (selectedTab == 0) {
        uiState.savedRecipes
    } else {
        uiState.myRecipes
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Saved (${uiState.savedRecipes.size})") }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My recipes (${uiState.myRecipes.size})") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (recipes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) {
                        "You haven't saved any recipes yet."
                    } else {
                        "You haven't added any recipes yet."
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = recipes,
                    key = { recipe -> recipe.id }
                ) { recipe ->
                    SwipeToDeleteRecipeCard(
                        direction = if (selectedTab == 0) {
                            DeleteSwipeDirection.RIGHT
                        } else {
                            DeleteSwipeDirection.LEFT
                        },
                        onDeleteRequest = {
                            recipePendingDeletion = recipe
                        },
                        content = {
                            ProfileRecipeCard(
                                recipe = recipe,
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

    recipePendingDeletion?.let { recipe ->
        AlertDialog(
            onDismissRequest = {
                recipePendingDeletion = null
            },
            title = {
                Text(
                    if (selectedTab == 0) {
                        "Remove saved recipe?"
                    } else {
                        "Delete your recipe?"
                    }
                )
            },
            text = {
                Text(
                    if (selectedTab == 0) {
                        "Remove \"${recipe.title}\" from your saved recipes?"
                    } else {
                        "Delete \"${recipe.title}\" from your recipes?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedTab == 0) {
                            viewModel.removeSavedRecipe(recipe)
                        } else {
                            viewModel.deleteMyRecipe(recipe)
                        }

                        if (uiState.selectedRecipeForDetails?.id == recipe.id) {
                            viewModel.selectRecipeForDetails(null)
                        }

                        recipePendingDeletion = null
                    }
                ) {
                    Text(if (selectedTab == 0) "Remove" else "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        recipePendingDeletion = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
