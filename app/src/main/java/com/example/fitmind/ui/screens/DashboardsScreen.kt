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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import com.example.fitmind.ui.components.CircularProgressIndicator
import com.example.fitmind.ui.components.MetricCardWithCircularProgress
import com.example.fitmind.viewmodel.HabitViewModel
import com.example.fitmind.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardsScreen(
    navController: NavController, 
    habitViewModel: HabitViewModel,
    progressViewModel: ProgressViewModel
) {
    val context = LocalContext.current
    
    // Inicializar ViewModels con contexto
    LaunchedEffect(Unit) {
        progressViewModel.initializeContext(context)
    }
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")
    
    // Observar métricas de progreso
    val progressMetrics by progressViewModel.progressMetrics.collectAsState()
    val hasData by progressViewModel.hasData.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Tab Bar superior con fondo oscuro forzado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF2C2C2C)) // Forzar fondo oscuro con Box
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent, // Transparente para que se vea el fondo del Box
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
                    GraphicsSection(progressMetrics, hasData)
                }
                1 -> {
                    // Sección Estadísticas
                    StatisticsSection(progressMetrics, hasData)
                }
            }
        }
    }
}

@Composable
fun GraphicsSection(
    progressMetrics: com.example.fitmind.data.model.ProgressMetrics,
    hasData: Boolean
) {
    if (!hasData) {
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen general de progreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📈 Resumen de Progreso",
                        color = Color(0xFF3A86FF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Progreso general
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                progress = progressMetrics.completionPercentage / 100f,
                                progressColor = Color(0xFF00C853),
                                modifier = Modifier.size(80.dp),
                                strokeWidth = 8f
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cumplimiento",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${progressMetrics.completedHabits}/${progressMetrics.totalHabits} hábitos",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                        
                        // Información adicional
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hábitos Activos",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${progressMetrics.totalHabits}",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            // Métricas de fitness
            Text(
                text = "🏃‍♂️ Métricas de Fitness",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            
            // Pasos
            MetricCardWithCircularProgress(
                title = "Pasos",
                value = "${progressMetrics.steps} / ${progressMetrics.maxSteps}",
                progress = progressMetrics.stepsProgress,
                icon = "🚶‍♂️",
                progressColor = Color(0xFF2196F3)
            )
            
            // Calorías
            MetricCardWithCircularProgress(
                title = "Calorías Quemadas",
                value = "${progressMetrics.calories} / ${progressMetrics.maxCalories} kcal",
                progress = progressMetrics.caloriesProgress,
                icon = "🔥",
                progressColor = Color(0xFFFF6B35)
            )
            
            // Kilómetros
            MetricCardWithCircularProgress(
                title = "Distancia Recorrida",
                value = String.format("%.1f / %.1f km", progressMetrics.kilometers, progressMetrics.maxKilometers),
                progress = progressMetrics.kilometersProgress,
                icon = "🏃‍♂️",
                progressColor = Color(0xFF9C27B0)
            )
        }
    }
}

@Composable
fun StatisticsSection(
    progressMetrics: com.example.fitmind.data.model.ProgressMetrics,
    hasData: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo gris claro como en la foto
            .padding(16.dp)
    ) {
        if (!hasData) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📊",
                    fontSize = 64.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "No hay estadísticas disponibles",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Agrega hábitos para ver tus estadísticas detalladas",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: Frecuencia cardíaca
                MetricCard(
                    icon = "❤️",
                    title = "Frecuencia cardíaca",
                    value = "${progressMetrics.heartRate} bpm",
                    progress = progressMetrics.heartRateProgress
                )
                
                // Tarjeta 2: Tiempo calentamiento (estimado basado en hábitos)
                MetricCard(
                    icon = "⚙️",
                    title = "Tiempo calentamiento",
                    value = "${progressMetrics.completedHabits * 5} min",
                    progress = if (progressMetrics.completedHabits > 0) minOf(progressMetrics.completedHabits * 0.1f, 1f) else 0f
                )
                
                // Tarjeta 3: Pasos
                MetricCard(
                    icon = "🚶‍♂️",
                    title = "Pasos",
                    value = "${progressMetrics.steps} / ${progressMetrics.maxSteps}",
                    progress = progressMetrics.stepsProgress
                )
                
                // Tarjeta 4: Kcal
                MetricCard(
                    icon = "🔥",
                    title = "Kcal",
                    value = "${progressMetrics.calories} / ${progressMetrics.maxCalories}",
                    progress = progressMetrics.caloriesProgress
                )
                
                // Tarjeta 5: Km
                MetricCard(
                    icon = "🏃‍♂️",
                    title = "Km",
                    value = String.format("%.1f / %.1f", progressMetrics.kilometers, progressMetrics.maxKilometers),
                    progress = progressMetrics.kilometersProgress
                )
                
                // Tarjeta adicional: Resumen de hábitos
                MetricCard(
                    icon = "📈",
                    title = "Progreso General",
                    value = "${progressMetrics.completedHabits}/${progressMetrics.totalHabits} hábitos completados",
                    progress = progressMetrics.completionPercentage / 100f
                )
            }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Valor
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
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
