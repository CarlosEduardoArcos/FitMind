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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra de pestañas superior
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier,
                        color = Color(0xFF06D6A0),
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color.White else Color.White.copy(alpha = 0.6f),
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Contenido de las pestañas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Pestaña Gráficos - Mensaje de la foto
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                                elevation = CardDefaults.cardElevation(6.dp),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Gráfico",
                                            tint = Color(0xFF3A86FF),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Aún no hay datos de progreso.",
                                            color = Color(0xFF3A86FF),
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Agrega tus primeros hábitos para ver tus gráficos.",
                                        color = Color(0xFF3A86FF),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Pestaña Estadísticas - Métricas de fitness
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Estadísticas",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

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
