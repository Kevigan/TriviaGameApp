package com.example.triviagameapp.ViewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timeLeft = mutableStateOf(10000)
    val timeLeft: State<Int> get() = _timeLeft

    // Job to keep track of the running timer coroutine
    private var timerJob: Job? = null

    // Start the timer
    fun startTimer(onTimeOut: () -> Unit) {
        // If there's already a running timer, cancel it first
        timerJob?.cancel()

        // Start a new coroutine
        timerJob = viewModelScope.launch {
            val interval = 1000L // Timer updates every 100ms
            while (_timeLeft.value > 0) {
                delay(interval)
                _timeLeft.value -= interval.toInt() // Decrease remaining time by 100ms
            }
            onTimeOut()  // Call timeout callback when the timer is done
        }
    }

    // Reset the timer
    fun resetTimer() {
        _timeLeft.value = 10000  // Reset to initial time
        timerJob?.cancel()  // Cancel the running coroutine
        timerJob = null  // Clear the job reference
    }

    fun stopTimer(){
        timerJob?.cancel()
        timerJob = null
    }
}



