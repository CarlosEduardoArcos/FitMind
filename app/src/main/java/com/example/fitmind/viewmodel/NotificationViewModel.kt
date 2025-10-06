package com.example.fitmind.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.fitmind.data.model.Notificacion
import com.example.fitmind.data.notifications.NotificationWorker
import com.example.fitmind.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * ViewModel for push notifications configuration and dispatching.
 */
class NotificationViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _enabled = MutableStateFlow(true)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _scheduledTime = MutableStateFlow<String?>(null)
    val scheduledTime: StateFlow<String?> = _scheduledTime.asStateFlow()

    private val _message = MutableStateFlow("¡Es hora de completar tus hábitos!")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setEnabled(value: Boolean) { _enabled.value = value }

    fun setScheduledTime(time: String?) { _scheduledTime.value = time }

    fun setMessage(text: String) { _message.value = text }

    fun scheduleNotification(context: Context, userId: String) {
        if (!_enabled.value) return
        
        viewModelScope.launch {
            try {
                val notif = Notificacion(
                    mensaje = _message.value,
                    fechaHora = _scheduledTime.value ?: System.currentTimeMillis().toString(),
                    usuarioId = userId
                )
                
                // Schedule using WorkManager
                scheduleWorkManagerNotification(context, notif)
                
                // Also save to Firebase
                repository.sendNotification(notif)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    private fun scheduleWorkManagerNotification(context: Context, notificacion: Notificacion) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val notificationData = Data.Builder()
            .putString("title", "Recordatorio FitMind")
            .putString("body", notificacion.mensaje)
            .putString("userId", notificacion.usuarioId)
            .build()

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(notificationData)
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.SECONDS) // For demo, show after 5 seconds
            .build()

        WorkManager.getInstance(context).enqueue(notificationWork)
    }

    fun cancelNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }
}


