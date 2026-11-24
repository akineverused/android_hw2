package com.example.foodgram.ui.theme.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.foodgram.model.Recipe
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    recipe: Recipe?,
    onSave: (Recipe) -> Unit,
    onBack: () -> Unit
) {
    if (recipe == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Recipe not found.",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }

        return
    }

    EditRecipeForm(
        recipe = recipe,
        onSave = onSave,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditRecipeForm(
    recipe: Recipe,
    onSave: (Recipe) -> Unit,
    onBack: () -> Unit
) {
    var title by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.title)
    }

    var category by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.category)
    }

    var cookingTime by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.cookingTimeMinutes.toString())
    }

    var description by rememberSaveable(recipe.id) {
        mutableStateOf(recipe.description)
    }

    var ingredients by rememberSaveable(recipe.id) {
        mutableStateOf(
            recipe.ingredients.ifEmpty { listOf("") }
        )
    }

    var instructions by rememberSaveable(recipe.id) {
        mutableStateOf(
            recipe.instructions.ifEmpty { listOf("") }
        )
    }

    var selectedImageUri by rememberSaveable(recipe.id) {
        mutableStateOf(
            recipe.imageUrl.takeIf { it.isNotBlank() }
        )
    }

    var categoryMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var validationError by rememberSaveable(recipe.id) {
        mutableStateOf(false)
    }

    LaunchedEffect(validationError) {
        if (validationError) {
            delay(10_000)
            validationError = false
        }
    }

    val categories = listOf(
        "Главное блюдо",
        "Десерт",
        "Завтрак",
        "Перекус",
        "Напитки"
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Edit recipe",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                validationError = false
            },
            label = { Text("Recipe name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = categoryMenuExpanded,
            onExpandedChange = {
                categoryMenuExpanded = !categoryMenuExpanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoryMenuExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = {
                    categoryMenuExpanded = false
                }
            ) {
                categories.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            category = item
                            categoryMenuExpanded = false
                            validationError = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cookingTime,
            onValueChange = {
                cookingTime = it
                validationError = false
            },
            label = { Text("Cooking time (minutes)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                validationError = false
            },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recipe photo",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedImageUri != null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Recipe photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    selectedImageUri = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Remove photo")
            }
        }

        Button(
            onClick = {
                imagePicker.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (selectedImageUri == null) {
                    "Choose photo"
                } else {
                    "Change photo"
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ingredients.forEachIndexed { index, ingredient ->
            OutlinedTextField(
                value = ingredient,
                onValueChange = { newValue ->
                    val updated = ingredients.toMutableList()
                    updated[index] = newValue

                    if (
                        index == updated.lastIndex &&
                        newValue.isNotBlank()
                    ) {
                        updated.add("")
                    }

                    ingredients = updated
                    validationError = false
                },
                label = {
                    Text("Ingredient ${index + 1}")
                },
                placeholder = {
                    Text("Например: 2 яйца")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cooking instructions",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        instructions.forEachIndexed { index, instruction ->
            OutlinedTextField(
                value = instruction,
                onValueChange = { newValue ->
                    val updated = instructions.toMutableList()
                    updated[index] = newValue

                    if (
                        index == updated.lastIndex &&
                        newValue.isNotBlank()
                    ) {
                        updated.add("")
                    }

                    instructions = updated
                    validationError = false
                },
                label = {
                    Text("Step ${index + 1}")
                },
                placeholder = {
                    Text("Опишите шаг приготовления")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (validationError) {
            Text(
                text = "Please fill in the required fields correctly.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                val time = cookingTime.toIntOrNull()

                val validIngredients = ingredients
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                val validInstructions = instructions
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                if (
                    title.isNotBlank() &&
                    category.isNotBlank() &&
                    time != null &&
                    time > 0 &&
                    description.isNotBlank() &&
                    validIngredients.isNotEmpty() &&
                    validInstructions.isNotEmpty()
                ) {
                    val updatedRecipe = recipe.copy(
                        title = title.trim(),
                        category = category.trim(),
                        cookingTimeMinutes = time,
                        description = description.trim(),
                        ingredients = validIngredients,
                        instructions = validInstructions,
                        imageUrl = selectedImageUri.orEmpty()
                    )

                    onSave(updatedRecipe)
                } else {
                    validationError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save changes")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}