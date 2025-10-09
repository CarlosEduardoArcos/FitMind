package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.NotificationViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
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
    
    // Estado local para el modo local
    var localModeEnabled by remember { mutableStateOf(true) }
    
    // Estado para mostrar/ocultar campos de notificación
    var showNotificationFields by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configuración",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Sección de Notificaciones
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔔 Notificaciones", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activar recordatorios", color = Color.Black)
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationViewModel.setEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color(0xFF06D6A0),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { showNotificationFields = !showNotificationFields },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = notificationsEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showNotificationFields) Color(0xFF3A86FF) else Color(0xFF06D6A0),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (showNotificationFields) "Ocultar Configuración" else "Configurar Recordatorio")
                    }
                    
                    if (showNotificationFields && notificationsEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = habitName,
                            onValueChange = { notificationViewModel.setHabitName(it) },
                            label = { Text("Nombre del hábito") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06D6A0),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = message,
                            onValueChange = { notificationViewModel.setMessage(it) },
                            label = { Text("Mensaje personalizado (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06D6A0),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = scheduledTime,
                            onValueChange = { notificationViewModel.setScheduledTime(it) },
                            label = { Text("Hora (HH:MM)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF06D6A0),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recordatorio diario", color = Color.Black)
                            Switch(
                                checked = isRecurring,
                                onCheckedChange = { notificationViewModel.setIsRecurring(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = Color(0xFF06D6A0),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = { 
                                val userId = authViewModel.getCurrentUserId() ?: "local_user"
                                notificationViewModel.scheduleNotification(context, userId)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF06D6A0),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isRecurring) "Programar Recordatorio Diario" else "Programar Recordatorio")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { notificationViewModel.scheduleTestNotification(context) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF3A86FF)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A86FF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Prueba", fontSize = 12.sp)
                            }
                            
                            OutlinedButton(
                                onClick = { notificationViewModel.cancelNotifications(context) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancelar", fontSize = 12.sp)
                            }
                        }
                        
                        // Mostrar mensajes de error o éxito
                        errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        
                        successMessage?.let { success ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = success,
                                color = Color(0xFF00C853),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            // Sección de Configuración de modo de la app
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚙️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configuración de modo de la app", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Usar modo local (sin Firebase)", color = Color.Black)
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
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Modo local activo: los datos no se sincronizan con la nube.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

                   // Sección de Información de la App
                   Card(
                       colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                       elevation = CardDefaults.cardElevation(6.dp),
                       shape = RoundedCornerShape(16.dp),
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       Column(modifier = Modifier.padding(16.dp)) {
                           Text("ℹ️ Información de la App", fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
                           Spacer(modifier = Modifier.height(8.dp))
                           Text("Versión: 1.0.0", color = Color.Black)
                           Text("Desarrollado con Jetpack Compose y Firebase", color = Color.Black)
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
                           containerColor = Color.Red.copy(alpha = 0.8f),
                           contentColor = Color.White
                       ),
                       shape = RoundedCornerShape(12.dp)
                   ) {
                       Text("🚪 Cerrar Sesión", fontWeight = FontWeight.Bold)
                   }
        }
    }
}


