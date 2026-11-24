package com.example.foodgram.ui.theme.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodgram.data.DummyData
import com.example.foodgram.data.repository.RecipeRepository
import com.example.foodgram.model.CurrentUser
import com.example.foodgram.model.Recipe
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

class MainViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var ratingJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialRecipes(DummyData.sampleRecipes)
        }

        viewModelScope.launch {
            repository.getAllRecipes().collect { recipes ->
                _uiState.update {
                    it.copy(
                        allRecipes = recipes,
                        myRecipes = recipes.filter { recipe ->
                            recipe.authorId == CurrentUser.id
                        }
                    )
                }
                applyFilters()
            }
        }

        viewModelScope.launch {
            repository.getSavedRecipes().collect { recipes ->
                _uiState.update {
                    it.copy(savedRecipes = recipes)
                }
                applyFilters()
            }
        }

        viewModelScope.launch {
            repository.getSkippedRecipes().collect { recipes ->
                _uiState.update {
                    it.copy(skippedRecipes = recipes)
                }
                applyFilters()
            }
        }

        viewModelScope.launch {
            repository.getAllRatingSummaries().collect { summaries ->
                _uiState.update {
                    it.copy(ratingSummaries = summaries)
                }
            }
        }
    }

    fun onSaveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.saveRecipe(recipe)
        }
    }

    fun onSkipRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.skipRecipe(recipe)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            applyFilters()
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
        applyFilters()
    }

    fun onCookingTimeSelected(maxTime: Int?) {
        _uiState.update {
            it.copy(selectedMaxCookingTime = maxTime)
        }
        applyFilters()
    }

    fun selectRecipeForDetails(recipe: Recipe?) {
        ratingJob?.cancel()

        _uiState.update {
            it.copy(
                selectedRecipeForDetails = recipe,
                averageRating = null,
                ratingCount = 0,
                userRating = null
            )
        }

        if (recipe == null) return

        ratingJob = viewModelScope.launch {
            combine(
                repository.getAverageRating(recipe.id),
                repository.getRatingCount(recipe.id),
                repository.getUserRating(recipe.id, CurrentUser.id)
            ) { average, count, userRating ->
                Triple(average, count, userRating)
            }.collect { (average, count, userRating) ->

                _uiState.update {
                    it.copy(
                        averageRating = average,
                        ratingCount = count,
                        userRating = userRating
                    )
                }
            }
        }
    }

    fun resetDeck() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = "Все",
                selectedMaxCookingTime = null
            )
        }

        viewModelScope.launch {
            repository.clearAllInteractions()
        }

        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value

        val query = state.searchQuery.trim().lowercase()
        val category = state.selectedCategory
        val maxCookingTime = state.selectedMaxCookingTime

        val hasActiveFilters =
            query.isNotBlank() ||
                    category != "Все" ||
                    maxCookingTime != null

        val filtered = state.allRecipes.filter { recipe ->

            val matchesQuery =
                query.isBlank() ||
                        recipe.title.lowercase().contains(query) ||
                        recipe.description.lowercase().contains(query) ||
                        recipe.category.lowercase().contains(query) ||
                        recipe.authorName.lowercase().contains(query)

            val matchesCategory =
                category == "Все" || recipe.category == category

            val matchesCookingTime =
                maxCookingTime == null ||
                        recipe.cookingTimeMinutes <= maxCookingTime

            val isNotAlreadyProcessed =
                hasActiveFilters ||
                        (
                                state.savedRecipes.none { it.id == recipe.id } &&
                                        state.skippedRecipes.none { it.id == recipe.id }
                                )

            matchesQuery &&
                    matchesCategory &&
                    matchesCookingTime &&
                    isNotAlreadyProcessed
        }

        _uiState.update {
            it.copy(availableRecipes = filtered)
        }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = "Все",
                selectedMaxCookingTime = null
            )
        }

        applyFilters()
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
        }
    }

    fun removeSavedRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.removeInteraction(recipe.id)
        }
    }

    fun deleteMyRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe.id)
        }
    }

    fun rateRecipe(recipeId: Int, rating: Int) {
        if (rating !in 1..5) return

        viewModelScope.launch {
            repository.saveRating(
                recipeId = recipeId,
                userId = CurrentUser.id,
                rating = rating
            )
        }
    }
}