package com.app.astrojournal.data.model

import java.time.LocalDate

data class WeeklyMoonDay(
    val dayLabel: String,
    val phase: String,
    val imageRes: Int,
    val age: Double,
    val illumination: Int,
    val date: LocalDate
)