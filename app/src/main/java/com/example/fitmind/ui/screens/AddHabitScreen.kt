package com.example.fitmind.ui.screens

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    val isLoading by habitViewModel.isLoading.collectAsState()
    val errorMessage by habitViewModel.errorMessage.collectAsState()
    val currentUserId = authViewModel.getCurrentUserId()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
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
                        currentUserId?.let { userId ->
                            val habit = Habito(
                                nombre = nombre,
                                categoria = categoria,
                                frecuencia = frecuencia,
                                fechaInicio = System.currentTimeMillis().toString(),
                                usuarioId = userId
                            )
                            habitViewModel.createHabit(habit)
                            navController.popBackStack()
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
                    } else {
                        Text("Guardar Hábito")
                    }
                }
            }
        }
    }
}


