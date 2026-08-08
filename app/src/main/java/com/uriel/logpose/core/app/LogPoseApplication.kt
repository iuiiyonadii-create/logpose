package com.uriel.logpose.core.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.uriel.logpose.core.compat.core.LogPoseLogger
import com.uriel.logpose.core.di.AppEntryPoint
import com.uriel.logpose.domain.repositories.VoiceRepository
import com.uriel.logpose.features.voice.VoskVoiceEngine
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * LogPose Application
 * Initializes Hilt and system-wide components.
 */
@HiltAndroidApp
class LogPoseApplication : Application() {

    @Inject lateinit var voiceRepository: VoiceRepository
    @Inject lateinit var voskEngine: VoskVoiceEngine
    
    // Arquitectura ALF-R v4.5 + Sherpa Local
    val anchorRepository = com.uriel.logpose.core.engine.AnchorRepository()
    lateinit var contextualResolver: com.uriel.logpose.core.engine.ContextualIntentResolver
    lateinit var sherpaEngine: com.uriel.logpose.core.speech.SherpaSpeechEngine
    private var contactsObserver: com.uriel.logpose.core.observers.ContactsAnchorObserver? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        contextualResolver = com.uriel.logpose.core.engine.ContextualIntentResolver(anchorRepository)
        sherpaEngine = com.uriel.logpose.core.speech.SherpaSpeechEngine(this)
        
        try {
            // 1. Inicializaciones asíncronas pesadas (Performance Optimization)
            val appScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            appScope.launch {
                sherpaEngine.initEngine()
                com.uriel.logpose.features.voice.FeedbackManager.initialize(this@LogPoseApplication)
                com.uriel.logpose.features.music.MusicManager.initialize(this@LogPoseApplication)
                com.uriel.logpose.core.parser.LabDiscoveryService.start()
                com.uriel.logpose.features.voice.VoiceManager.initialize(this@LogPoseApplication, voiceRepository)
                com.uriel.logpose.features.voice.CallManager.initialize(this@LogPoseApplication)
                
                com.uriel.logpose.thamis.learning.LearningEngine.initialize(this@LogPoseApplication)
                
                // v1.1: Limpieza de Misión #026 (Fix Secuestro Uzbekistan)
                com.uriel.logpose.thamis.learning.LearningEngine.forget("con ubekistan ponle ube")
                com.uriel.logpose.thamis.learning.LearningEngine.forget("con ubekistán ponle ube")
                
                // Misión #021.4: Hidratación ALF-R v4.5
                contactsObserver = com.uriel.logpose.core.observers.ContactsAnchorObserver(
                    this@LogPoseApplication, anchorRepository, appScope
                ).apply { register() }
                
                hydrateMusicAnchors()
                hydrateNavigationAnchors()
                
                // v5.0: Sherpa-ONNX se inicializa en paralelo (no bloquea Vosk)
                voskEngine.updateGrammar()
                com.uriel.logpose.core.nlp.LanguageRepository.initialize(this@LogPoseApplication)
                
                com.uriel.logpose.thamis.navigation.provider.NavigationProviderFactory.registerProvider(
                    com.uriel.logpose.thamis.navigation.provider.GoogleMapsProvider()
                )
                LogPoseLogger.i("LogPoseApplication: Sistemas de fondo listos (ALF-R v4.5 Active).")
                checkOrphanedSession()
            }
        } catch (e: Exception) {
            LogPoseLogger.e("LogPoseApplication: Error crítico en inicialización: ${e.message}")
        }

        createNotificationChannels()
    }

    private fun hydrateMusicAnchors() {
        val musicList = mutableListOf<Pair<String, com.uriel.logpose.core.engine.AnchorDomain>>()
        com.uriel.logpose.features.voice.MusicVocabulary.getAllArtists().forEach { 
            musicList.add(it to com.uriel.logpose.core.engine.AnchorDomain.MUSIC_ARTIST) 
        }
        com.uriel.logpose.features.voice.MusicVocabulary.getAllSongs().forEach { 
            musicList.add(it to com.uriel.logpose.core.engine.AnchorDomain.MUSIC_TRACK) 
        }
        anchorRepository.updateMusicEntities(musicList)
    }

    private fun hydrateNavigationAnchors() {
        val destinations = mutableListOf<String>()
        // 1. Destinos comunes del glosario
        destinations.addAll(com.uriel.logpose.core.parser.PhoneticDictionary(this).listaDe("navegacion.destinos_comunes"))
        
        // 2. Las 100 arterias principales (Inyectadas desde el Core de Inteligencia)
        // Usamos reflection o acceso directo si es accesible
        val streets = listOf(
            "corrientes", "rivadavia", "mayo", "9 de julio", "callao", "entre rios", "belgrano", "independencia",
            "santa fe", "cordoba", "pueyrredon", "las heras", "libertador", "alem", "paseo colon", "san juan",
            "cabildo", "juramento", "congreso", "monroe", "balbin", "constituyentes", "incas", "del tejar",
            "garcia del rio", "comodoro rivadavia", "cantilo", "lugones", "dorrego", "bullrich", "figueroa alcorta",
            "salguero", "jeronimo salguero", "medrano", "mario bravo", "billinghurst", "austria", "tagle",
            "scalabrini ortiz", "juan b justo", "san martin", "warnes", "honorio pueyrredon", "gaona", "diaz velez",
            "angel gallardo", "estado de israel", "alvarez thomas", "forest", "elcano", "triunvirato", "lacroze",
            "federico lacroze", "alvarez jonte", "beiro", "lope de vega", "segurolo", "san pedrito", "varela",
            "lafuente", "mariano acosta", "escalada", "victor hugo", "moliere", "irigoyen", "bragado", "tapalque",
            "alberti", "pichincha", "pasco", "rincon", "sarandi", "pozos", "nazca", "argerich", "cuenca", "helguera",
            "caseros", "garay", "boedo", "la plata", "directorio", "juan bautista alberdi", "asamblea", "curapaligue",
            "perito moreno", "dellepiane", "melo", "chiclana", "saenz", "amancio alcorta", "patricios", "montes de oca"
        )
        destinations.addAll(streets)
        
        anchorRepository.updateNavigationEntities(destinations.distinct())
        LogPoseLogger.i("LogPoseApplication: ${destinations.size} anclas de navegación hidratadas.")
    }

    private fun checkOrphanedSession() {
        if (com.uriel.logpose.thamis.world.engine.WorldModelEngine.restoreFromCheckpoint()) {
            val snapshot = com.uriel.logpose.thamis.world.engine.WorldModelEngine.getCurrentSnapshot()
            if (snapshot.systems.navigation.isNavigating) {
                LogPoseLogger.i("Application: Detectado viaje huérfano. Despertando Service...")
                val intent = android.content.Intent(this, com.uriel.logpose.core.services.LogPoseCallService::class.java).apply {
                    action = com.uriel.logpose.core.services.LogPoseCallService.ACTION_START_TRIP
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
        }
    }

    fun createNotificationChannels() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                val serviceChannel = NotificationChannel(
                    SERVICE_CHANNEL_ID,
                    "LogPose Active Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Used for background riding assistance"
                    setShowBadge(false)
                }
                manager.createNotificationChannel(serviceChannel)

                val tripChannelId = com.uriel.logpose.core.services.LogPoseCallService.TRIP_CHANNEL_ID
                val tripChannel = NotificationChannel(
                    tripChannelId,
                    "LogPose Trip Status",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows active trip information"
                    enableLights(false)
                }
                manager.createNotificationChannel(tripChannel)
            }
        } catch (e: Exception) {
            LogPoseLogger.e("THAMIS_LAB: Fallo en creación de canales: ${e.message}")
        }
    }

    companion object {
        const val SERVICE_CHANNEL_ID = "logpose_service_channel"
        lateinit var instance: LogPoseApplication
            private set

        val entryPoint: AppEntryPoint by lazy {
            EntryPointAccessors.fromApplication(instance, AppEntryPoint::class.java)
        }
    }
}
