package com.example.fitmind

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitmind.ui.screens.AddHabitScreen
import com.example.fitmind.ui.screens.DashboardsScreen
import com.example.fitmind.ui.screens.HomeScreen
import com.example.fitmind.ui.screens.LoginScreen
import com.example.fitmind.ui.screens.SettingsScreen
import com.example.fitmind.ui.screens.SplashScreen
import com.example.fitmind.viewmodel.ChartViewModel
import com.example.fitmind.viewmodel.HabitViewModel

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController, viewModel<HabitViewModel>()) }
        composable("addHabit") { AddHabitScreen(navController, viewModel<HabitViewModel>()) }
        composable("dashboards") { DashboardsScreen(navController, viewModel<ChartViewModel>()) }
        composable("settings") { SettingsScreen(navController) }
    }
}

