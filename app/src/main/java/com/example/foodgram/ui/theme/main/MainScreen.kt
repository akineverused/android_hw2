package com.example.foodgram.ui.theme.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodgram.ui.theme.components.ActionButtonsRow
import com.example.foodgram.ui.theme.components.FilterChipGroup
import com.example.foodgram.ui.theme.components.RecipeCard
import com.example.foodgram.ui.theme.components.RecipeDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var isSearchVisible by rememberSaveable { mutableStateOf(false) }

    val categories = listOf("Все", "Главное блюдо", "Десерт", "Завтрак", "Перекус", "Напитки")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Swiper 🍔", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Text(if (isSearchVisible) "✖️" else "🔍")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (isSearchVisible) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск по названию рецепта...") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            FilterChipGroup(
                categories = categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.onCategorySelected(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Сохранено: ${uiState.savedRecipes.size}",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Пропущено: ${uiState.skippedRecipes.size}",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isSearchingOrFiltering = uiState.searchQuery.isNotBlank() || uiState.selectedCategory != "Все"

            if (isSearchingOrFiltering) {
                if (uiState.availableRecipes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ничего не найдено 😢", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.availableRecipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { viewModel.selectRecipeForDetails(recipe) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                val currentRecipe = uiState.availableRecipes.firstOrNull()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentRecipe != null) {
                        RecipeCard(
                            recipe = currentRecipe,
                            onClick = { viewModel.selectRecipeForDetails(currentRecipe) }
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Все рецепты просмотрены! 🎉",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.resetDeck() }) {
                                Text("Начать заново")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentRecipe != null) {
                    ActionButtonsRow(
                        onSkip = { viewModel.onSkipRecipe(currentRecipe) },
                        onSave = { viewModel.onSaveRecipe(currentRecipe) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        uiState.selectedRecipeForDetails?.let { recipe ->
            RecipeDetailBottomSheet(
                recipe = recipe,
                onDismiss = { viewModel.selectRecipeForDetails(null) }
            )
        }
    }
}