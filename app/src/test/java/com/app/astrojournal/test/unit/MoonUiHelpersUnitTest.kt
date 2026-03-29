package com.app.astrojournal.test.unit

import com.app.astrojournal.R
import com.app.astrojournal.ui.screens.calculateIllumination
import com.app.astrojournal.ui.screens.getMoonPhaseImage
import com.app.astrojournal.ui.screens.getPhaseFromMoonAge
import org.junit.Assert.assertEquals
import org.junit.Test

class MoonUiHelpersUnitTest {

    @Test
    fun getPhaseFromMoonAge_mapsExpectedBoundaries() {
        assertEquals("New Moon", getPhaseFromMoonAge(0.0))
        assertEquals("Waxing Crescent", getPhaseFromMoonAge(2.0))
        assertEquals("First Quarter", getPhaseFromMoonAge(5.5))
        assertEquals("Waxing Gibbous", getPhaseFromMoonAge(10.0))
        assertEquals("Full Moon", getPhaseFromMoonAge(13.0))
        assertEquals("Waning Gibbous", getPhaseFromMoonAge(17.0))
        assertEquals("Last Quarter", getPhaseFromMoonAge(21.0))
        assertEquals("Waning Crescent", getPhaseFromMoonAge(25.0))
    }

    @Test
    fun calculateIllumination_returnsExpectedValues() {
        assertEquals(0, calculateIllumination(0.0))
        assertEquals(100, calculateIllumination(14.0))
        assertEquals(0, calculateIllumination(29.53))
    }

    @Test
    fun getMoonPhaseImage_mapsKnownPhases() {
        assertEquals(R.drawable.new_moon, getMoonPhaseImage("New Moon"))
        assertEquals(R.drawable.full_moon, getMoonPhaseImage("Full Moon"))
        assertEquals(R.drawable.third_quarter, getMoonPhaseImage("Last Quarter"))
        assertEquals(R.drawable.full_moon, getMoonPhaseImage("Unknown"))
    }
}
