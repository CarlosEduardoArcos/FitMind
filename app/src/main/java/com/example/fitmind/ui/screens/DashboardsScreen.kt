package com.example.fitmind.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.ui.components.ChartView
import com.example.fitmind.ui.components.StatsTile
import com.example.fitmind.viewmodel.ChartViewModel

@Composable
fun DashboardsScreen(navController: NavController, chartViewModel: ChartViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { i, title ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                val registros by chartViewModel.chartData.collectAsState()
                if (registros.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("📊 Aún no hay datos de progreso.\nAgrega tus primeros hábitos para ver tus gráficos.",
                            textAlign = TextAlign.Center)
                    }
                } else {
                    ChartView(registros)
                }
            }
            1 -> {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsTile(Icons.Default.Favorite, "Frecuencia cardíaca", "0 bpm", 0f)
                    StatsTile(Icons.Default.Settings, "Tiempo calentamiento", "0 min", 0f)
                    StatsTile(Icons.Default.Info, "Pasos", "0 / 8000", 0f)
                    StatsTile(Icons.Default.Star, "Kcal", "0 / 250", 0f)
                    StatsTile(Icons.Default.Place, "Km", "0 / 5", 0f)
                }
            }
        }
    }
}
