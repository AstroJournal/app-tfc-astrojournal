package com.astrojournal.shared.data.db



/**
 * Wrapper class for the SqlDelight database.
 * This class handles the creation of the database driver and exposes the database queries.
 */
class Database(driverFactory: DatabaseDriverFactory) {
    // initialize the driver using the provided factory
    private val driver = driverFactory.createDriver()
    
    // create the database instance
    val db = AstrojournalDatabase(driver)

    // expose the queries for collectibles
    val collectibleQueries = db.collectibleQueries
}

