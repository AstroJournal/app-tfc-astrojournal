package com.app.astrojournal.test.unit

import com.app.astrojournal.utils.generateWeeklyMoonForecast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MoonForecastUtilsUnitTest {

    @Test
    fun generateWeeklyMoonForecast_returnsSevenConsecutiveDays() {
        val today = LocalDate.now()

        val forecast = generateWeeklyMoonForecast()

        assertEquals(7, forecast.size)
        assertEquals("Today", forecast.first().dayLabel)

        forecast.forEachIndexed { index, day ->
            assertEquals(today.plusDays(index.toLong()), day.date)
            assertTrue(day.illumination in 0..100)
            assertTrue(day.age in 0.0..29.53)
            assertTrue(day.phase.isNotBlank())
            assertTrue(day.imageRes != 0)
        }
    }
}
