package com.app.astrojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shared.data.db.MeetupEventRepository
import com.astrojournal.shared.data.db.MeetupEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.app.astrojournal.utils.AstroEventCalculator
import com.app.astrojournal.data.model.AstroEvent

data class SocialEventsUiState(
    val allEvents: List<MeetupEvent> = emptyList(),
    val myEvents: List<MeetupEvent> = emptyList(),
    val upcomingAstroEvents: List<AstroEvent> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SocialEventsViewModel(
    private val repository: MeetupEventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialEventsUiState())
    val uiState: StateFlow<SocialEventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dbAll = repository.getAll()
                
                // Añadimos datos de prueba si la lista está completamente vacía (para que se vea contenido en "Todos")
                if (dbAll.isEmpty()) {
                    repository.insertEvent(
                        title = "Observación de las Perseidas",
                        description = "Noche de observación en la sierra seca. Lleva ropa de abrigo y telescopios.",
                        location = "Sierra Nevada, Granada",
                        dateTime = "2026-08-12 22:00",
                        isMine = false
                    )
                    repository.insertEvent(
                        title = "Taller de Astrofotografía",
                        description = "Aprende a capturar la Vía Láctea con expertos locales.",
                        location = "Parque Nacional de Gredos",
                        dateTime = "2026-05-15 20:00",
                        isMine = false
                    )
                }

                val all = repository.getAll()
                val my = repository.getMyEvents()
                val astro = AstroEventCalculator.getUpcomingEvents(limit = 15)

                _uiState.update { 
                    it.copy(
                        allEvents = all,
                        myEvents = my,
                        upcomingAstroEvents = astro,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun createEvent(title: String, description: String, location: String, dateTime: String, linkedAstroEventName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEvent(title, description, location, dateTime, isMine = true, linkedAstroEventName = linkedAstroEventName)
            loadEvents()
        }
    }

    fun updateEvent(id: Long, title: String, description: String, location: String, dateTime: String, linkedAstroEventName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEvent(id, title, description, location, dateTime, linkedAstroEventName = linkedAstroEventName)
            loadEvents()
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
            loadEvents()
        }
    }
}
