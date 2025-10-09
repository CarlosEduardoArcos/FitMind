package com.example.fitmind.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

/**
 * Clase para programar notificaciones locales usando AlarmManager
 * Permite programar recordatorios personalizados para hábitos
 */
class NotificationScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Verifica si la app tiene permisos para programar alarmas exactas
     */
    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Para versiones anteriores a Android 12, no se requiere verificación
        }
    }
    
    /**
     * Programa una notificación para un hábito específico
     * @param habitName Nombre del hábito
     * @param message Mensaje personalizado
     * @param hour Hora en formato 24h (0-23)
     * @param minute Minuto (0-59)
     * @param notificationId ID único para la notificación
     */
    fun scheduleNotification(
        habitName: String,
        message: String,
        hour: Int,
        minute: Int,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        // Verificar permisos antes de programar
        if (!canScheduleExactAlarms()) {
            throw SecurityException("La aplicación no tiene permisos para programar alarmas exactas. Por favor, habilita los permisos en Configuración > Aplicaciones > FitMind > Permisos")
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // Si la hora ya pasó hoy, programar para mañana
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_HABIT_NAME, habitName)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, message)
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Programar la alarma
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    
    /**
     * Programa notificaciones recurrentes diarias
     * @param habitName Nombre del hábito
     * @param message Mensaje personalizado
     * @param hour Hora en formato 24h (0-23)
     * @param minute Minuto (0-59)
     * @param notificationId ID único para la notificación
     */
    fun scheduleRecurringNotification(
        habitName: String,
        message: String,
        hour: Int,
        minute: Int,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        // Verificar permisos antes de programar
        if (!canScheduleExactAlarms()) {
            throw SecurityException("La aplicación no tiene permisos para programar alarmas exactas. Por favor, habilita los permisos en Configuración > Aplicaciones > FitMind > Permisos")
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // Si la hora ya pasó hoy, programar para mañana
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_HABIT_NAME, habitName)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, message)
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Programar la alarma recurrente
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Se repite cada día
            pendingIntent
        )
    }
    
    /**
     * Cancela una notificación programada
     * @param notificationId ID de la notificación a cancelar
     */
    fun cancelNotification(notificationId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    
    /**
     * Cancela todas las notificaciones programadas
     */
    fun cancelAllNotifications() {
        // Nota: AlarmManager no tiene un método directo para cancelar todas las alarmas
        // En una implementación real, deberías mantener un registro de las notificaciones activas
        // y cancelarlas individualmente. Por ahora, cancelamos algunas comunes.
        
        val commonIds = listOf(1001, 1002, 1003, 1004, 1005)
        commonIds.forEach { id ->
            cancelNotification(id)
        }
    }
    
    /**
     * Programa una notificación de prueba (5 segundos desde ahora)
     * Útil para probar que las notificaciones funcionan correctamente
     * @param habitName Nombre del hábito para la notificación de prueba
     */
    fun scheduleTestNotification(habitName: String) {
        // Verificar permisos antes de programar
        if (!canScheduleExactAlarms()) {
            throw SecurityException("La aplicación no tiene permisos para programar alarmas exactas. Por favor, habilita los permisos en Configuración > Aplicaciones > FitMind > Permisos")
        }
        val testNotificationId = 9999 // ID fijo para notificaciones de prueba
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_HABIT_NAME, habitName)
            putExtra(NotificationReceiver.EXTRA_MESSAGE, "🔔 Notificación de prueba para $habitName")
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, testNotificationId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            testNotificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Programar para 5 segundos desde ahora
        val testTime = Calendar.getInstance().apply {
            add(Calendar.SECOND, 5)
        }
        
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            testTime.timeInMillis,
            pendingIntent
        )
    }
    
    /**
     * Verifica si hay notificaciones programadas
     * @param notificationId ID de la notificación a verificar
     * @return true si hay una notificación programada
     */
    fun hasNotificationScheduled(notificationId: Int): Boolean {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        return pendingIntent != null
    }
}
