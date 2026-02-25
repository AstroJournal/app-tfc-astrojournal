package com.app.astrojournal.eventdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import com.app.shared.data.db.CollectibleRepository
import com.astrojournal.shared.data.db.Collectible

class EventDetailViewModel(
    private val repo: CollectibleRepository
) : ViewModel() {

    //List of collectibles associated to events
    var collectibles by mutableStateOf<List<Collectible>>(emptyList())
        private set
    var isEventObserved by mutableStateOf(false)
        private set

    init {
        loadCollectibles()
    }

    private fun loadCollectibles() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getAll()
            }
            collectibles = result
        }
    }

    fun addNote(eventId: Long, note: String) {     // ← CAMBIO: antes String
        if (note.isBlank()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.insertCollectible(
                    eventId = eventId,
                    observationDate = Instant.now().toString(),
                    notes = note,
                    observed = 0
                )
            }
            loadCollectibles()
        }
    }

    fun deleteNote(id: Long) {                     // ← CAMBIO: antes String
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.deleteById(id)
            }
            loadCollectibles()
        }
    }

    fun markObserved(observed: Boolean) {
        isEventObserved = observed
        }
    }
