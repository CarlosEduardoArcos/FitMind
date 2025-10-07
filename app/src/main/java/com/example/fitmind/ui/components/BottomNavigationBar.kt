package com.example.fitmind.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF3A86FF), Color(0xFF06D6A0))
    )

    val items = listOf(
        NavItem("home", Icons.Default.Home, "Hábitos"),
        NavItem("dashboards", Icons.Default.Info, "Gráficos"),
        NavItem("settings", Icons.Default.Settings, "Ajustes")
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(gradient)
            .padding(horizontal = 8.dp),
        containerColor = Color.Transparent
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val tint by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
            )
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.2f else 1f)

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = tint,
                        modifier = Modifier.scale(scale)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = tint,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
