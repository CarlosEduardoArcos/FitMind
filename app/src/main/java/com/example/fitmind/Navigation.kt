package com.example.fitmind

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
        composable(
            "splash",
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            }
        ) { SplashScreen(navController) }
        
        composable(
            "login",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) { LoginScreen(navController) }
        
        composable(
            "home",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) { HomeScreen(navController, viewModel<HabitViewModel>()) }
        
        composable(
            "addHabit",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) { AddHabitScreen(navController, viewModel<HabitViewModel>()) }
        
        composable(
            "dashboards",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) { DashboardsScreen(navController, viewModel<ChartViewModel>()) }
        
        composable(
            "settings",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
        ) { SettingsScreen(navController) }
    }
}


