package com.example.triviagameapp.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    // Mutable state to hold the remaining time
    private val _timeLeft = mutableStateOf(10000)
    val timeLeft: State<Int> get() = _timeLeft

    // Start the timer
    fun startTimer(onTimeOut: () -> Unit) {
        viewModelScope.launch {
            val interval = 100L
            while (_timeLeft.value > 0) {
                delay(interval)
                _timeLeft.value -= interval.toInt()
            }
            onTimeOut()  // Call timeout callback when the timer is done
        }
    }

    // Reset the timer if needed
    fun resetTimer() {
        _timeLeft.value = 10000
    }
}
