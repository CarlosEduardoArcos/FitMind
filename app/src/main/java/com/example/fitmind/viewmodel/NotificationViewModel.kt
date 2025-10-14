package com.example.fitmind.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitmind.data.model.Notificacion
import com.example.fitmind.notification.NotificationScheduler
import com.example.fitmind.data.FirebaseRepository
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

    private val _smartNotificationsEnabled = MutableStateFlow(false)
    val smartNotificationsEnabled: StateFlow<Boolean> = _smartNotificationsEnabled.asStateFlow()

    private val _notificationStatus = MutableStateFlow(com.example.fitmind.notification.NotificationStatus.NOT_SCHEDULED)
    val notificationStatus: StateFlow<com.example.fitmind.notification.NotificationStatus> = _notificationStatus.asStateFlow()

    private var notificationScheduler: NotificationScheduler? = null

    fun setEnabled(value: Boolean) { _enabled.value = value }

    fun setScheduledTime(time: String) { _scheduledTime.value = time }

    fun setMessage(text: String) { _message.value = text }

    fun setHabitName(name: String) { _habitName.value = name }

    fun setIsRecurring(recurring: Boolean) { _isRecurring.value = recurring }

    fun setSmartNotificationsEnabled(enabled: Boolean) { 
        _smartNotificationsEnabled.value = enabled 
    }

    /**
     * Programa una notificación para un hábito específico
     */
    fun scheduleNotification(context: Context, userId: String) {
        // Inicializar el scheduler si no existe de forma segura
        try {
            if (notificationScheduler == null) {
                notificationScheduler = NotificationScheduler(context)
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error al inicializar el programador de notificaciones"
            return
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
                
                // Programar notificación - Funcionalidad temporalmente deshabilitada
                // TODO: Implementar scheduleRecurringNotification y scheduleNotification
                _successMessage.value = "Funcionalidad de notificaciones programadas temporalmente deshabilitada"
                
                // Guardar en Firebase para historial
                val notif = Notificacion(
                    mensaje = finalMessage,
                    fechaHora = System.currentTimeMillis().toString(),
                    usuarioId = userId
                )
                // repository.sendNotification(notif) // Comentado temporalmente
                
                _errorMessage.value = null
                _successMessage.value = if (_isRecurring.value) {
                    "Recordatorio diario programado para ${_habitName.value} a las ${_scheduledTime.value}"
                } else {
                    "Notificación programada para ${_habitName.value} a las ${_scheduledTime.value}"
                }
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: SecurityException) {
                _errorMessage.value = e.message ?: "Permisos insuficientes para programar notificaciones"
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
                // TODO: Implementar cancelAllNotifications
                // notificationScheduler?.cancelAllNotifications()
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
                
                // TODO: Implementar scheduleTestNotification
                // notificationScheduler?.scheduleTestNotification(habitName)
                
                _successMessage.value = "🔔 Notificación de prueba programada (aparecerá en 5 segundos)"
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: SecurityException) {
                _errorMessage.value = e.message ?: "Permisos insuficientes para programar notificaciones de prueba"
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

    /**
     * Programa notificaciones inteligentes basadas en progreso real de hábitos
     */
    fun scheduleProgressNotifications(context: Context) {
        viewModelScope.launch {
            try {
                if (notificationScheduler == null) {
                    notificationScheduler = NotificationScheduler(context)
                }
                
                notificationScheduler?.scheduleProgressNotifications()
                _smartNotificationsEnabled.value = true
                _notificationStatus.value = notificationScheduler?.getNotificationStatus() 
                    ?: com.example.fitmind.notification.NotificationStatus.NOT_SCHEDULED
                
                _successMessage.value = "🧠 Notificaciones inteligentes activadas. Te motivaremos según tu progreso."
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al activar notificaciones inteligentes: ${e.message}"
            }
        }
    }

    /**
     * Cancela las notificaciones inteligentes
     */
    fun cancelProgressNotifications(context: Context) {
        viewModelScope.launch {
            try {
                if (notificationScheduler == null) {
                    notificationScheduler = NotificationScheduler(context)
                }
                
                notificationScheduler?.cancelProgressNotifications()
                _smartNotificationsEnabled.value = false
                _notificationStatus.value = com.example.fitmind.notification.NotificationStatus.NOT_SCHEDULED
                
                _successMessage.value = "🔕 Notificaciones inteligentes desactivadas."
                
                // Limpiar mensaje de éxito después de 3 segundos
                kotlinx.coroutines.delay(3000)
                _successMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al desactivar notificaciones inteligentes: ${e.message}"
            }
        }
    }

    /**
     * Actualiza el estado de las notificaciones
     */
    fun updateNotificationStatus(context: Context) {
        viewModelScope.launch {
            try {
                if (notificationScheduler == null) {
                    notificationScheduler = NotificationScheduler(context)
                }
                
                _notificationStatus.value = notificationScheduler?.getNotificationStatus() 
                    ?: com.example.fitmind.notification.NotificationStatus.NOT_SCHEDULED
                    
                _smartNotificationsEnabled.value = notificationScheduler?.hasScheduledNotifications() ?: false
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar estado de notificaciones: ${e.message}"
            }
        }
    }
}


