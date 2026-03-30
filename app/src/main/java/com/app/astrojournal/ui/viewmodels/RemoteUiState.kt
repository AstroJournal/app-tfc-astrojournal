package com.app.astrojournal.ui.viewmodels

sealed class RemoteUiState<out T> {
    data object Loading : RemoteUiState<Nothing>()
    data class Success<T>(val data: T) : RemoteUiState<T>()
    data class Error(val message: String) : RemoteUiState<Nothing>()
}
