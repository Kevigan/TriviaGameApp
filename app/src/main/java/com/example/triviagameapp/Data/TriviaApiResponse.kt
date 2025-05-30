package com.example.triviagameapp.Data

data class TriviaApiResponse(
    val response_code: Int,                      // Status code of the API response
    val results: List<TriviaQuestion>            // List of trivia questions
)
