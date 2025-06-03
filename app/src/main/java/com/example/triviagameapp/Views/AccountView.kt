package com.example.triviagameapp.Views

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.triviagameapp.R
import com.example.triviagameapp.Screen
import com.example.triviagameapp.ViewModels.SessionViewModel
import com.example.triviagameapp.ui.theme.QuizCyan
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

@Composable
fun AccountView(
    sessionViewModel: SessionViewModel,
    navController: NavController,
) {
    BackHandler {
        navController.navigate(Screen.HomeScreen.route)
    }

    val context = LocalContext.current
    val currentUser by sessionViewModel.currentUser.collectAsState()
    val isEmailUser by remember { derivedStateOf { sessionViewModel.isEmailUser } }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReauthDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.game_background), // Reuse your HomeView background
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val userEmail = currentUser?.email ?: "Unknown"

            Text(
                text = "Account: $userEmail",
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            // Change Password Button
            Button(
                onClick = {
                    if (isEmailUser) {
                        showChangePasswordDialog = true
                    } else {
                        Toast.makeText(context, "Password cannot be changed when logged in with Google", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Match HomeView button height
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isEmailUser) QuizCyan else Color.Gray
                )
            ) {
                Text("Change Password", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delete Account Button
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
            ) {
                Text("Delete Account", fontSize = 18.sp, color = MaterialTheme.colors.onError)
            }
        }

        // Dialogs
        if (showDeleteDialog) {
            DeleteAccountDialog(
                onConfirm = {
                    sessionViewModel.deleteUser(
                        onSuccess = {
                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.HomeScreen.route) {
                                popUpTo(Screen.HomeScreen.route) { inclusive = true }
                            }
                        },
                        onFailure = { e ->
                            if (e is FirebaseAuthRecentLoginRequiredException) {
                                showReauthDialog = true
                            } else {
                                Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        if (showReauthDialog) {
            currentUser?.email?.let { email ->
                ReAuthDialog(
                    email = email,
                    onConfirm = { password ->
                        sessionViewModel.reauthenticate(
                            email = email,
                            password = password,
                            onSuccess = {
                                sessionViewModel.deleteUser(
                                    onSuccess = {
                                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                        navController.navigate(Screen.HomeScreen.route) {
                                            popUpTo(Screen.HomeScreen.route) { inclusive = true }
                                        }
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Delete failed: ${it.message}", Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            onFailure = {
                                Toast.makeText(context, "Re-auth failed: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    onDismiss = { showReauthDialog = false }
                )
            }
        }

        if (showChangePasswordDialog) {
            currentUser?.email?.let { email ->
                ChangePasswordDialog(
                    email = email,
                    onChange = { old, new ->
                        sessionViewModel.changePassword(
                            email = email,
                            currentPassword = old,
                            newPassword = new,
                            onSuccess = {
                                Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                                showChangePasswordDialog = false
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    onDismiss = { showChangePasswordDialog = false }
                )
            }
        }
    }
}


@Composable
fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account") },
        text = { Text("This will permanently delete your account and data. Continue?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes, delete", color = MaterialTheme.colors.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
            }
        }
    )
}

@Composable
fun ReAuthDialog(
    email: String,
    onConfirm: (password: String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Re-authenticate") },
        text = {
            Column {
                Text("Please enter your password to continue.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(password) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    email: String,
    onChange: (oldPassword: String, newPassword: String) -> Unit,
    onDismiss: () -> Unit
) {
    var current by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = new,
                    onValueChange = { new = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (new != confirm) {
                    Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                } else {
                    onChange(current, new)
                }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

