package com.uriel.logpose.core.services

interface FeedbackSpeaker {
    fun speak(text: String, onComplete: () -> Unit = {})
}

object FeedbackDelegator : FeedbackSpeaker {
    var speaker: FeedbackSpeaker? = null

    override fun speak(text: String, onComplete: () -> Unit) {
        speaker?.speak(text, onComplete) ?: onComplete()
    }
}

interface MediaDucker {
    fun duck()
    fun unduck()
}

object MediaDuckDelegator : MediaDucker {
    var ducker: MediaDucker? = null

    override fun duck() {
        ducker?.duck()
    }

    override fun unduck() {
        ducker?.unduck()
    }
}

interface BluetoothEventsListener {
    fun onBluetoothConnected(device: com.uriel.logpose.domain.models.LogPoseDevice)
    fun onBluetoothDisconnected(device: com.uriel.logpose.domain.models.LogPoseDevice)
    fun onAssistantTriggered(context: android.content.Context)
}

object BluetoothEventsDelegator : BluetoothEventsListener {
    var listener: BluetoothEventsListener? = null

    override fun onBluetoothConnected(device: com.uriel.logpose.domain.models.LogPoseDevice) {
        listener?.onBluetoothConnected(device)
    }

    override fun onBluetoothDisconnected(device: com.uriel.logpose.domain.models.LogPoseDevice) {
        listener?.onBluetoothDisconnected(device)
    }

    override fun onAssistantTriggered(context: android.content.Context) {
        listener?.onAssistantTriggered(context)
    }
}

interface SpotifyControllerProvider {
    fun getSpotifyController(context: android.content.Context): android.media.session.MediaController?
    fun tryForceRebind(context: android.content.Context)
}

object SpotifyControllerDelegator : SpotifyControllerProvider {
    var provider: SpotifyControllerProvider? = null

    override fun getSpotifyController(context: android.content.Context): android.media.session.MediaController? {
        return provider?.getSpotifyController(context)
    }

    override fun tryForceRebind(context: android.content.Context) {
        provider?.tryForceRebind(context)
    }
}

interface MusicEntityLearner {
    fun learnMusicEntity(entity: String)
    fun addFavoriteArtist(artist: String)
    fun learnTrackArtistRelation(track: String, artist: String)
    fun learnPlaylist(playlist: String)
    fun learnVectorMemory(title: String, description: String)
}

object MusicEntityDelegator : MusicEntityLearner {
    var learner: MusicEntityLearner? = null

    override fun learnMusicEntity(entity: String) {
        learner?.learnMusicEntity(entity)
    }

    override fun addFavoriteArtist(artist: String) {
        learner?.addFavoriteArtist(artist)
    }

    override fun learnTrackArtistRelation(track: String, artist: String) {
        learner?.learnTrackArtistRelation(track, artist)
    }

    override fun learnPlaylist(playlist: String) {
        learner?.learnPlaylist(playlist)
    }

    override fun learnVectorMemory(title: String, description: String) {
        learner?.learnVectorMemory(title, description)
    }
}

interface CommandExecutor {
    fun execute(command: com.thamis.lab.core.contracts.command.LogPoseCommand)
}

object CommandExecutorDelegator : CommandExecutor {
    var executor: CommandExecutor? = null

    override fun execute(command: com.thamis.lab.core.contracts.command.LogPoseCommand) {
        executor?.execute(command)
    }
}

interface LabDiscoveryProvider {
    fun getPcIp(): String?
}

object LabDiscoveryDelegator : LabDiscoveryProvider {
    var provider: LabDiscoveryProvider? = null

    override fun getPcIp(): String? {
        return provider?.getPcIp()
    }
}

interface WorldDiagnosticsListener {
    fun checkDiagnostics(snapshotJson: String)
    fun updateHud(snapshotJson: String)
}

object WorldDiagnosticsDelegator : WorldDiagnosticsListener {
    var listener: WorldDiagnosticsListener? = null

    override fun checkDiagnostics(snapshotJson: String) {
        listener?.checkDiagnostics(snapshotJson)
    }

    override fun updateHud(snapshotJson: String) {
        listener?.updateHud(snapshotJson)
    }
}

interface MusicNormalizer {
    fun normalize(input: String): String
    fun isKnown(query: String): Boolean
}

object MusicNormalizerDelegator : MusicNormalizer {
    var normalizer: MusicNormalizer? = null

    override fun normalize(input: String): String {
        return normalizer?.normalize(input) ?: input.lowercase().trim()
    }

    override fun isKnown(query: String): Boolean {
        return normalizer?.isKnown(query) ?: false
    }
}

data class RecognizedVoiceCommand(
    val text: String,
    val confidence: Float = 1.0f,
    val durationMs: Long = 0L
)

interface VoiceEngineProvider {
    fun start()
    fun stop()
    fun getAmbientNoiseLevel(): Float
    fun getRecentAudioBuffer(seconds: Int): ShortArray
    fun observeCommands(): kotlinx.coroutines.flow.Flow<RecognizedVoiceCommand>
}

object VoiceEngineDelegator : VoiceEngineProvider {
    var provider: VoiceEngineProvider? = null

    override fun start() {
        provider?.start()
    }

    override fun stop() {
        provider?.stop()
    }

    override fun getAmbientNoiseLevel(): Float {
        return provider?.getAmbientNoiseLevel() ?: 0.0f
    }

    override fun getRecentAudioBuffer(seconds: Int): ShortArray {
        return provider?.getRecentAudioBuffer(seconds) ?: ShortArray(0)
    }

    override fun observeCommands(): kotlinx.coroutines.flow.Flow<RecognizedVoiceCommand> {
        return provider?.observeCommands() ?: kotlinx.coroutines.flow.emptyFlow()
    }
}

interface AudioTranscriptionProvider {
    suspend fun transcribeWhisper(pcmData: ShortArray): String?
    suspend fun transcribeSherpa(pcmData: ShortArray): String
}

object AudioTranscriptionDelegator : AudioTranscriptionProvider {
    var provider: AudioTranscriptionProvider? = null

    override suspend fun transcribeWhisper(pcmData: ShortArray): String? {
        return provider?.transcribeWhisper(pcmData)
    }

    override suspend fun transcribeSherpa(pcmData: ShortArray): String {
        return provider?.transcribeSherpa(pcmData) ?: ""
    }
}

interface DriverProfileProvider {
    fun getPreferredGasStation(): String
}

object DriverProfileDelegator : DriverProfileProvider {
    var provider: DriverProfileProvider? = null

    override fun getPreferredGasStation(): String {
        return provider?.getPreferredGasStation() ?: "YPF"
    }
}
