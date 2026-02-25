package com.app.astrojournal.utils

import com.app.astrojournal.data.model.WeeklyMoonDay
import com.app.astrojournal.ui.screens.getMoonPhaseImage
import com.app.astrojournal.ui.screens.getPhaseFromMoonAge
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale


fun generateWeeklyMoonForecast(currentAge: Double): List<WeeklyMoonDay> {
    val forecast = mutableListOf<WeeklyMoonDay>()
    val today = java.time.LocalDate.now()
    
    for (i in 0..6) {
        val age = (currentAge + i) % 29.53 // ciclo lunar
        val phase = getPhaseFromMoonAge(age)
        val imageRes = getMoonPhaseImage(phase)
        
        val date = today.plusDays(i.toLong())
        val dayLabel = if (i == 0) "Today" else date.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.SHORT, 
            java.util.Locale.getDefault()
        )
        
        forecast.add(WeeklyMoonDay(dayLabel, phase, imageRes))
    }
    return forecast
}
