package com.astrojournal.shared.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS implementation of DatabaseDriverFactory.
 * Creates a NativeSqliteDriver.
 */
actual class DatabaseDriverFactory {
    /**
     * Creates the Native SQL driver.
     * @return NativeSqliteDriver with the schema.
     */
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(AstrojournalDatabase.Schema, "astrojournal.db")
}
