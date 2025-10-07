package com.example.fitmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitmind.viewmodel.HabitViewModel

@Composable
fun DashboardsScreen(navController: NavController, habitViewModel: HabitViewModel) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Gráficos y Estadísticas",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Métricas de fitness con íconos y barras de progreso
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Frecuencia cardíaca
                    MetricCard(
                        icon = Icons.Default.Favorite,
                        title = "Frecuencia cardíaca",
                        value = "0 bpm",
                        progress = 0f
                    )
                    
                    // Tiempo calentamiento
                    MetricCard(
                        icon = Icons.Default.Settings,
                        title = "Tiempo calentamiento",
                        value = "0 min",
                        progress = 0f
                    )
                    
                    // Pasos
                    MetricCard(
                        icon = Icons.Default.Info,
                        title = "Pasos",
                        value = "0 / 8000",
                        progress = 0f
                    )
                    
                    // Calorías
                    MetricCard(
                        icon = Icons.Default.Star,
                        title = "Kcal",
                        value = "0 / 250",
                        progress = 0f
                    )
                    
                    // Kilómetros
                    MetricCard(
                        icon = Icons.Default.LocationOn,
                        title = "Km",
                        value = "0 / 5",
                        progress = 0f
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    progress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono azul
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF3A86FF),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Texto de la métrica
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Text(
                text = value,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        // Barra de progreso
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(80.dp)
                .height(8.dp),
            color = Color(0xFF06D6A0),
            trackColor = Color.Gray.copy(alpha = 0.3f)
        )
    }
}
