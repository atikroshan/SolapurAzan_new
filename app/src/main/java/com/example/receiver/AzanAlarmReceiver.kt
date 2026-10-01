package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.service.AzanForegroundService

class AzanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val azanName = intent.getStringExtra("AZAN_NAME") ?: return
        Log.d("AzanAlarmReceiver", "Received alarm for $azanName")
        
        val serviceIntent = Intent(context, AzanForegroundService::class.java).apply {
            putExtra("AZAN_NAME", azanName)
        }
        
        // Start Foreground Service safely
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        // Wake screen and launch MainActivity
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? android.os.PowerManager
            @Suppress("DEPRECATION")
            val wl = pm?.newWakeLock(
                android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP or
                android.os.PowerManager.ON_AFTER_RELEASE,
                "offlineazan:alarm_receiver_wake"
            )
            wl?.acquire(10000L)

            val launchIntent = Intent(context, com.example.MainActivity::class.java).apply {
                putExtra("FROM_ALARM", true)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                )
            }
            context.startActivity(launchIntent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
