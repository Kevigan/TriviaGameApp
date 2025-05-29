package com.example.triviagameapp.Views

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triviagameapp.R
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ui.theme.QuizBlue
import com.example.triviagameapp.ui.theme.QuizCyan
import com.example.triviagameapp.ui.theme.QuizCyanTransparent
import com.example.triviagameapp.ui.theme.QuizGreen
import com.example.triviagameapp.ui.theme.QuizOrange
import com.example.triviagameapp.ui.theme.QuizTitleBackground
import com.example.triviagameapp.ui.theme.QuizYellow

@Composable
fun HomeView(
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Scaffold(
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔹 Background Image
            Image(
                painter = painterResource(id = R.drawable.quiz_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 🔹 Title at top center
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
                    .background(
                        color = QuizCyanTransparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Quiz Trivia",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // 🔹 Buttons based on orientation
            if (isPortrait) {
                // Centered vertically in portrait
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuizButtons(navController)
                }
            } else {
                // Bottom-aligned in landscape
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuizButtons(navController)
                }
            }
        }
    }
}

@Composable
private fun QuizButtons(navController: NavController) {
    val buttonModifier = Modifier
        .width(260.dp)
        .height(60.dp)

    Button(
        onClick = {
            navController.navigate(Screen.GameScreen.route)
                  },
        modifier = buttonModifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = QuizCyan,
            contentColor = Color.White
        )
    ) {
        Text("Play", fontSize = 18.sp)
    }

    Button(
        onClick = { /* Leaderboard */ },
        modifier = buttonModifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = QuizCyan,
            contentColor = Color.White
        )
    ) {
        Text("Leaderboard", fontSize = 18.sp)
    }

    Button(
        onClick = { /* Settings */ },
        modifier = buttonModifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = QuizCyan,
            contentColor = Color.White
        )
    ) {
        Text("Settings", fontSize = 18.sp)
    }
}




