package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import com.example.fitmind.viewmodel.SettingsViewModel
import com.example.fitmind.data.FirebaseRepository
import com.example.fitmind.model.Habito
import com.example.fitmind.viewmodel.HabitViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    navController: NavController, 
    habitViewModel: HabitViewModel,
    settingsViewModel: SettingsViewModel,
    firebaseRepository: FirebaseRepository
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    var showCustomCategory by remember { mutableStateOf(false) }
    
    // Inicializar ViewModels con contexto
    LaunchedEffect(Unit) {
        settingsViewModel.initializeContext(context)
        habitViewModel.initializeContext(context, settingsViewModel, firebaseRepository)
    }
    
    // Categorías sugeridas
    val categories = listOf("Cardio", "Fuerza", "Yoga", "Meditación", "Estiramiento", "Aeróbica", "Nutrición", "Sueño", "Hidratación", "Otro")
    
    // Frecuencias predefinidas
    val frequencies = listOf("Diaria", "Semanal", "Cada 2 días", "Cada 3 días", "Mensual")
    
    // Función para sugerir categoría basada en el nombre
    fun suggestCategory(habitName: String): String {
        val name = habitName.lowercase()
        return when {
            name.contains("correr") || name.contains("trote") || name.contains("caminar") || name.contains("cardio") -> "Cardio"
            name.contains("pesas") || name.contains("gym") || name.contains("fuerza") || name.contains("musculación") -> "Fuerza"
            name.contains("meditar") || name.contains("respirar") || name.contains("mindfulness") -> "Meditación"
            name.contains("yoga") || name.contains("estiramiento") -> "Yoga"
            name.contains("agua") || name.contains("hidratar") -> "Hidratación"
            name.contains("dormir") || name.contains("sueño") -> "Sueño"
            name.contains("comer") || name.contains("nutrición") || name.contains("dieta") -> "Nutrición"
            else -> ""
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Agregar nuevo hábito",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        // Auto-sugerir categoría cuando se escribe el nombre
                        if (it.isNotEmpty()) {
                            val suggestedCategory = suggestCategory(it)
                            if (suggestedCategory.isNotEmpty() && !showCustomCategory) {
                                category = suggestedCategory
                            }
                        }
                    },
                    label = { 
                        Text(
                            "Nombre del hábito",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Menú desplegable de categorías
                Column {
                    Text(
                        text = "Categoría",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            label = { 
                                Text(
                                    "Selecciona categoría",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            cat,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        ) 
                                    },
                                    onClick = {
                                        category = cat
                                        showCustomCategory = (cat == "Otro")
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Campo personalizado para "Otro"
                    if (showCustomCategory) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { 
                                Text(
                                    "Categoría personalizada",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Menú desplegable de frecuencia
                Column {
                    Text(
                        text = "Frecuencia",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = frequencyExpanded,
                        onExpandedChange = { frequencyExpanded = !frequencyExpanded }
                    ) {
                        OutlinedTextField(
                            value = frequency,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded)
                            },
                            label = { 
                                Text(
                                    "Selecciona frecuencia",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 16.sp
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = frequencyExpanded,
                            onDismissRequest = { frequencyExpanded = false }
                        ) {
                            frequencies.forEach { freq ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            freq,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        ) 
                                    },
                                    onClick = {
                                        frequency = freq
                                        frequencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        try {
                            if (name.isNotBlank() && category.isNotBlank() && frequency.isNotBlank()) {
                                val nuevo = Habito(
                                    id = UUID.randomUUID().toString(),
                                    nombre = name,
                                    categoria = category,
                                    frecuencia = frequency
                                )
                                habitViewModel.addHabit(nuevo)
                                navController.navigate("home") {
                                    popUpTo("addHabit") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "⚠️ Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            // Si hay error, intentar navegación simple
                            try {
                                navController.navigate("home")
                            } catch (navError: Exception) {
                                // Si también falla la navegación simple, no hacer nada
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06D6A0),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar hábito", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


