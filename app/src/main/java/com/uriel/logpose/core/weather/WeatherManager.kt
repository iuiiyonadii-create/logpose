package com.uriel.logpose.core.weather

import com.uriel.logpose.core.services.AlertManager
import com.uriel.logpose.core.services.AlertPriority
import com.uriel.logpose.core.compat.core.LogPoseLogger

/**
 * Sector 6.1: Cerebro Meteorológico.
 * Por ahora usamos datos simulados para feedback inmediato, 
 * escalable a OpenWeatherMap API.
 */
object WeatherManager {

    fun reportCurrentWeather() {
        LogPoseLogger.i("WeatherManager: Generando reporte...")
        
        // Simulación de datos (a conectar con API en fase final)
        val temp = 22
        val condition = "soleado"
        val rainChance = 5
        
        val report = "Hacen $temp grados y está $condition. El riesgo de lluvia es del $rainChance por ciento. ¡Ideal para rutear!"
        
        AlertManager.enqueue(report, AlertPriority.NORMAL)
    }
    
    fun checkRainWarning() {
        // Podría dispararse solo si el rainChance > 70%
    }
}
