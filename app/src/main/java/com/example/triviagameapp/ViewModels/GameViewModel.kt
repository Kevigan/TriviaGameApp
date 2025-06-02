package com.example.triviagameapp.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _score = mutableStateOf(0)
    val score: State<Int> get() = _score

    fun increaseScore() {
        _score.value += 1
    }

}
