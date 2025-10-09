package com.example.fitmind.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Animaciones simples y funcionales para FitMind
 */

/**
 * Animación de fade-in con slide-down para menús
 */
@Composable
fun AnimatedDropdown(
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
 * Animación de scale-in para diálogos
 */
@Composable
fun AnimatedDialog(
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
 * Animación de slide-up para Snackbars
 */
@Composable
fun AnimatedSnackbar(
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
 * Animación de entrada de pantalla
 */
@Composable
fun AnimatedScreen(
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
 * Animación de escala para botones
 */
@Composable
fun rememberButtonScale(pressed: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(100, easing = FastOutSlowInEasing),
        label = "buttonScale"
    ).value
}

/**
 * Animación de elevación para botones
 */
@Composable
fun rememberButtonElevation(pressed: Boolean): Float {
    return animateFloatAsState(
        targetValue = if (pressed) 8f else 4f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "buttonElevation"
    ).value
}

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
 * Modifier para aplicar animación de elevación
 */
fun Modifier.animatedElevation(elevation: Float): Modifier {
    return this.graphicsLayer {
        shadowElevation = elevation
    }
}
