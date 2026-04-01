package com.app.astrojournal.utils

import java.time.DayOfWeek
import java.time.YearMonth

data class CalendarMonthMeta(
    val daysInMonth: Int,
    val firstDayOffset: Int,
    val totalCells: Int
)

fun calculateCalendarMonthMeta(month: YearMonth): CalendarMonthMeta {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfMonth = month.atDay(1).dayOfWeek
    val firstDayOffset = (firstDayOfMonth.value - DayOfWeek.MONDAY.value + 7) % 7
    val totalCells = if (firstDayOffset + daysInMonth > 35) 42 else 35

    return CalendarMonthMeta(
        daysInMonth = daysInMonth,
        firstDayOffset = firstDayOffset,
        totalCells = totalCells
    )
}
