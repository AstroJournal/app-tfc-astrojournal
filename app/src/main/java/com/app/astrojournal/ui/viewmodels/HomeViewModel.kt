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

import com.app.astrojournal.utils.AstroEventCalculator

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

    private fun fetchUpcomingEvents() {
        val events = AstroEventCalculator.getUpcomingEvents(limit = 7)
        _upcomingEvents.value = events
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
