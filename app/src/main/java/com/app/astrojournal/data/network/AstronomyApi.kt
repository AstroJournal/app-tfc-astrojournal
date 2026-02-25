package com.app.astrojournal.data.network

import com.app.astrojournal.data.model.AstronomyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AstronomyApi {
    @GET("astronomy.json")
    suspend fun getMoonData(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("dt") date: String
    ): AstronomyResponse
}
