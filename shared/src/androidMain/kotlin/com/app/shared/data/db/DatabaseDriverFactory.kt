package com.astrojournal.shared.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver() =
        AndroidSqliteDriver(AstrojournalDatabase.Schema, context, "astrojournal.db")
}
