package com.example.fitmind.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatsTile(
    icon: ImageVector,
    title: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Barra de progreso
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

// Componente específico para métricas fitness
@Composable
fun FitnessStatsTile(
    type: FitnessMetricType,
    currentValue: Int,
    targetValue: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (targetValue > 0) {
        (currentValue.toFloat() / targetValue.toFloat()).coerceAtMost(1f)
    } else 0f
    
    val valueText = when (type) {
        FitnessMetricType.HEART_RATE -> "$currentValue bpm"
        FitnessMetricType.WARMUP_TIME -> "$currentValue min"
        FitnessMetricType.STEPS -> "$currentValue / $targetValue"
        FitnessMetricType.CALORIES -> "$currentValue / $targetValue"
        FitnessMetricType.DISTANCE -> "$currentValue / $targetValue"
    }
    
    StatsTile(
        icon = type.icon,
        title = type.title,
        value = valueText,
        progress = progress,
        modifier = modifier
    )
}

enum class FitnessMetricType(
    val title: String,
    val icon: ImageVector
) {
    HEART_RATE("Frecuencia cardíaca", Icons.Default.Favorite),
    WARMUP_TIME("Tiempo calentamiento", Icons.Default.Settings),
    STEPS("Pasos", Icons.Default.Info),
    CALORIES("Kcal", Icons.Default.Star),
    DISTANCE("Km", Icons.Default.Place)
}
