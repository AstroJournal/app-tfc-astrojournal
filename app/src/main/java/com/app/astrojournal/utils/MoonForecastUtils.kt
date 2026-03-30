package com.app.astrojournal.utils

import com.app.astrojournal.data.model.WeeklyMoonDay
import java.time.LocalDate
import java.util.Date
import java.util.Locale

fun generateWeeklyMoonForecast(): List<WeeklyMoonDay> {
    val forecast = mutableListOf<WeeklyMoonDay>()
    val today = LocalDate.now()
    
    for (i in 0..6) {
        val date = today.plusDays(i.toLong())
        val javaDate = Date.from(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
        
        val phaseInfo = MoonCalculator.getMoonPhaseInfo(javaDate)
        val imageRes = MoonUiUtils.getMoonPhaseImage(phaseInfo.phaseName)
        
        val dayLabel = if (i == 0) "Today" else date.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.SHORT, 
            Locale.ENGLISH
        )
        
        forecast.add(
            WeeklyMoonDay(
                dayLabel = dayLabel,
                phase = phaseInfo.phaseName,
                imageRes = imageRes,
                age = phaseInfo.moonAge,
                illumination = phaseInfo.illumination,
                date = date
            )
        )
    }
    return forecast
}
