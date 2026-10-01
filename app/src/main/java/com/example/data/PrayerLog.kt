package com.example.data

import androidx.room.Entity

@Entity(tableName = "prayer_logs", primaryKeys = ["month", "day"])
data class PrayerLog(
    val month: Int,
    val day: Int,
    val fajrPrayed: Boolean = false,
    val dhuhrPrayed: Boolean = false,
    val asrPrayed: Boolean = false,
    val maghribPrayed: Boolean = false,
    val ishaPrayed: Boolean = false,
    val tahajjudPrayed: Boolean = false
) {
    fun isPerfectDay(): Boolean =
        fajrPrayed && dhuhrPrayed && asrPrayed && maghribPrayed && ishaPrayed

    fun getFivePrayersCount(): Int {
        var count = 0
        if (fajrPrayed) count++
        if (dhuhrPrayed) count++
        if (asrPrayed) count++
        if (maghribPrayed) count++
        if (ishaPrayed) count++
        return count
    }

    fun getCompletedPrayersCount(): Int {
        var count = getFivePrayersCount()
        if (tahajjudPrayed) count++
        return count
    }
}
