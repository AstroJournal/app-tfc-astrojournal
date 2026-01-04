package com.app.astrojournal.eventdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import com.app.shared.data.db.CollectibleRepository
import db.Collectible

class EventDetailViewModel(
    private val repo: CollectibleRepository
) : ViewModel() {

    var collectibles by mutableStateOf(emptyList<Collectible>())
        private set

    init {
        loadCollectibles()
    }

    private fun loadCollectibles() {
        viewModelScope.launch {
            collectibles = repo.getAll()
        }
    }

    fun eventoObservado(eventId: String, notes: String) {
        viewModelScope.launch {
            val id = UUID.randomUUID().toString()
            val date = Instant.now().toString()

            repo.insertCollectible(
                id = id,
                eventId = eventId,
                observationDate = date,
                notes = notes
            )

            // 🔥 Recargar la lista después de guardar
            loadCollectibles()
        }

    }
    fun borrarNota(id: String) {
        repo.deleteById(id)
        loadCollectibles() // refrescar lista
    }
}
