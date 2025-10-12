package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.fitmind.viewmodel.AdminViewModel
import com.example.fitmind.viewmodel.AuthViewModel

@Composable
fun AdminDashboardScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val users by adminViewModel.users.collectAsState()
    val totalUsers by adminViewModel.totalUsers.collectAsState()
    val totalHabits by adminViewModel.totalHabits.collectAsState()
    val averageHabits by adminViewModel.averageHabits.collectAsState()
    val isLoading by adminViewModel.isLoading.collectAsState()
    
    var showUserHabits by remember { mutableStateOf(false) }
    var selectedUserId by remember { mutableStateOf("") }
    var selectedUserName by remember { mutableStateOf("") }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    if (showUserHabits) {
        UserHabitsScreen(
            userId = selectedUserId,
            userName = selectedUserName,
            onDismiss = {
                showUserHabits = false
                selectedUserId = ""
                selectedUserName = ""
                adminViewModel.clearSelectedUserHabits()
            },
            adminViewModel = adminViewModel
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con título y botón logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Panel de Administración 👑",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(Modifier.height(4.dp))
                    
                    Text(
                        text = "Gestión de usuarios y hábitos",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
                
                // Botón de logout
                Button(
                    onClick = {
                        authViewModel.logout()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("admin") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🚪 Salir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(24.dp))

            // Estadísticas globales
            StatisticsCards(
                totalUsers = totalUsers,
                totalHabits = totalHabits,
                averageHabits = averageHabits
            )
            
            Spacer(Modifier.height(24.dp))

            // Lista de usuarios
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Usuarios registrados (${users.size})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Button(
                            onClick = {
                                adminViewModel.loadUsers()
                                adminViewModel.loadStatistics()
                                Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00D9A3)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Actualizar", color = Color.White)
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF00D9A3)
                            )
                        }
                    } else if (users.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay usuarios registrados",
                                color = Color(0xFFB0BEC5),
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = users,
                                key = { (uid, _) -> uid }
                            ) { (uid, userData) ->
                                UserCard(
                                    uid = uid,
                                    userData = userData,
                                    onViewHabits = { userId, userName ->
                                        selectedUserId = userId
                                        selectedUserName = userName
                                        showUserHabits = true
                                        adminViewModel.loadHabitsForUser(userId)
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

@Composable
fun StatisticsCards(
    totalUsers: Int,
    totalHabits: Int,
    averageHabits: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total usuarios
        StatisticCard(
            title = "Usuarios",
            value = totalUsers.toString(),
            icon = "👥",
            modifier = Modifier.weight(1f)
        )
        
        // Total hábitos
        StatisticCard(
            title = "Hábitos",
            value = totalHabits.toString(),
            icon = "📊",
            modifier = Modifier.weight(1f)
        )
        
        // Promedio hábitos
        StatisticCard(
            title = "Promedio",
            value = String.format("%.1f", averageHabits),
            icon = "📈",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun UserCard(
    uid: String,
    userData: Map<String, Any>,
    onViewHabits: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del usuario
            Text(
                text = if (userData["rol"] == "admin") "👑" else "👤",
                fontSize = 32.sp
            )
            
            Spacer(Modifier.width(16.dp))
            
            // Información del usuario
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userData["nombre"]?.toString() ?: "Sin nombre",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = userData["email"]?.toString() ?: "Sin email",
                    color = Color(0xFFB0BEC5),
                    fontSize = 14.sp
                )
                
                Text(
                    text = "Rol: ${userData["rol"]?.toString() ?: "usuario"}",
                    color = if (userData["rol"] == "admin") Color(0xFF00D9A3) else Color(0xFFB0BEC5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Botón ver hábitos
            Button(
                onClick = {
                    onViewHabits(uid, userData["nombre"]?.toString() ?: "Usuario")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D9A3)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "📊 Ver hábitos",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}
