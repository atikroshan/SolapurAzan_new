package com.example

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testIslamicDateString() {
    // Test June 16, 2026 before Maghrib (isAfterMaghrib = false) -> 29 Dhu al-Hijjah 1447 AH
    val resultBefore = getIslamicDateString(2026, 6, 16, false, "en")
    assertEquals("29 Dhu al-Hijjah 1447 AH", resultBefore)
    
    // Test June 16, 2026 after Maghrib (isAfterMaghrib = true) -> 1 Muharram 1448 AH
    val resultAfter = getIslamicDateString(2026, 6, 16, true, "en")
    assertEquals("1 Muharram 1448 AH", resultAfter)
    
    // Test Hindi conversion
    val resultBeforeHi = getIslamicDateString(2026, 6, 16, false, "hi")
    assertEquals("29 ज़ु अल-हिज्जह 1447 हिजरी", resultBeforeHi)
    
    // Test Urdu conversion
    val resultAfterUr = getIslamicDateString(2026, 6, 16, true, "ur")
    assertEquals("1 محرم 1448 ہجری", resultAfterUr)
  }

  private fun getIslamicDateString(gregorianYear: Int, gregorianMonth: Int, gregorianDay: Int, isAfterMaghrib: Boolean, language: String): String {
    val baseLocalDate = java.time.LocalDate.of(gregorianYear, gregorianMonth, gregorianDay)
    val localDate = if (isAfterMaghrib) baseLocalDate else baseLocalDate.minusDays(1)
    val hijrahDate = java.time.chrono.HijrahDate.from(localDate)
    val hYear = hijrahDate.get(java.time.temporal.ChronoField.YEAR)
    val hMonth = hijrahDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
    val hDay = hijrahDate.get(java.time.temporal.ChronoField.DAY_OF_MONTH)
    
    val monthName = when (language) {
        "hi" -> when (hMonth) {
            1 -> "मुहर्रम"
            2 -> "सफ़र"
            3 -> "रबी अल-अव्वल"
            4 -> "रबी अउ-थानी"
            5 -> "जमाद अल-अव्वल"
            6 -> "जमाद अउ-थानी"
            7 -> "रजब"
            8 -> "शबान"
            9 -> "रमज़ान"
            10 -> "शव्वाल"
            11 -> "ज़ु अल-क़ादा"
            12 -> "ज़ु अल-हिज्जह"
            else -> ""
        }
        "ur" -> when (hMonth) {
            1 -> "محرم"
            2 -> "صفر"
            3 -> "ربیع الاول"
            4 -> "ربیع الثانی"
            5 -> "جمادی الاول"
            6 -> "جمادی الثانی"
            7 -> "رجب"
            8 -> "شعبان"
            9 -> "رمضان"
            10 -> "شوال"
            11 -> "ذوالقعدہ"
            12 -> "ذوالحجہ"
            else -> ""
        }
        else -> when (hMonth) {
            1 -> "Muharram"
            2 -> "Safar"
            3 -> "Rabi' al-Awwal"
            4 -> "Rabi' ath-Thani"
            5 -> "Jumada al-Ula"
            6 -> "Jumada al-Akhirah"
            7 -> "Rajab"
            8 -> "Sha'ban"
            9 -> "Ramadan"
            10 -> "Shawwal"
            11 -> "Dhu al-Qadah"
            12 -> "Dhu al-Hijjah"
            else -> ""
        }
    }
    
    val suffix = when (language) {
        "hi" -> "हिजरी"
        "ur" -> "ہجری"
        else -> "AH"
    }
    
    return "$hDay $monthName $hYear $suffix"
  }
}
