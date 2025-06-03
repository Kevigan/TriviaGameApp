package com.example.triviagameapp.views

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.triviagameapp.R
import com.example.triviagameapp.UiEventDispatcher
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun LoginDialog(
    sessionViewModel: SessionViewModel,
    onLoginSuccess: () -> Unit,
    onDismiss: () -> Unit,
    googleSignInClient: GoogleSignInClient,
    launcher: ActivityResultLauncher<Intent>
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isRegisterMode) "Register Account" else "Login Required",
                color = MaterialTheme.colors.onSurface
            )
        },
        text = {
            Column {
                ThemedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email"
                )

                ThemedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true
                )

                // Add a new field for the user's name
                if (isRegisterMode) {
                    ThemedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = "Name"
                    )

                    ThemedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        isPassword = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Validate inputs
                if (email.isBlank() || password.isBlank() || (isRegisterMode && confirmPassword.isBlank()) || (isRegisterMode && userName.isBlank())) {
                    UiEventDispatcher.send("Please fill in all fields")
                    return@TextButton
                }

                if (isRegisterMode) {
                    // Validate passwords
                    if (password != confirmPassword) {
                        UiEventDispatcher.send("Passwords do not match")
                        return@TextButton
                    }

                    // Register the user and save to Firestore
                    sessionViewModel.register(
                        email.trim(), password,
                        onSuccess = {
                            // Now save the user's name to Firestore
                            sessionViewModel.saveUserToFirestore(userName.trim(), email.trim()) {
                                sessionViewModel.loadUserData()
                                UiEventDispatcher.send("Registered and logged in")
                                onLoginSuccess()
                            }
                        },
                        onFailure = {
                            Log.e("RegistrationError", "Registration failed: ${it.message}")
                            UiEventDispatcher.send("Registration failed: ${it.message}")
                        }
                    )
                } else {
                    sessionViewModel.login(
                        email.trim(), password,
                        onSuccess = {
                            UiEventDispatcher.send("Login successful")
                            onLoginSuccess()
                        },
                        onFailure = {
                            Log.e("LoginError", "Login failed: ${it.message}")
                            Toast.makeText(context, "${it.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }) {
                Text(text = if (isRegisterMode) "Register" else "Login", color = MaterialTheme.colors.onSurface)
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                    Text(
                        if (isRegisterMode) "Switch to Login" else "Switch to Register",
                        color = MaterialTheme.colors.onSurface
                    )
                }

                TextButton(onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }) {
                    Text("Sign in with Google", color = MaterialTheme.colors.onSurface)
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MaterialTheme.colors.onSurface)
                }
            }
        }
    )
}


@Composable
fun ThemedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    var showPassword by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colors

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = MaterialTheme.colors.onSurface // ✅ Theme-aware label
            )
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !showPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (isPassword) {
                val icon = if (showPassword)
                    R.drawable.baseline_visibility_off_24
                else
                    R.drawable.baseline_visibility_24

                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        tint = MaterialTheme.colors.onSurface // ✅ Theme-aware icon
                    )
                }
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onSurface,
            focusedBorderColor = MaterialTheme.colors.primary,
            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colors.primary,
            focusedLabelColor = MaterialTheme.colors.primary,
            unfocusedLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    )

}
