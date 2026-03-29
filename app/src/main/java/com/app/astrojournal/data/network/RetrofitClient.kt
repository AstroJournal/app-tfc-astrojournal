package com.app.astrojournal.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private fun retrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val astronomyApi: AstronomyApi by lazy {
        retrofit("https://api.weatherapi.com/v1/").create(AstronomyApi::class.java)
    }

    val apodApi: ApodApi by lazy {
        retrofit("https://api.nasa.gov/").create(ApodApi::class.java)
    }

    val visibilityApi: VisibilityApi by lazy {
        retrofit("https://api.open-meteo.com/").create(VisibilityApi::class.java)
    }
}
