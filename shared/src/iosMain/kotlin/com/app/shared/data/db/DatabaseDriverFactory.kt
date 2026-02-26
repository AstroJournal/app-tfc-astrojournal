package com.astrojournal.shared.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

// Actual implementation for iOS
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AstrojournalDatabase.Schema, "astrojournal.db")
    }
}
