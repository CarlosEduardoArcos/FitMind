package com.example.fitmind.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

/**
 * Menú desplegable para acciones de usuario en la SessionBar
 */
@Composable
fun UserMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onSwitchUser: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val isAdmin = currentUser?.email == "carloeduardo1987@gmail.com"

    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Header del menú
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        user = currentUser,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when {
                                isAdmin -> "Administrador"
                                currentUser?.displayName?.isNotBlank() == true -> currentUser.displayName ?: "Usuario"
                                currentUser?.email != null -> currentUser.email!!.split("@")[0]
                                else -> "Usuario"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentUser?.email ?: "Modo invitado",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                
                // Opciones del menú
                MenuOption(
                    icon = Icons.Default.Person,
                    title = "Perfil",
                    description = "Ver y editar perfil",
                    onClick = {
                        onProfile()
                        onDismiss()
                    }
                )
                
                if (!isAdmin) {
                MenuOption(
                    icon = Icons.Default.Person,
                    title = "Cambiar Usuario",
                    description = "Iniciar sesión con otra cuenta",
                    onClick = {
                        onSwitchUser()
                        onDismiss()
                    }
                )
                }
                
                MenuOption(
                    icon = Icons.Default.Info,
                    title = if (isAdmin) "Panel de Administración" else "Información de Cuenta",
                    description = if (isAdmin) "Acceder al panel de admin" else "Ver detalles de la cuenta",
                    onClick = {
                        // TODO: Implementar navegación al panel de admin
                        onDismiss()
                    }
                )
                
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                
                MenuOption(
                    icon = Icons.Default.ExitToApp,
                    title = "Cerrar Sesión",
                    description = "Salir de la cuenta actual",
                    onClick = {
                        onLogout()
                        onDismiss()
                    },
                    isDestructive = true
                )
            }
        }
    }
}

/**
 * Opción individual del menú de usuario
 */
@Composable
fun MenuOption(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val iconColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
