package com.app.astrojournal.data.network

import com.app.astrojournal.data.model.ApodResponse
import com.app.astrojournal.data.model.VisibilityResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String? = null,
        @Query("thumbs") thumbs: Boolean = true
    ): ApodResponse
}

interface VisibilityApi {
    @GET("v1/forecast")
    suspend fun getVisibility(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "sunrise,sunset,cloud_cover_mean",
        @Query("timezone") timezone: String = "auto",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): VisibilityResponse
}
