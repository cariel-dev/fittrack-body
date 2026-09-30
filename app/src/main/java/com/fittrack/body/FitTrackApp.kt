package com.fittrack.body

import android.app.Application
import com.fittrack.body.data.db.AppDatabase
import com.fittrack.body.data.export.ExportManager
import com.fittrack.body.data.repo.TrackerRepository

class FitTrackApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var repository: TrackerRepository
        private set
    lateinit var exportManager: ExportManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = TrackerRepository(
            weightDao = database.weightDao(),
            circumferenceDao = database.circumferenceDao(),
            skinfoldDao = database.skinfoldDao()
        )
        exportManager = ExportManager(this, repository)
    }
}
