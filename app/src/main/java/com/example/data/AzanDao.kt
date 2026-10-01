package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AzanDao {
    @Query("SELECT * FROM azan_timings WHERE month = :month AND day = :day LIMIT 1")
    fun getTimingsForDate(month: Int, day: Int): Flow<AzanTiming?>

    @Query("SELECT * FROM azan_timings WHERE month = :month AND day = :day LIMIT 1")
    suspend fun getTimingsForDateSync(month: Int, day: Int): AzanTiming?

    @Query("SELECT COUNT(*) FROM azan_timings")
    suspend fun getTimingsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimings(timings: List<AzanTiming>)

    @Query("UPDATE azan_timings SET fajr = :newTime WHERE month = :month AND day = :day")
    suspend fun updateFajr(month: Int, day: Int, newTime: String)

    @Query("UPDATE azan_timings SET dhuhr = :newTime WHERE month = :month AND day = :day")
    suspend fun updateDhuhr(month: Int, day: Int, newTime: String)

    @Query("UPDATE azan_timings SET asr = :newTime WHERE month = :month AND day = :day")
    suspend fun updateAsr(month: Int, day: Int, newTime: String)

    @Query("UPDATE azan_timings SET maghrib = :newTime WHERE month = :month AND day = :day")
    suspend fun updateMaghrib(month: Int, day: Int, newTime: String)

    @Query("UPDATE azan_timings SET isha = :newTime WHERE month = :month AND day = :day")
    suspend fun updateIsha(month: Int, day: Int, newTime: String)

    @Query("UPDATE azan_timings SET fajr = :newTime")
    suspend fun updateFajrAll(newTime: String)

    @Query("UPDATE azan_timings SET dhuhr = :newTime")
    suspend fun updateDhuhrAll(newTime: String)

    @Query("UPDATE azan_timings SET asr = :newTime")
    suspend fun updateAsrAll(newTime: String)

    @Query("UPDATE azan_timings SET maghrib = :newTime")
    suspend fun updateMaghribAll(newTime: String)

    @Query("UPDATE azan_timings SET isha = :newTime")
    suspend fun updateIshaAll(newTime: String)

    @Query("SELECT * FROM prayer_logs")
    fun getAllLogs(): Flow<List<PrayerLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PrayerLog)
}
