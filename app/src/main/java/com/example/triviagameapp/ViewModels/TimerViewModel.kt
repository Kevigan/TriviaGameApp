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
    fun startTimer(timerMillis: Int, onTimeOut: () -> Unit) {
        timerJob?.cancel()

        _timeLeft.value = timerMillis
        val interval = 100L

        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(interval)
                _timeLeft.value = (_timeLeft.value - interval.toInt()).coerceAtLeast(0)
            }
            onTimeOut()
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



