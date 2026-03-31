package com.app.astrojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.astrojournal.data.model.Astro
import com.app.astrojournal.data.model.AstroEvent
import com.app.astrojournal.utils.MoonCalculator
import com.app.astrojournal.utils.MoonPhaseInfo
import io.github.cosinekitty.astronomy.Body
import io.github.cosinekitty.astronomy.Time
import io.github.cosinekitty.astronomy.searchGlobalSolarEclipse
import io.github.cosinekitty.astronomy.searchLunarEclipse
import io.github.cosinekitty.astronomy.searchPeakMagnitude
import io.github.cosinekitty.astronomy.searchRelativeLongitude
import io.github.cosinekitty.astronomy.seasons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: Astro) : UiState()
    data class Error(val message: String) : UiState()
}

typealias MoonPhaseProvider = (Date) -> MoonPhaseInfo
typealias UpcomingEventsProvider = (Time) -> List<AstroEvent>

private fun defaultUpcomingEventsProvider(startTime: Time): List<AstroEvent> {
    val events = mutableListOf<AstroEvent>()
    val currentYear = startTime.toDateTime().year
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val yearsToSearch = listOf(currentYear, currentYear + 1)
    yearsToSearch.forEach { year ->
        val s = seasons(year)
        listOf(
            Triple("March Equinox", "Inicio de la primavera (N) / otono (S)", s.marchEquinox),
            Triple("June Solstice", "Inicio del verano (N) / invierno (S)", s.juneSolstice),
            Triple("September Equinox", "Inicio del otono (N) / primavera (S)", s.septemberEquinox),
            Triple("December Solstice", "Inicio del invierno (N) / verano (S)", s.decemberSolstice)
        ).forEach { (name, desc, time) ->
            if (time.toMillisecondsSince1970() > startTime.toMillisecondsSince1970()) {
                events.add(
                    AstroEvent(
                        name,
                        desc,
                        df.format(Date(time.toMillisecondsSince1970())),
                        time.toMillisecondsSince1970(),
                        com.app.astrojournal.data.model.EventType.OTHER
                    )
                )
            }
        }
    }

    val solar = searchGlobalSolarEclipse(startTime)
    events.add(
        AstroEvent(
            "Solar Eclipse",
            "Eclipse solar global (${solar.kind})",
            df.format(Date(solar.peak.toMillisecondsSince1970())),
            solar.peak.toMillisecondsSince1970(),
            com.app.astrojournal.data.model.EventType.CONJUNCTION
        )
    )

    val lunar = searchLunarEclipse(startTime)
    events.add(
        AstroEvent(
            "Lunar Eclipse",
            "Eclipse lunar (${lunar.kind})",
            df.format(Date(lunar.peak.toMillisecondsSince1970())),
            lunar.peak.toMillisecondsSince1970(),
            com.app.astrojournal.data.model.EventType.MOON_PHASE
        )
    )

    listOf(Body.Mars, Body.Jupiter, Body.Saturn).forEach { planet ->
        val opp = searchRelativeLongitude(planet, 0.0, startTime)
        events.add(
            AstroEvent(
                "$planet Opposition",
                "El planeta $planet esta en su punto mas cercano a la Tierra",
                df.format(Date(opp.toMillisecondsSince1970())),
                opp.toMillisecondsSince1970(),
                com.app.astrojournal.data.model.EventType.PLANET
            )
        )
    }

    try {
        val venusPeak = searchPeakMagnitude(Body.Venus, startTime)
        events.add(
            AstroEvent(
                "Venus Peak Brightness",
                "Maximo brillo de Venus en el cielo",
                df.format(Date(venusPeak.time.toMillisecondsSince1970())),
                venusPeak.time.toMillisecondsSince1970(),
                com.app.astrojournal.data.model.EventType.PLANET
            )
        )
    } catch (_: Exception) {
    }

    return events
}

class HomeViewModel(
    private val moonPhaseProvider: MoonPhaseProvider = { date -> MoonCalculator.getMoonPhaseInfo(date) },
    private val upcomingEventsProvider: UpcomingEventsProvider = ::defaultUpcomingEventsProvider,
    private val loadingDelayMs: Long = 500L
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _moonData = MutableStateFlow<Astro?>(null)
    val moonData: StateFlow<Astro?> = _moonData

    private val _upcomingEvents = MutableStateFlow<List<AstroEvent>>(emptyList())
    val upcomingEvents: StateFlow<List<AstroEvent>> = _upcomingEvents

    fun fetchMoonData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            if (loadingDelayMs > 0) {
                delay(loadingDelayMs)
            }

            try {
                val now = Date()
                val moonPhaseInfo = withContext(Dispatchers.Default) {
                    moonPhaseProvider(now)
                }

                val astroData = Astro(
                    moon_age = moonPhaseInfo.moonAge,
                    moon_illumination = moonPhaseInfo.illumination.toString(),
                    moon_phase = moonPhaseInfo.phaseName
                )

                val startTime = Time.fromMillisecondsSince1970(now.time)
                _upcomingEvents.value = withContext(Dispatchers.Default) {
                    upcomingEventsProvider(startTime)
                        .sortedBy { it.timestamp }
                        .take(7)
                }

                _moonData.value = astroData
                _uiState.value = UiState.Success(astroData)
            } catch (e: Exception) {
                _upcomingEvents.value = emptyList()
                _uiState.value = UiState.Error("Error calculating moon phase: ${e.message ?: "Unknown error"}")
            }
        }
    }
}
