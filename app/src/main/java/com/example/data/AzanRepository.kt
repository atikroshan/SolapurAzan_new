package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AzanRepository(
    private val context: Context? = null,
    private val dao: AzanDao,
    private val prayerLogDao: PrayerLogDao? = null
) {
    constructor(dao: AzanDao) : this(null, dao, null)

    fun getTimingsForDate(month: Int, day: Int): Flow<AzanTiming?> =
        dao.getTimingsForDate(month, day)

    suspend fun getTimingsForDateSync(month: Int, day: Int): AzanTiming? =
        dao.getTimingsForDateSync(month, day)

    fun getAllLogs(): Flow<List<PrayerLog>> =
        prayerLogDao?.getAllLogs() ?: dao.getAllLogs()

    suspend fun insertLog(log: PrayerLog) {
        prayerLogDao?.insertLog(log) ?: dao.insertLog(log)
    }

    suspend fun updatePrayerTime(month: Int, day: Int, prayerName: String, newTime: String, applyToAll: Boolean) {
        withContext(Dispatchers.IO) {
            if (applyToAll) {
                when (prayerName.lowercase()) {
                    "fajr" -> dao.updateFajrAll(newTime)
                    "dhuhr", "zohr", "jum'ah" -> dao.updateDhuhrAll(newTime)
                    "asr" -> dao.updateAsrAll(newTime)
                    "maghrib" -> dao.updateMaghribAll(newTime)
                    "isha" -> dao.updateIshaAll(newTime)
                }
            } else {
                when (prayerName.lowercase()) {
                    "fajr" -> dao.updateFajr(month, day, newTime)
                    "dhuhr", "zohr", "jum'ah" -> dao.updateDhuhr(month, day, newTime)
                    "asr" -> dao.updateAsr(month, day, newTime)
                    "maghrib" -> dao.updateMaghrib(month, day, newTime)
                    "isha" -> dao.updateIsha(month, day, newTime)
                }
            }
        }
    }

    suspend fun initializeDatabaseIfNeeded() {
        withContext(Dispatchers.IO) {
            val count = dao.getTimingsCount()
            if (count > 0) return@withContext

            val timingsList = mutableListOf<AzanTiming>()
            val daysInMonths = listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

            // Monthly base timings for Indian Standard Time (IST)
            val baseTimings = listOf(
                // Month 1 (Jan)
                Triple("05:40", "07:05", Triple("12:35", "16:25", Pair("18:05", "19:25"))),
                // Month 2 (Feb)
                Triple("05:35", "06:55", Triple("12:40", "16:40", Pair("18:25", "19:40"))),
                // Month 3 (Mar)
                Triple("05:15", "06:35", Triple("12:35", "16:45", Pair("18:35", "19:50"))),
                // Month 4 (Apr)
                Triple("04:45", "06:10", Triple("12:30", "16:45", Pair("18:50", "20:05"))),
                // Month 5 (May)
                Triple("04:25", "05:50", Triple("12:25", "16:50", Pair("19:05", "20:25"))),
                // Month 6 (Jun)
                Triple("04:15", "05:45", Triple("12:30", "17:00", Pair("19:15", "20:40"))),
                // Month 7 (Jul)
                Triple("04:25", "05:55", Triple("12:35", "17:05", Pair("19:15", "20:40"))),
                // Month 8 (Aug)
                Triple("04:40", "06:05", Triple("12:35", "16:55", Pair("19:00", "20:20"))),
                // Month 9 (Sep)
                Triple("04:39", "06:15", Triple("13:15", "17:15", Pair("18:38", "20:00"))),
                // Month 10 (Oct)
                Triple("04:55", "06:20", Triple("12:20", "16:15", Pair("18:15", "19:30"))),
                // Month 11 (Nov)
                Triple("05:10", "06:35", Triple("12:20", "16:05", Pair("18:00", "19:15"))),
                // Month 12 (Dec)
                Triple("05:30", "06:55", Triple("12:30", "16:15", Pair("18:00", "19:20")))
            )

            for (m in 1..12) {
                val totalDays = daysInMonths[m - 1]
                val base = baseTimings[m - 1]
                for (d in 1..totalDays) {
                    timingsList.add(
                        AzanTiming(
                            month = m,
                            day = d,
                            fajr = base.first,
                            sunrise = base.second,
                            dhuhr = base.third.first,
                            asr = base.third.second,
                            maghrib = base.third.third.first,
                            isha = base.third.third.second
                        )
                    )
                }
            }

            dao.insertTimings(timingsList)
        }
    }
}
