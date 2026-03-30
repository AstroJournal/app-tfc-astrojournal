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
                Triple("March Equinox", "Start of Spring (N) / Autumn (S)", s.marchEquinox),
                Triple("June Solstice", "Start of Summer (N) / Winter (S)", s.juneSolstice),
                Triple("September Equinox", "Start of Autumn (N) / Spring (S)", s.septemberEquinox),
                Triple("December Solstice", "Start of Winter (N) / Summer (S)", s.decemberSolstice)
            ).forEach { (name, desc, time) ->
                if (time.toMillisecondsSince1970() > startTime.toMillisecondsSince1970()) {
                    events.add(AstroEvent(name, desc, df.format(Date(time.toMillisecondsSince1970())), time.toMillisecondsSince1970(), EventType.OTHER))
                }
            }
        }

        val solar = searchGlobalSolarEclipse(startTime)
        events.add(AstroEvent("Solar Eclipse", "Global solar eclipse (${solar.kind})", df.format(Date(solar.peak.toMillisecondsSince1970())), solar.peak.toMillisecondsSince1970(), EventType.CONJUNCTION))
        
        val lunar = searchLunarEclipse(startTime)
        events.add(AstroEvent("Lunar Eclipse", "Lunar eclipse (${lunar.kind})", df.format(Date(lunar.peak.toMillisecondsSince1970())), lunar.peak.toMillisecondsSince1970(), EventType.MOON_PHASE))

        listOf(Body.Mars, Body.Jupiter, Body.Saturn).forEach { planet ->
            val opp = searchRelativeLongitude(planet, 0.0, startTime)
            val planetNameEn = when(planet) {
                Body.Mars -> "Mars"
                Body.Jupiter -> "Jupiter"
                Body.Saturn -> "Saturn"
                else -> planet.name
            }
            events.add(AstroEvent("Opposition of $planetNameEn", "The planet $planetNameEn is at its closest point to Earth", df.format(Date(opp.toMillisecondsSince1970())), opp.toMillisecondsSince1970(), EventType.PLANET))
        }

        try {
            val venusPeak = searchPeakMagnitude(Body.Venus, startTime)
            events.add(AstroEvent("Peak Brightness of Venus", "Maximum brightness of Venus in the sky", df.format(Date(venusPeak.time.toMillisecondsSince1970())), venusPeak.time.toMillisecondsSince1970(), EventType.PLANET))
        } catch (e: Exception) { }

        return events.sortedBy { it.timestamp }.take(limit)
    }
}
