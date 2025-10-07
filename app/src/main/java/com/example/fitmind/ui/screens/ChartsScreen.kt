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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitmind.R
import com.example.fitmind.ui.components.BottomNavigationBar
import com.example.fitmind.ui.components.MonthlyLineChart
import com.example.fitmind.ui.components.WeeklyBarChart
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.ChartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    chartViewModel: ChartViewModel = viewModel()
) {
    val currentUserId = authViewModel.getCurrentUserId()
    val weeklyData by chartViewModel.weeklyData.collectAsState()
    val monthlyData by chartViewModel.monthlyData.collectAsState()

    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            chartViewModel.observeWeekly(userId)
            chartViewModel.observeMonthly(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gráficos de Progreso", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = android.R.drawable.ic_menu_revert),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
                // Verificar si hay datos para mostrar
                val hasWeeklyData = weeklyData.isNotEmpty() && weeklyData.any { it.y > 0 }
                val hasMonthlyData = monthlyData.isNotEmpty() && monthlyData.any { it.y > 0 }
                
                if (!hasWeeklyData && !hasMonthlyData) {
                    // Mostrar mensaje cuando no hay datos
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📊 Aún no hay datos de progreso.\nAgrega tus primeros hábitos para ver tus gráficos.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
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
                        } else {
                            // Mensaje para datos semanales vacíos
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📈 Progreso Semanal",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "No hay datos de esta semana.\nCompleta algunos hábitos para ver tu progreso.",
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
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
                        } else {
                            // Mensaje para datos mensuales vacíos
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📅 Progreso Mensual",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "No hay datos de este mes.\nCompleta algunos hábitos para ver tu progreso.",
                                        textAlign = TextAlign.Center,
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }


