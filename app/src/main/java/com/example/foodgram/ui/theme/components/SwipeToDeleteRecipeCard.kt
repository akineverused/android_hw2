package com.example.foodgram.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

enum class DeleteSwipeDirection {
    RIGHT,
    LEFT
}

@Composable
fun SwipeToDeleteRecipeCard(
    direction: DeleteSwipeDirection,
    onDeleteRequest: () -> Unit,
    content: @Composable () -> Unit,
    actionColor: Color = Color(0xFFD32F2F),
    actionIcon: ImageVector = Icons.Default.Delete,
    actionDescription: String = "Удалить рецепт"
) {
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    val threshold = 120.dp
    val maxSwipe = 180.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(actionColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = actionIcon,
            contentDescription = actionDescription,
            tint = Color.White,
            modifier = Modifier
                .align(
                    if (direction == DeleteSwipeDirection.RIGHT) {
                        Alignment.CenterStart
                    } else {
                        Alignment.CenterEnd
                    }
                )
                .padding(horizontal = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = offsetX
                }
                .pointerInput(direction) {
                    val thresholdPx = threshold.toPx()
                    val maxSwipePx = maxSwipe.toPx()

                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()

                            offsetX = when (direction) {
                                DeleteSwipeDirection.RIGHT ->
                                    (offsetX + dragAmount)
                                        .coerceIn(0f, maxSwipePx)

                                DeleteSwipeDirection.LEFT ->
                                    (offsetX + dragAmount)
                                        .coerceIn(-maxSwipePx, 0f)
                            }
                        },
                        onDragEnd = {
                            val shouldPerformAction =
                                when (direction) {
                                    DeleteSwipeDirection.RIGHT ->
                                        offsetX >= thresholdPx

                                    DeleteSwipeDirection.LEFT ->
                                        offsetX <= -thresholdPx
                                }

                            offsetX = 0f

                            if (shouldPerformAction) {
                                onDeleteRequest()
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@Composable
fun SwipeableRecipeCard(
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    val threshold = 120.dp
    val maxSwipe = 180.dp

    val backgroundColor = when {
        offsetX > 0f -> Color(0xFF2196F3)
        offsetX < 0f -> Color(0xFFD32F2F)
        else -> Color(0xFFEEEEEE)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (offsetX > 0f) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Редактировать рецепт",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 24.dp)
            )
        }

        if (offsetX < 0f) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить рецепт",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 24.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = offsetX
                }
                .pointerInput(Unit) {
                    val thresholdPx = threshold.toPx()
                    val maxSwipePx = maxSwipe.toPx()

                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()

                            offsetX = (offsetX + dragAmount)
                                .coerceIn(-maxSwipePx, maxSwipePx)
                        },
                        onDragEnd = {
                            val editRequested = offsetX >= thresholdPx
                            val deleteRequested = offsetX <= -thresholdPx

                            offsetX = 0f

                            when {
                                editRequested -> onSwipeRight()
                                deleteRequested -> onSwipeLeft()
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        }
                    )
                }
        ) {
            content()
        }
    }
}