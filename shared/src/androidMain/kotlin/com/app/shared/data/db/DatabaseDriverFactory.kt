package com.astrojournal.shared.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android implementation of DatabaseDriverFactory.
 * Creates an AndroidSqliteDriver.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    /**
     * Creates the Android SQL driver.
     * @return AndroidSqliteDriver with the schema and context.
     */
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AstrojournalDatabase.Schema, context, "astrojournal.db")
}
