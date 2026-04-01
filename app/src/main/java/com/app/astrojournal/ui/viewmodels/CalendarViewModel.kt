package com.app.astrojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shared.data.db.CollectibleRepository
import com.astrojournal.shared.data.db.Collectible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the Calendar screen.
 * Loads all collectibles so the calendar can show agenda-dot indicators.
 */
class CalendarViewModel(
    private val repo: CollectibleRepository
) : ViewModel() {

    private val _collectibles = MutableStateFlow<List<Collectible>>(emptyList())
    val collectibles: StateFlow<List<Collectible>> = _collectibles

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val all = withContext(Dispatchers.IO) { repo.getAll() }
            _collectibles.value = all
        }
    }

    /**
     * Removes the 'agended' flag from a collectible record.
     * If neither observed nor notes are set, the record is deleted entirely.
     */
    fun unagendaEvent(collectibleId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existing = repo.getAll().find { it.id == collectibleId } ?: return@withContext
                val isObserved = existing.observed == 1L
                val hasNotes = !existing.notes.isNullOrBlank()
                if (isObserved || hasNotes) {
                    repo.updateAgended(collectibleId, 0)
                } else {
                    repo.deleteById(collectibleId)
                }
            }
            load()
        }
    }
}
