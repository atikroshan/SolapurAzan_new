package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow

class AzanForegroundService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: android.os.PowerManager.WakeLock? = null
    private var isScreenReceiverRegistered = false

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                // User requirement: When phone off/power button is pressed, immediately stop audio
                stopAzanAudio()
            }
        }
    }

    companion object {
        var serviceInstance: AzanForegroundService? = null
        val isPlayingAzan = MutableStateFlow(false)
        val currentPlayingPrayerName = MutableStateFlow<String?>(null)
        val lastAudioFinishedTime = MutableStateFlow(0L)
        val lastAudioPrayerIndex = MutableStateFlow(-1)

        fun stopService(context: Context) {
            try {
                serviceInstance?.stopAzanAudio()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            try {
                val stopIntent = Intent(context, AzanForegroundService::class.java).apply {
                    action = "STOP_AZAN"
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(stopIntent)
                } else {
                    context.startService(stopIntent)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        fun prayerNameToIndex(name: String?): Int {
            return when (name?.lowercase()?.trim()) {
                "fajr" -> 0
                "dhuhr" -> 1
                "jumah", "juma" -> 1
                "asr" -> 2
                "maghrib" -> 3
                "isha" -> 4
                "tahajjud" -> 5
                else -> -1
            }
        }

        fun initFromPrefs(context: Context) {
            try {
                val prefs = context.getSharedPreferences("azan_prefs", Context.MODE_PRIVATE)
                val savedTime = prefs.getLong("last_audio_finished_time", 0L)
                val savedIdx = prefs.getInt("last_audio_prayer_index", -1)
                if (savedTime > lastAudioFinishedTime.value) {
                    lastAudioFinishedTime.value = savedTime
                    lastAudioPrayerIndex.value = savedIdx
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun recordAudioFinished(context: Context, timeMs: Long, index: Int) {
            lastAudioFinishedTime.value = timeMs
            try {
                val prefs = context.getSharedPreferences("azan_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putLong("last_audio_finished_time", timeMs)
                    .putInt("last_audio_prayer_index", index)
                    .apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceInstance = this
        initFromPrefs(this)
        val azanName = intent?.getStringExtra("AZAN_NAME") ?: "Azan"
        val pIdx = prayerNameToIndex(azanName)
        if (pIdx != -1) {
            lastAudioPrayerIndex.value = pIdx
        }

        if (intent?.action == "STOP_AZAN") {
            stopAzanAudio()
            return START_NOT_STICKY
        }

        currentPlayingPrayerName.value = azanName
        
        createNotificationChannel()
        
        val stopIntent = Intent(this, AzanForegroundService::class.java).apply {
            action = "STOP_AZAN"
        }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("FROM_ALARM", true)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
        }
        val mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "azan_channel")
            .setContentTitle("It's time for $azanName")
            .setContentText("Azan is playing")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(mainPendingIntent)
            .setFullScreenIntent(mainPendingIntent, true)
            .addAction(R.mipmap.ic_launcher, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()
            
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(1, notification)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        // Requirement: Keep CPU running to ensure audio plays completely
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
            wakeLock = pm?.newWakeLock(
                android.os.PowerManager.PARTIAL_WAKE_LOCK,
                "offlineazan:service_audio_wake"
            )
            // Acquire indefinitely, we will release it in onDestroy
            wakeLock?.acquire()
            startActivity(mainIntent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        registerScreenOffReceiver()
        playAzan()

        return START_NOT_STICKY
    }

    private fun registerScreenOffReceiver() {
        if (!isScreenReceiverRegistered) {
            try {
                val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    androidx.core.content.ContextCompat.registerReceiver(
                        this,
                        screenOffReceiver,
                        filter,
                        androidx.core.content.ContextCompat.RECEIVER_EXPORTED
                    )
                } else {
                    registerReceiver(screenOffReceiver, filter)
                }
                isScreenReceiverRegistered = true
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun unregisterScreenOffReceiver() {
        if (isScreenReceiverRegistered) {
            try {
                unregisterReceiver(screenOffReceiver)
                isScreenReceiverRegistered = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopAzanAudio() {
        unregisterScreenOffReceiver()
        isPlayingAzan.value = false
        currentPlayingPrayerName.value = null
        recordAudioFinished(this, System.currentTimeMillis(), lastAudioPrayerIndex.value)
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                mediaPlayer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer = null
        }
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopSelf()
    }

    private fun playAzan() {
        if (mediaPlayer == null) {
            try {
                // Play authentic Azan audio from res/raw/azan.mp3
                mediaPlayer = MediaPlayer.create(this, R.raw.azan)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (mediaPlayer == null) {
                try {
                    val resId = resources.getIdentifier("azan", "raw", packageName)
                    if (resId != 0) {
                        mediaPlayer = MediaPlayer.create(this, resId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (mediaPlayer == null) {
                try {
                    val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    mediaPlayer = MediaPlayer.create(this, alarmUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // User requirement: Play once and then close
            mediaPlayer?.let { player ->
                player.isLooping = false
                isPlayingAzan.value = true

                player.setOnCompletionListener {
                    isPlayingAzan.value = false
                    currentPlayingPrayerName.value = null
                    recordAudioFinished(this@AzanForegroundService, System.currentTimeMillis(), lastAudioPrayerIndex.value)
                    stopSelf()
                }

                player.setOnErrorListener { _, _, _ ->
                    isPlayingAzan.value = false
                    currentPlayingPrayerName.value = null
                    recordAudioFinished(this@AzanForegroundService, System.currentTimeMillis(), lastAudioPrayerIndex.value)
                    stopSelf()
                    true
                }

                try {
                    player.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                    isPlayingAzan.value = false
                    currentPlayingPrayerName.value = null
                    recordAudioFinished(this@AzanForegroundService, System.currentTimeMillis(), lastAudioPrayerIndex.value)
                    stopSelf()
                }
            } ?: run {
                isPlayingAzan.value = false
                currentPlayingPrayerName.value = null
                recordAudioFinished(this, System.currentTimeMillis(), lastAudioPrayerIndex.value)
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceInstance == this) {
            serviceInstance = null
        }
        unregisterScreenOffReceiver()
        
        // Release wake lock if held
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        wakeLock = null

        if (isPlayingAzan.value) {
            recordAudioFinished(this, System.currentTimeMillis(), lastAudioPrayerIndex.value)
        }
        isPlayingAzan.value = false
        currentPlayingPrayerName.value = null
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "azan_channel",
                "Azan Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Azan audio playback service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
