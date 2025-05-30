package com.example.triviagameapp.Data

data class TriviaQuestion(
    val question: String,                     // The trivia question text
    val correct_answer: String,               // The correct answer
    val incorrect_answers: List<String>,     // List of incorrect answers
    val category: String? = null,             // Optional category of the question
    val difficulty: String? = null,           // Optional difficulty of the question, easy medium hard
    val type: String? = null                 // Type of question (e.g., multiple choice)
)
