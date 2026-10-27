package com.example.foodgram.ui.theme.main

import androidx.lifecycle.ViewModel
import com.example.foodgram.data.DummyData
import com.example.foodgram.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null

    init {
        val recipes = DummyData.sampleRecipes
        _uiState.update {
            it.copy(
                allRecipes = recipes,
                availableRecipes = recipes
            )
        }
    }

    fun onSaveRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            val updatedSaved = if (
                currentState.savedRecipes.any { it.id == recipe.id }
            ) {
                currentState.savedRecipes
            } else {
                currentState.savedRecipes + recipe
            }

            currentState.copy(
                savedRecipes = updatedSaved,
                skippedRecipes = currentState.skippedRecipes.filter {
                    it.id != recipe.id
                }
            )
        }

        applyFilters()
    }

    fun onSkipRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            val updatedSkipped = if (
                currentState.skippedRecipes.any { it.id == recipe.id }
            ) {
                currentState.skippedRecipes
            } else {
                currentState.skippedRecipes + recipe
            }

            currentState.copy(
                skippedRecipes = updatedSkipped,
                savedRecipes = currentState.savedRecipes.filter {
                    it.id != recipe.id
                }
            )
        }

        applyFilters()
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

    fun selectRecipeForDetails(recipe: Recipe?) {
        _uiState.update {
            it.copy(selectedRecipeForDetails = recipe)
        }
    }

    fun resetDeck() {
        _uiState.update {
            it.copy(
                savedRecipes = emptyList(),
                skippedRecipes = emptyList(),
                searchQuery = "",
                selectedCategory = "Все"
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase()
        val category = state.selectedCategory

        val filtered = state.allRecipes.filter { recipe ->
            val matchesQuery = query.isBlank() ||
                    recipe.title.lowercase().contains(query) ||
                    recipe.description.lowercase().contains(query) ||
                    recipe.category.lowercase().contains(query) ||
                    recipe.authorName.lowercase().contains(query)

            val matchesCategory =
                category == "Все" || recipe.category == category

            val isNotAlreadyProcessed =
                state.searchQuery.isNotBlank() ||
                        category != "Все" ||
                        (
                                state.savedRecipes.none { it.id == recipe.id } &&
                                        state.skippedRecipes.none { it.id == recipe.id }
                                )

            matchesQuery && matchesCategory && isNotAlreadyProcessed
        }

        _uiState.update {
            it.copy(availableRecipes = filtered)
        }
    }

    fun addRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            val updatedCatalog = currentState.allRecipes
                .filterNot { it.id == recipe.id } + recipe

            currentState.copy(
                allRecipes = updatedCatalog,
                myRecipes = currentState.myRecipes
                    .filterNot { it.id == recipe.id } + recipe
            )
        }

        applyFilters()
    }

    fun removeSavedRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            currentState.copy(
                savedRecipes = currentState.savedRecipes.filter {
                    it.id != recipe.id
                }
            )
        }

        applyFilters()
    }

    fun deleteMyRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            currentState.copy(
                myRecipes = currentState.myRecipes.filter {
                    it.id != recipe.id
                },
                allRecipes = currentState.allRecipes.filter {
                    it.id != recipe.id
                },
                savedRecipes = currentState.savedRecipes.filter {
                    it.id != recipe.id
                },
                skippedRecipes = currentState.skippedRecipes.filter {
                    it.id != recipe.id
                }
            )
        }

        applyFilters()
    }
}