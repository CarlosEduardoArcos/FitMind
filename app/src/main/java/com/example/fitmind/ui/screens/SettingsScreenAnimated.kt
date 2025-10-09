package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.HabitViewModel
import com.example.fitmind.viewmodel.NotificationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreenAnimated(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel(),
    habitViewModel: HabitViewModel = viewModel()
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
    
    // Haptic feedback
    val hapticFeedback = LocalHapticFeedback.current
    
    // Estado para animación de entrada de pantalla
    var screenVisible by remember { mutableStateOf(false) }
    
    // Iniciar animación de entrada
    LaunchedEffect(Unit) {
        screenVisible = true
    }

    // TimePicker Dialog con animación
    if (showTimePicker) {
        TimePickerDialogAnimated(
            selectedTime = selectedTime,
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
                notificationViewModel.setScheduledTime(time)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
        // Animación de entrada de pantalla
        AnimatedVisibility(
            visible = screenVisible,
            enter = slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header moderno con animación
                AnimatedContent(
                    targetState = "⚙️ Configuración",
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(600, easing = FastOutSlowInEasing)) +
                        slideInVertically(animationSpec = tween(600, easing = FastOutSlowInEasing)) { -it / 3 })
                            .togetherWith(
                                fadeOut(animationSpec = tween(300, easing = FastOutLinearInEasing)) +
                                slideOutVertically(animationSpec = tween(300, easing = FastOutLinearInEasing)) { it / 3 }
                            )
                    },
                    label = "header"
                ) { targetText ->
                    Text(
                        text = targetText,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 🔔 Sección de Notificaciones Moderna
                NotificationSettingsCardAnimated(
                    notificationsEnabled = notificationsEnabled,
                    onToggleNotifications = { notificationViewModel.setEnabled(it) },
                    showNotificationFields = showNotificationFields,
                    onToggleFields = { showNotificationFields = !showNotificationFields },
                    habits = habits,
                    selectedHabit = selectedHabit,
                    onHabitSelected = {
                        selectedHabit = it
                        notificationViewModel.setHabitName(it)
                    },
                    selectedTime = selectedTime,
                    onTimeClick = { showTimePicker = true },
                    isRecurring = isRecurring,
                    onToggleRecurring = { notificationViewModel.setIsRecurring(it) },
                    onScheduleNotification = {
                        val userId = authViewModel.getCurrentUserId() ?: "local_user"
                        notificationViewModel.scheduleNotification(context, userId)
                    },
                    onTestNotification = { notificationViewModel.scheduleTestNotification(context) },
                    onCancelNotifications = { notificationViewModel.cancelNotifications(context) }
                )

                // Sección de Configuración de modo de la app
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🎨 Tema de la Aplicación", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Modo Oscuro", color = Color.Black)
                            Switch(
                                checked = localModeEnabled,
                                onCheckedChange = { localModeEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = Color(0xFF06D6A0),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                // Sección de Cerrar Sesión
                FilledTonalButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🚪 Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
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
 * Dialog moderno para seleccionar hora con animaciones
 */
@Composable
fun TimePickerDialogAnimated(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = selectedTime.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
    
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    
    // Animación de rotación para el ícono de confirmación
    val confirmRotation by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "rotation"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🕐 Seleccionar Hora",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hora seleccionada: ${String.format("%02d:%02d", selectedHour, selectedMinute)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Selector de hora simple
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selector de horas
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hora", style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { 
                                    selectedHour = if (selectedHour > 0) selectedHour - 1 else 23
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Menos")
                            }
                            Text(
                                text = String.format("%02d", selectedHour),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { 
                                    selectedHour = if (selectedHour < 23) selectedHour + 1 else 0
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Más")
                            }
                        }
                    }

                    Text(":", style = MaterialTheme.typography.headlineLarge)

                    // Selector de minutos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minuto", style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { 
                                    selectedMinute = if (selectedMinute > 0) selectedMinute - 1 else 59
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Menos")
                            }
                            Text(
                                text = String.format("%02d", selectedMinute),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = { 
                                    selectedMinute = if (selectedMinute < 59) selectedMinute + 1 else 0
                                }
                            ) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Más")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                    onTimeSelected(timeString)
                },
                modifier = Modifier.graphicsLayer {
                    rotationZ = confirmRotation
                }
            ) {
                Icon(
                    Icons.Default.Check, 
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = confirmRotation
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Tarjeta moderna para configuración de notificaciones con animaciones
 */
@Composable
fun NotificationSettingsCardAnimated(
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
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
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
                        style = MaterialTheme.typography.titleLarge,
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
                
                // Animación para mostrar/ocultar campos de configuración
                AnimatedVisibility(
                    visible = showNotificationFields,
                    enter = slideInVertically(
                        initialOffsetY = { -it / 4 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { -it / 4 },
                        animationSpec = tween(250, easing = FastOutLinearInEasing)
                    ) + fadeOut(
                        animationSpec = tween(250, easing = FastOutLinearInEasing)
                    )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Selector de hábito moderno
                        HabitSelectorAnimated(
                            habits = habits,
                            selectedHabit = selectedHabit,
                            onHabitSelected = onHabitSelected,
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        )
                        
                        // Selector de hora moderno
                        TimeSelectorAnimated(
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
}

/**
 * Selector moderno de hábitos con ExposedDropdownMenuBox y animaciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitSelectorAnimated(
    habits: List<com.example.fitmind.model.Habito>,
    selectedHabit: String,
    onHabitSelected: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "Seleccionar Hábito",
            style = MaterialTheme.typography.labelLarge,
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
                        style = MaterialTheme.typography.bodyMedium,
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
                        .menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                // Animación para el menú desplegable
                AnimatedVisibility(
                    visible = expanded,
                    enter = slideInVertically(
                        initialOffsetY = { -it / 4 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { -it / 4 },
                        animationSpec = tween(250, easing = FastOutLinearInEasing)
                    ) + fadeOut(
                        animationSpec = tween(250, easing = FastOutLinearInEasing)
                    )
                ) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) }
                    ) {
                        habits.forEachIndexed { index, habit ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    initialOffsetY = { -it / 3 },
                                    animationSpec = tween(
                                        durationMillis = 200 + (index * 50),
                                        easing = FastOutSlowInEasing
                                    )
                                ) + fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 200 + (index * 50),
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            ) {
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
    }
}

/**
 * Selector moderno de hora con botón y animaciones
 */
@Composable
fun TimeSelectorAnimated(
    selectedTime: String,
    onTimeClick: () -> Unit
) {
    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec = tween(100, easing = FastOutSlowInEasing),
        label = "buttonScale"
    )
    val coroutineScope = rememberCoroutineScope()
    
    Column {
        Text(
            text = "Hora del Recordatorio",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedButton(
            onClick = {
                buttonPressed = true
                onTimeClick()
                // Resetear estado después de un breve delay
                coroutineScope.launch {
                    delay(150)
                    buttonPressed = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                },
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
                style = MaterialTheme.typography.titleMedium,
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
