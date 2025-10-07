package com.example.fitmind.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitmind.data.local.DataStoreManager
import com.example.fitmind.data.model.Habito
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel()
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val habitViewModelWithDataStore = remember(habitViewModel) {
        HabitViewModel(
            app = context.applicationContext as android.app.Application,
            dataStoreManager = dataStoreManager
        )
    }
    
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    val habits by habitViewModelWithDataStore.habits.collectAsState()
    val isLoading by habitViewModelWithDataStore.isLoading.collectAsState()
    val errorMessage by habitViewModelWithDataStore.errorMessage.collectAsState()
    val successfullyAdded by habitViewModelWithDataStore.successfullyAdded.collectAsState()
    val currentUserId = authViewModel.getCurrentUserId()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
    
    LaunchedEffect(successfullyAdded) {
        if (successfullyAdded) {
            Toast.makeText(context, "🏋️‍♂️ Hábito agregado correctamente", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("addHabit") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "FitMind", color = MaterialTheme.colorScheme.onPrimary) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                Text(
                    text = "Agregar Hábito",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del hábito") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = frecuencia,
                    onValueChange = { frecuencia = it },
                    label = { Text("Frecuencia (ej: Diario, Semanal)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val habit = Habito(
                            nombre = nombre,
                            categoria = categoria,
                            frecuencia = frecuencia,
                            fechaInicio = System.currentTimeMillis().toString(),
                            usuarioId = currentUserId ?: "guest"
                        )
                        habitViewModelWithDataStore.addHabitLocal(habit)
                        // Mostrar toast inmediatamente
                        Toast.makeText(context, "🏋️‍♂️ Hábito agregado correctamente", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("addHabit") { inclusive = true }
                        }
                    },
                    enabled = !isLoading && nombre.isNotBlank() && categoria.isNotBlank() && frecuencia.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text("Guardando...", modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Text("Guardar Hábito")
                    }
                }
            }
        }
    }
}


