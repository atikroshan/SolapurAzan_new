package com.example.data

import androidx.room.Entity

@Entity(tableName = "azan_timings", primaryKeys = ["month", "day"])
data class AzanTiming(
    val month: Int,
    val day: Int,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)
