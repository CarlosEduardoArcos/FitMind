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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    
    // Formatear fecha actual en español
    val currentDate = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "ES"))
    )
    
    // Auto-hide después de 4 segundos
    LaunchedEffect(Unit) {
        delay(4000)
        isVisible = false
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(
                    Color.Black.copy(alpha = 0.25f), 
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Mensaje de bienvenida compacto
            Text(
                text = "👋 Bienvenido, ${if (isGuestMode) "Invitado" else userName}",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 6f
                    )
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Fecha actual en texto más pequeño y discreto
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.9f)
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            if (!isGuestMode) {
                Spacer(modifier = Modifier.height(4.dp))
                
                // Indicador de progreso compacto
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF06D6A0),
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${(progressPercentage * 100).toInt()}%",
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
