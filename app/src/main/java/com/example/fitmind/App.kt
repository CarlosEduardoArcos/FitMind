package com.example.fitmind

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitmind.ui.theme.FitMindTheme
import com.example.fitmind.viewmodel.SettingsViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val settingsViewModel: SettingsViewModel = viewModel()
    
    // Cargar preferencia de tema desde DataStore
    val darkTheme by settingsViewModel.darkTheme.collectAsState(initial = false)
    
    // Inicializar el contexto del ViewModel
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        settingsViewModel.initializeContext(context)
    }

    FitMindTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavigation(
                navController = navController,
                darkTheme = darkTheme,
                onToggleTheme = { settingsViewModel.toggleDarkTheme() }
            )
        }
    }
}


