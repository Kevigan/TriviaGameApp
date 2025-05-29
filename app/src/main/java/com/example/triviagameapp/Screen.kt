package com.example.triviagameapp

sealed class Screen(val route: String){
    object HomeScreen : Screen("home_screen")
    object GameScreen : Screen("game_screen")
}

