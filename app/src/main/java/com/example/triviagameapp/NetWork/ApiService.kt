package com.example.triviagameapp.NetWork

import com.example.triviagameapp.Data.SessionTokenResponse
import com.example.triviagameapp.Data.TriviaApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Endpoint to request a session token
    @GET("api_token.php?command=request")
    suspend fun getSessionToken(): SessionTokenResponse

    @GET("api.php") // Endpoint to get trivia questions with parameters (amount, encoding, token)
    suspend fun getTriviaQuestions(
        @Query("amount") amount: Int,
        @Query("encode") encode: String,
        @Query("token") token: String? = null
    ): TriviaApiResponse

}