package com.example.triviagameapp

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.triviagameapp.ViewModels.GameViewModel
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.example.triviagameapp.ViewModels.TimerViewModel
import com.example.triviagameapp.views.AccountView
import com.example.triviagameapp.views.CategoryView
import com.example.triviagameapp.views.DifficultyView
import com.example.triviagameapp.views.GameView
import com.example.triviagameapp.views.HomeView
import com.example.triviagameapp.views.LeaderboardView
import com.example.triviagameapp.views.ScoreView
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun Navigation(
    navController: NavHostController,
    timerViewModel: TimerViewModel,
    gameViewModel: GameViewModel,
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    sessionViewModel: SessionViewModel
){
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route){

        composable(Screen.HomeScreen.route){
            HomeView(
                navController = navController,
                sessionViewModel = sessionViewModel,
                gameViewModel,
                googleSignInClient,
                googleSignInLauncher
                )
        }

        composable(Screen.GameScreen.route) {
            GameView(navController = navController, gameViewModel, timerViewModel)
        }

        composable(Screen.ScoreScreen.route){
            ScoreView(navController, gameViewModel, sessionViewModel)
        }

        composable(Screen.CategoryScreen.route){
            CategoryView(navController, gameViewModel)
        }

        composable(Screen.AccountScreen.route){
            AccountView(sessionViewModel, navController)
        }

        composable(Screen.LeaderboardScreen.route){
            LeaderboardView(sessionViewModel, navController)
        }

        composable(Screen.DifficultyScreen.route){
            DifficultyView(navController, gameViewModel)
        }
    }
}