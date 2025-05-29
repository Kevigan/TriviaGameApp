package com.example.triviagameapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triviagameapp.Views.GameView
import com.example.triviagameapp.Views.HomeView

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
){
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route){

        composable(Screen.HomeScreen.route){
            HomeView(navController)
        }

        composable(Screen.GameScreen.route){
            GameView(navController)
        }

    }
}