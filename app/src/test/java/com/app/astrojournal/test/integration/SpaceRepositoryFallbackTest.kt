package com.app.astrojournal.test.integration

import com.app.astrojournal.data.model.ApodResponse
import com.app.astrojournal.data.model.VisibilityResponse
import com.app.astrojournal.data.model.VisibilityDaily
import com.app.astrojournal.data.network.ApodApi
import com.app.astrojournal.data.network.VisibilityApi
import com.app.astrojournal.data.repository.SpaceRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SpaceRepositoryFallbackTest {

    @Test
    fun getApodWithFallback_usesPreviousDayWhenTodayFails() = runTest {
        val today = LocalDate.now()
        val todayIso = today.toString()
        val yesterdayIso = today.minusDays(1).toString()

        val fakeApodApi = object : ApodApi {
            override suspend fun getApod(apiKey: String, date: String?, thumbs: Boolean): ApodResponse {
                return when (date) {
                    todayIso -> throw IllegalStateException("today unavailable")
                    yesterdayIso -> ApodResponse(
                        date = yesterdayIso,
                        title = "Yesterday APOD",
                        explanation = "Fallback from previous day",
                        url = "https://example.com/yesterday.jpg",
                        hdurl = null,
                        thumbnailUrl = null,
                        mediaType = "image"
                    )
                    else -> throw IllegalStateException("unexpected date $date")
                }
            }
        }

        val repo = SpaceRepository(
            apodApi = fakeApodApi,
            visibilityApi = noOpVisibilityApi()
        )

        val result = repo.getApodWithFallback(today)

        assertEquals("Yesterday APOD", result.title)
        assertEquals("Fallback from previous day", result.description)
        assertEquals("https://example.com/yesterday.jpg", result.imageUrl)
    }

    @Test
    fun getApodWithFallback_whenAllApiCallsFail_returnsBuiltInFallback() = runTest {
        val fakeApodApi = object : ApodApi {
            override suspend fun getApod(apiKey: String, date: String?, thumbs: Boolean): ApodResponse {
                throw IllegalStateException("network down")
            }
        }

        val repo = SpaceRepository(
            apodApi = fakeApodApi,
            visibilityApi = noOpVisibilityApi()
        )

        val result = repo.getApodWithFallback(LocalDate.now())

        assertTrue(result.imageUrl.isNotBlank())
        assertTrue(result.description.isNotBlank())
    }

    @Test
    fun getApodForDate_usesThumbnailWhenPrimaryUrlsMissing() = runTest {
        val fakeApodApi = object : ApodApi {
            override suspend fun getApod(apiKey: String, date: String?, thumbs: Boolean): ApodResponse {
                return ApodResponse(
                    date = date ?: LocalDate.now().toString(),
                    title = "Video APOD",
                    explanation = "Has thumbnail only",
                    url = null,
                    hdurl = null,
                    thumbnailUrl = "https://example.com/thumb.jpg",
                    mediaType = "video"
                )
            }
        }

        val repo = SpaceRepository(
            apodApi = fakeApodApi,
            visibilityApi = noOpVisibilityApi()
        )

        val result = repo.getApodForDate(LocalDate.now())

        assertEquals("https://example.com/thumb.jpg", result.imageUrl)
        assertEquals("Has thumbnail only", result.description)
    }

    private fun noOpVisibilityApi(): VisibilityApi {
        return object : VisibilityApi {
            override suspend fun getVisibility(
                latitude: Double,
                longitude: Double,
                daily: String,
                timezone: String,
                startDate: String,
                endDate: String
            ): VisibilityResponse {
                return VisibilityResponse(
                    daily = VisibilityDaily(
                        time = listOf(startDate),
                        sunrise = listOf("06:00"),
                        sunset = listOf("20:00"),
                        cloudCoverMean = listOf(30.0)
                    )
                )
            }
        }
    }
}
