package com.example.fitmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitmind.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController, 
    darkTheme: Boolean, 
    onToggleTheme: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val isLoading by authViewModel.isLoading.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()

    val gradient = Brush.verticalGradient(
        colors = if (darkTheme)
            listOf(Color.Black, Color(0xFF1B1B1B))
        else
            listOf(Color(0xFF3A86FF), Color(0xFF06D6A0)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    // Navegar según el rol del usuario
    LaunchedEffect(currentUser, userRole) {
        if (currentUser != null && userRole != null) {
            when (userRole) {
                "admin" -> {
                    Toast.makeText(context, "Bienvenido Admin! 👑", Toast.LENGTH_SHORT).show()
                    navController.navigate("admin") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                "usuario" -> {
                    Toast.makeText(context, "Bienvenido a FitMind! 🧠", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (darkTheme) "Modo oscuro" else "Modo claro",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
            Switch(
                checked = darkTheme,
                onCheckedChange = { onToggleTheme() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF06D6A0),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Bienvenido a FitMind 🧠",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color.White
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Formato de email inválido", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.login(email.trim(), password) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error: ${message ?: "Credenciales incorrectas"}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF3A86FF)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF3A86FF)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Iniciar sesión", fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = {
                Toast.makeText(context, "Entrando en modo invitado", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }) {
                Text("Entrar como invitado", color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = {
                navController.navigate("register")
            }) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

