package com.app.astrojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.astrojournal.data.model.*
import com.app.astrojournal.utils.MoonCalculator
import io.github.cosinekitty.astronomy.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Sealed class para representar los diferentes estados de la UI
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: Astro) : UiState()
    data class Error(val message: String) : UiState()
}

class HomeViewModel : ViewModel() {

    // Estado de la UI (Loading, Success, Error)
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    // Mantener moonData para compatibilidad con el código existente
    private val _moonData = MutableStateFlow<Astro?>(null)
    val moonData: StateFlow<Astro?> = _moonData

    // Nueva lista de eventos astronómicos próximos
    private val _upcomingEvents = MutableStateFlow<List<AstroEvent>>(emptyList())
    val upcomingEvents: StateFlow<List<AstroEvent>> = _upcomingEvents

    /**
     * Obtiene los datos de la luna y eventos próximos usando cálculos astronómicos locales.
     */
    fun fetchMoonData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Simular carga para mejorar UX
            delay(500)
            
            try {
                // 1. Calcular la fase lunar actual (Usando MoonCalculator actualizado)
                val moonPhaseInfo = MoonCalculator.getMoonPhaseInfo()
                
                val astroData = Astro(
                    moon_age = moonPhaseInfo.moonAge,
                    moon_illumination = moonPhaseInfo.illumination.toString(),
                    moon_phase = moonPhaseInfo.phaseName
                )
                
                // 2. Calcular eventos próximos (Fases lunares y conjunciones)
                fetchUpcomingEvents()
                
                _moonData.value = astroData
                _uiState.value = UiState.Success(astroData)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.Error("Error calculating moon phase: ${e.message ?: "Unknown error"}")
            }
        }
    }

    /**
     * Calcula los próximos eventos astronómicos (Estaciones, Eclipses, Oposiciones) para los siguientes meses.
     */
    private fun fetchUpcomingEvents() {
        val events = mutableListOf<AstroEvent>()
        val startTime = Time.fromMillisecondsSince1970(Date().time)
        val currentYear = startTime.toDateTime().year
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        // 1. Agregar Estaciones (Equinoccios y Solsticios)
        val yearsToSearch = listOf(currentYear, currentYear + 1)
        yearsToSearch.forEach { year ->
            val s = seasons(year)
            listOf(
                Triple("March Equinox", "Inicio de la primavera (N) / otoño (S)", s.marchEquinox),
                Triple("June Solstice", "Inicio del verano (N) / invierno (S)", s.juneSolstice),
                Triple("September Equinox", "Inicio del otoño (N) / primavera (S)", s.septemberEquinox),
                Triple("December Solstice", "Inicio del invierno (N) / verano (S)", s.decemberSolstice)
            ).forEach { (name, desc, time) ->
                if (time.toMillisecondsSince1970() > startTime.toMillisecondsSince1970()) {
                    events.add(AstroEvent(name, desc, df.format(Date(time.toMillisecondsSince1970())), time.toMillisecondsSince1970(), EventType.OTHER))
                }
            }
        }

        // 2. Agregar Eclipses
        val solar = searchGlobalSolarEclipse(startTime)
        events.add(AstroEvent("Solar Eclipse", "Eclipse solar global (${solar.kind})", df.format(Date(solar.peak.toMillisecondsSince1970())), solar.peak.toMillisecondsSince1970(), EventType.CONJUNCTION))
        
        val lunar = searchLunarEclipse(startTime)
        events.add(AstroEvent("Lunar Eclipse", "Eclipse lunar (${lunar.kind})", df.format(Date(lunar.peak.toMillisecondsSince1970())), lunar.peak.toMillisecondsSince1970(), EventType.MOON_PHASE))

        // 3. Agregar Oposiciones Planetarias (Mejor momento para ver planetas)
        listOf(Body.Mars, Body.Jupiter, Body.Saturn).forEach { planet ->
            val opp = searchRelativeLongitude(planet, 0.0, startTime)
            events.add(AstroEvent("$planet Opposition", "El planeta $planet está en su punto más cercano a la Tierra", df.format(Date(opp.toMillisecondsSince1970())), opp.toMillisecondsSince1970(), EventType.PLANET))
        }

        // 4. Venus Peak Magnitude
        try {
            val venusPeak = searchPeakMagnitude(Body.Venus, startTime)
            events.add(AstroEvent("Venus Peak Brightness", "Máximo brillo de Venus en el cielo", df.format(Date(venusPeak.time.toMillisecondsSince1970())), venusPeak.time.toMillisecondsSince1970(), EventType.PLANET))
        } catch (e: Exception) { /* Solo Venus soportado */ }

        // Ordenar cronológicamente y tomar los primeros 7
        _upcomingEvents.value = events.sortedBy { it.timestamp }.take(7)
    }

    /**
     * Traduce el índice de fase de astronomy-engine a nombre legible.
     */
    private fun getPhaseNameFromIndex(index: Int): String = when (index) {
        0 -> "New Moon"
        1 -> "First Quarter"
        2 -> "Full Moon"
        3 -> "Last Quarter"
        else -> "Unknown"
    }
}
