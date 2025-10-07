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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardsScreen(navController: NavController, habitViewModel: HabitViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tab Bar superior con colores exactos especificados
        Surface(
            color = Color(0xFF2C2C2C), // Fondo gris oscuro exacto
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFF2C2C2C), // Fondo gris oscuro explícito
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier,
                        color = Color(0xFF00C853), // Verde turquesa brillante exacto
                        height = 4.dp // Grosor 3-4dp
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
                                color = if (selectedTabIndex == index) {
                                    Color(0xFF00C853) // Verde turquesa brillante para activo
                                } else {
                                    Color(0xFFFFFFFF) // Blanco puro para inactivo
                                },
                                fontSize = 16.sp, // Tamaño exacto especificado
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        // Contenido principal con gradiente turquesa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2196F3), Color(0xFF00BCD4)), // Gradiente azul a turquesa
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Sección Gráficos
                    GraphicsSection()
                }
                1 -> {
                    // Sección Estadísticas (vacía por ahora)
                    StatisticsSection()
                }
            }
        }
    }
}

@Composable
fun GraphicsSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de gráfico de barras
            Text(
                text = "📊",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Texto principal
            Text(
                text = "Aún no hay datos de progreso.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Texto secundario
            Text(
                text = "Agrega tus primeros hábitos para ver tus gráficos.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatisticsSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo gris claro como en la foto
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta 1: Frecuencia cardíaca
            MetricCard(
                icon = "❤️",
                title = "Frecuencia cardíaca",
                value = "0 bpm",
                progress = 0f
            )
            
            // Tarjeta 2: Tiempo calentamiento
            MetricCard(
                icon = "⚙️",
                title = "Tiempo calentamiento",
                value = "0 min",
                progress = 0f
            )
            
            // Tarjeta 3: Pasos
            MetricCard(
                icon = "ℹ️",
                title = "Pasos",
                value = "0 / 8000",
                progress = 0f
            )
            
            // Tarjeta 4: Kcal
            MetricCard(
                icon = "⭐",
                title = "Kcal",
                value = "0 / 250",
                progress = 0f
            )
            
            // Tarjeta 5: Km
            MetricCard(
                icon = "📍",
                title = "Km",
                value = "0 / 5",
                progress = 0f
            )
        }
    }
}

@Composable
fun MetricCard(
    icon: String,
    title: String,
    value: String,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono azul
            Text(
                text = icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Valor
                Text(
                    text = value,
                    color = Color.Black,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Barra de progreso
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF00C853), // Verde turquesa
                    trackColor = Color.LightGray
                )
            }
        }
    }
}
