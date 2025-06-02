package com.example.triviagameapp.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triviagameapp.Data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // LiveData to hold UserData (the data can be observed in Composables)
    private val _userData = mutableStateOf<UserData?>(null)
    val userData: State<UserData?> get() = _userData

    // Fetch user data from Firestore
    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            val userRef = db.collection("users").document(userId)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    _userData.value = documentSnapshot.toObject(UserData::class.java)
                }
            }.addOnFailureListener { exception ->
                // Handle error, possibly set an error state
            }
        }
    }

    // Save/Update user data (including scores) in Firestore
    fun saveUserData(userId: String, category: String, score: Int) {
        viewModelScope.launch {
            val userRef = db.collection("users").document(userId)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentUser = documentSnapshot.toObject(UserData::class.java)

                    // Update category scores and total score
                    val updatedCategoryScores = currentUser?.categoryScores?.toMutableMap() ?: mutableMapOf()
                    updatedCategoryScores[category] = updatedCategoryScores.getOrDefault(category, 0) + score

                    val updatedTotalScore = updatedCategoryScores.values.sum()

                    val updatedUserData = UserData(
                        userId = userId,
                        name = currentUser?.name ?: "Unknown",
                        email = currentUser?.email ?: "Unknown",
                        categoryScores = updatedCategoryScores,
                        totalScore = updatedTotalScore
                    )

                    userRef.set(updatedUserData)
                        .addOnSuccessListener {
                            // Successfully saved
                        }
                        .addOnFailureListener {
                            // Handle error
                        }
                }
            }
        }
    }

    // Check if user exists and create if needed
    fun createUserIfNotExist(userId: String) {
        viewModelScope.launch {
            val userRef = db.collection("users").document(userId)
            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    val newUser = UserData(
                        userId = userId,
                        name = "New User",  // You can prompt for the name
                        email = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown",
                        categoryScores = mapOf(),  // Empty category scores
                        totalScore = 0
                    )
                    userRef.set(newUser)
                }
            }
        }
    }
}
