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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.triviagameapp.ui.theme.RedTransparent

@Composable
fun GameView(
    navController: NavController,
    timerViewModel: TimerViewModel,
    apiService: ApiService = RetrofitInstance.apiService
) {
    val triviaApiViewModel: TriviaApiViewModel = viewModel(
        factory = TriviaApiViewModelFactory(apiService)
    )

    // Observe the trivia questions and loading state
    val triviaQuestions by triviaApiViewModel.triviaQuestions
    val isLoading by triviaApiViewModel.isLoading

    // Track the current question index
    var currentQuestionIndex by rememberSaveable { mutableStateOf(0) }

    // Store the shuffled answers in a state to retain it across configuration changes
    val shuffledAnswers = rememberSaveable { mutableStateOf<List<String>>(emptyList()) }

    // Track time left using rememberSaveable
    //val timeLeft by rememberSaveable { timerViewModel.timeLeft }

    // Track dialog visibility using rememberSaveable
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val showExitDialog = rememberSaveable { mutableStateOf(false) }
    val showNextQuestionDialog = rememberSaveable { mutableStateOf(false) }
    val showAnswerFeedbackDialog = rememberSaveable { mutableStateOf(false) }

    // Track if the game has started
    var gameStarted by rememberSaveable { mutableStateOf(false) }

    // Feedback message (Correct or Incorrect)
    val answerFeedbackMessage = rememberSaveable { mutableStateOf(listOf("", "")) }

    // Fetch trivia questions when the composable is first composed
    LaunchedEffect(key1 = triviaQuestions.isEmpty()) {
        if (triviaQuestions.isEmpty()) {
            try {
                triviaApiViewModel.fetchSessionToken()  // Fetch session token first
                triviaApiViewModel.fetchTriviaQuestions()  // Fetch trivia questions once the token is ready
            } catch (e: Exception) {
                Log.e("GameView", "Error fetching trivia questions", e)
            }
        }
    }

    // Show the dialog when questions are fetched (but only if the dialog hasn't been shown yet)
    LaunchedEffect(key1 = triviaQuestions.isNotEmpty()) {
        if (triviaQuestions.isNotEmpty() && !showDialog.value && !gameStarted) {
            showDialog.value = true
        }
    }

    // Handle the start button click
    fun startGame() {
        gameStarted = true  // Start the game
        currentQuestionIndex = 0  // Start with the first question
        timerViewModel.resetTimer()
        timerViewModel.startTimer {
            // Handle timeout (move to next question after time out)
            showNextQuestionDialog.value = true
        }
    }

    // Handle exit confirmationCustomDialog
    fun exitGame() {
        showExitDialog.value = false
        navController.navigateUp() // Exit logic
    }

    fun shuffleAnswers(index: Int): List<String> {
        val currentQuestion = triviaQuestions.getOrNull(index)
        return if (currentQuestion != null) {
            (listOf(currentQuestion.correct_answer) + currentQuestion.incorrect_answers).shuffled()
        } else {
            emptyList() // Return an empty list if the question is not available
        }
    }

    // Handle the "Next" button click to move to the next question
    fun moveToNextQuestion() {
        if (currentQuestionIndex < triviaQuestions.size - 1) {
            currentQuestionIndex++
            shuffledAnswers.value = shuffleAnswers(currentQuestionIndex)
            timerViewModel.resetTimer()
            timerViewModel.startTimer {
                showNextQuestionDialog.value = true
            }
        }
        showNextQuestionDialog.value = false
    }

    // Stop the timer when an answer is selected and check the correctness of the answer
    fun handleAnswerSelection(selectedAnswer: String) {
        timerViewModel.stopTimer()
        val currentQuestion = triviaQuestions.getOrNull(currentQuestionIndex)
        val isCorrect = currentQuestion?.correct_answer == selectedAnswer

        // Set the feedback message list
        answerFeedbackMessage.value = if (isCorrect) {
            listOf("Correct!", "")
        } else {
            listOf("Incorrect!", currentQuestion?.correct_answer ?: "N/A")
        }
        // Show the feedback dialog
        showAnswerFeedbackDialog.value = true
    }

    // Get the current configuration to check the orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // Intercept back button to show confirmation
    BackHandler {
        if (!showDialog.value) {  // Only show exit dialog if the start dialog is not visible
            when {
                showExitDialog.value -> {
                    // If the exit dialog is visible, dismiss it
                    showExitDialog.value = false
                }

                else -> {
                    // If no dialogs are visible, show the exit dialog
                    showExitDialog.value = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.game_background),
            contentDescription = "Game Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            if (gameStarted) {
                QuestionTimer(
                    //timeLeft = timeLeft,
                    modifier = Modifier.padding(top = 16.dp),
                    timerViewModel
                )
            }

            if (gameStarted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                        .background(QuizCyan2, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Question Text
                    val currentQuestion = triviaQuestions.getOrNull(currentQuestionIndex)
                    Text(
                        text = currentQuestion?.question ?: "Loading...",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Answers (below the question)
                if (!isLoading) {
                    val currentQuestion = triviaQuestions.getOrNull(currentQuestionIndex)
                    currentQuestion?.let { trivia ->
                        if (shuffledAnswers.value.isEmpty()) {
                            // Shuffle answers only once
                            shuffledAnswers.value =
                                (listOf(trivia.correct_answer) + trivia.incorrect_answers).shuffled()
                        }

                        if (isPortrait) {
                            // Portrait mode: Arrange answers in a column (one on top of the other)
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                shuffledAnswers.value.forEach { answer ->
                                    AnswerButton(text = answer) {
                                        // Handle answer selection
                                        handleAnswerSelection(answer)
                                    }
                                }
                            }
                        } else {
                            // Landscape mode: Arrange answers in two rows (2 on top, 2 on bottom)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // First row with 2 answers
                                shuffledAnswers.value.take(2).forEach { answer ->
                                    AnswerButton(
                                        text = answer,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth() ,
                                    ) {
                                        handleAnswerSelection(answer)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Second row with 2 answers
                                shuffledAnswers.value.drop(2).take(2).forEach { answer ->
                                    AnswerButton(
                                        text = answer,
                                        modifier = Modifier
                                            .weight(1f) // Equally share the space
                                            .fillMaxWidth() // Fill max width in landscape mode
                                    ) {
                                        handleAnswerSelection(answer)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Show loading screen if the data is being fetched
                LoadingScreen()
            }
        }
        Log.d("GameView", "Blaaablaaalbaaaal: ${answerFeedbackMessage.value[1]}")
        // Show Exit Confirmation Dialog
        CustomDialog(
            showDialog = showExitDialog,
            title = "Quit Game?",
            message = "Are you sure you want to exit the game? Your progress will be lost.",
            buttonText = "Yes",
            onButtonClick = {
                exitGame()
            },
            headerTextColor = Color.Red,
            backgroundAlpha = 0.5f
        )

        // Show Start Game Dialog when trivia questions are fetched
        CustomDialog(
            showDialog = showDialog,
            title = "Start Game",
            message = "Press Start to begin the game.",
            buttonText = "Start",
            onButtonClick = {
                startGame()
            }
        )

        // Show Next Question Dialog when time runs out
        CustomDialog(
            showDialog = showNextQuestionDialog,
            title = "Next Question",
            message = "Time's up! Click next to proceed.",
            buttonText = "Next",
            onButtonClick = {
                moveToNextQuestion()
            }
        )

        // Show Answer Feedback Dialog after answer selection
        CustomDialog(
            showDialog = showAnswerFeedbackDialog,
            title = answerFeedbackMessage.value[0],
            message =  if (answerFeedbackMessage.value[0] == "Incorrect") {
                "The correct answer was: ${answerFeedbackMessage.value[1]}"
            } else {
                //Log.d("GameView", "Displaying correct answer message: ${answerFeedbackMessage.value[1]}")
                "Well done!"
            },
            buttonText = "Next",
            backgroundColor = RedTransparent,
            backgroundAlpha = 0.5f,
            background2Alpha = 1f,
            onButtonClick = {
                moveToNextQuestion()
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = QuizCyan)
    }
}

@Composable
fun AnswerButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth(), // Ensure buttons fill width in portrait mode
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
    //timeLeft: Int,  // Pass remaining time (timeLeft)
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel
) {
    val timeLeft by rememberSaveable { timerViewModel.timeLeft }

    // Calculate progress as the ratio of remaining time to total time
    val totalTime = 10000f
    val progress = timeLeft / totalTime  // Calculate the progress
    LinearProgressIndicator(
        progress = progress.coerceIn(0f, 1f),  // Make sure progress is between 0 and 1
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        color = QuizCyan,
        backgroundColor = Color.LightGray
    )
}

@Composable
fun CustomDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    backgroundColor: Color = QuizCyan,
    headerTextColor: Color = Color.White,
    textColor: Color = Color.LightGray,
    backgroundAlpha: Float = 0.1f,
    background2Alpha: Float = 1.0f,
) {
    if (showDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = backgroundAlpha))
        ) {
            // The actual dialog content
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-75).dp)
                    .background(
                        color = backgroundColor.copy(alpha = background2Alpha),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Title
                    Text(
                        text = title,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = headerTextColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Message
                    Text(
                        text = message,
                        fontSize = 26.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Button
                    TextButton(
                        onClick = {
                            onButtonClick()
                            showDialog.value = false // Hide the dialog after pressing the button
                        }
                    ) {
                        Text(text = buttonText, fontSize = 26.sp, color = Color.White)
                    }
                }
            }
        }
    }
}













