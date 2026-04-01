package com.app.astrojournal.test.unit

import com.app.astrojournal.R
import com.app.astrojournal.utils.getMoonPhaseIcon
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsUnitTest {

    @Test
    fun getMoonPhaseIcon_mapsKnownPhasesAndFallback() {
        assertEquals(R.drawable.ic_moon_new, getMoonPhaseIcon("New Moon"))
        assertEquals(R.drawable.ic_moon_first_quarter, getMoonPhaseIcon("First Quarter"))
        assertEquals(R.drawable.ic_moon_full, getMoonPhaseIcon("Full Moon"))
        assertEquals(R.drawable.ic_moon_last_quarter, getMoonPhaseIcon("Last Quarter"))
        assertEquals(R.drawable.ic_moon_full, getMoonPhaseIcon("NotARealPhase"))
    }
}
