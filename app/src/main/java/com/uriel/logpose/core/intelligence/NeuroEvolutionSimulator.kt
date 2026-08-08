package com.uriel.logpose.core.intelligence

import com.uriel.logpose.core.app.LogPoseApplication
import com.uriel.logpose.core.parser.PhoneticDictionary
import com.uriel.logpose.core.services.LogPoseHudService
import com.uriel.logpose.thamis.learning.LearningEngine
import com.uriel.logpose.features.voice.MusicVocabulary
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
    
    private val dictionary by lazy { PhoneticDictionary(LogPoseApplication.instance) }

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

    private val dailyWordsPool = listOf(
        "amigazo", "ndeah", "buenardo", "nashe", "mandale mecha", "de ruta", "re cheto", "posta",
        "que hora es", "como esta el clima", "buen dia", "buenas noches", "chau", "hasta luego",
        "tengo hambre", "que puedo comer", "llamame mas tarde", "recordame comprar pan",
        "música", "estación", "película", "español", "niño", "chamba", "laburo", "bondi"
    )

    fun startInfiniteTraining() {
        if (training_job_active()) return
        
        android.util.Log.i("LogPose", "ENSAYO: INICIANDO GIMNASIO SINGULARIDAD v22.22")
        
        trainingJob = scope.launch {
            var cycle = 1
            while (isActive) {
                MusicVocabulary.clearCache()
                
                val dictionaryFull = (dictionary.listaDe("apps") + 
                                     dictionary.listaDe("comandos") +
                                     dictionary.listaDe("musica.artistas") +
                                     dictionary.listaDe("modismos")).distinct()

                // Alturas de graduación para Nivel 10
                val graduationHeights = listOf(100, 500, 1200, 2500, 4000, 8000, 11000)
                val graduationStreets = mainStreets.map { street -> "$street ${graduationHeights.random()}" }

                val fullPool = (dictionaryFull + dailyWordsPool + graduationStreets).distinct()
                val needLearning = fullPool.filter { !LearningEngine.isGraduated(it) }.shuffled()
                
                if (needLearning.isNotEmpty()) {
                    for (input in needLearning) {
                        if (!isActive) break
                        ejecutarExamen(input, cycle)
                        delay(35) // Velocidad Matrix
                        cycle++
                    }
                } else {
                    android.util.Log.i("LogPose", "💎 ESTATUS: Mente Maestra Urbana Alcanzada. Patrullando Argentina...")
                    LogPoseHudService.updateStatus("👑 MENTE MAESTRA")
                    
                    val patrolSample = List(20) { "${mainStreets.random()} ${Random.nextInt(100, 15000)}" }
                    for (input in patrolSample) {
                        if (!isActive) break
                        ejecutarExamen(input, cycle)
                        delay(1000) 
                        cycle++
                    }
                    delay(5000) 
                }
            }
        }
    }

    private suspend fun ejecutarExamen(input: String, cycle: Int) {
        val maturity = LearningEngine.getMaturityLevel(input)
        val expected = applyStaffLogic(input)
        val currentTranslation = MusicVocabulary.normalize(input)
        
        fun String.dna(): String {
            return this.lowercase()
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                .replace("ü", "u").replace("ñ", "n")
                .replace(Regex("[^a-z0-9]"), "")
        }
        
        val targetDna = expected.dna()
        val heardDna = currentTranslation.dna()
        
        val isGraduated = maturity >= 10
        val testSuccess = heardDna == targetDna || (targetDna.length > 5 && heardDna.contains(targetDna))

        if (testSuccess) {
            if (!isGraduated) {
                android.util.Log.i("LogPose", "LAB_EXITO #$cycle: '$input' -> '$currentTranslation' [Nivel $maturity/10]")
            }
            LearningEngine.updateMaturity(input, true)
        } else {
            android.util.Log.e("LogPose", "LAB_FALLO #$cycle: '$input' dio '$currentTranslation' (Se esperaba '$expected')")
            android.util.Log.d("LogPose", "   [DNA Match] target='$targetDna' heard='$heardDna'")
            
            LearningEngine.learn(input, expected, com.thamis.lab.core.contracts.intent.Intent.OPEN_APP)
            LearningEngine.updateMaturity(input, false) 
            delay(50)
        }
    }

    private fun training_job_active(): Boolean = trainingJob?.isActive == true

    private fun applyStaffLogic(input: String): String {
        val base = input.lowercase()
            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
            .replace("ü", "u").replace("ñ", "n")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        
        var result = base
        for (avenue in mainStreets.sortedByDescending { it.length }) {
            if (base.contains(avenue)) {
                if (!base.contains("avenida") && !base.contains("diagonal") && !base.contains("pasaje")) {
                    result = "avenida $base"
                } else if (base.contains("calle ")) {
                    result = base.replace("calle ", "avenida ")
                }
                break
            }
        }

        fun has(pattern: String) = result.contains(pattern, ignoreCase = true)
        fun isExact(pattern: String) = result == pattern.lowercase()

        return when {
            isExact("recoleta") -> "recoleta"
            isExact("palermo") -> "palermo"
            isExact("belgrano") -> "belgrano"
            isExact("caballito") -> "caballito"
            has("mecha") -> "mandale mecha"
            has("tifani") || has("tifi") -> "tiffany"
            has("morfar") || has("hambre") -> "donde comer"
            has("ypf") || has("shell") || has("axion") || has("nafta") -> "estacion de servicio"
            result == "rock" -> "rock"
            result == "malbe" -> "malbec"
            has("youtube") || has("yutu") || has("yt music") || has("yu tub") -> "youtube music"
            (has("duq") || has("duque") || has("doce") || has("cole")) && !has("recoleta") && !has("nicki") -> "duki"
            has("true") || has("pudi") -> "trueno"
            has("pisa") || has("viza") -> "bizarrap"
            else -> result
        }
    }

    fun stopTraining() {
        trainingJob?.cancel()
        LogPoseHudService.updateStatus("THAMIS: LISTO")
        LogPoseHudService.showDebug(null)
    }
}
