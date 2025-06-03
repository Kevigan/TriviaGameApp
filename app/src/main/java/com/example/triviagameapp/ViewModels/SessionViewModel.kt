package com.example.triviagameapp.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.triviagameapp.Data.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()  // Initialize Firestore instance

    // Mutable state to hold the current authenticated user
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userName = MutableStateFlow("Unknown User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userScore = MutableStateFlow(0)
    val userScore: StateFlow<Int> = _userScore.asStateFlow()

    val isEmailUser: Boolean
        get() = _currentUser.value?.providerData?.any {
            it.providerId == EmailAuthProvider.PROVIDER_ID
        } == true


    // Initializing Auth State Listener to track user changes
    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user

            // Auto-load user data if logged in
            if (user != null) {
                loadUserData()
            } else {
                _userName.value = "Not logged in"
                _userScore.value = 0
            }
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

        val userId = user.uid
        val userDocRef = db.collection("users").document(userId)

        // First delete from Firestore
        userDocRef.delete().addOnSuccessListener {
            // Then delete from FirebaseAuth
            user.delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }.addOnFailureListener { exception ->
            Log.e("SessionViewModel", "Failed to delete Firestore user document", exception)
            onFailure(exception)
        }
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
    fun saveUserToFirestore(userName: String, email: String, onComplete: () -> Unit = {}) {
        val user = _currentUser.value ?: return
        val userId = user.uid
        val finalUserName = user.displayName ?: userName

        val userDocRef = db.collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "userId" to userId,
                    "name" to finalUserName,
                    "email" to email,
                    "totalScore" to 0,
                    "categoryScores" to mapOf<String, Int>()
                )

                userDocRef.set(userData).addOnSuccessListener {
                    Log.d("SessionViewModel", "New user created in Firestore")
                    onComplete()
                }.addOnFailureListener {
                    Log.e("SessionViewModel", "Failed to create user", it)
                    onComplete()
                }
            } else {
                Log.d("SessionViewModel", "User already exists, skipping Firestore overwrite")
                onComplete()
            }
        }.addOnFailureListener {
            Log.e("SessionViewModel", "Failed to check if user exists", it)
            onComplete()
        }
    }



    // Fetch user data from Firestore
    fun fetchUserDataFromFirestore(onSuccess: (UserData?) -> Unit) {
        val user = _currentUser.value ?: return onSuccess(null)

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val data = snapshot.toObject(UserData::class.java)
                onSuccess(data)
            }
            .addOnFailureListener {
                Log.e("SessionViewModel", "Failed to fetch user data", it)
                onSuccess(null)
            }
    }

    fun loadUserData() {
        val user = _currentUser.value ?: return
        fetchUserDataFromFirestore { data ->
            _userName.value = data?.name ?: "Unknown User"
            _userScore.value = data?.totalScore ?: 0
        }
    }

    fun updateTotalScore(newScore: Int, onComplete: (Boolean) -> Unit = {}) {
        val user = _currentUser.value ?: return onComplete(false)
        val userRef = db.collection("users").document(user.uid)

        db.runTransaction { tx ->
            val snapshot = tx.get(userRef)
            val currentScore = snapshot.getLong("totalScore") ?: 0L
            val updatedScore = currentScore + newScore
            tx.update(userRef, "totalScore", updatedScore)
        }.addOnSuccessListener {
            _userScore.value += newScore
            Log.d("SessionViewModel", "Score updated in Firestore")
            onComplete(true)
        }.addOnFailureListener {
            Log.e("SessionViewModel", "Failed to update score", it)
            onComplete(false)
        }
    }

    fun fetchLeaderboard(onResult: (List<UserData>) -> Unit) {
        db.collection("users")
            .orderBy("totalScore", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val leaderboard = snapshot.toObjects(UserData::class.java)
                onResult(leaderboard)
            }
            .addOnFailureListener { e ->
                Log.e("SessionViewModel", "Failed to fetch leaderboard", e)
                onResult(emptyList())
            }
    }

}
