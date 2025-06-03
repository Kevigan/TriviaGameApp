package com.example.triviagameapp.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _score = mutableStateOf(0)
    val score: State<Int> get() = _score

    var selectedCategoryId by mutableStateOf<Int?>(null)
        private set

    var selectedDifficulty by mutableStateOf<String?>(null)
        private set

    var increaseScoreValue by mutableStateOf(1)
        private set

    var timerValue by mutableStateOf(30) // Default to 30s for easy
        private set

    fun setCategory(id: Int) {
        selectedCategoryId = id
    }

    fun setDifficulty(diff: String) {
        selectedDifficulty = diff.lowercase()

        when (selectedDifficulty) {
            "easy" -> {
                increaseScoreValue = 1
                timerValue = 30_000
            }
            "medium" -> {
                increaseScoreValue = 2
                timerValue = 20_000
            }
            "hard" -> {
                increaseScoreValue = 3
                timerValue = 10_000
            }
            else -> {
                increaseScoreValue = 1
                timerValue = 30_000
            }
        }
    }

    fun increaseScore() {
        _score.value += increaseScoreValue
    }

    fun resetScore() {
        _score.value = 0
    }
}

