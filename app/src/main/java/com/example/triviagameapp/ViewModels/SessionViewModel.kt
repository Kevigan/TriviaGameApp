package com.example.triviagameapp.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.triviagameapp.Data.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()  // Initialize Firestore instance

    // Mutable state to hold the current authenticated user
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Initializing Auth State Listener to track user changes
    init {
        auth.addAuthStateListener {
            _currentUser.value = it.currentUser
        }
    }

    // Sign Out method to log the user out of the app
    fun signOut(googleSignInClient: GoogleSignInClient? = null) {
        auth.signOut()
        googleSignInClient?.signOut() // Sign out from Google as well if applicable
    }

    // Change password after reauthentication
    fun changePassword(
        email: String,
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    // Reauthenticate the user with email and password
    fun reauthenticate(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Delete the user from Firebase authentication
    fun deleteUser(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser ?: return
        user.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Login method to authenticate the user
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Register method to create a new account with email and password
    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Password reset method to send reset instructions to the user's email
    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Save user data to Firestore (including name, email, score)
    fun saveUserToFirestore(userName: String, email: String) {
        val user = _currentUser.value ?: return  // If user is null, return early

        val userId = user.uid  // User ID from the current authenticated user
        val finalUserName = user.displayName ?: userName // Use either Google display name or the user-provided name

        val userData = hashMapOf(
            "userId" to userId,
            "name" to finalUserName,  // Use either Google display name or the user-provided name
            "email" to email,
            "totalScore" to 0, // Initialize with zero score
            "categoryScores" to mapOf<String, Int>() // Initialize with empty category scores
        )

        db.collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("SessionViewModel", "User data saved to Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e("SessionViewModel", "Error saving user data", exception)
            }
    }

    // Get user name from Firestore (for email login)
    fun getUserNameFromFirestore(onSuccess: (String?) -> Unit) {
        val user = auth.currentUser
        user?.let {
            db.collection("users")
                .document(it.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val name = documentSnapshot.getString("name")
                    onSuccess(name)
                }
                .addOnFailureListener { exception ->
                    onSuccess(null)
                    Log.e("SessionViewModel", "Error getting user name", exception)
                }
        } ?: onSuccess(null)
    }

    // Fetch user data from Firestore
    fun fetchUserDataFromFirestore(onSuccess: (UserData?) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            Log.e("SessionViewModel", "No user is currently signed in.")
            onSuccess(null)
            return
        }

        val userRef = db.collection("users").document(user.uid)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.toObject(UserData::class.java)
                onSuccess(userData)
            } else {
                Log.e("SessionViewModel", "User document not found.")
                onSuccess(null)
            }
        }.addOnFailureListener { exception ->
            // Log the exception for better debugging
            Log.e("SessionViewModel", "Error fetching user data", exception)
            onSuccess(null)
        }
    }

    fun updateTotalScore(newScore: Int, onComplete: (Boolean) -> Unit = {}) {
        val user = _currentUser.value
        if (user == null) {
            Log.e("SessionViewModel", "No user is signed in.")
            onComplete(false)
            return
        }

        val userRef = db.collection("users").document(user.uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentScore = snapshot.getLong("totalScore") ?: 0L
            val updatedScore = currentScore + newScore
            transaction.update(userRef, "totalScore", updatedScore)
        }.addOnSuccessListener {
            Log.d("SessionViewModel", "User score updated successfully.")
            onComplete(true)
        }.addOnFailureListener { exception ->
            Log.e("SessionViewModel", "Failed to update score", exception)
            onComplete(false)
        }
    }

}
