package com.example.fitmind.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Utilidades de animación Material Motion para FitMind
 * Proporciona transiciones fluidas y modernas siguiendo las directrices de Material Design
 */

/**
 * Transición de fade-in con slide-down para menús desplegables
 */
@Composable
fun fadeSlideDownTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it / 4 },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it / 4 },
            animationSpec = tween(250, easing = FastOutLinearInEasing)
        ) + fadeOut(
            animationSpec = tween(250, easing = FastOutLinearInEasing)
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Transición de scale-in para diálogos y modales
 */
@Composable
fun scaleInTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        exit = scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(150, easing = FastOutLinearInEasing)
        ) + fadeOut(
            animationSpec = tween(150, easing = FastOutLinearInEasing)
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Transición de slide-up para Snackbars
 */
@Composable
fun slideUpTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(200, easing = FastOutLinearInEasing)
        ) + fadeOut(
            animationSpec = tween(200, easing = FastOutLinearInEasing)
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Transición de entrada de pantalla completa
 */
@Composable
fun screenEnterTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { 40 },
            animationSpec = tween(300, easing = FastOutLinearInEasing)
        ) + fadeOut(
            animationSpec = tween(300, easing = FastOutLinearInEasing)
        ),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Animación de pulsación para botones
 */
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    ).value
}

/**
 * Animación de brillo para botones presionados
 */
@Composable
fun rememberShineAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    ).value
}

/**
 * Animación de rotación para iconos
 */
@Composable
fun rememberRotationAnimation(
    targetRotation: Float = 0f,
    duration: Int = 300
): Float {
    return animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "rotation"
    ).value
}

/**
 * Animación de elevación para botones
 */
@Composable
fun rememberElevationAnimation(
    pressed: Boolean,
    defaultElevation: Float = 4f,
    pressedElevation: Float = 8f
): Float {
    return animateFloatAsState(
        targetValue = if (pressed) pressedElevation else defaultElevation,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "elevation"
    ).value
}

/**
 * Animación de escala para elementos interactivos
 */
@Composable
fun rememberScaleAnimation(
    pressed: Boolean,
    defaultScale: Float = 1f,
    pressedScale: Float = 0.95f
): Float {
    return animateFloatAsState(
        targetValue = if (pressed) pressedScale else defaultScale,
        animationSpec = tween(100, easing = FastOutSlowInEasing),
        label = "scale"
    ).value
}

/**
 * Modifier para aplicar animación de brillo
 */
fun Modifier.shineEffect(shine: Float): Modifier {
    return this.graphicsLayer {
        alpha = 0.7f + (shine * 0.3f)
        scaleX = 1f + (shine * 0.02f)
        scaleY = 1f + (shine * 0.02f)
    }
}

/**
 * Modifier para aplicar animación de pulsación
 */
fun Modifier.pulseEffect(pulse: Float): Modifier {
    return this.graphicsLayer {
        scaleX = pulse
        scaleY = pulse
    }
}

/**
 * Modifier para aplicar animación de rotación
 */
fun Modifier.rotationEffect(rotation: Float): Modifier {
    return this.graphicsLayer {
        rotationZ = rotation
    }
}

/**
 * Modifier para aplicar animación de escala
 */
fun Modifier.scaleEffect(scale: Float): Modifier {
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
