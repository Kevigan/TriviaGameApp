package com.example.triviagameapp.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.triviagameapp.Data.UserData
import com.example.triviagameapp.R
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.example.triviagameapp.ui.theme.QuizCyan

@Composable
fun LeaderboardView(
    sessionViewModel: SessionViewModel,
    navController: NavController
) {
    BackHandler {
        navController.navigate(Screen.HomeScreen.route)
    }

    val context = LocalContext.current
    var leaderboard by remember { mutableStateOf<List<UserData>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessionViewModel.fetchLeaderboard {
            leaderboard = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.game_background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.h4,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(leaderboard) { index, user ->
                    Card(
                        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.85f),
                        elevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${index + 1}. ${user.name}", color = Color.Black)
                            Text(text = "${user.totalScore} pts", color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = QuizCyan,
                    contentColor = Color.White
                )
            ) {
                Text("Back")
            }
        }
    }
}
