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

class ObservedEventsViewModel(
    private val repo: CollectibleRepository
) : ViewModel() {

    private val _observed = MutableStateFlow<List<Collectible>>(emptyList())
    val observed: StateFlow<List<Collectible>> = _observed

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val all = withContext(Dispatchers.IO) { repo.getAll() }
            _observed.value = all.filter { it.observed == 1L }
        }
    }

    fun updateNote(id: Long, note: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.updateNotes(id, note.ifBlank { null })
            }
            load()
        }
    }
}
