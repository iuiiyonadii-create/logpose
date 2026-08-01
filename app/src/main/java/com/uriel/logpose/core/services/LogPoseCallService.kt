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
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.uriel.logpose.R
import com.uriel.logpose.core.app.AppContainer
import com.uriel.logpose.core.app.MainActivity
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.notifications.NotificationHelper
import com.uriel.logpose.core.telecom.LogPoseTelecom
import com.uriel.logpose.core.telecom.ScoStateManager
import com.uriel.logpose.core.utils.BatteryMonitor
import com.uriel.logpose.core.utils.PowerManagerHelper
import com.uriel.logpose.features.voice.VoskVoiceEngine
import com.uriel.logpose.features.navigation.NavigationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference

class LogPoseCallService : Service() {

    enum class ServiceTripStatus { IDLE, CONNECTING, ACTIVE, ERROR }

    private lateinit var telecom: LogPoseTelecom
    private lateinit var batteryMonitor: BatteryMonitor
    private lateinit var attributionContext: Context
    private lateinit var scoStateManager: ScoStateManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var tripJob: Job? = null
    private var batteryPollingJob: Job? = null
    private var watchdogJob: Job? = null
    private lateinit var watchdog: ListenerWatchdog

    // SINCRO CLAUDE: Umbrales de batería para modo ahorro (Tarea A)
    private var isBatteryLow = false
    private val BATTERY_THRESHOLD_LOW = 20
    private val BATTERY_THRESHOLD_NORMAL = 25

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
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    inner class LocalBinder : Binder() {
        fun getService(): LogPoseCallService = this@LogPoseCallService
    }

    override fun onCreate() {
        super.onCreate()
        // SINCRO CLAUDE: Detectamos reinicio inesperado por batería
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

        AppContainer.voskEngine.setAttributionContext(attributionContext)

        telecom = com.uriel.logpose.core.telecom.LogPoseTelecom(
            attributionContext,
            attributionContext.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        )
        
        scoStateManager = ScoStateManager(this, attributionContext.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager) {
            // Callback de reconexión
            if (isTripActive) {
                LogPoseLogger.i("Service: Gatillando reconexión SCO automática.")
                AppContainer.communicationManager.hammerScoConnection()
            }
        }
        
        com.uriel.logpose.features.music.MusicManager.initialize(attributionContext)
        AppContainer.communicationManager.updateContext(attributionContext)

        batteryMonitor = BatteryMonitor(attributionContext)
        watchdog = ListenerWatchdog(attributionContext)
        registerHeadsetReceiver()
        scoStateManager.startMonitoring()
        
        // SINCRO CLAUDE: Solicitamos ignorar optimizaciones de batería para asegurar supervivencia
        PowerManagerHelper.requestIgnoreBatteryOptimizations(this)

        FlightRecorder.initialize(this)
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
                
                // SINCRO: Garantizar canales en S8
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

                onSystemsReady()
                updateNotification("Viaje activo")
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
            NavigationManager.stopNavigation()
            AlertManager.enqueue("Viaje finalizado.")

            com.uriel.logpose.thamis.ThamisAssistant.stop()
            batteryPollingJob?.cancel()
            watchdogJob?.cancel()
            ComfortNoiseManager.stop()
            AppContainer.communicationManager.stopCommunication()

            removeOverlay()
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

    private fun onSystemsReady() {
        createInvisibleOverlay()
        
        // SINCRO: Enviar "Hola" al PC para aparecer en la lista del laboratorio
        com.uriel.logpose.core.network.PCBridge.sendCommand("RIDER_ONLINE:¡Listo para el reparto!")

        val isScoActive = com.uriel.logpose.core.app.AppContainer.communicationManager.isScoActive.value
        ComfortNoiseManager.start(attributionContext, isScoActive)
        
        // SINCRO CLAUDE: El Service ya no intenta llamar métodos estáticos.
        // Toda la lógica de voz está encapsulada en ThamisAssistant + VoskEngine
        com.uriel.logpose.thamis.ThamisAssistant.start(attributionContext)

        startBatteryPolling()
        startWatchdog()
    }

    private fun startWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = serviceScope.launch {
            while (isActive) {
                delay(60_000L) // SINCRO: Aumentamos a 1 minuto para menos spam
                // SINCRO CLAUDE: Si el servicio está vivo, refrescamos el heartbeat
                if (LogPoseNotificationListener.getInstance() != null) {
                    LogPoseNotificationListener.updateHeartbeat()
                }
                val lastHeartbeat = LogPoseNotificationListener.getLastHeartbeat()
                watchdog.checkAndHandle(lastHeartbeat)
            }
        }
    }

    private fun createInvisibleOverlay() {
        if (overlayView != null) return
        
        // SINCRO CLAUDE: Verificamos permiso para evitar BadTokenException
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(this)) {
            LogPoseLogger.w("Service: No hay permiso SYSTEM_ALERT_WINDOW. Omitiendo overlay.")
            return
        }

        try {
            // SINCRO CLAUDE: Usamos attributionContext para el WindowManager también
            windowManager = attributionContext.getSystemService(WINDOW_SERVICE) as WindowManager
            overlayView = View(attributionContext)
            val params = WindowManager.LayoutParams(
                1, 1,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY 
                else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            )
            windowManager?.addView(overlayView, params)
            LogPoseLogger.i("Service: Overlay invisible creado para persistencia.")
        } catch (e: Exception) {
            LogPoseLogger.e("Service: Falló la creación del overlay: ${e.message}")
        }
    }

    private fun removeOverlay() {
        try {
            overlayView?.let { windowManager?.removeView(it) }
            overlayView = null
        } catch (e: Exception) {
            LogPoseLogger.e("Service: Error al remover overlay: ${e.message}")
        }
    }

    fun dismissBanner() { _bannerText.value = null }

    private fun startBatteryPolling() {
        batteryPollingJob?.cancel()
        batteryPollingJob = serviceScope.launch {
            while (isActive) {
                val phonePct = batteryMonitor.phoneBatteryPct
                updateNotification("Viaje activo — Tel: $phonePct%")
                
                // SINCRO CLAUDE: Lógica de modo ahorro con histéresis (Tarea A)
                if (!isBatteryLow && phonePct <= BATTERY_THRESHOLD_LOW) {
                    isBatteryLow = true
                    LogPoseLogger.w("ThamisBattery: Entrando en modo BATERÍA BAJA ($phonePct%)")
                    AppContainer.voskEngine.setPowerSaveMode(true)
                } else if (isBatteryLow && phonePct >= BATTERY_THRESHOLD_NORMAL) {
                    isBatteryLow = false
                    LogPoseLogger.i("ThamisBattery: Restaurando modo NORMAL ($phonePct%)")
                    AppContainer.voskEngine.setPowerSaveMode(false)
                }

                delay(60000L)
            }
        }
    }

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
            .setPriority(NotificationCompat.PRIORITY_LOW) // SINCRO: LOW para evitar que "salte" en pantalla todo el tiempo
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
        headsetReceiver?.let { unregisterReceiver(it) }
        removeOverlay()
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
