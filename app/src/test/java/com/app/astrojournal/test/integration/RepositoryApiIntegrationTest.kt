package com.app.astrojournal.test.integration

import com.app.astrojournal.data.model.Astro
import com.app.astrojournal.data.model.Astronomy
import com.app.astrojournal.data.model.AstronomyResponse
import com.app.astrojournal.data.network.AstronomyApi
import com.app.astrojournal.data.repository.AstronomyRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryApiIntegrationTest {

    @Test
    fun repository_readsMoonDataFromApiContract() = runTest {
        val fakeApi = object : AstronomyApi {
            override suspend fun getMoonData(apiKey: String, location: String, date: String): AstronomyResponse {
                return AstronomyResponse(
                    astronomy = Astronomy(
                        astro = Astro(
                            moon_phase = "Full Moon",
                            moon_illumination = "100",
                            moon_age = 14.0
                        )
                    )
                )
            }
        }

        val repository = AstronomyRepository(fakeApi)
        val result = repository.getMoonData("key", "Madrid", "2026-03-20")

        assertEquals("Full Moon", result.moon_phase)
        assertEquals("100", result.moon_illumination)
        assertEquals(14.0, result.moon_age, 0.0)
    }
}
