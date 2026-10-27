package com.example.foodgram.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.foodgram.model.Recipe

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder

@Composable
fun getCategoryColors(category: String): Pair<Color, Color> {
    return when (category) {
        "Десерт" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Главное блюдо" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        "Завтрак" -> Pair(Color(0xFFFFFDE7), Color(0xFFF57F17))
        "Напитки" -> Pair(Color(0xFFE1F5FE), Color(0xFF0277BD))
        "Перекус" -> Pair(Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        else -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAuthorClick: (() -> Unit)? = null
) {
    val (backgroundColor, accentColor) = getCategoryColors(recipe.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(540.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            if (recipe.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.62f)
                        .align(Alignment.TopCenter)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.62f)
                        .background(backgroundColor)
                        .align(Alignment.TopCenter),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (recipe.category) {
                            "Десерт" -> "🍰"
                            "Главное блюдо" -> "🍲"
                            "Завтрак" -> "🥞"
                            "Напитки" -> "🥤"
                            "Перекус" -> "🥗"
                            else -> "🍽️"
                        },
                        fontSize = 76.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.35f to Color.Transparent,
                                0.55f to backgroundColor.copy(alpha = 0.75f),
                                0.72f to backgroundColor,
                                1.0f to backgroundColor
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.92f)
                    ) {
                        Text(
                            text = recipe.category,
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            ),
                            color = accentColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.92f)
                    ) {
                        Text(
                            text = "◷ ${recipe.cookingTimeMinutes} мин",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            color = Color(0xFF29251F),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF29251F),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF554B42),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalDivider(
                        color = accentColor.copy(alpha = 0.2f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = recipe.authorName
                                    .firstOrNull()
                                    ?.uppercase() ?: "👨‍🍳",
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onAuthorClick?.invoke()
                                },
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = recipe.authorName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF29251F),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "Посмотреть профиль →",
                                style = MaterialTheme.typography.bodySmall,
                                color = accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}