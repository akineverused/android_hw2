
package com.example.foodgram

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodgram.model.Recipe
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import coil3.compose.AsyncImage
import com.example.foodgram.model.CurrentUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    title: String,
    onTitleChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    cookingTime: String,
    onCookingTimeChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onRecipeAdded: (Recipe) -> Unit,
    onBack: () -> Unit,
    ingredients: List<String>,
    onIngredientsChange: (List<String>) -> Unit,
    instructions: List<String>,
    onInstructionsChange: (List<String>) -> Unit
) {
    var categoryMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedImageUri by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri?.toString()
    }

    val categories = listOf(
        "Главное блюдо",
        "Десерт",
        "Завтрак",
        "Перекус",
        "Напитки"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Add recipe",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
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
                placeholder = { Text("Выберите категорию") },
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
                            onCategoryChange(item)
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cookingTime,
            onValueChange = onCookingTimeChange,
            label = { Text("Cooking time (minutes)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
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
                contentDescription = "Selected recipe photo",
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

                    onIngredientsChange(updated)
                },
                label = { Text("Ingredient ${index + 1}") },
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

                    onInstructionsChange(updated)
                },
                label = { Text("Step ${index + 1}") },
                placeholder = {
                    Text("Опишите шаг приготовления")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val time = cookingTime.toIntOrNull()

                if (
                    title.isNotBlank() &&
                    category.isNotBlank() &&
                    time != null &&
                    time > 0 &&
                    description.isNotBlank()
                ) {
                    val recipe = Recipe(
                        id = System.currentTimeMillis().toInt(),
                        title = title.trim(),
                        category = category.trim(),
                        cookingTimeMinutes = time,
                        description = description.trim(),
                        ingredients = ingredients
                            .map { it.trim() }
                            .filter { it.isNotBlank() },
                        instructions = instructions
                            .map { it.trim() }
                            .filter { it.isNotBlank() },
                        imageUrl = selectedImageUri.orEmpty(),
                        authorId = CurrentUser.id,
                        authorName = CurrentUser.name
                    )

                    onRecipeAdded(recipe)
                } else {
                    println("Form validation failed")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save recipe")
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
