package com.example.fitmind.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.model.Habito
import com.example.fitmind.ui.components.BottomNavigationBar
import com.example.fitmind.viewmodel.HabitViewModel

@Composable
fun HomeScreen(navController: NavController, habitViewModel: HabitViewModel) {
    val habits by habitViewModel.habits.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<Habito?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addHabit") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar hábito")
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Mis hábitos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            if (habits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("🧘‍♂️ Aún no tienes hábitos. Presiona ➕ para agregar uno.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(habits) { habit ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(habit.nombre, fontWeight = FontWeight.Bold)
                                    Text(habit.categoria, color = Color.Gray)
                                    Text("Frecuencia: ${habit.frecuencia}", color = Color.Gray)
                                }
                                IconButton(onClick = {
                                    habitToDelete = habit
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar hábito")
                                }
                            }
                        }
                    }
                }
            }
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


