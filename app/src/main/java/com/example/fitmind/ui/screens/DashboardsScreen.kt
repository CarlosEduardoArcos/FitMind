package com.example.fitmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardsScreen(navController: NavController, habitViewModel: HabitViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tab Bar superior con estilo oscuro (igual que barra inferior)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF1A1A1A), // Mismo fondo que barra inferior
            contentColor = Color.White,
            modifier = Modifier.height(56.dp), // Altura fija como barra inferior
            indicator = { tabPositions ->
                androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier,
                    color = Color(0xFF06D6A0), // Mismo verde que barra inferior
                    height = 4.dp // Grosor como barra inferior
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
                                Color(0xFF06D6A0) // Verde turquesa para activo (mismo que barra inferior)
                            } else {
                                Color.White.copy(alpha = 0.7f) // Gris claro para inactivo
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sección de Estadísticas\n(Próximamente)",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
