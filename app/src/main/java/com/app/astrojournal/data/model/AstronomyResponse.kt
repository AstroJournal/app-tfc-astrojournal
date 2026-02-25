package com.app.astrojournal.data.model

data class AstronomyResponse(
    val astronomy: Astronomy
)
data class Astronomy(val astro: Astro)
data class Astro(
    val moon_phase: String,
    val moon_illumination: String,
    val moon_age: Double
)

