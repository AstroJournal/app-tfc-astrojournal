package com.app.astrojournal.di

import android.content.Context
import com.app.shared.data.db.CollectibleRepository
import com.app.shared.data.db.CollectibleStore
import com.astrojournal.shared.data.db.AstrojournalDatabase
import com.astrojournal.shared.data.db.DatabaseDriverFactory


object AppModule {
    lateinit var database: AstrojournalDatabase
    lateinit var collectibleRepository: CollectibleStore

    fun init(context: Context) {
        val driverFactory = DatabaseDriverFactory(context)
        val driver = driverFactory.createDriver()
        database = AstrojournalDatabase(driver)
        collectibleRepository = CollectibleRepository(database.collectibleQueries)
    }
}
