package com.example.fitmind.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitmind.R
import com.example.fitmind.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.random.Random

/**
 * Worker para verificar el progreso de hábitos y enviar notificaciones inteligentes
 * Se ejecuta cada 24 horas para motivar al usuario con recordatorios personalizados
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val firebaseRepository = FirebaseRepository()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        const val CHANNEL_ID = "fitmind_progress_notifications"
        const val NOTIFICATION_ID = 1001
        const val WORK_TAG = "progress_notification_work"
    }

    override suspend fun doWork(): Result {
        return try {
            createNotificationChannel()
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                // No hay usuario autenticado, no enviar notificaciones
                return Result.success()
            }

            val userId = currentUser.uid
            val habits = firebaseRepository.getHabitsFromFirebase(userId)
            
            val pendingHabits = findPendingHabits(habits)
            
            if (pendingHabits.isNotEmpty()) {
                val randomHabit = pendingHabits.random()
                val motivationalMessage = generateMotivationalMessage(randomHabit["nombre"] as? String ?: "tu hábito")
                
                sendNotification(motivationalMessage)
            } else {
                // Si no hay hábitos pendientes, cancelar notificaciones
                cancelNotifications()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    /**
     * Encuentra hábitos que no se han completado en los últimos 3 días
     */
    private fun findPendingHabits(habits: List<Map<String, Any>>): List<Map<String, Any>> {
        val currentTime = System.currentTimeMillis()
        val threeDaysAgo = currentTime - (3 * 24 * 60 * 60 * 1000) // 3 días en millisegundos
        
        return habits.filter { habit ->
            val lastCompleted = habit["lastCompleted"] as? Long ?: 0
            val isCompleted = habit["completado"] as? Boolean ?: false
            
            // Si no está completado o no se ha completado en los últimos 3 días
            !isCompleted && lastCompleted < threeDaysAgo
        }
    }

    /**
     * Genera mensajes motivacionales variados
     */
    private fun generateMotivationalMessage(habitName: String): String {
        val messages = listOf(
            "🌱 ¡No te rindas! Retoma tu hábito de $habitName hoy.",
            "💪 ¡Tu progreso te espera! No olvides cumplir tu hábito de $habitName.",
            "🔥 ¡Un nuevo día, una nueva oportunidad para crecer!",
            "⭐ ¡Tu constancia es tu superpoder! Continúa con $habitName.",
            "🚀 ¡Cada pequeño paso cuenta! Retoma $habitName hoy.",
            "🌟 ¡Tu futuro yo te lo agradecerá! No olvides $habitName.",
            "💎 ¡La disciplina es libertad! Continúa con $habitName.",
            "🎯 ¡Enfócate en tu objetivo! Tu hábito de $habitName te espera.",
            "🌈 ¡Cada día es una nueva oportunidad! Retoma $habitName.",
            "⚡ ¡Tu energía positiva puede mover montañas! Continúa con $habitName."
        )
        
        return messages.random()
    }

    /**
     * Envía la notificación motivacional
     */
    private fun sendNotification(message: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🧠 FitMind - Recordatorio")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Cancela todas las notificaciones pendientes
     */
    private fun cancelNotifications() {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.cancel(NOTIFICATION_ID)
    }

    /**
     * Crea el canal de notificación para Android 8.0+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FitMind Recordatorios"
            val descriptionText = "Notificaciones motivacionales basadas en tu progreso de hábitos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
