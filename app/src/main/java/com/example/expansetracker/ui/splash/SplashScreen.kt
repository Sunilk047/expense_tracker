import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.expansetracker.R
import com.example.expansetracker.data.local.UserSessionManager
import com.example.expansetracker.data.repository.AuthRepository
import com.example.expansetracker.ui.theme.white

@Composable
fun SplashScreen(navController: NavController, authRepository: AuthRepository) {
    // Show splash for a short duration
    var isChecked by remember { mutableStateOf(false) }
    // Animation (optional)
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000)
    )

    // Trigger animation and navigation
    LaunchedEffect(true) {
        startAnimation = true
        delay(2000) // splash duration
        if (authRepository.isLoggedIn()) {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
        isChecked = true
    }


    // Splash Screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isChecked) {
                CircularProgressIndicator(color = white)
            }
            Image(
                painter = painterResource(id = R.drawable.logo), // your logo here
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer { alpha = alphaAnim.value } // optional fade-in animation
            )

            Spacer(modifier = Modifier.height(16.dp)) // space between logo and text

            Text(
                text = "Expanse Tracker",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.graphicsLayer { alpha = alphaAnim.value } // fade-in with logo
            )
        }
    }
}