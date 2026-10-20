package com.example.foodgram.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionButtonsRow(
    onSkip: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(
            onClick = onSkip,
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            Text(text = "❌ Пропустить", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(text = "❤️ Сохранить", fontSize = 16.sp, color = Color.White)
        }
    }
}