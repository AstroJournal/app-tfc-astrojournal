package com.app.astrojournal.utils


import com.app.astrojournal.R

fun getMoonPhaseIcon(phaseName: String): Int {
    return when (phaseName) {
        "New Moon" -> R.drawable.ic_moon_new
        "First Quarter" -> R.drawable.ic_moon_first_quarter
        "Full Moon", "Full" -> R.drawable.ic_moon_full
        "Waning Gibbous" -> R.drawable.ic_moon_waning_gibbous
        "Last Quarter" -> R.drawable.ic_moon_last_quarter
        "Waxing Gibbous" -> R.drawable.ic_moon_first_quarter
        "Waxing Crescent" -> R.drawable.ic_moon_first_quarter
        "Waning Crescent" -> R.drawable.ic_moon_last_quarter
        else -> R.drawable.ic_moon_full
    }
}