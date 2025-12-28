package com.astrojournal.shared.data.db

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    /**
     * Creates a SqlDriver for the current platform.
     * @return SqlDriver instance.
     */
    fun createDriver(): SqlDriver
}