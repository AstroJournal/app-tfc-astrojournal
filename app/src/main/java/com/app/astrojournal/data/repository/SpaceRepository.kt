package com.app.astrojournal.data.repository

import com.app.astrojournal.data.model.ApodUi
import com.app.astrojournal.data.model.VisibilityUi
import com.app.astrojournal.data.network.ApodApi
import com.app.astrojournal.data.network.RetrofitClient
import com.app.astrojournal.data.network.VisibilityApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SpaceRepository(
    private val apodApi: ApodApi = RetrofitClient.apodApi,
    private val visibilityApi: VisibilityApi = RetrofitClient.visibilityApi
) {
    companion object {
        private var cachedApod: ApodUi? = null
        private var cachedVisibility: VisibilityUi? = null
    }

    private val isoDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun getApodForDate(date: LocalDate): ApodUi {
        val result = apodApi.getApod(apiKey = "DEMO_KEY", date = date.format(isoDateFormatter))
        return mapApodResult(result)
    }

    private suspend fun getApodToday(): ApodUi {
        val result = apodApi.getApod(apiKey = "DEMO_KEY")
        return mapApodResult(result)
    }

    private fun mapApodResult(result: com.app.astrojournal.data.model.ApodResponse): ApodUi {
        val resolvedImageUrl = normalizeUrl(
            result.url
            ?: result.hdurl
            ?: result.thumbnailUrl
            ?: ""
        )
        val imageToUse = if (resolvedImageUrl.isBlank()) {
            "https://apod.nasa.gov/apod/image/2603/Message_Arecibo_960.jpg"
        } else {
            resolvedImageUrl
        }

        val apod = ApodUi(
            title = result.title,
            description = result.explanation,
            imageUrl = imageToUse,
            mediaType = result.mediaType
        )
        cachedApod = apod
        return apod
    }

    suspend fun getApodWithFallback(date: LocalDate): ApodUi {
        val todayTry = runCatching { getApodToday() }.getOrNull()
        if (todayTry != null) return todayTry

        val requestedDateTry = runCatching { getApodForDate(date) }.getOrNull()
        if (requestedDateTry != null) return requestedDateTry

        for (daysBack in 1L..7L) {
            val previousDayTry = runCatching { getApodForDate(date.minusDays(daysBack)) }.getOrNull()
            if (previousDayTry != null) return previousDayTry
        }

        return cachedApod?.takeIf { it.imageUrl.isNotBlank() } ?: ApodUi(
            title = "A Message from Earth",
            description = "En 1974 se transmitió desde el radiotelescopio de Arecibo un mensaje codificado en binario hacia el cúmulo estelar M13. Esta imagen representa ese histórico intento simbólico de comunicación interestelar.",
            imageUrl = "https://apod.nasa.gov/apod/image/2603/Message_Arecibo_960.jpg",
            mediaType = "placeholder"
        )
    }

    private fun normalizeUrl(url: String): String {
        return if (url.startsWith("http://")) {
            "https://${url.removePrefix("http://")}" 
        } else {
            url
        }
    }

    suspend fun getVisibilityForDate(date: LocalDate): VisibilityUi {
        val isoDate = date.format(isoDateFormatter)
        val response = visibilityApi.getVisibility(
            latitude = 40.4168,
            longitude = -3.7038,
            startDate = isoDate,
            endDate = isoDate
        )

        val sunrise = response.daily.sunrise.firstOrNull().orEmpty()
        val sunset = response.daily.sunset.firstOrNull().orEmpty()
        val cloudCover = response.daily.cloudCoverMean.firstOrNull()?.toInt() ?: -1
        val observable = cloudCover in 0..60
        val cloudLabel = if (cloudCover >= 0) "$cloudCover%" else "N/D"

        val visibility = VisibilityUi(
            isObservable = observable,
            window = "$sunset - $sunrise",
            cloudCoverPercent = cloudCover,
            message = if (observable) {
                "Buenas condiciones estimadas para observación"
            } else {
                "Condiciones limitadas para observar"
            } + " (nubes: $cloudLabel)"
        )
        cachedVisibility = visibility
        return visibility
    }

    suspend fun getVisibilityWithFallback(date: LocalDate): VisibilityUi {
        return runCatching { getVisibilityForDate(date) }
            .getOrElse {
                cachedVisibility ?: VisibilityUi(
                    isObservable = false,
                    window = "N/D",
                    cloudCoverPercent = -1,
                    message = "Información no disponible. Sincroniza cuando tengas conexión."
                )
            }
    }

}
