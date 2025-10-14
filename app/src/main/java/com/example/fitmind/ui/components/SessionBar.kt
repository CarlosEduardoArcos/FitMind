package com.example.fitmind.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.example.fitmind.utils.NetworkUtils

/**
 * Barra de estado moderna que muestra el usuario actual y su rol
 * Visible en toda la aplicación con diseño inspirado en Notion y Google Fit
 */
@Composable
fun SessionBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    onLogoutClick: () -> Unit = {},
    onUserMenuClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser by remember(auth.currentUser) { 
        derivedStateOf { auth.currentUser } 
    }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val isConnected by remember { mutableStateOf(true) } // TODO: Implementar NetworkUtils.isConnected

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(500)
        ) + fadeIn(animationSpec = tween(500)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1A1A1A),
                                Color(0xFF2E2E2E)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar y información del usuario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar circular dinámico
                    UserAvatar(
                        user = currentUser,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Nombre o email del usuario
                        Text(
                            text = when {
                                currentUser?.email == "carloeduardo1987@gmail.com" -> "Administrador"
                                currentUser?.displayName?.isNotBlank() == true -> currentUser?.displayName ?: "Usuario"
                                currentUser?.email != null -> currentUser?.email?.split("@")?.get(0) ?: "Usuario"
                                else -> "Usuario"
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Email del usuario (si no es admin)
                        if (currentUser?.email != "carloeduardo1987@gmail.com") {
                            Text(
                                text = currentUser?.email ?: "Modo invitado",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                // Indicadores de estado y menú
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Indicador de conexión
                    ConnectionIndicator(
                        isConnected = isConnected,
                        modifier = Modifier.size(12.dp)
                    )
                    
                    // Menú desplegable
                    IconButton(
                        onClick = onUserMenuClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú de usuario",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Avatar circular dinámico que muestra iniciales o ícono según el rol
 */
@Composable
fun UserAvatar(
    user: com.google.firebase.auth.FirebaseUser?,
    modifier: Modifier = Modifier
) {
    val isAdmin = user?.email == "carloeduardo1987@gmail.com"
    val avatarColor = when {
        isAdmin -> Color(0xFF9C27B0) // Violeta brillante para admin
        user != null -> Color(0xFF00D9A3) // Verde turquesa para usuario
        else -> Color(0xFFB0BEC5) // Gris claro para invitado
    }
    
    val avatarContent = when {
        isAdmin -> "A"
        user?.displayName?.isNotBlank() == true -> user.displayName!!.first().uppercase()
        user?.email != null -> user.email!!.first().uppercase()
        else -> "?"
    }

    Box(
        modifier = modifier
            .background(
                color = avatarColor,
                shape = CircleShape
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isAdmin) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = avatarContent,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Indicador de estado de conexión
 */
@Composable
fun ConnectionIndicator(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isConnected) Color(0xFF4CAF50) else Color(0xFF757575),
                shape = CircleShape
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isConnected) {
            // Ícono de conexión (punto verde)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        } else {
            // Ícono de sin conexión (punto gris)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Versión compacta de la SessionBar para espacios reducidos
 */
@Composable
fun CompactSessionBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    onUserMenuClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser by remember(auth.currentUser) { 
        derivedStateOf { auth.currentUser } 
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Row(
            modifier = modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1A1A1A),
                            Color(0xFF2E2E2E)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserAvatar(
                user = currentUser,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                text = when {
                    currentUser?.email == "carloeduardo1987@gmail.com" -> "Admin"
                    currentUser?.displayName?.isNotBlank() == true -> currentUser?.displayName ?: "Usuario"
                    currentUser?.email != null -> currentUser?.email?.split("@")?.get(0) ?: "Usuario"
                    else -> "Invitado"
                },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            IconButton(
                onClick = onUserMenuClick,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menú",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
