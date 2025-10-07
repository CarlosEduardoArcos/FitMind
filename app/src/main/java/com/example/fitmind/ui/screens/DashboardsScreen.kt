package com.example.fitmind.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitmind.ui.components.BottomNavigationBar
import com.example.fitmind.ui.components.FitnessStatsTile
import com.example.fitmind.ui.components.FitnessMetricType
import com.example.fitmind.ui.components.MonthlyLineChart
import com.example.fitmind.ui.components.WeeklyBarChart
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.ChartViewModel
import com.example.fitmind.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    chartViewModel: ChartViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")
    
    val currentUserId = authViewModel.getCurrentUserId()
    val weeklyData by chartViewModel.weeklyData.collectAsState()
    val monthlyData by chartViewModel.monthlyData.collectAsState()
    val habits by habitViewModel.habits.collectAsState()

    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            try {
                chartViewModel.observeWeekly(userId)
                chartViewModel.observeMonthly(userId)
            } catch (e: Exception) {
                Log.e("FitMind", "Error al cargar datos de gráficos: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Tableros", 
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { 
                        try {
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Log.e("FitMind", "Error al navegar hacia atrás: ${e.message}")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // TabRow para las pestañas
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { 
                            try {
                                selectedTab = index
                            } catch (e: Exception) {
                                Log.e("FitMind", "Error al cambiar pestaña: ${e.message}")
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }
            
            // Contenido de las pestañas
            when (selectedTab) {
                0 -> {
                    // Pestaña Gráficos
                    val hasWeeklyData = weeklyData.isNotEmpty() && weeklyData.any { it.y > 0 }
                    val hasMonthlyData = monthlyData.isNotEmpty() && monthlyData.any { it.y > 0 }
                    
                    if (!hasWeeklyData && !hasMonthlyData) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📊 Aún no hay datos de progreso",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Registra algunos hábitos para ver tus gráficos de progreso aquí.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
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
                            // Weekly Progress Card
                            if (hasWeeklyData) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Tu progreso semanal",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Hábitos completados por día",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        WeeklyBarChart(
                                            data = weeklyData,
                                            modifier = Modifier.padding(top = 16.dp)
                                        )
                                    }
                                }
                            }

                            // Monthly Progress Card
                            if (hasMonthlyData) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Hábitos completados este mes",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Progreso diario en porcentaje",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        MonthlyLineChart(
                                            data = monthlyData,
                                            modifier = Modifier.padding(top = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Pestaña Estadísticas
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Título de la sección
                        Text(
                            text = "Métricas Fitness",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Tiles de métricas
                        FitnessStatsTile(
                            type = FitnessMetricType.HEART_RATE,
                            currentValue = 0,
                            targetValue = 100,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        FitnessStatsTile(
                            type = FitnessMetricType.WARMUP_TIME,
                            currentValue = 0,
                            targetValue = 10,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        FitnessStatsTile(
                            type = FitnessMetricType.STEPS,
                            currentValue = 0,
                            targetValue = 8000,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        FitnessStatsTile(
                            type = FitnessMetricType.CALORIES,
                            currentValue = 0,
                            targetValue = 250,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        FitnessStatsTile(
                            type = FitnessMetricType.DISTANCE,
                            currentValue = 0,
                            targetValue = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Información adicional
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "💡 Información",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Estas métricas se actualizarán automáticamente cuando registres tu actividad física en la aplicación.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
