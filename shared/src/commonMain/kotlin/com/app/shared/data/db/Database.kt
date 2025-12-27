package com.astrojournal.shared.data.db

import com.app.shared.data.db.DatabaseDriverFactory

class Database(driverFactory: DatabaseDriverFactory) {
    private val driver = driverFactory.createDriver()
    val db = AstrojournalDatabase(driver)

    val observationQueries = db.observationQueries
}

