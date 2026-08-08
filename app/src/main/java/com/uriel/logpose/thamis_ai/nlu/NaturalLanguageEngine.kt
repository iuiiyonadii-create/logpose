package com.uriel.logpose.thamis_ai.nlu

import com.uriel.logpose.domain.nlu.NluResult

/**
 * Orchestrator for normalization, classification and extraction.
 */
import com.uriel.logpose.thamis.normalizer.LanguageNormalizer as GlobalNormalizer

class NaturalLanguageEngine {

    private val classifier = IntentClassifier()
    private val extractor = EntityExtractor()

    fun process(text: String): NluResult {
        val cleanText = GlobalNormalizer.normalize(text)
        val intent = classifier.classify(cleanText)
        val entities = extractor.extract(cleanText)
        
        return NluResult(intent, entities)
    }
}
