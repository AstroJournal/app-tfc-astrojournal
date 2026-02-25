package com.app.astrojournal.di

import android.content.Context
import com.app.shared.data.db.CollectibleRepository
import com.astrojournal.shared.data.db.AstrojournalDatabase
import com.astrojournal.shared.data.db.DatabaseDriverFactory
import java.time.Instant


object AppModule {
    lateinit var database: AstrojournalDatabase
    lateinit var collectibleRepository: CollectibleRepository

    fun init(context: Context) {
        val driverFactory = DatabaseDriverFactory(context)
        val driver = driverFactory.createDriver()
        database = AstrojournalDatabase(driver)
        collectibleRepository = CollectibleRepository(database.collectibleQueries)
    }
}
