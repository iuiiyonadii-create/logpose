package com.uriel.logpose.core.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.uriel.logpose.R
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.notifications.NotificationHelper
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.telecom.ScoStateManager
import com.uriel.logpose.core.utils.PowerManagerHelper
import com.uriel.logpose.features.voice.PlaybackAwareMicGate
import com.uriel.logpose.features.voice.VoskVoiceEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class LogPoseCallService : Service() {

    enum class ServiceTripStatus { IDLE, CONNECTING, ACTIVE, ERROR }

    @Inject lateinit var telecom: LogPoseTelecom
    @Inject lateinit var voskEngine: VoskVoiceEngine
    @Inject lateinit var communicationManager: BluetoothCommunicationManager
    @Inject lateinit var micGate: PlaybackAwareMicGate
    @Inject lateinit var tripOrchestrator: TripOrchestrator

    private lateinit var attributionContext: Context
    private lateinit var scoStateManager: ScoStateManager
    private val mediaButtonTrigger by lazy { com.uriel.logpose.features.bluetooth.MediaButtonTrigger(this) }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var tripJob: Job? = null
    private var watchdogJob: Job? = null
    private lateinit var watchdog: ListenerWatchdog

    private val _recognizedCommands = MutableSharedFlow<VoskVoiceEngine.RecognizedCommand>(replay = 1)
    val recognizedCommands: SharedFlow<VoskVoiceEngine.RecognizedCommand> = _recognizedCommands.asSharedFlow()

    private val _tripStatus = MutableStateFlow(ServiceTripStatus.IDLE)
    val tripStatus: StateFlow<ServiceTripStatus> = _tripStatus.asStateFlow()

    private val _bannerText = MutableStateFlow<String?>(null)
    val bannerText: StateFlow<String?> = _bannerText.asStateFlow()

    var isTripActive = false
        private set
    
    var isHeadsetConnected = false
        private set

    private val binder = LocalBinder()
    private var headsetReceiver: BroadcastReceiver? = null

    inner class LocalBinder : Binder() {
        fun getService(): LogPoseCallService = this@LogPoseCallService
    }

    override fun onCreate() {
        super.onCreate()
        if (LogPoseCallService._isServiceRunning.value) {
            LogPoseLogger.w("Service: Reinicio detectado. HyperOS mató el proceso previo.")
        }
        
        LogPoseCallService._isServiceRunning.value = false
        instanceRef = WeakReference(this)
        
        attributionContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            createAttributionContext("audio")
        } else {
            this
        }

        voskEngine.setAttributionContext(attributionContext)

        scoStateManager = ScoStateManager(this, attributionContext.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager) {
            if (isTripActive) {
                LogPoseLogger.i("Service: Gatillando reconexión SCO automática.")
                communicationManager.hammerScoConnection()
            }
        }
        
        com.uriel.logpose.features.music.MusicManager.initialize(attributionContext)
        communicationManager.updateContext(attributionContext)

        watchdog = ListenerWatchdog(attributionContext)
        AudioPathGuardian.initialize(this)
        registerHeadsetReceiver()
        scoStateManager.startMonitoring()
        
        PowerManagerHelper.requestIgnoreBatteryOptimizations(this)
        FlightRecorder.initialize(this)
        checkAndRestoreSession()
    }

    private fun checkAndRestoreSession() {
        val restored = com.uriel.logpose.thamis.world.engine.WorldModelEngine.restoreFromCheckpoint()
        if (restored) {
            val snapshot = com.uriel.logpose.thamis.world.engine.WorldModelEngine.getCurrentSnapshot()
            if (snapshot.systems.navigation.isNavigating) {
                LogPoseLogger.i("Recovery: Detectada sesión de navegación previa. Restaurando viaje...")
                startTrip()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRIP -> startTrip()
            ACTION_END_TRIP -> endTrip()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun startTrip() {
        if (isTripActive || _tripStatus.value == ServiceTripStatus.CONNECTING) return
        
        PowerManagerHelper.acquireWakeLock(this)
        
        val startIntent = Intent(this, LogPoseCallService::class.java)
        startForegroundService(startIntent)

        tripJob?.cancel()
        tripJob = serviceScope.launch {
            try {
                LogPoseCallService._isServiceRunning.value = true
                _tripStatus.value = ServiceTripStatus.CONNECTING
                
                com.uriel.logpose.core.app.LogPoseApplication.instance.createNotificationChannels()
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID, 
                        buildOngoingNotification("Conectando..."),
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or 
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL or
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(NOTIFICATION_ID, buildOngoingNotification("Conectando..."))
                }
                
                AlertManager.enqueue("LogPose iniciado. Buen viaje, Uriel.", priority = AlertPriority.SYSTEM)
                
                isTripActive = true
                isHeadsetConnected = true
                _tripStatus.value = ServiceTripStatus.ACTIVE
                
                try {
                    val component = android.content.ComponentName(this@LogPoseCallService, LogPoseNotificationListener::class.java)
                    android.service.notification.NotificationListenerService.requestRebind(component)
                } catch (e: Exception) {
                    LogPoseLogger.e("Service: Falló rebind de notificaciones: ${e.message}")
                }

                tripOrchestrator.startTrip {
                    updateNotification("Viaje activo")
                }
                
                startWatchdog()
                com.uriel.logpose.core.workers.ServicePersistenceWorker.schedule(applicationContext)
                FlightRecorder.startSession()
            } catch (e: Exception) {
                LogPoseLogger.e("THAMIS_LAB: Fallo crítico al iniciar viaje: ${e.message}")
                stopSelf()
            }
        }
    }

    fun endTrip() {
        PowerManagerHelper.releaseWakeLock()
        FlightRecorder.stopSession()

        LogPoseCallService._isServiceRunning.value = false
        isTripActive = false
        _tripStatus.value = ServiceTripStatus.IDLE
        
        com.uriel.logpose.thamis.thamis_final.ThamisCore.getInstance(this).shutdown()
        com.uriel.logpose.core.workers.ServicePersistenceWorker.stop(applicationContext)
        
        tripJob?.cancel()
        serviceScope.launch {
            tripOrchestrator.endTrip()
            AlertManager.enqueue("Viaje finalizado.")

            watchdogJob?.cancel()

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    fun yieldAudio() {
        if (!isTripActive) return
        ComfortNoiseManager.duck()
        updateNotification("LogPose en segundo plano")
    }

    fun resumeAudio() {
        if (!isTripActive) return
        ComfortNoiseManager.restoreVolume()
        updateNotification("Viaje activo — casco conectado")
    }

    private fun startWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = serviceScope.launch {
            while (isActive) {
                delay(15_000L) // v2.0: Intervalo reducido (Misión #025)
                
                // 1. Salud del Notification Listener
                if (LogPoseNotificationListener.getInstance() != null) {
                    LogPoseNotificationListener.updateHeartbeat()
                }
                val lastHeartbeat = LogPoseNotificationListener.getLastHeartbeat()
                watchdog.checkAndHandle(lastHeartbeat)
                
                // 2. Salud de la Captura de Audio (Micrófono)
                IntercomCaptureManager.checkHealth(this@LogPoseCallService)
            }
        }
    }

    fun dismissBanner() { _bannerText.value = null }

    private fun registerHeadsetReceiver() {
        headsetReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)
                if (state == BluetoothProfile.STATE_CONNECTED && !isTripActive) {
                    NotificationHelper.showStartTripSuggestion(applicationContext)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(headsetReceiver, IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED), Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(headsetReceiver, IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
        }
    }

    private fun buildOngoingNotification(status: String): Notification {
        val stopIntent = Intent(this, LogPoseCallService::class.java).apply { action = ACTION_END_TRIP }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val openAppIntent = PendingIntent.getActivity(this, 0, Intent(this, com.uriel.logpose.core.app.MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, TRIP_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("LogPose")
            .setContentText(status)
            .setContentIntent(openAppIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Finalizar viaje", stopPendingIntent)
            .build()
    }

    private fun updateNotification(status: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildOngoingNotification(status))
    }

    override fun onDestroy() {
        super.onDestroy()
        LogPoseCallService._isServiceRunning.value = false
        mediaButtonTrigger.destroy()
        headsetReceiver?.let { unregisterReceiver(it) }
        tripOrchestrator.endTrip()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START_TRIP = "com.uriel.logpose.action.START_TRIP"
        const val ACTION_END_TRIP = "com.uriel.logpose.action.END_TRIP"
        const val NOTIFICATION_ID = 1001
        const val TRIP_CHANNEL_ID = "trip_channel"

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private var instanceRef: WeakReference<LogPoseCallService>? = null
        val instance: LogPoseCallService? get() = instanceRef?.get()
    }
}
