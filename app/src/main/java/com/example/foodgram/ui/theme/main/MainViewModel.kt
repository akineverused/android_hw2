package com.example.foodgram.ui.theme.main

import androidx.lifecycle.ViewModel
import com.example.foodgram.data.DummyData
import com.example.foodgram.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private val allOriginalRecipes = DummyData.sampleRecipes

    init {
        _uiState.update { it.copy(availableRecipes = allOriginalRecipes) }
    }

    fun onSaveRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            val alreadySaved = currentState.savedRecipes.any { it.id == recipe.id }
            val updatedSaved = if (alreadySaved) {
                currentState.savedRecipes
            } else {
                currentState.savedRecipes + recipe
            }

            val updatedSkipped = currentState.skippedRecipes.filter { it.id != recipe.id }

            currentState.copy(
                savedRecipes = updatedSaved,
                skippedRecipes = updatedSkipped
            )
        }
        applyFilters()
    }

    fun onSkipRecipe(recipe: Recipe) {
        _uiState.update { currentState ->
            val alreadySkipped = currentState.skippedRecipes.any { it.id == recipe.id }
            val updatedSkipped = if (alreadySkipped) {
                currentState.skippedRecipes
            } else {
                currentState.skippedRecipes + recipe
            }

            val updatedSaved = currentState.savedRecipes.filter { it.id != recipe.id }

            currentState.copy(
                savedRecipes = updatedSaved,
                skippedRecipes = updatedSkipped
            )
        }
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    fun selectRecipeForDetails(recipe: Recipe?) {
        _uiState.update { it.copy(selectedRecipeForDetails = recipe) }
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
        val query = _uiState.value.searchQuery.trim().lowercase()
        val category = _uiState.value.selectedCategory

        val filtered = if (query.isBlank()) {
            allOriginalRecipes.filter { recipe ->
                val matchesCategory = (category == "Все") || (recipe.category == category)
                val notSaved = _uiState.value.savedRecipes.none { it.id == recipe.id }
                val notSkipped = _uiState.value.skippedRecipes.none { it.id == recipe.id }
                matchesCategory && notSaved && notSkipped
            }
        } else {
            allOriginalRecipes.filter { recipe ->
                val matchesQuery = recipe.title.lowercase().contains(query)
                val matchesCategory = (category == "Все") || (recipe.category == category)
                matchesQuery && matchesCategory
            }
        }

        _uiState.update { it.copy(availableRecipes = filtered) }
    }

    fun addRecipe(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(
            availableRecipes = _uiState.value.availableRecipes + recipe,
            myRecipes = _uiState.value.myRecipes + recipe
        )
    }
}