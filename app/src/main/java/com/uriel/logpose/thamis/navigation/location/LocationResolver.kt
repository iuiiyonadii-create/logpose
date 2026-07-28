package com.uriel.logpose.thamis.navigation.location

import java.util.Locale

/**
 * Intérprete de lenguaje natural para consultas de ubicación.
 */
object LocationResolver {
    fun resolve(input: String): LocationIntent {
        val query = input.lowercase(Locale.getDefault()).trim()
        
        return when {
            query.contains("donde estoy") || query.contains("dónde estoy") || 
            query.contains("donde me encuentro") || query.contains("dónde me encuentro") -> LocationIntent.WHERE_AM_I
            
            query.contains("ciudad") -> LocationIntent.WHAT_CITY
            
            query.contains("calle") -> LocationIntent.WHAT_STREET
            
            query.contains("gasolinera") || query.contains("estacion") || 
            query.contains("estación") || query.contains("combustible") ||
            query.contains("nafta") -> LocationIntent.NEAREST_GAS
            
            query.contains("estacionamiento") || query.contains("parqueo") || 
            query.contains("donde dejar la moto") -> LocationIntent.NEAREST_PARKING
            
            query.contains("hospital") || query.contains("medico") || 
            query.contains("médico") || query.contains("clinica") || 
            query.contains("clínica") -> LocationIntent.NEAREST_HOSPITAL
            
            query.contains("policia") || query.contains("comisaria") || 
            query.contains("policía") || query.contains("comisaría") -> LocationIntent.NEAREST_POLICE
            
            else -> LocationIntent.UNKNOWN
        }
    }
}
