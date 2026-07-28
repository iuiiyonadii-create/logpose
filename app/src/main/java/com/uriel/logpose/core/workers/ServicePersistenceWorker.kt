package com.uriel.logpose.core.workers

import android.content.Context
import android.content.Intent
import androidx.work.*
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.services.LogPoseCallService
import java.util.concurrent.TimeUnit

/**
 * ServicePersistenceWorker: Se encarga de asegurar que el servicio de viaje
 * siga vivo en dispositivos Xiaomi/HyperOS que matan agresivamente apps.
 */
class ServicePersistenceWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val instance = LogPoseCallService.instance
        if (instance == null || !instance.isTripActive) {
            LogPoseLogger.d("PersistenceWorker: El viaje no parece estar activo o el servicio está detenido.")
            return Result.success()
        }

        LogPoseLogger.i("PersistenceWorker: Verificando salud de LogPoseCallService...")
        
        // El intent de START_TRIP es idempotente, si ya está corriendo no hace nada malo
        val intent = Intent(applicationContext, LogPoseCallService::class.java).apply {
            action = LogPoseCallService.ACTION_START_TRIP
        }
        
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent)
            } else {
                applicationContext.startService(intent)
            }
        } catch (e: Exception) {
            LogPoseLogger.e("PersistenceWorker: Error al intentar revivir servicio: ${e.message}")
            return Result.retry()
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "logpose_persistence_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ServicePersistenceWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            LogPoseLogger.i("PersistenceWorker: Programado ciclo de 15m.")
        }

        fun stop(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            LogPoseLogger.i("PersistenceWorker: Detenido.")
        }
    }
}
