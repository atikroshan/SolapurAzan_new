package com.example.worker

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.data.AzanDatabase
import com.example.data.AzanTiming
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object PrayerWorkScheduler {

    private const val TAG = "PrayerWorkScheduler"
    private const val DAILY_SYNC_WORK_NAME = "prayer_daily_sync_work"

    suspend fun scheduleAllPrayerNotifications(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val db = AzanDatabase.getDatabase(context)
        val dao = db.azanDao()

        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        val currentMonth = cal.get(Calendar.MONTH) + 1
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)

        val todayTimings = dao.getTimingsForDateSync(currentMonth, currentDay)
        if (todayTimings == null) {
            Log.w(TAG, "No prayer timings found in database for $currentMonth/$currentDay")
            return
        }

        // Cal for tomorrow if prayer has already passed today
        val tomorrowCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val tomorrowTimings = dao.getTimingsForDateSync(
            tomorrowCal.get(Calendar.MONTH) + 1,
            tomorrowCal.get(Calendar.DAY_OF_MONTH)
        ) ?: todayTimings

        val isFridayToday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        val isFridayTomorrow = tomorrowCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        val todayDhuhr = if (isFridayToday) "12:30" else todayTimings.dhuhr
        val tomorrowDhuhr = if (isFridayTomorrow) "12:30" else tomorrowTimings.dhuhr

        schedulePrayer(workManager, "Fajr", todayTimings.fajr, tomorrowTimings.fajr)
        schedulePrayer(workManager, "Dhuhr", todayDhuhr, tomorrowDhuhr)
        schedulePrayer(workManager, "Asr", todayTimings.asr, tomorrowTimings.asr)
        schedulePrayer(workManager, "Maghrib", todayTimings.maghrib, tomorrowTimings.maghrib)
        schedulePrayer(workManager, "Isha", todayTimings.isha, tomorrowTimings.isha)

        // Schedule daily periodic sync to refresh notifications daily
        scheduleDailySync(context)
    }

    private fun schedulePrayer(
        workManager: WorkManager,
        prayerName: String,
        todayTime: String,
        tomorrowTime: String
    ) {
        val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        val targetCal = getCalendarForTime(todayTime, isTomorrow = false)

        val (scheduledCal, prayerTime) = if (targetCal.timeInMillis > now.timeInMillis) {
            targetCal to todayTime
        } else {
            getCalendarForTime(tomorrowTime, isTomorrow = true) to tomorrowTime
        }

        val delayMillis = scheduledCal.timeInMillis - now.timeInMillis
        if (delayMillis <= 0) return

        val workRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    PrayerNotificationWorker.KEY_PRAYER_NAME to prayerName,
                    PrayerNotificationWorker.KEY_PRAYER_TIME to prayerTime
                )
            )
            .addTag("prayer_notification")
            .build()

        val uniqueWorkName = "prayer_notify_$prayerName"
        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        Log.d(TAG, "Scheduled WorkManager notification for $prayerName in ${delayMillis / 1000 / 60} minutes")
    }

    private fun getCalendarForTime(time24: String, isTomorrow: Boolean): Calendar {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
        if (isTomorrow) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        val parts = time24.split(":")
        if (parts.size == 2) {
            val h = parts[0].toIntOrNull() ?: 0
            val m = parts[1].toIntOrNull() ?: 0
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
        }
        return cal
    }

    fun scheduleDailySync(context: Context) {
        try {
            val syncRequest = PeriodicWorkRequestBuilder<PrayerDailySyncWorker>(
                12, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                DAILY_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun cancelPrayerNotification(context: Context, prayerName: String) {
        WorkManager.getInstance(context).cancelUniqueWork("prayer_notify_$prayerName")
    }
}
