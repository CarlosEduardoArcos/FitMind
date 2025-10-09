package com.example.fitmind.ui.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Utilidades para microanimaciones visuales sutiles en FitMind
 * Proporciona animaciones pequeñas que mejoran la experiencia sin ser invasivas
 */

/**
 * Animación de escala para botones al presionarlos
 */
@Composable
fun rememberButtonScale(pressed: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(80, easing = FastOutSlowInEasing), // OPT: Reducido de 100ms a 80ms
        label = "buttonScale"
    ).value
}

/**
 * Animación de rotación sutil para íconos
 */
@Composable
fun rememberIconRotation(rotated: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (rotated) 8f else 0f, // OPT: Reducido de 10° a 8°
        animationSpec = tween(150, easing = FastOutSlowInEasing), // OPT: Reducido de 200ms a 150ms
        label = "iconRotation"
    ).value
}

/**
 * Animación de bounce sutil para elementos completados
 */
@Composable
fun rememberBounceAnimation(triggered: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (triggered) 1.05f else 1f, // OPT: Reducido de 1.1f a 1.05f
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium // OPT: Aumentado de Low a Medium para mejor rendimiento
        ),
        label = "bounce"
    ).value
}

/**
 * Animación de elevación para tarjetas
 */
@Composable
fun rememberCardElevation(hovered: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (hovered) 6f else 4f, // OPT: Reducido de 8f a 6f
        animationSpec = tween(120, easing = FastOutSlowInEasing), // OPT: Reducido de 150ms a 120ms
        label = "cardElevation"
    ).value
}

/**
 * Animación de pulso sutil para elementos importantes
 */
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f, // OPT: Reducido de 1.05f a 1.02f para menor consumo
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing), // OPT: Aumentado de 1000ms a 1500ms para menor frecuencia
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    ).value
}

/**
 * Animación de slide-in desde la derecha
 */
@Composable
fun rememberSlideInAnimation(visible: Boolean): Float {
    val density = LocalDensity.current
    val slideDistance = with(density) { 15.dp.toPx() } // OPT: Reducido de 20dp a 15dp
    
    return animateFloatAsState(
        targetValue = if (visible) 0f else slideDistance,
        animationSpec = tween(250, easing = FastOutSlowInEasing), // OPT: Reducido de 300ms a 250ms
        label = "slideIn"
    ).value
}

/**
 * Animación de fade-in sutil
 */
@Composable
fun rememberFadeInAnimation(visible: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(150, easing = FastOutSlowInEasing), // OPT: Reducido de 200ms a 150ms
        label = "fadeIn"
    ).value
}

/**
 * Animación de escala para elementos que aparecen
 */
@Composable
fun rememberScaleInAnimation(visible: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f, // OPT: Reducido de 0.8f a 0.85f
        animationSpec = tween(200, easing = FastOutSlowInEasing), // OPT: Reducido de 250ms a 200ms
        label = "scaleIn"
    ).value
}

// === MODIFIERS PARA APLICAR ANIMACIONES ===

/**
 * Modifier para aplicar animación de escala
 */
fun Modifier.animatedScale(scale: Float): Modifier {
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Modifier para aplicar animación de rotación
 */
fun Modifier.animatedRotation(rotation: Float): Modifier {
    return this.graphicsLayer {
        rotationZ = rotation
    }
}

/**
 * Modifier para aplicar animación de elevación
 */
fun Modifier.animatedElevation(elevation: Float): Modifier {
    return this.graphicsLayer {
        shadowElevation = elevation
    }
}

/**
 * Modifier para aplicar animación de deslizamiento horizontal
 */
fun Modifier.animatedSlideX(offsetX: Float): Modifier {
    return this.graphicsLayer {
        translationX = offsetX
    }
}

/**
 * Modifier para aplicar animación de deslizamiento vertical
 */
fun Modifier.animatedSlideY(offsetY: Float): Modifier {
    return this.graphicsLayer {
        translationY = offsetY
    }
}

/**
 * Modifier para aplicar animación de opacidad
 */
fun Modifier.animatedAlpha(alpha: Float): Modifier {
    return this.graphicsLayer {
        this.alpha = alpha
    }
}

/**
 * Modifier combinado para microanimación de botón
 */
@Composable
fun Modifier.buttonMicroAnimation(
    pressed: Boolean,
    scale: Float = 0.95f
): Modifier {
    val animatedScale = animateFloatAsState(
        targetValue = if (pressed) scale else 1f,
        animationSpec = tween(80, easing = FastOutSlowInEasing), // OPT: Reducido de 100ms a 80ms
        label = "buttonMicroAnimation"
    ).value
    
    return this.graphicsLayer {
        this.scaleX = animatedScale
        this.scaleY = animatedScale
    }
}

/**
 * Modifier combinado para microanimación de tarjeta
 */
@Composable
fun Modifier.cardMicroAnimation(
    hovered: Boolean,
    scale: Float = 1.01f, // OPT: Reducido de 1.02f a 1.01f
    elevation: Float = 6f // OPT: Reducido de 8f a 6f
): Modifier {
    val animatedScale = animateFloatAsState(
        targetValue = if (hovered) scale else 1f,
        animationSpec = tween(120, easing = FastOutSlowInEasing), // OPT: Reducido de 150ms a 120ms
        label = "cardScale"
    ).value
    
    val animatedElevation = animateFloatAsState(
        targetValue = if (hovered) elevation else 4f,
        animationSpec = tween(120, easing = FastOutSlowInEasing), // OPT: Reducido de 150ms a 120ms
        label = "cardElevation"
    ).value
    
    return this.graphicsLayer {
        this.scaleX = animatedScale
        this.scaleY = animatedScale
        this.shadowElevation = animatedElevation
    }
}

/**
 * Modifier combinado para microanimación de ícono
 */
@Composable
fun Modifier.iconMicroAnimation(
    completed: Boolean,
    rotation: Float = 8f, // OPT: Reducido de 10f a 8f
    scale: Float = 1.05f // OPT: Reducido de 1.1f a 1.05f
): Modifier {
    val animatedRotation = animateFloatAsState(
        targetValue = if (completed) rotation else 0f,
        animationSpec = tween(150, easing = FastOutSlowInEasing), // OPT: Reducido de 200ms a 150ms
        label = "iconRotation"
    ).value
    
    val animatedScale = animateFloatAsState(
        targetValue = if (completed) scale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium // OPT: Aumentado de Low a Medium para mejor rendimiento
        ),
        label = "iconScale"
    ).value
    
    return this.graphicsLayer {
        this.rotationZ = animatedRotation
        this.scaleX = animatedScale
        this.scaleY = animatedScale
    }
}
