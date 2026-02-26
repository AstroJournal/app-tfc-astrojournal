package com.app.shared.data.db

import com.astrojournal.shared.data.db.AstrojournalDatabase
import com.astrojournal.shared.data.db.DatabaseDriverFactory

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AstrojournalDatabase(databaseDriverFactory.createDriver())
    val collectibleQueries = database.collectibleQueries
}