package com.project.fitnessbuddy.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LetterImage(
    letter: Char,
    backgroundColor: Color,
    textColor: Color,
    size: Int,
    padding: PaddingValues
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(padding)
    ) {
        Surface(
            modifier = Modifier
                .size(size.dp),
            shape = CircleShape,
            color = backgroundColor
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = letter.toString().uppercase(),
                    style = TextStyle(
                        fontSize = (size / 2).sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }
        }
    }
}

@Composable
fun WidgetLetterImage(letter: Char, padding: PaddingValues) {
    LetterImage(
        letter = letter,
        backgroundColor = Color.Gray,
        textColor = Color.White,
        size = 48,
        padding = padding
    )
}
