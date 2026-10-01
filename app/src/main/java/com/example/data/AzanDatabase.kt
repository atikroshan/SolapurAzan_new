package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AzanTiming::class, PrayerLog::class], version = 1, exportSchema = false)
abstract class AzanDatabase : RoomDatabase() {
    abstract fun azanDao(): AzanDao
    abstract fun prayerLogDao(): PrayerLogDao

    companion object {
        @Volatile
        private var INSTANCE: AzanDatabase? = null

        fun getDatabase(context: Context): AzanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AzanDatabase::class.java,
                    "azan_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
