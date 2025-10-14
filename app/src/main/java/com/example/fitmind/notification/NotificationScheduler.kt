package com.example.fitmind.notification

import android.content.Context
import androidx.work.*
import com.example.fitmind.worker.NotificationWorker
import java.util.concurrent.TimeUnit

/**
 * Clase para gestionar la programación de notificaciones inteligentes
 * Maneja la creación, actualización y cancelación de trabajos de WorkManager
 */
class NotificationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    companion object {
        private const val WORK_NAME = "fitmind_progress_notifications"
        private const val INTERVAL_HOURS = 24L // Verificar cada 24 horas
    }

    /**
     * Programa las notificaciones inteligentes para ejecutarse cada 24 horas
     */
    fun scheduleProgressNotifications() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            INTERVAL_HOURS,
            TimeUnit.HOURS,
            1, // Flex interval - ejecutar dentro de 1 hora del tiempo programado
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(NotificationWorker.WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Usar enqueueUniquePeriodicWork para evitar duplicados
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWork
        )
    }

    /**
     * Cancela todas las notificaciones programadas
     */
    fun cancelProgressNotifications() {
        workManager.cancelUniqueWork(WORK_NAME)
        workManager.cancelAllWorkByTag(NotificationWorker.WORK_TAG)
    }

    /**
     * Verifica si hay notificaciones programadas
     */
    fun hasScheduledNotifications(): Boolean {
        val workInfos = workManager.getWorkInfosByTag(NotificationWorker.WORK_TAG).get()
        return workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
    }

    /**
     * Obtiene el estado actual de las notificaciones
     */
    fun getNotificationStatus(): NotificationStatus {
        val workInfos = workManager.getWorkInfosByTag(NotificationWorker.WORK_TAG).get()
        
        return when {
            workInfos.isEmpty() -> NotificationStatus.NOT_SCHEDULED
            workInfos.any { it.state == WorkInfo.State.ENQUEUED } -> NotificationStatus.SCHEDULED
            workInfos.any { it.state == WorkInfo.State.RUNNING } -> NotificationStatus.RUNNING
            workInfos.any { it.state == WorkInfo.State.FAILED } -> NotificationStatus.FAILED
            else -> NotificationStatus.COMPLETED
        }
    }
}

/**
 * Estados posibles de las notificaciones
 */
enum class NotificationStatus {
    NOT_SCHEDULED,    // No hay notificaciones programadas
    SCHEDULED,        // Notificaciones programadas y esperando
    RUNNING,          // Verificación en progreso
    COMPLETED,        // Última verificación completada exitosamente
    FAILED            // Última verificación falló
}
