package com.app.astrojournal.utils

import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.data.model.EventType
import io.github.cosinekitty.astronomy.*
import java.text.SimpleDateFormat
import java.util.*

object AstroEventCalculator {

    fun getUpcomingEvents(limit: Int = 15): List<AstroEvent> {
        val events = mutableListOf<AstroEvent>()
        val startTime = Time.fromMillisecondsSince1970(Date().time)
        val currentYear = startTime.toDateTime().year
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val yearsToSearch = listOf(currentYear, currentYear + 1)
        yearsToSearch.forEach { year ->
            val s = seasons(year)
            listOf(
                Triple("Equinoccio de Marzo", "Inicio de la primavera (N) / otoño (S)", s.marchEquinox),
                Triple("Solsticio de Junio", "Inicio del verano (N) / invierno (S)", s.juneSolstice),
                Triple("Equinoccio de Septiembre", "Inicio del otoño (N) / primavera (S)", s.septemberEquinox),
                Triple("Solsticio de Diciembre", "Inicio del invierno (N) / verano (S)", s.decemberSolstice)
            ).forEach { (name, desc, time) ->
                if (time.toMillisecondsSince1970() > startTime.toMillisecondsSince1970()) {
                    events.add(AstroEvent(name, desc, df.format(Date(time.toMillisecondsSince1970())), time.toMillisecondsSince1970(), EventType.OTHER))
                }
            }
        }

        val solar = searchGlobalSolarEclipse(startTime)
        events.add(AstroEvent("Eclipse Solar", "Eclipse solar global (${solar.kind})", df.format(Date(solar.peak.toMillisecondsSince1970())), solar.peak.toMillisecondsSince1970(), EventType.CONJUNCTION))
        
        val lunar = searchLunarEclipse(startTime)
        events.add(AstroEvent("Eclipse Lunar", "Eclipse lunar (${lunar.kind})", df.format(Date(lunar.peak.toMillisecondsSince1970())), lunar.peak.toMillisecondsSince1970(), EventType.MOON_PHASE))

        listOf(Body.Mars, Body.Jupiter, Body.Saturn).forEach { planet ->
            val opp = searchRelativeLongitude(planet, 0.0, startTime)
            val planetNameEs = when(planet) {
                Body.Mars -> "Marte"
                Body.Jupiter -> "Júpiter"
                Body.Saturn -> "Saturno"
                else -> planet.name
            }
            events.add(AstroEvent("Oposición de $planetNameEs", "El planeta $planetNameEs está en su punto más cercano a la Tierra", df.format(Date(opp.toMillisecondsSince1970())), opp.toMillisecondsSince1970(), EventType.PLANET))
        }

        try {
            val venusPeak = searchPeakMagnitude(Body.Venus, startTime)
            events.add(AstroEvent("Máximo brillo de Venus", "Máximo brillo de Venus en el cielo", df.format(Date(venusPeak.time.toMillisecondsSince1970())), venusPeak.time.toMillisecondsSince1970(), EventType.PLANET))
        } catch (e: Exception) { }

        return events.sortedBy { it.timestamp }.take(limit)
    }
}
