package com.example.triviagameapp.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triviagameapp.R
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ViewModels.GameViewModel
import com.example.triviagameapp.ui.theme.QuizCyan

@Composable
fun DifficultyView(
    navController: NavController,
    gameViewModel: GameViewModel
) {
    BackHandler {
        navController.navigate(Screen.HomeScreen.route)
    }
    // Fullscreen background
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.game_background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Difficulty",
                style = MaterialTheme.typography.h4,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            listOf("Easy", "Medium", "Hard").forEach { label ->
                Button(
                    onClick = {
                        gameViewModel.setDifficulty(label.lowercase())
                        navController.navigate(Screen.GameScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = QuizCyan,
                        contentColor = Color.White
                    )
                ) {
                    Text(label, fontSize = 20.sp)
                }
            }
        }
    }
}
