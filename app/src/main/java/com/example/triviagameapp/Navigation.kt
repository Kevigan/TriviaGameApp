package com.example.triviagameapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triviagameapp.ViewModels.TimerViewModel
import com.example.triviagameapp.Views.CategoryView
import com.example.triviagameapp.Views.GameView
import com.example.triviagameapp.Views.HomeView
import com.example.triviagameapp.Views.ScoreView

@Composable
fun Navigation(
    navController: NavHostController,
    timerViewModel: TimerViewModel
){
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route){

        composable(Screen.HomeScreen.route){
            HomeView(navController)
        }

        composable("gameScreen/{categoryId}") { backStackEntry ->
            // Retrieve the categoryId from the navigation arguments
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toInt() ?: 0
            GameView(navController = navController, categoryId = categoryId, timerViewModel)
        }

        composable(Screen.ScoreScreen.route){
            ScoreView(navController)
        }

        composable(Screen.CategoryScreen.route){
            CategoryView(navController)
        }

    }
}