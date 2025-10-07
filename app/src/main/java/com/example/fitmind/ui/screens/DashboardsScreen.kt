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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardsScreen(navController: NavController, habitViewModel: HabitViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra superior oscura
        TopAppBar(
            title = {
                Text("Gráficos y Estadísticas", color = Color.White)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1A1A1A)
            )
        )

        // Contenido principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Pasos diarios: 0 / 8000", color = Color(0xFF3A86FF))
                    LinearProgressIndicator(progress = { 0f })
                    Text("Calorías: 0 / 250 kcal", color = Color(0xFF3A86FF))
                    LinearProgressIndicator(progress = { 0f })
                    Text("Distancia: 0 / 5 km", color = Color(0xFF3A86FF))
                    LinearProgressIndicator(progress = { 0f })
                    Spacer(Modifier.height(8.dp))
                    Text("Estadísticas generales:", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                    Text("- Hábitos completados: 0%")
                    Text("- Promedio diario: 0 hábitos")
                }
            }
        }
    }
}
