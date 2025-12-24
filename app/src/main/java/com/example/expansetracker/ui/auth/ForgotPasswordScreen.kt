package com.example.expansetracker.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expansetracker.ui.components.AppButton
import com.example.expansetracker.ui.components.AppTextField
import com.example.expansetracker.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Header icon
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Enter your registered email to receive reset link",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )

                Spacer(Modifier.height(32.dp))

                AppTextField(
                    value = email,
                    label = "Email",
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    isError = emailError,
                    onValueChange = {
                        email = it.trim()
                        emailError = false
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Send reset link button
                AppButton(
                    text = if (viewModel.isLoading) "Please wait..." else "Send Reset Link",
                    enabled = !viewModel.isLoading,
                    onClick = {
                        emailError = email.isBlank() ||
                                !Patterns.EMAIL_ADDRESS.matcher(email).matches()

                        if (emailError) {
                            Toast.makeText(
                                context,
                                "Please enter a valid email",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@AppButton
                        }

                        viewModel.resetPassword(
                            email = email,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Password reset email sent",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Back to login
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Back to Login")
                }
            }
        }
    }
}
