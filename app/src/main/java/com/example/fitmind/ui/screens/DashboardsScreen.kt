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
import com.example.fitmind.ui.components.CircularProgressIndicator
import com.example.fitmind.ui.components.MetricCardWithCircularProgress
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
                                Color(0xFF1E3C72),
                                Color(0xFF2A5298)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Sección Gráficos
                        Text("Sección de Gráficos - En desarrollo", color = Color.White)
                    }
                    1 -> {
                        // Sección Estadísticas
                        Text("Sección de Estadísticas - En desarrollo", color = Color.White)
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
