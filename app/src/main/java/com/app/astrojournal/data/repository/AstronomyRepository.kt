package com.app.astrojournal.data.repository

import com.app.astrojournal.data.model.Astro
import com.app.astrojournal.data.network.AstronomyApi
import com.app.astrojournal.data.network.RetrofitClient

class AstronomyRepository(
    private val api: AstronomyApi = RetrofitClient.astronomyApi
) {

    suspend fun getMoonData(apiKey: String, location: String, date: String): Astro {
        return api.getMoonData(apiKey, location, date).astronomy.astro
    }
}
