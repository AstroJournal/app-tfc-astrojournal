package com.app.astrojournal.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shared.data.db.CollectibleStore
import com.astrojournal.shared.data.db.Collectible
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

sealed class EventDetailUiState {
    data object Loading : EventDetailUiState()
    data class Success(val eventId: Long) : EventDetailUiState()
    data class Error(val message: String) : EventDetailUiState()
}

enum class AgendaFilter {
    ALL,
    PENDING,
    OBSERVED
}

class EventDetailViewModel(
    private val repo: CollectibleStore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val addedMessageShownEventIds = mutableSetOf<Long>()

    var collectibles by mutableStateOf<List<Collectible>>(emptyList())
        private set

    var isEventObserved by mutableStateOf(false)
        private set
    var isEventAgended by mutableStateOf(false)
        private set
    var currentEventNote by mutableStateOf("")
        private set

    var uiState by mutableStateOf<EventDetailUiState>(EventDetailUiState.Loading)
        private set

    var agendaFilter by mutableStateOf(AgendaFilter.ALL)
        private set

    private val dbMutex = Mutex()
    private val latestSaveVersionByEvent = ConcurrentHashMap<Long, Long>()

    init {
        loadCollectibles()
        cleanupDuplicates()
    }

    private fun cleanupDuplicates() {
        viewModelScope.launch {
            dbMutex.withLock {
                withContext(ioDispatcher) {
                    val all = repo.getAll()
                    val groups = all.groupBy { it.eventId }
                    groups.forEach { (_, records) ->
                        if (records.size > 1) {
                            val bestRecord = records
                                .sortedWith(
                                    compareByDescending<Collectible> { it.observed }
                                        .thenByDescending { it.notes?.length ?: -1L }
                                )
                                .first()
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
            val result = withContext(ioDispatcher) {
                repo.getAll()
            }
            collectibles = result
        }
    }

    fun loadEvent(eventId: Long) {
        viewModelScope.launch {
            uiState = EventDetailUiState.Loading
            runCatching {
                withContext(ioDispatcher) {
                    val record = repo.getByEventId(eventId)
                    Triple(record?.observed == 1L, record?.agended == 1L, record?.notes ?: "")
                }
            }.onSuccess { (observed, agended, note) ->
                isEventObserved = observed
                isEventAgended = agended
                currentEventNote = note
                uiState = EventDetailUiState.Success(eventId)
            }.onFailure { error ->
                uiState = EventDetailUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun updateEventStatus(eventId: Long) {
        val record = collectibles.lastOrNull { it.eventId == eventId }
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
        val version = (latestSaveVersionByEvent[eventId] ?: 0L) + 1L
        latestSaveVersionByEvent[eventId] = version
        viewModelScope.launch {
            saveEventState(eventId, eventName, date, note, agended, observed, version)
        }
    }

    private suspend fun saveEventState(
        eventId: Long,
        eventName: String,
        date: String,
        note: String,
        agended: Boolean,
        observed: Boolean,
        version: Long = latestSaveVersionByEvent[eventId] ?: 0L
    ) {
        dbMutex.withLock {
            val latestVersion = latestSaveVersionByEvent[eventId] ?: 0L
            if (version < latestVersion) return

            withContext(ioDispatcher) {
                val existing = repo.getByEventId(eventId)

                val finalObserved = if (observed) 1 else 0
                val finalAgended = if (agended) 1 else 0
                val shouldExist = agended || observed || note.isNotBlank()

                if (shouldExist) {
                    if (existing != null) {
                        repo.updateObserved(existing.id, finalObserved)
                        repo.updateAgended(existing.id, finalAgended)
                        repo.updateNotes(existing.id, note.ifBlank { null })
                    } else {
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
                    repo.deleteByEventId(eventId)
                }
            }
        }
        loadCollectibles()
        loadEvent(eventId)
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                repo.deleteById(id)
            }
            loadCollectibles()
        }
    }

    fun markObserved(observed: Boolean) {
        isEventObserved = observed
    }

    fun updateAgendaFilter(filter: AgendaFilter) {
        agendaFilter = filter
    }

    fun hasShownAddedMessage(eventId: Long): Boolean = addedMessageShownEventIds.contains(eventId)

    fun markAddedMessageShown(eventId: Long) {
        addedMessageShownEventIds.add(eventId)
    }

}
