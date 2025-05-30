package com.example.triviagameapp.Data

data class SessionTokenResponse(
    val response_code: Int,  // Response code (0 for success)
    val token: String        // The unique session token
)

