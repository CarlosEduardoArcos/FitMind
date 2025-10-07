package com.example.fitmind.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.util.Log
import com.example.fitmind.R
import com.example.fitmind.core.AppConfig
import com.example.fitmind.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var progress by remember { mutableStateOf(0f) }
    var startAnimation by remember { mutableStateOf(false) }
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(1000),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        authViewModel.checkUserSession()
        
        // Animate progress bar
        while (progress < 1f) {
            progress += 0.02f
            kotlinx.coroutines.delay(50)
        }
        
        kotlinx.coroutines.delay(500) // Show splash for additional 0.5 seconds
    }
    
    // Navigate based on authentication state or guest mode
    LaunchedEffect(isAuthenticated, progress) {
        if (startAnimation && progress >= 1f) { // Only navigate after animation and progress complete
            try {
                if (AppConfig.isGuestMode || AppConfig.isMockMode) {
                    navController.navigate("home") { 
                        popUpTo("splash") { inclusive = true } 
                    }
                } else if (isAuthenticated) {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                Log.e("FitMind", "Error en navegación del SplashScreen: ${e.message}")
                // En caso de error, ir a login como fallback
                try {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                } catch (fallbackError: Exception) {
                    Log.e("FitMind", "Error en navegación de fallback: ${fallbackError.message}")
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Logo/Icon with rotation animation
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "FitMind Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
                    .graphicsLayer {
                        rotationZ = (progress * 360f)
                    }
            )
            
            // App Name
            Text(
                text = "FitMind",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.alpha(alphaAnim)
            )
            
            // Tagline
            Text(
                text = "Transforma tu mente, un hábito a la vez",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .width(200.dp)
                    .clip(RoundedCornerShape(50))
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Loading Text
            Text(
                text = "Cargando tu bienestar...",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}


