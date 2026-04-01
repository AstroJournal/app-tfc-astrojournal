package com.app.shared.data.db

import com.astrojournal.shared.data.db.User
import com.astrojournal.shared.data.db.UserQueries

sealed class UserInsertResult {
    object Success : UserInsertResult()
    object DuplicateUsername : UserInsertResult()
    object DuplicateEmail : UserInsertResult()
    data class Error(val message: String) : UserInsertResult()
}

class UserRepository(
    private val queries: UserQueries
) {

    fun insertUser(
        username: String,
        email: String,
        passwordHash: String,
        createdAt: String
    ): UserInsertResult {
        // Check uniqueness manually before insert so we can give specific feedback
        if (queries.selectByUsername(username).executeAsOneOrNull() != null) {
            return UserInsertResult.DuplicateUsername
        }
        if (queries.selectByEmail(email).executeAsOneOrNull() != null) {
            return UserInsertResult.DuplicateEmail
        }
        return try {
            queries.insertUser(
                username = username,
                email = email,
                password = passwordHash,
                createdAt = createdAt
            )
            UserInsertResult.Success
        } catch (e: Exception) {
            UserInsertResult.Error(e.message ?: "Error desconocido")
        }
    }

    fun updatePassword(id: Long, newPasswordHash: String) {
        // El orden de SQLDelight es (password, id) basado en el archivo .sq
        queries.updatePassword(newPasswordHash, id)
    }

    fun findByCredentials(email: String, passwordHash: String): User? {
        return queries.selectByCredentials(email, passwordHash).executeAsOneOrNull()
    }

    fun findByUsername(username: String): User? {
        return queries.selectByUsername(username).executeAsOneOrNull()
    }

    fun findByEmail(email: String): User? {
        return queries.selectByEmail(email).executeAsOneOrNull()
    }

    fun getAll(): List<User> {
        return queries.selectAll().executeAsList()
    }

    fun deleteById(id: Long) {
        queries.deleteById(id)
    }
}
