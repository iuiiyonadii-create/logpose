package com.uriel.logpose.thamis_ai.nlu

import com.uriel.logpose.domain.nlu.NluResult

/**
 * Orchestrator for normalization, classification and extraction.
 */
class NaturalLanguageEngine {

    private val normalizer = LanguageNormalizer()
    private val classifier = IntentClassifier()
    private val extractor = EntityExtractor()

    fun process(text: String): NluResult {
        val cleanText = normalizer.normalize(text)
        val intent = classifier.classify(cleanText)
        val entities = extractor.extract(cleanText)
        
        return NluResult(intent, entities)
    }
}
