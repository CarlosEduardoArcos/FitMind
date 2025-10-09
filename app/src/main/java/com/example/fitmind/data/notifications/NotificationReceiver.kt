package com.example.fitmind.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fitmind.MainActivity
import com.example.fitmind.R

/**
 * Receptor de notificaciones locales para recordatorios de hábitos
 * Se ejecuta cuando llega la hora programada de notificación
 */
class NotificationReceiver : BroadcastReceiver() {
    
    companion object {
        private const val CHANNEL_ID = "fitmind_reminders_channel"
        private const val NOTIFICATION_ID = 1001
        
        // Claves para el Intent
        const val EXTRA_HABIT_NAME = "habit_name"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME) ?: "tu hábito"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "¡Es hora de trabajar en $habitName! 💪"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_ID)
        
        showNotification(context, habitName, message, notificationId)
    }
    
    private fun showNotification(
        context: Context, 
        habitName: String, 
        message: String, 
        notificationId: Int
    ) {
        // Crear canal de notificación si no existe
        createNotificationChannel(context)
        
        // Intent para abrir la app cuando se toque la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_habits", true) // Para abrir directamente la sección de hábitos
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Crear la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔔 Recordatorio FitMind")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setLights(0xFF00C853.toInt(), 1000, 1000)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Ver Hábitos",
                pendingIntent
            )
            .build()
        
        // Mostrar la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de Hábitos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de recordatorios para hábitos"
                enableLights(true)
                lightColor = 0xFF00C853.toInt()
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
