package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerLogDao {
    @Query("SELECT * FROM prayer_logs")
    fun getAllLogs(): Flow<List<PrayerLog>>

    @Query("SELECT * FROM prayer_logs WHERE month = :month AND day = :day LIMIT 1")
    fun getLogForDate(month: Int, day: Int): Flow<PrayerLog?>

    @Query("SELECT * FROM prayer_logs WHERE month = :month AND day = :day LIMIT 1")
    suspend fun getLogForDateSync(month: Int, day: Int): PrayerLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PrayerLog)

    @Update
    suspend fun updateLog(log: PrayerLog)

    @Delete
    suspend fun deleteLog(log: PrayerLog)
}
