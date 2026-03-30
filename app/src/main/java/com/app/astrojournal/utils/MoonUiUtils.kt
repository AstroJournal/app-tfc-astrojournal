package com.app.astrojournal.utils

import com.app.astrojournal.R

object MoonUiUtils {

    // Note: We used to translate to Spanish here, now we just return the English phaseName directly
    // since the whole app is in English.
    fun getPhaseNameInSpanish(phaseName: String): String {
        return phaseName
    }

    fun getMoonPhaseImage(phaseName: String): Int {
        return when (phaseName.lowercase().replace(" ", "_")) {
            "new_moon" -> R.drawable.new_moon
            "waxing_crescent" -> R.drawable.waxing_crescent
            "first_quarter" -> R.drawable.first_quarter
            "waxing_gibbous" -> R.drawable.waxing_gibbous
            "full_moon" -> R.drawable.full_moon
            "waning_gibbous" -> R.drawable.waning_gibbous
            "last_quarter" -> R.drawable.third_quarter
            "waning_crescent" -> R.drawable.waning_crescent
            else -> R.drawable.full_moon
        }
    }

    fun getPhaseFromMoonAge(age: Double): String = when {
        age < 1.8 -> "New Moon"
        age < 5.5 -> "Waxing Crescent"
        age < 9.2 -> "First Quarter"
        age < 12.9 -> "Waxing Gibbous"
        age < 16.6 -> "Full Moon"
        age < 20.3 -> "Waning Gibbous"
        age < 24.1 -> "Last Quarter"
        else -> "Waning Crescent"
    }

    fun calculateIllumination(age: Double): Int {
        return if (age <= 14.0) ((age / 14.0) * 100).toInt()
        else (100 - ((age - 14) / 15.53 * 100)).toInt()
    }
}
