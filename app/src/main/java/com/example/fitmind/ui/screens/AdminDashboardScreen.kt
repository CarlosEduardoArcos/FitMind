package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.data.FirebaseRepository

@Composable
fun AdminDashboardScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val repository = remember { FirebaseRepository() }
    var users by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    LaunchedEffect(Unit) {
        repository.getAllUsers { usersList ->
            users = usersList
        }
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
            Text(
                text = "Panel de Administración 👑",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Gestión de usuarios y hábitos",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Usuarios registrados (${users.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A86FF)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    if (users.isEmpty()) {
                        Text(
                            text = "Cargando usuarios...",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(users) { user ->
                                UserCard(user = user)
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = {
                    Toast.makeText(context, "Actualizando lista de usuarios...", Toast.LENGTH_SHORT).show()
                    repository.getAllUsers { usersList ->
                        users = usersList
                        Toast.makeText(context, "Lista actualizada", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3A86FF)
                )
            ) {
                Text("Actualizar lista", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UserCard(user: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (user["rol"] == "admin") "👑" else "👤",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user["nombre"]?.toString() ?: "Sin nombre",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = user["email"]?.toString() ?: "Sin email",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Text(
                    text = "Rol: ${user["rol"]?.toString() ?: "usuario"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (user["rol"] == "admin") Color(0xFF00C853) else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
