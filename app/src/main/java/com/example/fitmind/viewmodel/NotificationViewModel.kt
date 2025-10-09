package com.example.fitmind.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.model.Notificacion
import com.example.fitmind.data.notifications.NotificationScheduler
import com.example.fitmind.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel para configuración y programación de notificaciones locales
 */
class NotificationViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _scheduledTime = MutableStateFlow("09:00")
    val scheduledTime: StateFlow<String> = _scheduledTime.asStateFlow()

    private val _message = MutableStateFlow("¡Es hora de completar tus hábitos!")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _habitName = MutableStateFlow("")
    val habitName: StateFlow<String> = _habitName.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _isRecurring = MutableStateFlow(true)
    val isRecurring: StateFlow<Boolean> = _isRecurring.asStateFlow()

    private var notificationScheduler: NotificationScheduler? = null

    fun setEnabled(value: Boolean) { _enabled.value = value }

    fun setScheduledTime(time: String) { _scheduledTime.value = time }

    fun setMessage(text: String) { _message.value = text }

    fun setHabitName(name: String) { _habitName.value = name }

    fun setIsRecurring(recurring: Boolean) { _isRecurring.value = recurring }

    /**
     * Programa una notificación para un hábito específico
     */
    fun scheduleNotification(context: Context, userId: String) {
        // Inicializar el scheduler si no existe
        if (notificationScheduler == null) {
            notificationScheduler = NotificationScheduler(context)
        }
        if (!_enabled.value) {
            _errorMessage.value = "Las notificaciones están deshabilitadas"
            return
        }
        
        if (_habitName.value.isBlank()) {
            _errorMessage.value = "Debes especificar un nombre de hábito"
            return
        }
        
        viewModelScope.launch {
            try {
                val timeParts = _scheduledTime.value.split(":")
                if (timeParts.size != 2) {
                    _errorMessage.value = "Formato de hora inválido"
                    return@launch
                }
                
                val hour = timeParts[0].toIntOrNull() ?: 9
                val minute = timeParts[1].toIntOrNull() ?: 0
                
                if (hour !in 0..23 || minute !in 0..59) {
                    _errorMessage.value = "Hora inválida. Use formato HH:MM (00:00 - 23:59)"
                    return@launch
                }
                
                val notificationId = if (_isRecurring.value) 1001 else System.currentTimeMillis().toInt()
                val finalMessage = if (_message.value.isBlank()) {
                    "¡Es hora de trabajar en ${_habitName.value}! 💪"
                } else {
                    _message.value
                }
                
                // Programar notificación
                if (_isRecurring.value) {
                    notificationScheduler?.scheduleRecurringNotification(
                        _habitName.value,
                        finalMessage,
                        hour,
                        minute,
                        notificationId
                    )
                } else {
                    notificationScheduler?.scheduleNotification(
                        _habitName.value,
                        finalMessage,
                        hour,
                        minute,
                        notificationId
                    )
                }
                
                // Guardar en Firebase para historial
                val notif = Notificacion(
                    mensaje = finalMessage,
                    fechaHora = System.currentTimeMillis().toString(),
                    usuarioId = userId
                )
                repository.sendNotification(notif)
                
                _errorMessage.value = null
                _successMessage.value = if (_isRecurring.value) {
                    "Recordatorio diario programado para ${_habitName.value} a las ${_scheduledTime.value}"
                } else {
                    "Notificación programada para ${_habitName.value} a las ${_scheduledTime.value}"
                }
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al programar notificación: ${e.message}"
            }
        }
    }

    /**
     * Cancela todas las notificaciones programadas
     */
    fun cancelNotifications(context: Context) {
        viewModelScope.launch {
            try {
                if (notificationScheduler == null) {
                    notificationScheduler = NotificationScheduler(context)
                }
                notificationScheduler?.cancelAllNotifications()
                _successMessage.value = "Todas las notificaciones han sido canceladas"
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al cancelar notificaciones: ${e.message}"
            }
        }
    }
    
    /**
     * Programa una notificación de prueba (5 segundos)
     */
    fun scheduleTestNotification(context: Context) {
        viewModelScope.launch {
            try {
                if (notificationScheduler == null) {
                    notificationScheduler = NotificationScheduler(context)
                }
                val habitName = if (_habitName.value.isBlank()) "tu hábito" else _habitName.value
                val testMessage = "🔔 Notificación de prueba para $habitName"
                
                notificationScheduler?.scheduleNotification(
                    habitName,
                    testMessage,
                    Calendar.getInstance().apply {
                        add(Calendar.SECOND, 5)
                    }.get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().apply {
                        add(Calendar.SECOND, 5)
                    }.get(Calendar.MINUTE),
                    System.currentTimeMillis().toInt()
                )
                
                _successMessage.value = "Notificación de prueba programada (5 segundos)"
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al programar notificación de prueba: ${e.message}"
            }
        }
    }
    
    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Limpia los mensajes de éxito
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
}


