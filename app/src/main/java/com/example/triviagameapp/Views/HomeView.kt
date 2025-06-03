package com.example.triviagameapp.Views

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ViewModels.GameViewModel
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.example.triviagameapp.ui.theme.QuizBlue
import com.example.triviagameapp.ui.theme.QuizCyan
import com.example.triviagameapp.ui.theme.QuizCyanTransparent
import com.example.triviagameapp.ui.theme.QuizGreen
import com.example.triviagameapp.ui.theme.QuizOrange
import com.example.triviagameapp.ui.theme.QuizTitleBackground
import com.example.triviagameapp.ui.theme.QuizYellow
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun HomeView(
    navController: NavController,
    sessionViewModel: SessionViewModel,
    gameViewModel: GameViewModel,
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>
) {
    gameViewModel.resetScore()

    val scaffoldState = rememberScaffoldState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val currentUser by sessionViewModel.currentUser.collectAsState()
    val userName by sessionViewModel.userName.collectAsState()
    val userScore by sessionViewModel.userScore.collectAsState()

    val showLoginDialog = remember { mutableStateOf(currentUser == null) }

    // Watch for auth changes to control login dialog
    LaunchedEffect(currentUser) {
        showLoginDialog.value = currentUser == null
    }

    if (showLoginDialog.value) {
        LoginDialog(
            onLoginSuccess = { showLoginDialog.value = false },
            onDismiss = { },
            sessionViewModel = sessionViewModel,
            googleSignInClient = googleSignInClient,
            launcher = googleSignInLauncher
        )
    }

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
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(bottom = 1.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Quiz Trivia",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Score: $userScore",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = if (currentUser != null)
                            "Logged in as: $userName"
                        else
                            "Not logged in",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (isPortrait) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuizButtons(navController, true, sessionViewModel, googleSignInClient)
                }
            } else {
                // Bottom-aligned in landscape
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuizButtons(navController, false, sessionViewModel, googleSignInClient)
                }
            }
        }
    }
}

@Composable
private fun QuizButtons(
    navController: NavController,
    isPortrait: Boolean,
    sessionViewModel: SessionViewModel,
    googleSignInClient: GoogleSignInClient
) {
    val buttonModifier = Modifier
        .width(260.dp)
        .height(60.dp)

    if (isPortrait) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Button(
                onClick = { navController.navigate(Screen.CategoryScreen.route) },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
            ) {
                Text("Play", fontSize = 18.sp)
            }

            Button(
                onClick = { navController.navigate(Screen.LeaderboardScreen.route) },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
            ) {
                Text("Leaderboard", fontSize = 18.sp)
            }

            Button(
                onClick = { navController.navigate(Screen.AccountScreen.route) },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
            ) {
                Text("Account", fontSize = 18.sp)
            }

            Button(
                onClick = { sessionViewModel.signOut(googleSignInClient) },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
            ) {
                Text("Logout", fontSize = 18.sp)
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.CategoryScreen.route) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
                ) {
                    Text("Play", fontSize = 18.sp)
                }

                Button(
                    onClick = { navController.navigate(Screen.LeaderboardScreen.route) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
                ) {
                    Text("Leaderboard", fontSize = 18.sp)
                }

                Button(
                    onClick = { navController.navigate(Screen.AccountScreen.route) },
                    modifier = buttonModifier,
                    colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
                ) {
                    Text("Account", fontSize = 18.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { sessionViewModel.signOut(googleSignInClient) },
                    modifier = buttonModifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(QuizCyan, contentColor = Color.White)
                ) {
                    Text("Logout", fontSize = 18.sp)
                }
            }
        }
    }
}





