package com.example.fitmind.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable reutilizable que muestra el indicador de sesión activa del usuario
 * Diseño moderno inspirado en Google Fit y Notion Mobile
 */
@Composable
fun SessionIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    showAdminBadge: Boolean = false
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser by remember(auth.currentUser) { 
        derivedStateOf { auth.currentUser } 
    }

    AnimatedVisibility(
        visible = isVisible && currentUser != null,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        val userEmail = currentUser?.email ?: ""
        val isAdmin = userEmail == "carloeduardo1987@gmail.com"
        
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1A1A1A).copy(alpha = 0.8f),
                                Color(0xFF2E2E2E).copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icono del usuario
                Icon(
                    imageVector = if (isAdmin) Icons.Default.Star else Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = if (isAdmin) Color(0xFF06D6A0) else Color(0xFF3A86FF),
                    modifier = Modifier.size(24.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Texto principal
                    Text(
                        text = if (isAdmin) "Sesión activa: Administrador" else "Sesión activa",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Email del usuario
                    Text(
                        text = userEmail,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Badge de admin si aplica
                if (isAdmin && showAdminBadge) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF06D6A0)
                    ) {
                        Text(
                            text = "ADMIN",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Versión compacta del indicador de sesión para espacios reducidos
 */
@Composable
fun CompactSessionIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser by remember(auth.currentUser) { 
        derivedStateOf { auth.currentUser } 
    }

    AnimatedVisibility(
        visible = isVisible && currentUser != null,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        val userEmail = currentUser?.email ?: ""
        val isAdmin = userEmail == "carloeduardo1987@gmail.com"
        
        Row(
            modifier = modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1A1A1A).copy(alpha = 0.8f),
                            Color(0xFF2E2E2E).copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isAdmin) Icons.Default.Star else Icons.Default.Person,
                contentDescription = null,
                tint = if (isAdmin) Color(0xFF06D6A0) else Color(0xFF3A86FF),
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = if (isAdmin) "Admin" else "Usuario",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Indicador para modo invitado
 */
@Composable
fun GuestModeIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2D3748).copy(alpha = 0.8f),
                                Color(0xFF4A5568).copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF718096),
                    modifier = Modifier.size(24.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Modo invitado activo",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = "Los datos se guardarán localmente",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
