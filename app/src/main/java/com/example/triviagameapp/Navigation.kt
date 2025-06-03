package com.example.triviagameapp

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triviagameapp.ViewModels.GameViewModel
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.example.triviagameapp.ViewModels.TimerViewModel
import com.example.triviagameapp.Views.AccountView
import com.example.triviagameapp.Views.CategoryView
import com.example.triviagameapp.Views.DifficultyView
import com.example.triviagameapp.Views.GameView
import com.example.triviagameapp.Views.HomeView
import com.example.triviagameapp.Views.LeaderboardView
import com.example.triviagameapp.Views.ScoreView
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

        composable(Screen.GameScreen.route) { backStackEntry ->
            // Retrieve the categoryId from the navigation arguments
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 0
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