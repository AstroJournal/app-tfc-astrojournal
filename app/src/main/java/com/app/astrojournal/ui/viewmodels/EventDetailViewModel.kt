package com.app.astrojournal.ui.viewmodels

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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventDetailViewModel(
    private val repo: CollectibleRepository
) : ViewModel() {

    // List of all collectibles (notes, agenda items, etc.)
    var collectibles by mutableStateOf<List<Collectible>>(emptyList())
        private set

    // Status for the CURRENT event being viewed
    var isEventObserved by mutableStateOf(false)
        private set
    var isEventAgended by mutableStateOf(false)
        private set
    var currentEventNote by mutableStateOf("")
        private set

    private val dbMutex = Mutex()

    init {
        loadCollectibles()
        cleanupDuplicates()
    }

    private fun cleanupDuplicates() {
        viewModelScope.launch {
            dbMutex.withLock {
                withContext(Dispatchers.IO) {
                    val all = repo.getAll()
                    val groups = all.groupBy { it.eventId }
                    groups.forEach { (eventId, records) ->
                        if (records.size > 1) {
                            // Keep the most complete record (one with note or observed status)
                            val bestRecord = records.sortedWith(compareByDescending<Collectible> { it.observed }.thenByDescending { it.notes?.length ?: -1L }).first()
                            records.filter { it.id != bestRecord.id }.forEach {
                                repo.deleteById(it.id)
                            }
                        }
                    }
                }
            }
            loadCollectibles()
        }
    }

    private fun loadCollectibles() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getAll()
            }
            collectibles = result
        }
    }

    /**
     * Updates the status (Observed/Agended) and note for a specific event ID.
     */
    fun updateEventStatus(eventId: Long) {
        val record = collectibles.find { it.eventId == eventId }
        isEventObserved = record?.observed == 1L
        isEventAgended = record?.agended == 1L
        currentEventNote = record?.notes ?: ""
    }

    fun updateNote(eventId: Long, eventName: String, date: String, note: String) {
        viewModelScope.launch {
            saveEventState(eventId, eventName, date, note, isEventAgended, isEventObserved)
        }
    }

    fun saveFullEventState(
        eventId: Long,
        eventName: String,
        date: String,
        note: String,
        agended: Boolean,
        observed: Boolean
    ) {
        viewModelScope.launch {
            saveEventState(eventId, eventName, date, note, agended, observed)
        }
    }

    private suspend fun saveEventState(
        eventId: Long,
        eventName: String,
        date: String,
        note: String,
        agended: Boolean,
        observed: Boolean
    ) {
        dbMutex.withLock {
            withContext(Dispatchers.IO) {
                val existing = repo.getAll().find { it.eventId == eventId }
                
                val finalObserved = if (observed) 1 else 0
                val finalAgended = if (agended) 1 else 0
                val shouldExist = agended || observed || note.isNotBlank()

                if (shouldExist) {
                    if (existing != null) {
                        // Update existing
                        repo.updateObserved(existing.id, finalObserved)
                        repo.updateAgended(existing.id, finalAgended)
                        repo.updateNotes(existing.id, note.ifBlank { null })
                    } else {
                        // Insert new
                        repo.insertCollectible(
                            eventId = eventId,
                            eventName = eventName,
                            observationDate = date,
                            notes = note.ifBlank { null },
                            observed = finalObserved,
                            agended = finalAgended
                        )
                    }
                } else if (existing != null) {
                    // Remove if no longer needed
                    repo.deleteById(existing.id)
                }
            }
        }
        loadCollectibles()
    }

    fun deleteNote(id: Long) {
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
