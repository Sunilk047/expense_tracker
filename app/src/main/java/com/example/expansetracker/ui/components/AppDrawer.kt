package com.example.expansetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expansetracker.data.repository.AuthRepository
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import android.widget.Toast

/* 🔑 Hilt EntryPoint to access AuthRepository */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthRepoEntryPoint {
    fun authRepository(): AuthRepository
}

@Composable
fun AppDrawer(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 Get AuthRepository from Hilt
    val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        AuthRepoEntryPoint::class.java
    )
    val authRepository = entryPoint.authRepository()

    // 🔹 Load saved user
    val user = authRepository.getLoggedInUser()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        /* ================= HEADER ================= */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user?.fullName ?: "User",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        /* ================= MENU ================= */

        Column(modifier = Modifier.padding(vertical = 8.dp)) {

            NavigationDrawerItem(
                label = { Text("Expense List") },
                icon = { Icon(Icons.Default.List, null) },
                selected = false,
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )

            NavigationDrawerItem(
                label = { Text("Profile") },
                icon = { Icon(Icons.Default.Person, null) },
                selected = false,
                onClick = {
                    navController.navigate("profile")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            /* ================= LOGOUT ================= */

            NavigationDrawerItem(
                label = { Text("Logout") },
                icon = { Icon(Icons.Default.ExitToApp, null) },
                selected = false,
                onClick = {
                    scope.launch {
                        authRepository.logout()
                        Toast.makeText(
                            context,
                            "Logged out successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
