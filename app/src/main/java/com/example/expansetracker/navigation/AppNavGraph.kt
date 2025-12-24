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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

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

        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }


        composable("forgot") { ForgotPasswordScreen(navController) }
        composable("setNewPassword") { SetNewPasswordScreen(navController) }
        composable("dashboard") { ExpenseHomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable(
            route = "add_edit?expenseId={expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")
            AddEditExpenseScreen(
                navController = navController,
                expenseId = expenseId
            )
        }

    }
}
