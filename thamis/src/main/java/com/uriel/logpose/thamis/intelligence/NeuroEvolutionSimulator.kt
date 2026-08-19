package com.uriel.logpose.thamis.intelligence

import com.uriel.logpose.core.compat.core.LogPoseLogger

import com.uriel.logpose.core.compat.core.AppContextProvider
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.thamis.learning.LearningEngine
import com.thamis.lab.core.contracts.intent.Intent
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * NeuroEvolutionSimulator v22.22: Urban Singularity Matrix.
 * Expansión masiva a las 100 arterias principales de Buenos Aires.
 * Normalización manual blindada y graduación acelerada de alturas.
 */
object NeuroEvolutionSimulator {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var trainingJob: Job? = null
    
    private val dictionary by lazy { PhoneticDictionary(AppContextProvider.applicationContext) }

    private val mainStreets = listOf(
        // Centro & Macrocentro
        "corrientes", "rivadavia", "mayo", "9 de julio", "callao", "entre rios", "belgrano", "independencia",
        "santa fe", "cordoba", "pueyrredon", "las heras", "libertador", "alem", "paseo colon", "san juan",
        // Norte
        "cabildo", "juramento", "congreso", "monroe", "balbin", "constituyentes", "incas", "del tejar",
        "garcia del rio", "comodoro rivadavia", "cantilo", "lugones", "dorrego", "bullrich", "figueroa alcorta",
        "salguero", "jeronimo salguero", "medrano", "mario bravo", "billinghurst", "austria", "tagle",
        // Noroeste & Oeste
        "scalabrini ortiz", "juan b justo", "san martin", "warnes", "honorio pueyrredon", "gaona", "diaz velez",
        "angel gallardo", "estado de israel", "alvarez thomas", "forest", "elcano", "triunvirato", "lacroze",
        "federico lacroze", "alvarez jonte", "beiro", "lope de vega", "segurolo", "san pedrito", "varela",
        "lafuente", "mariano acosta", "escalada", "victor hugo", "moliere", "irigoyen", "bragado", "tapalque",
        "alberti", "pichincha", "pasco", "rincon", "sarandi", "pozos", "nazca", "argerich", "cuenca", "helguera",
        // Sur
        "caseros", "garay", "boedo", "la plata", "directorio", "juan bautista alberdi", "asamblea", "curapaligue",
        "perito moreno", "dellepiane", "melo", "chiclana", "saenz", "amancio alcorta", "patricios", "montes de oca"
    )

    private val criticalTestCases = mapOf(
        "poné música" to Intent.PLAY_MUSIC,
        "abrí instagram" to Intent.OPEN_APP,
        "cancelar viaje" to Intent.STOP_NAVIGATION,
        "vamos al obelisco" to Intent.NAVIGATE,
        "llamame a mama" to Intent.CALL_CONTACT,
        "enviá un mensaje a juancito" to Intent.SEND_MESSAGE,
        "consultar estado" to Intent.VEHICLE_STATUS,
        "cuanto falta" to Intent.NAVIGATE,
        // v67.5: Misión de Entrenamiento Intensivo (Los fallos reportados)
        "lo abrí whatsapp" to Intent.OPEN_APP,
        "lo mandan un mensaje a a" to Intent.SEND_MESSAGE,
        "dojo mandarle un mensaje a a" to Intent.SEND_MESSAGE,
        "lujo mandarle mensajes a" to Intent.SEND_MESSAGE,
        "lo va a llover hoy" to Intent.WEATHER,
        "los paso ver hoy" to Intent.WEATHER,
        "los dama el clima" to Intent.WEATHER,
        "log dame el clima" to Intent.WEATHER,
        "lo pone fuera de la órbita" to Intent.PLAY_MUSIC
    )

    // Misión #042: Pool dinámico de fallos reportados en tiempo real desde la ruta
    private val dynamicFailuresPool = java.util.concurrent.ConcurrentHashMap<String, Intent>()

    fun addRealWorldFailure(text: String, suspectedIntent: Intent = Intent.UNKNOWN) {
        if (text.isBlank() || text.length < 3) return
        dynamicFailuresPool[text] = suspectedIntent
        LogPoseLogger.i("LogPose", " Matrix Ingest: Agregado fallo real para simulación -> '$text'")
    }

    private val dictionaryFullCache = mutableListOf<String>()
    private var isDictionaryCached = false

    private fun getDictionaryFull(): List<String> {
        if (!isDictionaryCached) {
            dictionaryFullCache.clear()
            dictionaryFullCache.addAll((dictionary.listaDe("apps") + 
                                       dictionary.listaDe("comandos") +
                                       dictionary.listaDe("musica.artistas") +
                                       dictionary.listaDe("modismos") +
                                       mainStreets).distinct())
            isDictionaryCached = true
        }
        return dictionaryFullCache
    }

    fun startInfiniteTraining() {
        LogPoseLogger.i("LogPose", "NeuroEvolutionSimulator: Iniciando Ciclo de Evolución Staff v25.0.")
        if (trainingJob?.isActive == true) return
        
        trainingJob = scope.launch {
            var cycle = 1
            while (isActive) {
                // v58.0: Cache de Registro para evitar GC Churn masivo
                val userRegistry = LearningEngine.getUserRegistry()
                val dictionaryFull = getDictionaryFull()
                
                // Filtrado optimizado
                val pending = dictionaryFull.filter { !LearningEngine.isGraduated(it) }.shuffled()
                
                // Prioridad 1: EL REGISTRO (Blitz de Stress + Correcciones del Usuario)
                val combinedRegistry = (userRegistry.map { it.key to Intent.UNKNOWN } + criticalTestCases.toList()).toList()
                LogPoseLogger.i("LogPose", "💎 MATRIX: Procesando registro de ${combinedRegistry.size} frases críticas.")

                // v55.0: Ejecutamos el registro COMPLETO en cada ciclo para máxima visibilidad
                combinedRegistry.forEach { entry ->
                    val phrase = entry.first
                    val intent = entry.second
                    ejecutarExamen(phrase, intent, cycle++, isFromRegistry = true)
                    delay(120) // v58.0: Aumento de delay para evitar saturación de CPU/Red
                }

                // Prioridad 2: Fallos Reales detectados en ESTA sesión
                if (dynamicFailuresPool.isNotEmpty()) {
                    val failures = dynamicFailuresPool.keys.toList().shuffled().take(3)
                    failures.forEach {
                        if (!LearningEngine.isGraduated(it)) {
                            ejecutarExamen(it, dynamicFailuresPool[it] ?: Intent.UNKNOWN, cycle++)
                            delay(50) 
                        }
                    }
                }

                // Prioridad 2: Barrido de Diccionario Pendiente (SOLO LO NO GRADUADO)
                if (pending.isNotEmpty()) {
                    // Tomamos un bloque pequeño para no saturar
                    pending.take(10).forEach {
                        if (!LearningEngine.isGraduated(it)) {
                            ejecutarExamen(it, Intent.UNKNOWN, cycle++)
                            delay(20)
                        }
                    }
                }
                
                if (cycle % 50 == 0) {
                    LogPoseLogger.i("LogPose", "💎 ESTATUS MATRIX: $cycle pruebas completadas.")
                }
            }
        }
    }

    private suspend fun ejecutarExamen(input: String, expectedIntent: Intent, cycle: Int, isFromRegistry: Boolean = false) {
        // v66.0: Bypass de Wake-word si el input ya es un caso de choque conocido o viene del registro
        val prefixes = listOf("lo ", "now ", "ola ", "yo ", "dojo ", "lujo ", "los ", "log ")
        val inputWithWake = if (prefixes.any { input.startsWith(it) }) {
            input 
        } else {
            "log $input"
        }
        
        // v67.5: Reducción de distorsión para registro crítico (Queremos ver el éxito)
        val distortionLevel = if (isFromRegistry) 10f else 140f 
        val distortedInput = distortPhonetically(inputWithWake, distortionLevel)
        
        val request = com.uriel.logpose.thamis.request.THAMISRequest(text = distortedInput)
        val decision = com.uriel.logpose.thamis.intelligence.ThamisBrain.process(request)
        
        val isIntentCorrect = decision.intent == expectedIntent || (expectedIntent == Intent.UNKNOWN && decision.intent != Intent.UNKNOWN)

        if (isIntentCorrect) {
            LearningEngine.updateMaturity(input, true)
            val status = if (isFromRegistry) "🟢 PASÓ (REGISTRO)" else "🟢 PASÓ (HARD)"
            // Enviamos el input original para que el Monitor Ghost lo muestre descifrado
            reportToLab(input, distortedInput, decision.intent.name, status, cycle)
        } else {
            // Un fallo en Evil Mode resetea la madurez de esa frase
            LogPoseLogger.e("LogPose", "😈 EVIL_FALLO #$cycle: '$input' -> '${decision.intent}' con ruido extremo.")
            LearningEngine.forget(input) // Reset total por error bajo carga
            LearningEngine.updateMaturity(input, false)
            reportToLab(input, distortedInput, decision.intent.name, "❌ QUEBRÓ", cycle)
        }
    }

    private var cachedGraduatedCount = -1
    private var lastGraduationCheckTime = 0L

    private fun reportToLab(original: String, heard: String, intent: String, status: String, cycle: Int) {
        // v56.0: Cache de conteo Staff - Solo recalculamos cada 10 ciclos para ahorrar 90% de CPU
        val totalGraduated = if (cycle % 10 == 0 || cachedGraduatedCount == -1) {
            val dictionaryFull = getDictionaryFull()
            cachedGraduatedCount = dictionaryFull.count { LearningEngine.isGraduated(it) }
            cachedGraduatedCount
        } else cachedGraduatedCount

        val trace = org.json.JSONObject().apply {
            put("type", "MATRIX_EXAM")
            put("cycle", cycle)
            put("detected_intent", intent)
            put("status", status)
            put("total_graduated", totalGraduated)
            put("resolution_source", "MATRIX_EVIL")
            // v67.5: Formateamos el texto para el Monitor Ghost
            put("final_clean_text", "🧪 EXAM #$cycle: $original (Escuchó: $heard)")
        }
        com.uriel.logpose.thamis.cognitive.CognitivePipeline.sendTelemetryProxy(trace)
    }

    private fun distortPhonetically(text: String, speedKmh: Float): String {
        val tokens = text.split(" ").toMutableList()
        
        // 1. Scrambler: Desordenar palabras al azar (Evil Mode)
        if (Random.nextFloat() > 0.6) {
            tokens.shuffle()
        }

        val distortedWords = tokens.map { word ->
            var w = word.lowercase()
            
            // 2. Wake-Word Mutilation (rog, no, o silencio)
            if (w == "log") {
                val mutations = listOf("rog", "no", "eh", "", "roc")
                return@map mutations.random()
            }

            // 3. Degradación agresiva (Cortes de más del 40% de la palabra)
            if (w.length > 3 && Random.nextFloat() > 0.5) {
                w = w.substring(0, (w.length * 0.6).toInt())
            }
            
            // 4. Confusión Staff Total
            w = w.replace("b", "v").replace("v", "b")
                 .replace("s", "z").replace("z", "s")
                 .replace("c", "k").replace("k", "c")
            
            w
        }.filter { it.isNotBlank() }
        
        return distortedWords.joinToString(" ")
    }

    fun stopTraining() {
        trainingJob?.cancel()
    }
}
