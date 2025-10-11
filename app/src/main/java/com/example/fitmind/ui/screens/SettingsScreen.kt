package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.util.Log
import java.time.LocalTime
import com.example.fitmind.ui.utils.InteractionFeedback
import com.example.fitmind.ui.utils.rememberInteractionFeedback
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.HabitViewModel
import com.example.fitmind.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    notificationViewModel: NotificationViewModel,
    habitViewModel: HabitViewModel
) {
    val context = LocalContext.current
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF00BCD4), Color(0xFF2196F3)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    // Estados del ViewModel de notificaciones
    val notificationsEnabled by notificationViewModel.enabled.collectAsState()
    val scheduledTime by notificationViewModel.scheduledTime.collectAsState()
    val message by notificationViewModel.message.collectAsState()
    val habitName by notificationViewModel.habitName.collectAsState()
    val isRecurring by notificationViewModel.isRecurring.collectAsState()
    val errorMessage by notificationViewModel.errorMessage.collectAsState()
    val successMessage by notificationViewModel.successMessage.collectAsState()
    
    // Estados del ViewModel de hábitos
    val habits by habitViewModel.habits.collectAsState()
    
    // Estados locales para la UI moderna
    var localModeEnabled by remember { mutableStateOf(true) }
    var showNotificationFields by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHabit by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("09:00") }
    var expanded by remember { mutableStateOf(false) }
    
    // Snackbar para feedback visual
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Sistema de feedback interactivo
    val interactionFeedback = rememberInteractionFeedback()

    // TimePicker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            selectedTime = selectedTime,
            onTimeSelected = { time ->
                try {
                    interactionFeedback.onSelection()
                } catch (e: Exception) {
                    Log.e("FitMind", "Error al ejecutar vibración en selección de tiempo: ${e.message}")
                }
                selectedTime = time
                showTimePicker = false
                notificationViewModel.setScheduledTime(time)
            },
            onDismiss = { showTimePicker = false }
        )
    }
    
    // Manejo de Snackbar para feedback
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                actionLabel = "Entendido"
            )
            notificationViewModel.clearError()
        }
    }
    
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            notificationViewModel.clearSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header moderno
            Text(
                text = "⚙️ Configuración",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 🔔 Sección de Notificaciones Moderna
            NotificationSettingsCard(
                notificationsEnabled = notificationsEnabled,
                onToggleNotifications = { 
                    try {
                        interactionFeedback.onThemeToggle()
                    } catch (e: Exception) {
                        Log.e("FitMind", "Error al ejecutar vibración en toggle notificaciones: ${e.message}")
                    }
                    notificationViewModel.setEnabled(it) 
                },
                showNotificationFields = showNotificationFields,
                onToggleFields = { showNotificationFields = !showNotificationFields },
                habits = habits,
                selectedHabit = selectedHabit,
                onHabitSelected = {
                    try {
                        interactionFeedback.onSelection()
                    } catch (e: Exception) {
                        Log.e("FitMind", "Error al ejecutar vibración en selección de hábito: ${e.message}")
                    }
                    selectedHabit = it
                    notificationViewModel.setHabitName(it)
                },
                selectedTime = selectedTime,
                onTimeClick = { showTimePicker = true },
                isRecurring = isRecurring,
                onToggleRecurring = { notificationViewModel.setIsRecurring(it) },
                onScheduleNotification = {
                    interactionFeedback.onNotificationScheduled()
                    val userId = authViewModel.getCurrentUserId() ?: "local_user"
                    notificationViewModel.scheduleNotification(context, userId)
                },
                onTestNotification = { 
                    interactionFeedback.onNotificationScheduled()
                    notificationViewModel.scheduleTestNotification(context) 
                },
                onCancelNotifications = { 
                    interactionFeedback.onNotificationsCancelled()
                    notificationViewModel.cancelNotifications(context) 
                }
            )

            // Sección de Configuración de modo de la app
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚙️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Configuración de modo de la app", 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Usar modo local (sin Firebase)", 
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = localModeEnabled,
                            onCheckedChange = { 
                                try {
                                    interactionFeedback.onThemeToggle()
                                } catch (e: Exception) {
                                    Log.e("FitMind", "Error al ejecutar vibración en toggle modo local: ${e.message}")
                                }
                                localModeEnabled = it 
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Modo local activo: los datos no se sincronizan con la nube.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

                   // Sección de Información de la App
                   Card(
                       colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                       elevation = CardDefaults.cardElevation(4.dp),
                       shape = RoundedCornerShape(12.dp),
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       Column(modifier = Modifier.padding(16.dp)) {
                           Text(
                               "ℹ️ Información de la App", 
                               fontWeight = FontWeight.Bold, 
                               color = MaterialTheme.colorScheme.onBackground,
                               fontSize = 16.sp
                           )
                           Spacer(modifier = Modifier.height(8.dp))
                           Text(
                               "Versión: 1.0.0", 
                               color = MaterialTheme.colorScheme.onBackground,
                               fontSize = 16.sp,
                               fontWeight = FontWeight.Medium
                           )
                           Text(
                               "Desarrollado con Jetpack Compose y Firebase", 
                               color = MaterialTheme.colorScheme.onSurfaceVariant,
                               fontSize = 14.sp
                           )
                       }
                   }

                   // Botón de Logout
                   Button(
                       onClick = {
                           authViewModel.logout()
                           Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                           navController.navigate("login") {
                               popUpTo("settings") { inclusive = true }
                           }
                       },
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(bottom = 16.dp), // Padding extra para asegurar visibilidad
                       colors = ButtonDefaults.buttonColors(
                           containerColor = MaterialTheme.colorScheme.error,
                           contentColor = MaterialTheme.colorScheme.onError
                       ),
                       shape = RoundedCornerShape(12.dp)
                   ) {
                       Text(
                           "🚪 Cerrar Sesión", 
                           fontWeight = FontWeight.Bold,
                           fontSize = 16.sp
                       )
                   }
        }
        
        // Snackbar para feedback visual
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Dialog moderno para seleccionar hora usando MaterialTimePicker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = selectedTime.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
    
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🕐 Seleccionar Hora",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TimePicker moderno de Material 3
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.primaryContainer,
                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectorColor = MaterialTheme.colorScheme.primary,
                        periodSelectorBorderColor = MaterialTheme.colorScheme.primary,
                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Hora seleccionada: ${String.format("%02d:%02d", state.hour, state.minute)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    val timeString = String.format("%02d:%02d", state.hour, state.minute)
                    onTimeSelected(timeString)
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Tarjeta moderna para configuración de notificaciones
 */
@Composable
fun NotificationSettingsCard(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    showNotificationFields: Boolean,
    onToggleFields: () -> Unit,
    habits: List<com.example.fitmind.model.Habito>,
    selectedHabit: String,
    onHabitSelected: (String) -> Unit,
    selectedTime: String,
    onTimeClick: () -> Unit,
    isRecurring: Boolean,
    onToggleRecurring: (Boolean) -> Unit,
    onScheduleNotification: () -> Unit,
    onTestNotification: () -> Unit,
    onCancelNotifications: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con ícono y switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Recordatorios Inteligentes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
            
            if (notificationsEnabled) {
                // Botón para mostrar/ocultar configuración
                FilledTonalButton(
                    onClick = onToggleFields,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (showNotificationFields) 
                            MaterialTheme.colorScheme.secondaryContainer 
                        else 
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (showNotificationFields) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showNotificationFields) "Ocultar Configuración" else "Configurar Recordatorio"
                    )
                }
                
                if (showNotificationFields) {
                    // Selector de hábito moderno
                    HabitSelector(
                        habits = habits,
                        selectedHabit = selectedHabit,
                        onHabitSelected = onHabitSelected,
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    )
                    
                    // Selector de hora moderno
                    TimeSelector(
                        selectedTime = selectedTime,
                        onTimeClick = onTimeClick
                    )
                    
                    // Switch para recordatorio diario
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recordatorio diario",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Switch(
                            checked = isRecurring,
                            onCheckedChange = onToggleRecurring,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    // Botón principal para programar
                    FilledTonalButton(
                        onClick = onScheduleNotification,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRecurring) "Programar Recordatorio Diario" else "Programar Recordatorio",
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Botones secundarios
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onTestNotification,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Prueba")
                        }
                        
                        OutlinedButton(
                            onClick = onCancelNotifications,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Selector moderno de hábitos con ExposedDropdownMenuBox
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitSelector(
    habits: List<com.example.fitmind.model.Habito>,
    selectedHabit: String,
    onHabitSelected: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Column {
            Text(
                text = "Seleccionar Hábito",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        
        if (habits.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Aún no tienes hábitos. Agrega uno para crear recordatorios.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = selectedHabit,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    label = { Text("Hábito") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // TODO: Actualizar a nueva API cuando MenuAnchorType esté disponible
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    habits.forEach { habit ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = habit.nombre,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${habit.categoria})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onHabitSelected(habit.nombre)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Selector moderno de hora con botón
 */
@Composable
fun TimeSelector(
    selectedTime: String,
    onTimeClick: () -> Unit
) {
    Column {
            Text(
                text = "Hora del Recordatorio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        
        OutlinedButton(
            onClick = onTimeClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = selectedTime,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

