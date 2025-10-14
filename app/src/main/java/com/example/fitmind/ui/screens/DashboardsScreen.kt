package com.example.fitmind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.fitmind.ui.components.CircularProgressIndicator
import com.example.fitmind.ui.components.MetricCardWithCircularProgress
import com.example.fitmind.ui.components.ChartView
import com.example.fitmind.ui.components.StatsTile
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.HabitViewModel
import com.example.fitmind.viewmodel.ProgressViewModel
import com.example.fitmind.data.FirebaseRepository
import com.example.fitmind.ui.components.SessionBar
import com.example.fitmind.ui.components.UserMenu
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardsScreen(
    navController: NavController, 
    habitViewModel: HabitViewModel,
    progressViewModel: ProgressViewModel,
    authViewModel: AuthViewModel? = null,
    firebaseRepository: FirebaseRepository? = null
) {
    val context = LocalContext.current
    
    // Inicializar ViewModels con contexto
    LaunchedEffect(Unit) {
        val userId = authViewModel?.getCurrentUserId()
        if (userId != null && firebaseRepository != null) {
            progressViewModel.initializeContext(context, userId, firebaseRepository)
        } else {
            progressViewModel.initializeContext(context)
        }
    }
    
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Gráficos", "Estadísticas")
    
    // Observar métricas de progreso
    val progressMetrics by progressViewModel.progressMetrics.collectAsState()
    val hasData by progressViewModel.hasData.collectAsState()
    val isUpdating by progressViewModel.isUpdating.collectAsState()
    val lastUpdateTime by progressViewModel.lastUpdateTime.collectAsState()
    
    // Observar hábitos para estadísticas
    val habits by habitViewModel.habits.collectAsState()
    val completedHabits = habits.count { it.completado }
    val totalHabits = habits.size
    val progressPercentage = if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0f
    
    var showUserMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // SessionBar moderna
        SessionBar(
            onLogoutClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login")
            },
            onUserMenuClick = { showUserMenu = !showUserMenu }
        )
        // Tab Bar superior con fondo oscuro forzado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF2C2C2C)) // Forzar fondo oscuro con Box
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent, // Transparente para que se vea el fondo del Box
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth(),
                indicator = { tabPositions ->
                    androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier,
                        color = Color(0xFF00C853), // Verde turquesa brillante exacto
                        height = 4.dp // Grosor 3-4dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color.White else Color.White.copy(alpha = 0.7f),
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // Contenido de las pestañas con fondo degradado
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3A86FF),
                                Color(0xFF06D6A0)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Sección Gráficos
                        ChartsSection(
                            habits = habits,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    1 -> {
                        // Sección Estadísticas
                        StatisticsSection(
                            habits = habits,
                            completedHabits = completedHabits,
                            totalHabits = totalHabits,
                            progressPercentage = progressPercentage,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
        }
        
        // UserMenu desplegable
        UserMenu(
            isExpanded = showUserMenu,
            onDismiss = { showUserMenu = false },
            onLogout = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login")
            },
            onSwitchUser = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login")
            },
            onProfile = {
                // TODO: Implementar navegación al perfil
                showUserMenu = false
            }
        )
    }
}

@Composable
fun ChartsSection(
    habits: List<com.example.fitmind.model.Habito>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (habits.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Aún no hay datos de progreso.\nAgrega tus primeros hábitos para ver tus gráficos.",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF3A86FF),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Mostrar gráficos cuando hay datos
            ChartView(data = habits.map { it.nombre })
        }
    }
}

@Composable
fun StatisticsSection(
    habits: List<com.example.fitmind.model.Habito>,
    completedHabits: Int,
    totalHabits: Int,
    progressPercentage: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (habits.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "No hay estadísticas disponibles.\nAgrega hábitos para ver tus estadísticas detalladas.",
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF3A86FF),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Estadísticas de hábitos
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📊 Estadísticas de Hábitos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A86FF),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    StatsTile(
                        icon = Icons.Default.CheckCircle,
                        title = "Hábitos Completados",
                        value = "$completedHabits/$totalHabits",
                        progress = progressPercentage
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatsTile(
                        icon = Icons.Default.Star,
                        title = "Progreso General",
                        value = "${(progressPercentage * 100).toInt()}%",
                        progress = progressPercentage
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatsTile(
                        icon = Icons.Default.Favorite,
                        title = "Hábitos Activos",
                        value = "$totalHabits hábitos",
                        progress = if (totalHabits > 0) 1f else 0f
                    )
                }
            }
            
            // Métricas fitness adicionales (opcional)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💪 Métricas Fitness",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF06D6A0),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    StatsTile(
                        icon = Icons.Default.Favorite,
                        title = "Frecuencia Cardíaca",
                        value = "0/100 bpm",
                        progress = 0.3f
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatsTile(
                        icon = Icons.Default.Settings,
                        title = "Tiempo de Calentamiento",
                        value = "0/10 min",
                        progress = 0.5f
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatsTile(
                        icon = Icons.Default.Person,
                        title = "Pasos Diarios",
                        value = "0/8000",
                        progress = 0.2f
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatsTile(
                        icon = Icons.Default.Star,
                        title = "Calorías Quemadas",
                        value = "0/250 kcal",
                        progress = 0.4f
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatsTile(
                        icon = Icons.Default.Place,
                        title = "Distancia Recorrida",
                        value = "0/5 km",
                        progress = 0.1f
                    )
                }
            }
        }
    }
}
