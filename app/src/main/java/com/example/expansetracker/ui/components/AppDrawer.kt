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
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expansetracker.ui.theme.Purple40
import com.example.expansetracker.ui.theme.white
import com.example.expansetracker.viewmodel.AuthViewModel
import com.example.expansetracker.viewmodel.ExpenseViewModel

@Composable
fun AppDrawer(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
    ) {

        /* ================= HEADER ================= */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple40)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

//                if (viewModel.userPhoto != null) {
//                    AsyncImage(
//                        model = viewModel.userPhoto,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(72.dp)
//                            .clip(CircleShape)
//                    )
//                } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(white)
                )
//                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = viewModel.userName,
                    style = MaterialTheme.typography.titleMedium,
                    color = white
                )

                Text(
                    text = viewModel.userEmail ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = white
                )
            }
        }

        /* ================= MENU ================= */

        Column(modifier = Modifier.padding(vertical = 8.dp)) {

            NavigationDrawerItem(
                label = { Text("Dashboard") },
                icon = { Icon(Icons.Default.List, null) },
                selected = false,
                onClick = {
                    navController.navigate("dashboard")
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
                    expenseViewModel.clearOnLogout()
                    viewModel.logout()
                    Toast.makeText(
                        context,
                        "Logged out successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}