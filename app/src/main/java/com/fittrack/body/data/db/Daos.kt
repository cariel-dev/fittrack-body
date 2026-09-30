package com.fittrack.body.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    suspend fun getAllOnce(): List<WeightEntry>

    @Query("SELECT * FROM weight_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): WeightEntry?

    @Upsert
    suspend fun upsert(entry: WeightEntry)

    @Query("DELETE FROM weight_entries WHERE date = :date")
    suspend fun deleteByDate(date: String)
}

@Dao
interface CircumferenceDao {
    @Query("SELECT * FROM circumference_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<CircumferenceEntry>>

    @Query("SELECT * FROM circumference_entries ORDER BY date DESC")
    suspend fun getAllOnce(): List<CircumferenceEntry>

    @Query("SELECT * FROM circumference_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): CircumferenceEntry?

    @Upsert
    suspend fun upsert(entry: CircumferenceEntry)

    @Query("DELETE FROM circumference_entries WHERE date = :date")
    suspend fun deleteByDate(date: String)
}

@Dao
interface SkinfoldDao {
    @Query("SELECT * FROM skinfold_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<SkinfoldEntry>>

    @Query("SELECT * FROM skinfold_entries ORDER BY date DESC")
    suspend fun getAllOnce(): List<SkinfoldEntry>

    @Query("SELECT * FROM skinfold_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SkinfoldEntry?

    @Upsert
    suspend fun upsert(entry: SkinfoldEntry)

    @Query("DELETE FROM skinfold_entries WHERE date = :date")
    suspend fun deleteByDate(date: String)
}
