package com.example.triviagameapp.Views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triviagameapp.ui.theme.QuizCyan

@Composable
fun GameView() {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Question Text
        Text(
            text = "How many studio albums have the duo Daft Punk released?", // Replace with state
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        )

        // 🔹 Answer Options (Bottom Aligned)
        if (isPortrait) {
            // Portrait: 4 vertical answers at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                listOf("1", "5", "2", "4").forEach { answer ->
                    AnswerButton(text = answer)
                }
            }
        } else {
            // Landscape: 2x2 grid at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnswerButton(text = "1", modifier = Modifier.weight(1f))
                    AnswerButton(text = "5", modifier = Modifier.weight(1f))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnswerButton(text = "2", modifier = Modifier.weight(1f))
                    AnswerButton(text = "4", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AnswerButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* Handle answer selection */ },
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = QuizCyan,
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

