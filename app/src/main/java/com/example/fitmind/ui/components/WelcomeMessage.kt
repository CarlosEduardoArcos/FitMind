package com.example.fitmind.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WelcomeMessage(
    userName: String,
    isGuestMode: Boolean,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    // Auto-hide después de 3 segundos
    LaunchedEffect(Unit) {
        delay(3000)
        isVisible = false
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000)),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "👋 Bienvenido, ${if (isGuestMode) "Invitado" else userName}",
                    color = Color(0xFF3A86FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun WelcomeMessageWithProgress(
    userName: String,
    isGuestMode: Boolean,
    progressPercentage: Float,
    userRole: String = "user",
    sessionStatus: String = "Conectado (modo online)",
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    // Animación suave del progreso
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    
    // Auto-hide después de 3 segundos
    LaunchedEffect(Unit) {
        delay(3000)
        isVisible = false
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mensaje de bienvenida simple y limpio
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "👋 Bienvenido, ${if (isGuestMode) "Invitado" else userName}",
                    color = Color(0xFF3A86FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                // Badge de admin si es necesario
                if (userRole == "admin") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⭐",
                        fontSize = 14.sp
                    )
                }
            }
            
            if (!isGuestMode) {
                // Indicador circular de progreso discreto
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF06D6A0),
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round,
                        trackColor = Color(0xFF3A86FF).copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(progressPercentage * 100).toInt()}% completado hoy",
                        color = Color(0xFF06D6A0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            
            // Estado de sesión discreto
            Text(
                text = if (isGuestMode) "Modo invitado" else sessionStatus,
                color = Color(0xFF3A86FF).copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
