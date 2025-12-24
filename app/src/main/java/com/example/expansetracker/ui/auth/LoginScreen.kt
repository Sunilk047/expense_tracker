package com.example.expansetracker.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expansetracker.ui.components.AppButton
import com.example.expansetracker.ui.components.AppTextField
import com.example.expansetracker.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

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

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Sign in to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                /* ---------------- Email ---------------- */
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

                /* ---------------- Password ---------------- */
                AppTextField(
                    value = password,
                    label = "Password",
                    isPassword = true,
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    isError = passwordError,
                    onValueChange = {
                        password = it
                        passwordError = false
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(
                        onClick = { navController.navigate("forgot") },
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("Forgot password?") }
                }

                Spacer(Modifier.height(24.dp))

                AppButton(
                    text = if (viewModel.isLoading) "Please wait..." else "Login",
                    enabled = !viewModel.isLoading,
                    onClick = {
                        emailError = email.isBlank() ||
                                !Patterns.EMAIL_ADDRESS.matcher(email).matches()

                        passwordError = password.isBlank() || password.length < 6

                        if (emailError || passwordError) {
                            Toast.makeText(
                                context,
                                "Please enter valid credentials",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@AppButton
                        }

                        viewModel.login(
                            email,
                            password,
                            onSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("signup") }) {
                    Text("Create new account")
                }
            }
        }
    }
}
