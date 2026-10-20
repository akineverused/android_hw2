package com.example.foodgram.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodgram.model.Recipe

@Composable
fun getCategoryColors(category: String): Pair<Color, Color> {
    return when (category) {
        "Десерт" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))        // Зеленоватый фон, зеленый акцент
        "Главное блюдо" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100)) // Пастельно-оранжевый, оранжевый акцент
        "Завтрак" -> Pair(Color(0xFFFFFDE7), Color(0xFFF57F17))       // Желтоватый фон, темно-желтый акцент
        "Напитки" -> Pair(Color(0xFFE1F5FE), Color(0xFF0277BD))       // Голубой фон, синий акцент
        "Перекус" -> Pair(Color(0xFFF3E5F5), Color(0xFF7B1FA2))       // Сиреневый фон, фиолетовый акцент
        else -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, accentColor) = getCategoryColors(recipe.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = accentColor
                    ) {
                        Text(
                            text = recipe.category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "⏱ ${recipe.cookingTimeMinutes} мин",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                Column {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray,
                        maxLines = 4
                    )
                }

                Text(
                    text = "Нажмите для подробного рецепта ➔",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}