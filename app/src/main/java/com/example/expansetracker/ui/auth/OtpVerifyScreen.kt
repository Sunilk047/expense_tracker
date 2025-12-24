package com.example.expansetracker.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expansetracker.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun OtpVerifyScreen(
    navController: NavController, email: String, authRepository: AuthRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var otp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var remainingTime by remember { mutableStateOf(30) }
    var canResend by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        remainingTime = 30
        canResend = false

        while (remainingTime > 0) {
            kotlinx.coroutines.delay(1000)
            remainingTime--
        }

        canResend = true
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Verify OTP",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Enter the OTP sent to $email",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("OTP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    error = when {
                        otp.isBlank() -> "OTP is required"
                        otp.length != 6 -> "OTP must be 6 digits"
                        else -> null
                    }

                    if (error != null) return@Button

                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)

            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Text("Verify OTP")
                }
            }
            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        Toast.makeText(context, "OTP resent", Toast.LENGTH_SHORT).show()

                        // Restart timer
                        remainingTime = 30
                        canResend = false

                        while (remainingTime > 0) {
                            kotlinx.coroutines.delay(1000)
                            remainingTime--
                        }
                        canResend = true
                        isLoading = false
                    }
                },
                enabled = canResend && !isLoading
            ) {
                Text(
                    if (canResend) "Resend OTP"
                    else "Resend OTP in ${remainingTime}s"
                )
            }


//            TextButton(onClick = { navController.popBackStack() }) {
//                Text("Back")
//            }
        }
    }
}
