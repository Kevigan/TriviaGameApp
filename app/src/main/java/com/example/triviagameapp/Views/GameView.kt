package com.example.triviagameapp.Views

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triviagameapp.R
import com.example.triviagameapp.ui.theme.QuizCyan
import com.example.triviagameapp.ui.theme.QuizCyan2
import kotlinx.coroutines.delay

@Composable
fun GameView(
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val showExitDialog = remember { mutableStateOf(false) }

    // Intercept back button to show confirmation
    BackHandler {
        showExitDialog.value = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔹 Background Image
        Image(
            painter = painterResource(id = R.drawable.game_background),
            contentDescription = "Game Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Adjust content scale to cover the screen
        )

        // 🔹 Question Background Box
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                .background(QuizCyan2, shape = RoundedCornerShape(12.dp))
                .padding(16.dp) // Padding inside the background
        ) {
            // 🔹 Question Text
            Text(
                text = "How many studio albums have the duo Daft Punk released?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        QuestionTimer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp),
            onTimeOut = { /* handle timeout */ }
        )

        // 🔹 Answer Options (Bottom Aligned)
        if (isPortrait) {
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
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnswerButton(text = "1", modifier = Modifier.weight(1f))
                    AnswerButton(text = "5", modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnswerButton(text = "2", modifier = Modifier.weight(1f))
                    AnswerButton(text = "4", modifier = Modifier.weight(1f))
                }
            }
        }

        // 🔹 Exit Confirmation Dialog
        if (showExitDialog.value) {
            AlertDialog(
                onDismissRequest = { showExitDialog.value = false },
                title = {
                    Text(
                        text = "Quit Game?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to exit the game? Your progress will be lost.",
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog.value = false
                        navController.navigateUp()
                    }) {
                        Text("Yes", color = Color.Red) // custom button text color
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog.value = false }) {
                        Text("Cancel", color = QuizCyan)
                    }
                },
                backgroundColor = Color(0xFF1C1C1E), // custom background color
                shape = MaterialTheme.shapes.medium // optional: rounded corners
            )

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

@Composable
fun QuestionTimer(
    totalTime: Int = 10_000,
    modifier: Modifier = Modifier,
    onTimeOut: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(totalTime) }

    LaunchedEffect(key1 = totalTime) {
        val interval = 100L
        while (timeLeft > 0) {
            delay(interval)
            timeLeft -= interval.toInt()
        }
        onTimeOut()
    }

    val progress = timeLeft / totalTime.toFloat()

    LinearProgressIndicator(
        progress = progress.coerceIn(0f, 1f),
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        color = QuizCyan,
        backgroundColor = Color.LightGray
    )
}


