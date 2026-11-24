package com.example.foodgram.ui.theme.main

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodgram.Screen
import com.example.foodgram.model.CurrentUser
import com.example.foodgram.ui.theme.components.ActionButtonsRow
import com.example.foodgram.ui.theme.components.FilterChipGroup
import com.example.foodgram.ui.theme.components.RecipeCard
import com.example.foodgram.ui.theme.components.RecipeDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isSearchVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val categories = listOf(
        "Все",
        "Главное блюдо",
        "Десерт",
        "Завтрак",
        "Перекус",
        "Напитки"
    )

    val timeFilters: List<Pair<String, Int?>> = listOf(
        "Любое время" to null,
        "До 15 мин" to 15,
        "До 30 мин" to 30,
        "До 60 мин" to 60
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0.dp),
                title = {
                    Text(
                        text = "Food Swiper 🍔",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isSearchVisible) {
                                isSearchVisible = false
                                viewModel.resetFilters()
                            } else {
                                isSearchVisible = true
                            }
                        }
                    ) {
                        Text(
                            if (isSearchVisible) "✖️" else "🔍"
                        )
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

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.35f
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Поиск рецептов...")
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Категории",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                            FilterChipGroup(
                                categories = categories,
                                selectedCategory = uiState.selectedCategory,
                                onCategorySelected = viewModel::onCategorySelected
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Время приготовления",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                timeFilters.forEach { (label, maxTime) ->
                                    FilterChip(
                                        selected = uiState.selectedMaxCookingTime == maxTime,
                                        onClick = {
                                            viewModel.onCookingTimeSelected(maxTime)
                                        },
                                        label = {
                                            Text(label)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

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

            val isSearchingOrFiltering =
                uiState.searchQuery.isNotBlank() ||
                        uiState.selectedCategory != "Все" ||
                        uiState.selectedMaxCookingTime != null

            if (isSearchingOrFiltering) {

                if (uiState.availableRecipes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ничего не найдено 😢",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            bottom = 16.dp
                        )
                    ) {
                        items(
                            items = uiState.availableRecipes,
                            key = { recipe -> recipe.id }
                        ) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = {
                                    viewModel.selectRecipeForDetails(recipe)
                                },
                                onAuthorClick = {
                                    if (recipe.authorId == CurrentUser.id) {
                                        navController.navigate(Screen.Profile.route) {
                                            launchSingleTop = true
                                        }
                                    } else {
                                        navController.navigate(
                                            Screen.UserProfile.createRoute(recipe.authorId)
                                        )
                                    }
                                },
                                averageRating = uiState.ratingSummaries[recipe.id]?.first,
                                ratingCount = uiState.ratingSummaries[recipe.id]?.second ?: 0
                            )
                        }
                    }
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

                        var dragAmount by remember(currentRecipe.id) {
                            mutableFloatStateOf(0f)
                        }

                        RecipeCard(
                            recipe = currentRecipe,
                            onClick = {
                                viewModel.selectRecipeForDetails(currentRecipe)
                            },
                            onAuthorClick = {
                                if (currentRecipe.authorId == CurrentUser.id) {
                                    navController.navigate(Screen.Profile.route) {
                                        launchSingleTop = true
                                    }
                                } else {
                                    navController.navigate(
                                        Screen.UserProfile.createRoute(currentRecipe.authorId)
                                    )
                                }
                            },
                            averageRating = uiState.ratingSummaries[currentRecipe.id]?.first,
                            ratingCount = uiState.ratingSummaries[currentRecipe.id]?.second ?: 0,
                            modifier = Modifier
                                .graphicsLayer {
                                    translationX = dragAmount
                                    rotationZ = dragAmount / 30f
                                }
                                .pointerInput(currentRecipe.id) {
                                    detectHorizontalDragGestures(
                                        onHorizontalDrag = { _, dragDelta ->
                                            dragAmount += dragDelta
                                        },
                                        onDragEnd = {
                                            when {
                                                dragAmount > 100f -> {
                                                    viewModel.onSaveRecipe(currentRecipe)
                                                }

                                                dragAmount < -100f -> {
                                                    viewModel.onSkipRecipe(currentRecipe)
                                                }
                                            }

                                            dragAmount = 0f
                                        },
                                        onDragCancel = {
                                            dragAmount = 0f
                                        }
                                    )
                                }
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

                            Button(
                                onClick = viewModel::resetDeck
                            ) {
                                Text("Начать заново")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (currentRecipe != null) {
                    ActionButtonsRow(
                        onSkip = {
                            viewModel.onSkipRecipe(currentRecipe)
                        },
                        onSave = {
                            viewModel.onSaveRecipe(currentRecipe)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        uiState.selectedRecipeForDetails?.let { recipe ->
            RecipeDetailBottomSheet(
                recipe = recipe,
                averageRating = uiState.averageRating,
                ratingCount = uiState.ratingCount,
                userRating = uiState.userRating,
                onRate = { rating ->
                    viewModel.rateRecipe(recipe.id, rating)
                },
                onDismiss = {
                    viewModel.selectRecipeForDetails(null)
                }
            )
        }
    }
}