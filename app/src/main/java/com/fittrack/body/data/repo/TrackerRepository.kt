package com.fittrack.body.data.repo

import com.fittrack.body.data.db.CircumferenceDao
import com.fittrack.body.data.db.CircumferenceEntry
import com.fittrack.body.data.db.SkinfoldDao
import com.fittrack.body.data.db.SkinfoldEntry
import com.fittrack.body.data.db.WeightDao
import com.fittrack.body.data.db.WeightEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrackerRepository(
    private val weightDao: WeightDao,
    private val circumferenceDao: CircumferenceDao,
    private val skinfoldDao: SkinfoldDao
) {
    fun observeWeights(): Flow<List<WeightEntry>> = weightDao.observeAll()
    fun observeCircumferences(): Flow<List<CircumferenceEntry>> = circumferenceDao.observeAll()
    fun observeSkinfolds(): Flow<List<SkinfoldEntry>> = skinfoldDao.observeAll()

    suspend fun getWeightsOnce() = weightDao.getAllOnce()
    suspend fun getCircumferencesOnce() = circumferenceDao.getAllOnce()
    suspend fun getSkinfoldsOnce() = skinfoldDao.getAllOnce()

    suspend fun saveWeight(date: String, weightKg: Float, note: String = "") {
        weightDao.upsert(WeightEntry(date, weightKg, note))
    }

    suspend fun saveCircumference(entry: CircumferenceEntry) {
        circumferenceDao.upsert(entry.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun saveSkinfold(entry: SkinfoldEntry) {
        skinfoldDao.upsert(entry.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteWeight(date: String) = weightDao.deleteByDate(date)
    suspend fun deleteCircumference(date: String) = circumferenceDao.deleteByDate(date)
    suspend fun deleteSkinfold(date: String) = skinfoldDao.deleteByDate(date)

    suspend fun restoreBackup(
        weights: List<WeightEntry>,
        circumferences: List<CircumferenceEntry>,
        skinfolds: List<SkinfoldEntry>
    ) {
        weights.forEach { weightDao.upsert(it) }
        circumferences.forEach { circumferenceDao.upsert(it) }
        skinfolds.forEach { skinfoldDao.upsert(it) }
    }

    companion object {
        private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        fun today(): String = LocalDate.now().format(fmt)
    }
}
