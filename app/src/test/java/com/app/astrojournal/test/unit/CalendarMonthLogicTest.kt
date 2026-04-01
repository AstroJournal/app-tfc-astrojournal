package com.app.astrojournal.test.unit

import com.app.astrojournal.utils.calculateCalendarMonthMeta
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.YearMonth

class CalendarMonthLogicTest {

    @Test
    fun monthMeta_returnsCorrectDaysAndFirstDayOffset_forLeapFebruary() {
        val month = YearMonth.of(2024, 2)

        val meta = calculateCalendarMonthMeta(month)

        assertEquals(29, meta.daysInMonth)
        assertEquals(3, meta.firstDayOffset)
        assertEquals(35, meta.totalCells)
    }

    @Test
    fun monthMeta_returnsCorrectDaysAndFirstDayOffset_forSeptember2025() {
        val month = YearMonth.of(2025, 9)

        val meta = calculateCalendarMonthMeta(month)

        assertEquals(30, meta.daysInMonth)
        assertEquals(0, meta.firstDayOffset)
        assertEquals(35, meta.totalCells)
    }
}
