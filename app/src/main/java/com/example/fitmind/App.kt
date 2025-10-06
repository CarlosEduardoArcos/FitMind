package com.example.fitmind

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.fitmind.ui.theme.FitMindTheme

@Composable
fun App() {
    FitMindTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavigation()
        }
    }
}


