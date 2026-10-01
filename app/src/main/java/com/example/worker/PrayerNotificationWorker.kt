package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.PreferencesRepository
import kotlinx.coroutines.flow.first

class PrayerNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prayerName = inputData.getString(KEY_PRAYER_NAME) ?: "Prayer"
        val prayerTime = inputData.getString(KEY_PRAYER_TIME) ?: ""

        // Check user preferences to ensure notifications are enabled for this prayer
        val prefs = PreferencesRepository(context)
        val isEnabled = prefs.isAzanEnabled(prayerName).first()
        if (!isEnabled) {
            return Result.success()
        }

        sendNotification(prayerName, prayerTime)
        return Result.success()
    }

    private fun sendNotification(prayerName: String, prayerTime: String) {
        val channelId = "prayer_notifications_workmanager"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Prayer Times (Azan Reminders)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when prayer times are reached based on the database schedule"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("FROM_ALARM", true)
            putExtra("PRAYER_NAME", prayerName)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val title = "Time for $prayerName Prayer"
        val timeDisplay = if (prayerTime.isNotEmpty()) " ($prayerTime)" else ""
        val content = "It is now time for $prayerName$timeDisplay. Aao Allah ki raah mein chalein."

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(prayerName.hashCode(), notification)
    }

    companion object {
        const val KEY_PRAYER_NAME = "key_prayer_name"
        const val KEY_PRAYER_TIME = "key_prayer_time"
    }
}
