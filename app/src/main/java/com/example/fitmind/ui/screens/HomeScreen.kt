package com.example.fitmind.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.fitmind.model.Habito
import com.example.fitmind.ui.utils.InteractionFeedback
import com.example.fitmind.ui.utils.rememberInteractionFeedback
import com.example.fitmind.ui.utils.rememberButtonScale
import com.example.fitmind.ui.utils.rememberIconRotation
import com.example.fitmind.ui.utils.animatedScale
import com.example.fitmind.ui.utils.animatedRotation
import com.example.fitmind.viewmodel.HabitViewModel
import com.example.fitmind.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(
    navController: NavController, 
    habitViewModel: HabitViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    
    // Inicializar ViewModel con contexto
    LaunchedEffect(Unit) {
        habitViewModel.initializeContext(context)
        settingsViewModel.initializeContext(context)
    }
    
    // OPT: Manejo seguro de estado
    val habits by habitViewModel.habits.collectAsState()
    val appModeStatus by settingsViewModel.appModeStatus.collectAsState()
    val connectionType by settingsViewModel.connectionType.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    
    // Sistema de feedback interactivo
    val interactionFeedback = rememberInteractionFeedback()
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
            // Indicador de estado de conexión
            ConnectionStatusIndicator(
                appModeStatus = appModeStatus,
                connectionType = connectionType,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
            
            Text(
                text = "Mis Hábitos",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
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
                    // OPT: Usar key para optimizar recomposiciones
                    items(
                        items = habits,
                        key = { habit -> habit.id ?: habit.nombre } // OPT: Key única para cada hábito
                    ) { habit ->
                        HabitCard(
                            habit = habit,
                            onToggleComplete = { habitViewModel.toggleComplete(habit) },
                            onDelete = { habitViewModel.deleteHabitLocal(habit) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { 
                try {
                    interactionFeedback.onHabitAdded()
                    navController.navigate("addHabit") 
                } catch (e: Exception) {
                    // Si hay error, no hacer nada
                }
            },
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
    onToggleComplete: (Habito) -> Unit,
    onDelete: (Habito) -> Unit
) {
    // OPT: Usar remember para evitar recrear instancias
    val interactionFeedback = rememberInteractionFeedback()
    
    // OPT: Optimizar animación de progreso - reducir duración
    val animatedProgress by animateFloatAsState(
        targetValue = if (habit.completado) 1f else 0f,
        animationSpec = tween(durationMillis = 300) // OPT: Reducido de 500ms a 300ms
    )
    
    // OPT: Usar remember para microanimaciones estables
    val iconScale = rememberButtonScale(habit.completado)
    val iconRotation = rememberIconRotation(habit.completado)
    
    // OPT: Usar remember para colores calculados
    val cardColor = remember(habit.completado) {
        if (habit.completado) {
            Color(0xFF06D6A0).copy(alpha = 0.1f)
        } else {
            Color.White.copy(alpha = 0.95f)
        }
    }
    
    val textColor = remember(habit.completado) {
        if (habit.completado) Color(0xFF06D6A0) else Color.Black
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor // OPT: Usar color calculado con remember
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
                    color = textColor // OPT: Usar color calculado con remember
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
                // Botón completar con microanimación y feedback
                IconButton(
                    onClick = { 
                        interactionFeedback.onHabitCompleted()
                        onToggleComplete(habit) 
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .animatedScale(iconScale)
                        .animatedRotation(iconRotation)
                ) {
                    Icon(
                        imageVector = if (habit.completado) Icons.Default.CheckCircle else Icons.Default.Favorite,
                        contentDescription = "Completar hábito",
                        tint = if (habit.completado) Color(0xFF06D6A0) else Color.Gray
                    )
                }
                
                // Botón eliminar con feedback
                IconButton(
                    onClick = { 
                        interactionFeedback.onHabitDeleted()
                        onDelete(habit) 
                    }
                ) {
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

/**
 * Componente para mostrar el estado de conexión
 */
@Composable
fun ConnectionStatusIndicator(
    appModeStatus: com.example.fitmind.viewmodel.AppModeStatus,
    connectionType: String,
    modifier: Modifier = Modifier
) {
    val statusMessage = when (appModeStatus) {
        com.example.fitmind.viewmodel.AppModeStatus.ONLINE_CONNECTED -> "🟢 Conectado (modo online)"
        com.example.fitmind.viewmodel.AppModeStatus.ONLINE_NO_CONNECTION -> "🔴 Sin conexión (modo local temporal)"
        com.example.fitmind.viewmodel.AppModeStatus.OFFLINE_MODE -> "🔴 Modo sin conexión"
    }
    
    val backgroundColor = when (appModeStatus) {
        com.example.fitmind.viewmodel.AppModeStatus.ONLINE_CONNECTED -> 
            MaterialTheme.colorScheme.primaryContainer
        com.example.fitmind.viewmodel.AppModeStatus.ONLINE_NO_CONNECTION -> 
            MaterialTheme.colorScheme.errorContainer
        com.example.fitmind.viewmodel.AppModeStatus.OFFLINE_MODE -> 
            MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when (appModeStatus) {
        com.example.fitmind.viewmodel.AppModeStatus.ONLINE_CONNECTED -> 
            MaterialTheme.colorScheme.onPrimaryContainer
        com.example.fitmind.viewmodel.AppModeStatus.ONLINE_NO_CONNECTION -> 
            MaterialTheme.colorScheme.onErrorContainer
        com.example.fitmind.viewmodel.AppModeStatus.OFFLINE_MODE -> 
            MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = statusMessage,
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                if (connectionType != "Sin conexión") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($connectionType)",
                        color = contentColor.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


