package com.example.data

data class MasjidItem(
    val id: String,
    val name: String,
    val area: String,
    val city: String,
    val state: String,
    val fajrOffset: Int = 0,
    val dhuhrOffset: Int = 0,
    val asrOffset: Int = 0,
    val maghribOffset: Int = 0,
    val ishaOffset: Int = 0,
    val fajrJammatOffset: Int = 25,
    val dhuhrJammatOffset: Int = 20,
    val asrJammatOffset: Int = 20,
    val maghribJammatOffset: Int = 10,
    val ishaJammatOffset: Int = 20,
    val jumahAzanTime: String = "12:30",
    val jumahJammatTime: String = "13:30",
    val photoUrl: String = "",
    val fajrAzanFixed: String? = null,
    val fajrJammatFixed: String? = null,
    val zoharAzanFixed: String? = null,
    val zoharJammatFixed: String? = null,
    val asrAzanFixed: String? = null,
    val asrJammatFixed: String? = null,
    val maghribAzanFixed: String? = null,
    val maghribJammatFixed: String? = null,
    val ishaAzanFixed: String? = null,
    val ishaJammatFixed: String? = null,
    val adminId: String = "admin",
    val adminPassword: String = "9960171516"
) {
    val address: String
        get() = listOf(area, city, state).filter { it.isNotBlank() }.joinToString(", ")
}

fun adjustTime(time24: String, offsetMinutes: Int): String {
    if (!time24.contains(":")) return time24
    val parts = time24.split(":")
    val h = parts[0].toIntOrNull() ?: return time24
    val m = parts[1].toIntOrNull() ?: return time24
    var total = h * 60 + m + offsetMinutes
    while (total < 0) total += 1440
    total %= 1440
    return String.format(java.util.Locale.US, "%02d:%02d", total / 60, total % 60)
}

fun AzanTiming.applyMasjidOffsets(masjid: MasjidItem): AzanTiming {
    return this.copy(
        fajr = masjid.fajrAzanFixed ?: adjustTime(fajr, masjid.fajrOffset),
        dhuhr = masjid.zoharAzanFixed ?: adjustTime(dhuhr, masjid.dhuhrOffset),
        asr = masjid.asrAzanFixed ?: adjustTime(asr, masjid.asrOffset),
        maghrib = masjid.maghribAzanFixed ?: adjustTime(maghrib, masjid.maghribOffset),
        isha = masjid.ishaAzanFixed ?: adjustTime(isha, masjid.ishaOffset)
    )
}

object MasjidRepository {
    private var _dynamicMasajid: List<MasjidItem> = emptyList()
    val dynamicMasajid: List<MasjidItem>
        get() = _dynamicMasajid

    val defaultMasjid: MasjidItem
        get() = dynamicMasajid.firstOrNull() ?: staticDefaultMasjid

    val staticDefaultMasjid = MasjidItem(
        id = "100111111",
        name = "Mohammadiya Masjid",
        area = "Swagat Nagar",
        city = "Solapur",
        state = "Maharashtra",
        photoUrl = "https://lh3.googleusercontent.com/d/1QP6elu7nlZwmrxjG4p-eyZWOrEaEdiwo=w1000",
        jumahAzanTime = "12:30",
        jumahJammatTime = "13:30",
        fajrAzanFixed = "05:40",
        fajrJammatFixed = "06:15",
        zoharAzanFixed = "13:15",
        zoharJammatFixed = "13:30",
        asrAzanFixed = "17:17",
        asrJammatFixed = "17:30",
        maghribAzanFixed = "18:10",
        maghribJammatFixed = "18:12",
        ishaAzanFixed = "19:50",
        ishaJammatFixed = "19:59"
    )

    val staticSecondMasjid = MasjidItem(
        id = "100111112",
        name = "Hajrat Imam Hussain",
        area = "Tai Chowk",
        city = "Solapur",
        state = "Maharashtra",
        photoUrl = "",
        jumahAzanTime = "12:30",
        jumahJammatTime = "13:30",
        fajrAzanFixed = "05:42",
        fajrJammatFixed = "06:17",
        zoharAzanFixed = "13:15",
        zoharJammatFixed = "13:30",
        asrAzanFixed = "17:18",
        asrJammatFixed = "17:32",
        maghribAzanFixed = "18:11",
        maghribJammatFixed = "18:13",
        ishaAzanFixed = "19:55",
        ishaJammatFixed = "20:05",
        adminId = "admin",
        adminPassword = "9970595659"
    )

    fun getAllMasajid(): List<MasjidItem> {
        return if (dynamicMasajid.isNotEmpty()) {
            dynamicMasajid
        } else {
            masajid
        }
    }

    fun getMasjidById(id: String): MasjidItem {
        return getAllMasajid().find { it.id == id } ?: defaultMasjid
    }

    fun setDynamicMasajid(list: List<MasjidItem>) {
        _dynamicMasajid = list
    }

    val masajid: List<MasjidItem> = listOf(
        staticDefaultMasjid,
        staticSecondMasjid
    )
}
