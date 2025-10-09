package com.example.fitmind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.fitmind.ui.theme.FitMindTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OPT: Inicialización mínima y segura
        // Firebase se inicializa automáticamente en FitMindApplication

        setContent {
            // OPT: Usar rememberSaveable para persistir tema
            var darkTheme by rememberSaveable { mutableStateOf(false) }
            val navController = rememberNavController()

            FitMindTheme(darkTheme = darkTheme) {
                AppNavigation(
                    navController = navController,
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
    
}
