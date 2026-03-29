package com.app.astrojournal.data.model

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String? = null,
    val hdurl: String? = null,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null,
    @SerializedName("media_type") val mediaType: String
)

data class VisibilityResponse(
    val daily: VisibilityDaily
)

data class VisibilityDaily(
    val time: List<String> = emptyList(),
    val sunrise: List<String> = emptyList(),
    val sunset: List<String> = emptyList(),
    @SerializedName("cloud_cover_mean") val cloudCoverMean: List<Double> = emptyList()
)

data class ApodUi(
    val title: String,
    val description: String,
    val imageUrl: String,
    val mediaType: String
)

data class VisibilityUi(
    val isObservable: Boolean,
    val window: String,
    val cloudCoverPercent: Int,
    val message: String
)
