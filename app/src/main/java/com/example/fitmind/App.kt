package com.example.fitmind

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.fitmind.ui.theme.FitMindTheme

@Composable
fun App() {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()

    FitMindTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavigation(
                navController = navController,
                darkTheme = darkTheme,
                onToggleTheme = { darkTheme = !darkTheme }
            )
        }
    }
}


