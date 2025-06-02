package com.example.triviagameapp.Data

data class UserData(
    val userId: String = "",  // Default value for userId
    val name: String = "",    // Default value for name
    val email: String = "",   // Default value for email
    val totalScore: Int = 0,  // Default value for totalScore
    val categoryScores: Map<String, Int> = emptyMap()  // Default value for categoryScores
)


