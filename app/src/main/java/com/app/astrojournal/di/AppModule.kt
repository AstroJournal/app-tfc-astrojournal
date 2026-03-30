package com.app.astrojournal.di

import android.content.Context
import com.app.shared.data.db.CollectibleRepository
import com.app.shared.data.db.MeetupEventRepository
import com.app.shared.data.db.UserRepository
import com.astrojournal.shared.data.db.AstrojournalDatabase
import com.astrojournal.shared.data.db.DatabaseDriverFactory


object AppModule {
    lateinit var database: AstrojournalDatabase
    lateinit var collectibleRepository: CollectibleRepository
    lateinit var userRepository: UserRepository
    lateinit var meetupEventRepository: MeetupEventRepository

    fun init(context: Context) {
        val driverFactory = DatabaseDriverFactory(context)
        val driver = driverFactory.createDriver()
        database = AstrojournalDatabase(driver)
        collectibleRepository = CollectibleRepository(database.collectibleQueries)
        userRepository = UserRepository(database.userQueries)
        meetupEventRepository = MeetupEventRepository(database.meetupEventQueries)
    }
}

