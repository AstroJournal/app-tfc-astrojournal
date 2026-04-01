package com.app.astrojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shared.data.db.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = LoginUiState.Error("Invalid email format")
            return
        }
        if (password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please enter your password")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val passwordHash = sha256(password)
            val user = userRepository.findByCredentials(email, passwordHash)
            if (user != null) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Incorrect email or password")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
