package com.example.fitmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitmind.viewmodel.AdminViewModel

@Composable
fun UserHabitsScreen(
    userId: String,
    userName: String,
    onDismiss: () -> Unit,
    adminViewModel: AdminViewModel
) {
    val selectedUserHabits by adminViewModel.selectedUserHabits.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
            elevation = CardDefaults.cardElevation(16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
                    .padding(20.dp)
            ) {
                // Header con botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hábitos de $userName",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedUserHabits.size} hábitos registrados",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                
                // Lista de hábitos
                if (selectedUserHabits.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📝",
                                fontSize = 64.sp
                            )
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Text(
                                text = "Este usuario aún no ha registrado hábitos",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = "Los hábitos aparecerán aquí cuando el usuario los agregue",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(selectedUserHabits) { habit ->
                                HabitCard(habit = habit)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitCard(habit: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono del hábito según categoría
                val categoryIcon = when (habit["categoria"]?.toString()?.lowercase()) {
                    "ejercicio", "deporte" -> "💪"
                    "alimentación", "comida" -> "🍎"
                    "salud", "medicina" -> "🏥"
                    "trabajo", "productividad" -> "💼"
                    "estudio", "educación" -> "📚"
                    "relajación", "meditación" -> "🧘"
                    "social", "familia" -> "👥"
                    else -> "✅"
                }
                
                Text(
                    text = categoryIcon,
                    fontSize = 24.sp
                )
                
                Spacer(Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit["nombre"]?.toString() ?: "Hábito sin nombre",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = habit["categoria"]?.toString() ?: "Sin categoría",
                        color = Color(0xFF00D9A3),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Frecuencia:",
                    color = Color(0xFFB0BEC5),
                    fontSize = 12.sp
                )
                
                Text(
                    text = habit["frecuencia"]?.toString() ?: "No especificada",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
