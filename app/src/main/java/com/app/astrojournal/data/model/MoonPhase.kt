package com.app.astrojournal.data.model

sealed class MoonPhaseType {
    data class Regular(val day: Int) : MoonPhaseType()
    data object Full : MoonPhaseType()
}

