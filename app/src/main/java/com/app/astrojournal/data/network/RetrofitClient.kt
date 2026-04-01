package com.app.astrojournal.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val astronomyApi: AstronomyApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AstronomyApi::class.java)
    }

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apodApi: ApodApi by lazy {
        retrofit("https://api.nasa.gov/").create(ApodApi::class.java)
    }

    val visibilityApi: VisibilityApi by lazy {
        retrofit("https://api.open-meteo.com/").create(VisibilityApi::class.java)
    }
}