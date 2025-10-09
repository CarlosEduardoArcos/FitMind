package com.example.fitmind.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.model.Habito
import com.example.fitmind.viewmodel.HabitViewModel

@Composable
fun HomeScreen(navController: NavController, habitViewModel: HabitViewModel) {
    val habits by habitViewModel.habits.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habito?>(null) }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Mis Hábitos",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            if (habits.isEmpty()) {
                Spacer(modifier = Modifier.height(80.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Aún no tienes hábitos.\nPresiona + para agregar uno.",
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF3A86FF),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    items(habits) { habit ->
                        HabitCard(
                            habit = habit,
                            onDelete = { habitViewModel.deleteHabitLocal(habit) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("addHabit") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF06D6A0),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar hábito")
        }
    }

    if (showDialog && habitToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    habitViewModel.deleteHabitLocal(habitToDelete!!)
                    showDialog = false
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } },
            title = { Text("Eliminar hábito") },
            text = { Text("¿Seguro que quieres eliminar '${habitToDelete!!.nombre}'?") }
        )
    }
}

@Composable
fun HabitCard(
    habit: Habito,
    onDelete: (Habito) -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (habit.completado) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.completado) {
                Color(0xFF06D6A0).copy(alpha = 0.1f)
            } else {
                Color.White.copy(alpha = 0.95f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.nombre,
                    fontWeight = FontWeight.Bold,
                    color = if (habit.completado) Color(0xFF06D6A0) else Color.Black
                )
                Text(
                    text = habit.categoria,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Frecuencia: ${habit.frecuencia}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                
                // Barra de progreso animada
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF06D6A0),
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
            }
            
            Row {
                // Botón completar
                IconButton(
                    onClick = { habitViewModel.toggleComplete(habit) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = if (habit.completado) Icons.Default.CheckCircle else Icons.Default.Favorite,
                        contentDescription = "Completar hábito",
                        tint = if (habit.completado) Color(0xFF06D6A0) else Color.Gray
                    )
                }
                
                // Botón eliminar
                IconButton(onClick = { onDelete(habit) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar hábito",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


