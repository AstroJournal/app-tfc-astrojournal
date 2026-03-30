package com.app.astrojournal.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.astrojournal.data.model.ApodUi
import com.app.astrojournal.data.model.VisibilityUi
import com.app.astrojournal.data.repository.SpaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class EventOfTheDayViewModel(
    private val repository: SpaceRepository = SpaceRepository()
) : ViewModel() {

    var apodState by mutableStateOf<RemoteUiState<ApodUi>>(RemoteUiState.Loading)
        private set

    var visibilityState by mutableStateOf<RemoteUiState<VisibilityUi>>(RemoteUiState.Loading)
        private set

    init {
        load()
    }

    fun load() {
        val today = LocalDate.now()
        viewModelScope.launch {
            apodState = RemoteUiState.Loading
            val apod = withContext(Dispatchers.IO) { repository.getApodWithFallback(today) }
            apodState = RemoteUiState.Success(apod)
        }

        viewModelScope.launch {
            visibilityState = RemoteUiState.Loading
            val visibility = withContext(Dispatchers.IO) { repository.getVisibilityWithFallback(today) }
            visibilityState = RemoteUiState.Success(visibility)
        }
    }
}
