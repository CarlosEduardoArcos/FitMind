package com.example.fitmind

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitmind.ui.components.BottomNavigationBar
import com.example.fitmind.ui.screens.AddHabitScreen
import com.example.fitmind.ui.screens.AdminDashboardScreen
import com.example.fitmind.ui.screens.DashboardsScreen
import com.example.fitmind.ui.screens.HomeScreen
import com.example.fitmind.ui.screens.LoginScreen
import com.example.fitmind.ui.screens.RegisterScreen
import com.example.fitmind.ui.screens.SettingsScreen
import com.example.fitmind.ui.screens.SplashScreen
import com.example.fitmind.viewmodel.AdminViewModel
import com.example.fitmind.viewmodel.AuthViewModel
import com.example.fitmind.viewmodel.ChartViewModel
import com.example.fitmind.viewmodel.HabitViewModel
import com.example.fitmind.viewmodel.NotificationViewModel
import com.example.fitmind.viewmodel.ProgressViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    
    // OPT: Crear ViewModels compartidos para evitar múltiples instancias
    val authViewModel: AuthViewModel = viewModel()
    val habitViewModel: HabitViewModel = viewModel()
    val progressViewModel: ProgressViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    
    val userRole by authViewModel.userRole.collectAsState()
    val isAdmin = userRole == "admin"

    val showBottomBar = currentRoute !in listOf("splash", "login", "register")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController, isAdmin)
            }
        }
    ) { innerPadding ->
        Box(modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") { SplashScreen(navController) }
                composable("login") { LoginScreen(navController, darkTheme, onToggleTheme, authViewModel) }
                composable("register") { RegisterScreen(navController, authViewModel) }
                composable("admin") { AdminDashboardScreen(navController, viewModel<AdminViewModel>(), authViewModel) }
                composable("home") { HomeScreen(navController, habitViewModel) }
                composable("addHabit") { AddHabitScreen(navController, habitViewModel) }
                composable("dashboards") { 
                    DashboardsScreen(
                        navController, 
                        habitViewModel,
                        progressViewModel
                    ) 
                }
                composable("settings") {
                    SettingsScreen(
                        navController,
                        authViewModel,
                        notificationViewModel,
                        habitViewModel
                    )
                }
            }
        }
    }
}

