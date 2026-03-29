package com.app.astrojournal.test.unit

import com.app.astrojournal.utils.MoonCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.util.Date

class MoonPhaseCalculationTest {

    @Test
    fun moonPhaseInfo_returnsValidRangesAndAllowedNames() {
        val date = Date(0L)

        val result = MoonCalculator.getMoonPhaseInfo(date)

        assertEquals(date, result.date)
        assertTrue(result.illumination in 0..100)
        assertTrue(result.moonAge in 0.0..29.53)
        assertTrue(
            result.phaseName in setOf(
                "New Moon",
                "Waxing Crescent",
                "First Quarter",
                "Waxing Gibbous",
                "Full Moon",
                "Waning Gibbous",
                "Last Quarter",
                "Waning Crescent"
            )
        )
    }

    @Test
    fun moonPhaseInfo_isDeterministicForSameDate() {
        val date = Date(1_700_000_000_000L)

        val first = MoonCalculator.getMoonPhaseInfo(date)
        val second = MoonCalculator.getMoonPhaseInfo(date)

        assertEquals(first.phaseName, second.phaseName)
        assertEquals(first.illumination, second.illumination)
        assertEquals(first.moonAge, second.moonAge, 0.000001)
    }

    @Test
    fun moonPhase_forKnownNewMoonDate_isNewMoon() {
        val date = Date.from(Instant.parse("2024-04-08T18:00:00Z"))

        val result = MoonCalculator.getMoonPhaseInfo(date)

        assertEquals("New Moon", result.phaseName)
        assertTrue(result.illumination <= 5)
    }
}
