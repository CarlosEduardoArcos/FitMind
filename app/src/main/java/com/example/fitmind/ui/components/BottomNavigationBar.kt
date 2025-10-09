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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitmind.ui.utils.InteractionFeedback
import com.example.fitmind.ui.utils.rememberInteractionFeedback

@Composable
fun BottomNavigationBar(
    navController: NavController,
    isAdmin: Boolean = false
) {
    val interactionFeedback = rememberInteractionFeedback()
    
    val baseItems = listOf(
        NavItem("home", Icons.Default.Home, "Inicio"),
        NavItem("dashboards", Icons.Default.Info, "Gráficos"),
        NavItem("settings", Icons.Default.Settings, "Configuración")
    )
    
    val adminItems = listOf(
        NavItem("home", Icons.Default.Home, "Inicio"),
        NavItem("dashboards", Icons.Default.Info, "Gráficos"),
        NavItem("admin", Icons.Default.Person, "Admin"),
        NavItem("settings", Icons.Default.Settings, "Configuración")
    )
    
    val items = if (isAdmin) adminItems else baseItems

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 6.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val tint by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.3f else 1f)

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
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

