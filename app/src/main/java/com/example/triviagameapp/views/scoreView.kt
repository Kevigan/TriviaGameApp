package com.example.triviagameapp.views

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triviagameapp.R
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ViewModels.GameViewModel
import com.example.triviagameapp.ViewModels.SessionViewModel

@Composable
fun ScoreView(
    navController: NavController,
    gameViewModel: GameViewModel,
    sessionViewModel: SessionViewModel
) {
    val userScore by sessionViewModel.userScore.collectAsState()

    BackHandler {
        navController.navigate(Screen.HomeScreen.route)
    }

    LaunchedEffect(Unit) {
        sessionViewModel.updateTotalScore(gameViewModel.score.value) { success ->
            if (success) {
                Log.d("ScoreView", "Score updated successfully.")
            } else {
                Log.e("ScoreView", "Failed to update score.")
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.game_background),
            contentDescription = "Game Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Column for the score and buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title "Score"
            Text(
                text = "Score: ${gameViewModel.score.value}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )

            Text(
                text = "All Time Score: $userScore",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Row for buttons side by side
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Play Again Button
                Button(
                    onClick = { navController.navigate(Screen.CategoryScreen.route)},
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Play Again", fontSize = 18.sp)
                }

                // Return to Menu Button
                Button(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route)
                    },
                    modifier = Modifier.weight(1f) // Make the button fill the available space
                ) {
                    Text("Return to Menu", fontSize = 18.sp)
                }
            }
        }
    }
}
