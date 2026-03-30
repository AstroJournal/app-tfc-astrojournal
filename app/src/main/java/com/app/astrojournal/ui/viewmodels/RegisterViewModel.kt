package com.app.astrojournal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shared.data.db.UserInsertResult
import com.app.shared.data.db.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.time.LocalDateTime

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(
        username: String,
        email: String,
        password: String,
        repeatPassword: String
    ) {
        // --- Client-side validation ---
        if (username.isBlank()) {
            _uiState.value = RegisterUiState.Error("El nombre de usuario no puede estar vacío")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = RegisterUiState.Error("Formato de email inválido")
            return
        }
        if (password.length < 6) {
            _uiState.value = RegisterUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (password != repeatPassword) {
            _uiState.value = RegisterUiState.Error("Las contraseñas no coinciden")
            return
        }

        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val passwordHash = sha256(password)
            val createdAt = LocalDateTime.now().toString()

            when (val result = userRepository.insertUser(username, email, passwordHash, createdAt)) {
                is UserInsertResult.Success ->
                    _uiState.value = RegisterUiState.Success
                is UserInsertResult.DuplicateUsername ->
                    _uiState.value = RegisterUiState.Error("Este nombre de usuario ya está en uso")
                is UserInsertResult.DuplicateEmail ->
                    _uiState.value = RegisterUiState.Error("Este email ya está registrado")
                is UserInsertResult.Error ->
                    _uiState.value = RegisterUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
