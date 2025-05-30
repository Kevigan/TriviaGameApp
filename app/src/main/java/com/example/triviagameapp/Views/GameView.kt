package com.example.triviagameapp.Views

import android.content.res.Configuration
import android.util.Log
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
import com.example.triviagameapp.NetWork.ApiService
import com.example.triviagameapp.NetWork.RetrofitInstance
import com.example.triviagameapp.ViewModels.TimerViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triviagameapp.ViewModels.TriviaApiViewModel
import com.example.triviagameapp.ViewModels.TriviaApiViewModelFactory

@Composable
fun GameView(
    navController: NavController,
    apiService: ApiService = RetrofitInstance.apiService, // Retrofit instance
    timerViewModel: TimerViewModel = viewModel() // Timer ViewModel
) {
    // Use the factory to create the TriviaApiViewModel
    val triviaApiViewModel: TriviaApiViewModel = viewModel(
        factory = TriviaApiViewModelFactory(apiService)
    )

    // Observe the trivia questions and loading state
    val triviaQuestions by triviaApiViewModel.triviaQuestions
    val isLoading by triviaApiViewModel.isLoading

    // Track the current question index
    var currentQuestionIndex by remember { mutableStateOf(0) }

    // Fetch trivia questions when the composable is first composed
    LaunchedEffect(key1 = triviaQuestions.isEmpty()) {
        // Only fetch the trivia questions if they are not already loaded
        if (triviaQuestions.isEmpty()) {
            try {
                triviaApiViewModel.fetchSessionToken()  // Fetch session token first
                triviaApiViewModel.fetchTriviaQuestions()  // Fetch trivia questions once the token is ready
            } catch (e: Exception) {
                Log.e("GameView", "Error fetching trivia questions", e)
                // Optionally show a UI message to the user
            }
        }
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val showExitDialog = remember { mutableStateOf(false) }

    // Observe the time left from the TimerViewModel using by delegation
    val timeLeft = timerViewModel.timeLeft.value

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
            contentScale = ContentScale.Crop
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
            val currentQuestion = triviaQuestions.getOrNull(currentQuestionIndex)
            Text(
                text = currentQuestion?.question ?: "Loading...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // 🔹 Timer
        QuestionTimer(
            totalTime = timeLeft,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp), // Add padding to avoid overlap
            onTimeOut = {
                // Handle timeout here
            }
        )

        // Show loading screen if the data is being fetched
        if (isLoading) {
            LoadingScreen()
        } else {
            // Show the trivia content once the questions are loaded
            val currentQuestion = triviaQuestions.getOrNull(currentQuestionIndex)

            currentQuestion?.let { trivia ->
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display the answers (example)
                    (listOf(trivia.correct_answer) + trivia.incorrect_answers).shuffled().forEach { answer ->
                        AnswerButton(text = answer) {
                            // Handle answer selection
                            // After selecting an answer, move to the next question
                            if (currentQuestionIndex < triviaQuestions.size - 1) {
                                currentQuestionIndex++
                            }
                        }
                    }
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
                        Text("Yes", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog.value = false }) {
                        Text("Cancel", color = QuizCyan)
                    }
                },
                backgroundColor = Color(0xFF1C1C1E),
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    // Simple loading screen with a circular progress indicator
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = QuizCyan)
    }
}

@Composable
fun AnswerButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
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
    val progress = totalTime / 10000f

    LinearProgressIndicator(
        progress = progress.coerceIn(0f, 1f),
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        color = QuizCyan,
        backgroundColor = Color.LightGray
    )
}

