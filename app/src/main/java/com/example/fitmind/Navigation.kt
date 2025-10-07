package com.example.fitmind

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.util.Log
import com.example.fitmind.ui.screens.AddHabitScreen
import com.example.fitmind.ui.screens.ChartsScreen
import com.example.fitmind.ui.screens.DashboardsScreen
import com.example.fitmind.ui.screens.HabitDetailsScreen
import com.example.fitmind.ui.screens.HomeScreen
import com.example.fitmind.ui.screens.LoginScreen
import com.example.fitmind.ui.screens.RegisterScreen
import com.example.fitmind.ui.screens.SettingsScreen
import com.example.fitmind.ui.screens.SplashScreen

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val AddHabit = "addHabit"
    const val HabitDetails = "habitDetails"
    const val Charts = "charts"
    const val Dashboards = "dashboards"
    const val Settings = "settings"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController, 
        startDestination = Routes.Splash,
        enterTransition = { fadeIn() + slideInHorizontally() },
        exitTransition = { fadeOut() + slideOutHorizontally() }
    ) {
        composable(Routes.Splash) { SplashScreen(navController) }
        composable(Routes.Login) { LoginScreen(navController) }
        composable(Routes.Register) { RegisterScreen(navController) }
        composable(Routes.Home) { HomeScreen(navController) }
        composable(Routes.AddHabit) { AddHabitScreen(navController) }
        composable(Routes.HabitDetails) { HabitDetailsScreen(navController) }
        composable(Routes.Charts) { ChartsScreen(navController) }
        composable(Routes.Dashboards) { 
            DashboardsScreen(navController) 
        }
        composable(Routes.Settings) { SettingsScreen(navController) }
    }
}


