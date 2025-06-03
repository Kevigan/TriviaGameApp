package com.example.triviagameapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.triviagameapp.ViewModels.GameViewModel
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.example.triviagameapp.ViewModels.TimerViewModel
import com.example.triviagameapp.Views.GameView
import com.example.triviagameapp.Views.HomeView
import com.example.triviagameapp.ui.theme.TriviaGameAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var timerViewModel: TimerViewModel
        lateinit var gameViewModel: GameViewModel
        lateinit var sessionViewModel: SessionViewModel

        super.onCreate(savedInstanceState)

        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        sessionViewModel = ViewModelProvider(this).get(SessionViewModel::class.java)

        setContent {
            val context = LocalContext.current

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("545378547300-c8q4ucoiae3ur6lv05cu6ous3t8rcq1a.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = remember {
                GoogleSignIn.getClient(context, gso)
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task, sessionViewModel, context, onLoginSuccess = {
                    Toast.makeText(context, "Signed in with Google", Toast.LENGTH_SHORT).show()
                })
            }
            val navController = rememberNavController()

            TriviaGameAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(navController, timerViewModel, gameViewModel, googleSignInClient, launcher, sessionViewModel)
                }
            }
        }
    }

}
fun handleGoogleSignInResult(
    task: Task<GoogleSignInAccount>,
    sessionViewModel: SessionViewModel,
    context: Context,  // Added context as a parameter
    onLoginSuccess: () -> Unit
) {
    try {
        if (task.isSuccessful) {
            val account = task.result
            val idToken = account?.idToken // Use this for Firebase authentication
            val email = account?.email
            val displayName = account?.displayName ?: "Unknown" // Use the Google display name if available

            // Authenticate with Firebase using the Google ID token
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { firebaseTask ->
                    if (firebaseTask.isSuccessful) {
                        // After Firebase authentication, save the user to Firestore
                        sessionViewModel.saveUserToFirestore(displayName, email ?: "")
                        sessionViewModel.loadUserData()
                        onLoginSuccess() // Trigger the login success
                    } else {
                        // Handle error if Firebase sign-in fails
                        Toast.makeText(context, "Firebase Authentication failed: ${firebaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Handle error if Google sign-in fails
            Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    } catch (e: ApiException) {
        // Handle error if Google Sign-In throws an ApiException
        Toast.makeText(context, "Google Sign-In error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}




