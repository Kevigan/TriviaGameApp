package com.example.triviagameapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.triviagameapp.ViewModels.TimerViewModel
import com.example.triviagameapp.Views.GameView
import com.example.triviagameapp.Views.HomeView
import com.example.triviagameapp.ui.theme.TriviaGameAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var timerViewModel: TimerViewModel
        super.onCreate(savedInstanceState)

        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        setContent {
            val navController = rememberNavController()

            TriviaGameAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(navController, timerViewModel)
                }
            }
        }
    }
}

