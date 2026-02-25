package com.app.astrojournal.utils

import io.github.cosinekitty.astronomy.*
import java.util.*
import kotlin.math.*

/**
 * Calculadora de fases lunares y eventos astronómicos usando la librería astronomy-engine.
 * Esta implementación proporciona una precisión mucho mayor que las fórmulas manuales.
 */
object MoonCalculator {
    
    /**
     * Calcula la información de la luna para una fecha y ubicación específicas.
     * @param date Fecha para el cálculo.
     * @return MoonPhaseInfo con datos precisos de fase, iluminación y edad.
     */
    fun getMoonPhaseInfo(date: Date = Date()): MoonPhaseInfo {
        // En la versión nativa de Kotlin, Time se crea desde milisegundos
        val time = Time.fromMillisecondsSince1970(date.time)
        
        // Las funciones en Kotlin son de nivel superior (top-level), sin prefijo Astronomy
        val phaseDeg = moonPhase(time)
        
        // Calcular la iluminación (0-100%)
        val illum = illumination(Body.Moon, time)
        
        // Identificar el nombre de la fase basándose en los grados
        val phaseName = getMoonPhaseNameFromDegrees(phaseDeg)
        
        // La "edad" de la luna es una aproximación basada en el ciclo de 29.53 días
        val moonAge = (phaseDeg / 360.0) * 29.53
        
        return MoonPhaseInfo(
            moonAge = moonAge,
            illumination = (illum.phaseFraction * 100).toInt(), // Usar phaseFraction (camelCase)
            phaseName = phaseName,
            date = date
        )
    }

    /**
     * Determina el nombre de la fase lunar basándose en el ángulo de fase.
     * 0 = Luna Nueva, 90 = Cuarto Creciente, 180 = Luna Llena, 270 = Cuarto Menguante.
     */
    private fun getMoonPhaseNameFromDegrees(degrees: Double): String {
        return when {
            degrees < 11.25 || degrees > 348.75 -> "New Moon"
            degrees < 78.75 -> "Waxing Crescent"
            degrees < 101.25 -> "First Quarter"
            degrees < 168.75 -> "Waxing Gibbous"
            degrees < 191.25 -> "Full Moon"
            degrees < 258.75 -> "Waning Gibbous"
            degrees < 281.25 -> "Last Quarter"
            else -> "Waning Crescent"
        }
    }
}

/**
 * Clase de datos que contiene información de la fase lunar.
 */
data class MoonPhaseInfo(
    val moonAge: Double,           // Edad aproximada de la luna en días
    val illumination: Int,         // Porcentaje de iluminación (0-100)
    val phaseName: String,         // Nombre de la fase (ej: "Full Moon")
    val date: Date                 // Fecha para la cual se calculó
)

