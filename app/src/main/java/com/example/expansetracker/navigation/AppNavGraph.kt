package com.example.expansetracker.navigation

import SplashScreen
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expansetracker.data.repository.AuthRepository
import com.example.expansetracker.ui.auth.*
import com.example.expansetracker.ui.expense.*
import com.example.expansetracker.ui.profile.ProfileScreen
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Hilt EntryPoint to access AuthRepository inside composables */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthRepoEntryPoint {
    fun authRepository(): AuthRepository
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    // 🔑 Get AuthRepository from Hilt
    val context = LocalContext.current
    val entryPoint = EntryPointAccessors.fromApplication(
        context,
        AuthRepoEntryPoint::class.java
    )
    val authRepository = entryPoint.authRepository()
    NavHost(
        navController = navController,
        startDestination = "splash",

        // ▶ Forward navigation animation
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it }
            ) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 }
            ) + fadeOut()
        },

        // ◀ Back navigation animation
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it }
            ) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it }
            ) + fadeOut()
        }
    ) {

        composable("splash") { SplashScreen(navController, authRepository) }
        composable("login") { LoginScreen(navController, authRepository = authRepository) }
        composable("signup") { SignupScreen(navController, authRepository = authRepository) }
        composable(
            route = "otpVerify/{email}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")!!
            OtpVerifyScreen(
                navController = navController,
                email = email,
                authRepository = authRepository
            )
        }

        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("setNewPassword") { SetNewPasswordScreen(navController) }
        composable("home") { ExpenseHomeScreen(navController) }

        // ✅ EDIT EXPENSE
        composable(
            route = "edit_expense/{expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val expenseId =
                backStackEntry.arguments?.getLong("expenseId")

            AddEditExpenseScreen(
                navController = navController,
                expenseId = expenseId
            )
        }
        composable("add") {
            AddEditExpenseScreen(
                navController, expenseId = null
            )
        }
        composable("profile") { ProfileScreen(navController, authRepository) }

    }
}
