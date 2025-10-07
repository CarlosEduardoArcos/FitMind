package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.model.Habito
import com.example.fitmind.viewmodel.HabitViewModel

@Composable
fun AddHabitScreen(navController: NavController, habitViewModel: HabitViewModel) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Agregar nuevo hábito", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
        OutlinedTextField(value = frecuencia, onValueChange = { frecuencia = it }, label = { Text("Frecuencia (ej: diaria)") })

        Button(
            onClick = {
                if (nombre.isNotBlank() && categoria.isNotBlank() && frecuencia.isNotBlank()) {
                    val nuevo = Habito(nombre, categoria, frecuencia)
                    habitViewModel.addHabitLocal(nuevo)
                    Toast.makeText(context, "🏋️‍♀️ Hábito agregado", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("addHabit") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar hábito")
        }
    }
}


