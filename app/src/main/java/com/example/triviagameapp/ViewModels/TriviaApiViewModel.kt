package com.example.triviagameapp.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.triviagameapp.NetWork.ApiService
import com.example.triviagameapp.Data.TriviaQuestion

import java.net.URLDecoder

class TriviaApiViewModel(private val apiService: ApiService) : ViewModel() {

    private val _triviaQuestions = mutableStateOf<List<TriviaQuestion>>(emptyList())
    val triviaQuestions: State<List<TriviaQuestion>> get() = _triviaQuestions

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _sessionToken = mutableStateOf<String?>(null)

    // Decode URL-encoded strings
    private fun decodeText(text: String): String {
        return URLDecoder.decode(text, "UTF-8")
    }

    fun fetchSessionToken() {
        viewModelScope.launch {
            try {
                val response = apiService.getSessionToken()
                if (response.response_code == 0) {
                    _sessionToken.value = response.token
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchTriviaQuestions(categoryId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = _sessionToken.value
                val response = apiService.getTriviaQuestions(
                    amount = 10,
                    encode = "url3986",
                    token = token,
                    category = categoryId
                )

                // Decode each question and answer
                _triviaQuestions.value = response.results.map {
                    it.copy(
                        question = decodeText(it.question),
                        correct_answer = decodeText(it.correct_answer),
                        incorrect_answers = it.incorrect_answers.map { answer -> decodeText(answer) }
                    )
                }

            } catch (e: Exception) {
                _triviaQuestions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
