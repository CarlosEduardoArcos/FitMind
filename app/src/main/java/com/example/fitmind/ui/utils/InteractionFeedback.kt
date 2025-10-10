package com.example.fitmind.ui.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Sistema centralizado de feedback interactivo para FitMind
 * Maneja vibración, sonidos y efectos hápticos de manera consistente
 */
class InteractionFeedback(private val context: Context) {
    
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    // OPT: Optimizar SoundPool para menor consumo de batería
    private val soundPool: SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        SoundPool.Builder()
            .setMaxStreams(2) // OPT: Reducido de 4 a 2 para menor consumo
            .setAudioAttributes(audioAttributes)
            .build()
    } else {
        @Suppress("DEPRECATION")
        SoundPool(2, AudioManager.STREAM_MUSIC, 0) // OPT: Reducido de 4 a 2
    }
    
    // IDs de sonidos (se cargarán dinámicamente)
    private var successChimeId: Int = 0
    private var deleteToneId: Int = 0
    private var notificationPingId: Int = 0
    private var habitCompleteId: Int = 0
    private var navigationClickId: Int = 0
    private var toggleSwitchId: Int = 0
    
    init {
        loadSounds()
    }
    
    /**
     * Carga los archivos de sonido desde res/raw/
     * Si no existen los archivos, usa sonidos del sistema
     */
    private fun loadSounds() {
        try {
            // Intentar cargar sonidos personalizados
            // Nota: Los archivos de sonido se pueden agregar más tarde
            // Por ahora, usamos solo vibración como feedback principal
            loadSystemSounds()
        } catch (e: Exception) {
            // Si no existen los archivos, usar sonidos del sistema
            loadSystemSounds()
        }
    }
    
    /**
     * Carga sonidos del sistema como fallback
     */
    private fun loadSystemSounds() {
        // Para desarrollo, usaremos vibración como feedback principal
        // En producción, se pueden agregar archivos de sonido reales
        successChimeId = -1
        deleteToneId = -1
        notificationPingId = -1
        habitCompleteId = -1
        navigationClickId = -1
        toggleSwitchId = -1
    }
    
    /**
     * Reproduce un sonido específico
     */
    private fun playSound(soundId: Int, volume: Float = 0.3f) {
        if (soundId != -1) {
            soundPool.play(soundId, volume, volume, 1, 0, 1.0f)
        }
    }
    
    /**
     * Vibración personalizada para diferentes tipos de feedback
     * Versión segura con validación completa de contexto y hardware
     */
    private fun vibrate(pattern: LongArray, amplitude: IntArray? = null) {
        try {
            // Validar que el patrón no sea nulo o vacío
            if (pattern.isEmpty()) {
                Log.w("InteractionFeedback", "Patrón de vibración vacío, usando patrón por defecto")
                val defaultPattern = longArrayOf(0, 50)
                performVibration(defaultPattern, amplitude)
                return
            }
            
            // Validar que el vibrator esté disponible
            if (!vibrator.hasVibrator()) {
                Log.w("InteractionFeedback", "El dispositivo no tiene vibrador o no está disponible")
                return
            }
            
            // Validar que todos los valores del patrón sean válidos
            val validPattern = pattern.filter { it >= 0 }.toLongArray()
            if (validPattern.isEmpty()) {
                Log.w("InteractionFeedback", "Patrón de vibración inválido, usando patrón por defecto")
                val defaultPattern = longArrayOf(0, 50)
                performVibration(defaultPattern, amplitude)
                return
            }
            
            performVibration(validPattern, amplitude)
            
        } catch (e: Exception) {
            Log.e("InteractionFeedback", "Error al ejecutar vibración: ${e.message}")
        }
    }
    
    /**
     * Ejecuta la vibración de forma segura
     */
    private fun performVibration(pattern: LongArray, amplitude: IntArray?) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(pattern, amplitude, -1)
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.e("InteractionFeedback", "Error al ejecutar vibración con VibrationEffect: ${e.message}")
            // Fallback: intentar vibración simple
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val simpleEffect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(simpleEffect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50)
                }
            } catch (fallbackError: Exception) {
                Log.e("InteractionFeedback", "Error en fallback de vibración: ${fallbackError.message}")
            }
        }
    }
    
    // === FEEDBACK ESPECÍFICOS PARA FITMIND ===
    
    /**
     * Feedback al agregar un nuevo hábito
     */
    fun onHabitAdded() {
        // OPT: Vibración optimizada - más corta para menor consumo
        vibrate(longArrayOf(0, 50, 25, 50))
        playSound(successChimeId, 0.3f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback al eliminar un hábito
     */
    fun onHabitDeleted() {
        // OPT: Vibración optimizada - más corta
        vibrate(longArrayOf(0, 80))
        playSound(deleteToneId, 0.2f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback al completar un hábito
     */
    fun onHabitCompleted() {
        // OPT: Vibración optimizada - patrón más corto
        vibrate(longArrayOf(0, 60, 30, 80))
        playSound(habitCompleteId, 0.4f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback al programar una notificación
     */
    fun onNotificationScheduled() {
        // OPT: Vibración optimizada - más corta
        vibrate(longArrayOf(0, 50, 25, 50))
        playSound(notificationPingId, 0.3f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback al cancelar notificaciones
     */
    fun onNotificationsCancelled() {
        // OPT: Vibración optimizada - más corta
        vibrate(longArrayOf(0, 80, 40, 40))
        playSound(deleteToneId, 0.2f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback al cambiar de sección en navegación
     */
    fun onNavigationClick() {
        // OPT: Vibración mínima para navegación
        vibrate(longArrayOf(0, 20)) // OPT: Reducido de 30ms a 20ms
    }
    
    /**
     * Feedback al activar/desactivar modo oscuro
     */
    fun onThemeToggle() {
        // OPT: Vibración optimizada
        vibrate(longArrayOf(0, 40, 20, 50))
        playSound(toggleSwitchId, 0.2f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback al seleccionar hora o hábito
     */
    fun onSelection() {
        // OPT: Vibración mínima para selección
        vibrate(longArrayOf(0, 20)) // OPT: Reducido de 30ms a 20ms
    }
    
    /**
     * Feedback de confirmación general
     */
    fun onConfirm() {
        // OPT: Vibración optimizada
        vibrate(longArrayOf(0, 50, 25, 50))
        playSound(successChimeId, 0.3f) // OPT: Volumen reducido
    }
    
    /**
     * Feedback de error o advertencia
     */
    fun onError() {
        // OPT: Vibración optimizada - más corta
        vibrate(longArrayOf(0, 100, 50, 100))
    }
    
    /**
     * Función pública segura para vibración desde cualquier lugar
     * Úsala cuando necesites vibración segura fuera de los métodos específicos
     */
    fun vibrate(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            
            if (vibrator == null || !vibrator.hasVibrator()) {
                Log.w("InteractionFeedback", "El dispositivo no tiene vibrador o no está disponible")
                return
            }

            val pattern = longArrayOf(0, 50, 30, 50) // patrón de vibración válido
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            Log.e("InteractionFeedback", "Error al ejecutar vibración: ${e.message}")
        }
    }
    
    /**
     * Libera recursos del SoundPool
     */
    fun release() {
        soundPool.release()
    }
}

/**
 * Composable helper para usar InteractionFeedback en la UI
 */
@Composable
fun rememberInteractionFeedback(): InteractionFeedback {
    val context = LocalContext.current
    return remember { InteractionFeedback(context) }
}

/**
 * Extensión para HapticFeedback que integra con nuestro sistema
 */
fun HapticFeedback.performFeedback(type: FeedbackType) {
    when (type) {
        FeedbackType.HABIT_ADDED -> {
            performHapticFeedback(HapticFeedbackType.LongPress)
        }
        FeedbackType.HABIT_DELETED -> {
            performHapticFeedback(HapticFeedbackType.LongPress)
        }
        FeedbackType.HABIT_COMPLETED -> {
            performHapticFeedback(HapticFeedbackType.LongPress)
        }
        FeedbackType.NOTIFICATION_SCHEDULED -> {
            performHapticFeedback(HapticFeedbackType.LongPress)
        }
        FeedbackType.NAVIGATION -> {
            performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        FeedbackType.SELECTION -> {
            performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        FeedbackType.CONFIRM -> {
            performHapticFeedback(HapticFeedbackType.LongPress)
        }
        FeedbackType.TOGGLE -> {
            performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
}

/**
 * Tipos de feedback disponibles
 */
enum class FeedbackType {
    HABIT_ADDED,
    HABIT_DELETED,
    HABIT_COMPLETED,
    NOTIFICATION_SCHEDULED,
    NAVIGATION,
    SELECTION,
    CONFIRM,
    TOGGLE
}
