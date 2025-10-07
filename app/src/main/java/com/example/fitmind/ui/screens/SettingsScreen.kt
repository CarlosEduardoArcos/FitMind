package com.example.fitmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Configuración",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔔 Notificaciones", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                    Text("Activa o desactiva los recordatorios de tus hábitos.")
                    Divider(Modifier.padding(vertical = 8.dp))
                    Text("🎨 Modo de la app", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                    Text("Alterna entre modo claro y oscuro desde la pantalla de inicio de sesión.")
                    Divider(Modifier.padding(vertical = 8.dp))
                    Text("ℹ️ Información de la app", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                    Text("Versión 1.0. FitMind te ayuda a desarrollar y mantener hábitos saludables.")
                }
            }
        }
    }
}


