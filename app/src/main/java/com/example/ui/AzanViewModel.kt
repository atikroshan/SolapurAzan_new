package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AzanRepository
import com.example.data.AzanTiming
import com.example.data.PrayerLog
import com.example.data.PreferencesRepository
import com.example.data.MasjidItem
import com.example.data.MasjidRepository
import com.example.data.applyMasjidOffsets
import com.example.data.adjustTime
import com.example.service.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

import com.example.data.GoogleSheetMasjidSync
import kotlinx.coroutines.flow.firstOrNull

data class UIState(
    val language: String = "en",
    val todayTimings: AzanTiming? = null,
    val fajrEnabled: Boolean = true,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true,
    val selectedDate: Calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")),
    val fajrPrayed: Boolean = false,
    val dhuhrPrayed: Boolean = false,
    val asrPrayed: Boolean = false,
    val maghribPrayed: Boolean = false,
    val ishaPrayed: Boolean = false,
    val tahajjudPrayed: Boolean = false,
    val allLogs: List<PrayerLog> = emptyList(),
    val customJammatTimes: Map<String, String> = emptyMap(),
    val customJumahAzan: String? = null,
    val selectedMasjid: MasjidItem = MasjidRepository.defaultMasjid,
    val allMasajid: List<MasjidItem> = MasjidRepository.getAllMasajid(),
    val restoredTaqwaPoints: Int = 0,
    val isSetupCompleted: Boolean = true,
    val isSyncingSheet: Boolean = false,
    val appsScriptUrl: String = "",
    val lastSyncStatusMessage: String? = null
)

class AzanViewModel(
    private val context: Context,
    private val repository: AzanRepository,
    private val prefs: PreferencesRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _currentCalendar = MutableStateFlow(Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")))
    private val _isSyncingSheet = MutableStateFlow(false)
    private val _masajidList = MutableStateFlow<List<MasjidItem>>(MasjidRepository.getAllMasajid())
    private val _lastSyncStatusMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            try {
                val cached = prefs.cachedGoogleSheetCsvFlow.firstOrNull()
                val csvToUse = if (!cached.isNullOrBlank() && cached.contains("Password", ignoreCase = true)) cached else GoogleSheetMasjidSync.DEFAULT_CSV_CONTENT
                val parsed = GoogleSheetMasjidSync.parseCsv(csvToUse)
                if (parsed.isNotEmpty()) {
                    MasjidRepository.setDynamicMasajid(parsed)
                    _masajidList.value = parsed
                    if (cached.isNullOrBlank()) {
                        prefs.setCachedGoogleSheetCsv(GoogleSheetMasjidSync.DEFAULT_CSV_CONTENT)
                    }
                    val currentSelectedId = prefs.selectedMasjidIdFlow.firstOrNull()
                    if (currentSelectedId == null || parsed.none { it.id == currentSelectedId }) {
                        prefs.setSelectedMasjidId(parsed.first().id)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
            syncGoogleSheet()
        }
    }

    fun syncGoogleSheet(forceOverwriteLocal: Boolean = false) {
        viewModelScope.launch {
            try {
                _isSyncingSheet.value = true
                val csv = GoogleSheetMasjidSync.fetchCsv()
                val parsed = GoogleSheetMasjidSync.parseCsv(csv)
                if (parsed.isNotEmpty()) {
                    val localEditedIds = if (forceOverwriteLocal) {
                        prefs.clearAllLocalAdminOverrides()
                        emptySet()
                    } else {
                        prefs.localAdminEditedMasajidFlow.firstOrNull() ?: emptySet()
                    }
                    val currentLocalMasajid = _masajidList.value
                    val mergedList = if (localEditedIds.isNotEmpty()) {
                        parsed.map { remoteMasjid ->
                            if (localEditedIds.contains(remoteMasjid.id)) {
                                val local = currentLocalMasajid.find { it.id == remoteMasjid.id }
                                local ?: remoteMasjid
                            } else {
                                remoteMasjid
                            }
                        }
                    } else {
                        parsed
                    }
                    MasjidRepository.setDynamicMasajid(mergedList)
                    _masajidList.value = mergedList
                    val newCsv = GoogleSheetMasjidSync.buildCsv(mergedList)
                    prefs.setCachedGoogleSheetCsv(newCsv)
                    val currentSelectedId = prefs.selectedMasjidIdFlow.firstOrNull()
                    if (currentSelectedId == null || mergedList.none { it.id == currentSelectedId }) {
                        prefs.setSelectedMasjidId(mergedList.first().id)
                    }
                }
            } catch (e: Exception) {
                // Fallback to offline / cached
            } finally {
                _isSyncingSheet.value = false
            }
        }
    }

    fun completeSetup(masjidId: String) {
        viewModelScope.launch {
            prefs.setSelectedMasjidId(masjidId)
            prefs.setSetupCompleted(true)
        }
    }

    fun nextDay() {
        val next = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
            timeInMillis = _currentCalendar.value.timeInMillis
            add(Calendar.DAY_OF_MONTH, 1)
        }
        _currentCalendar.value = next
    }

    fun previousDay() {
        val prev = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
            timeInMillis = _currentCalendar.value.timeInMillis
            add(Calendar.DAY_OF_MONTH, -1)
        }
        _currentCalendar.value = prev
    }

    fun selectToday() {
        _currentCalendar.value = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
    }

    fun refreshDate() {
        _currentCalendar.value = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
    }

    fun selectDate(month: Int, day: Int) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
            timeInMillis = _currentCalendar.value.timeInMillis
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
        _currentCalendar.value = cal
    }

    fun togglePrayerPrayed(name: String) {
        togglePrayed(name)
    }

    fun togglePrayerForDate(month: Int, day: Int, name: String) {
        viewModelScope.launch {
            val currentLog = uiState.value.allLogs.find { it.month == month && it.day == day }
                ?: PrayerLog(month = month, day = day)

            val updatedLog = when (name.lowercase()) {
                "fajr" -> currentLog.copy(fajrPrayed = !currentLog.fajrPrayed)
                "dhuhr", "zohr", "jum'ah" -> currentLog.copy(dhuhrPrayed = !currentLog.dhuhrPrayed)
                "asr" -> currentLog.copy(asrPrayed = !currentLog.asrPrayed)
                "maghrib" -> currentLog.copy(maghribPrayed = !currentLog.maghribPrayed)
                "isha" -> currentLog.copy(ishaPrayed = !currentLog.ishaPrayed)
                "tahajjud" -> currentLog.copy(tahajjudPrayed = !currentLog.tahajjudPrayed)
                else -> currentLog
            }
            repository.insertLog(updatedLog)
        }
    }

    fun updatePrayerTime(prayerName: String, newTime: String, applyToAll: Boolean = false) {
        val cal = _currentCalendar.value
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        viewModelScope.launch {
            repository.updatePrayerTime(m, d, prayerName, newTime, applyToAll)
        }
    }

    fun togglePrayed(name: String) {
        viewModelScope.launch {
            val cal = _currentCalendar.value
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val currentLog = uiState.value.allLogs.find { it.month == month && it.day == day }
                ?: PrayerLog(month = month, day = day)

            val updatedLog = when (name.lowercase()) {
                "fajr" -> currentLog.copy(fajrPrayed = !currentLog.fajrPrayed)
                "dhuhr", "zohr", "jum'ah" -> currentLog.copy(dhuhrPrayed = !currentLog.dhuhrPrayed)
                "asr" -> currentLog.copy(asrPrayed = !currentLog.asrPrayed)
                "maghrib" -> currentLog.copy(maghribPrayed = !currentLog.maghribPrayed)
                "isha" -> currentLog.copy(ishaPrayed = !currentLog.ishaPrayed)
                "tahajjud" -> currentLog.copy(tahajjudPrayed = !currentLog.tahajjudPrayed)
                else -> currentLog
            }
            repository.insertLog(updatedLog)
        }
    }

    private val togglesFlow = combine(
        prefs.isAzanEnabled("Fajr"),
        prefs.isAzanEnabled("Dhuhr"),
        prefs.isAzanEnabled("Asr"),
        prefs.isAzanEnabled("Maghrib"),
        prefs.isAzanEnabled("Isha")
    ) { f, d, a, m, i ->
        listOf(f, d, a, m, i)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val timingsFlow = _currentCalendar.flatMapLatest { cal ->
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        repository.getTimingsForDate(m, d)
    }

    val uiState: StateFlow<UIState> = combine(
        combine(prefs.languageFlow, togglesFlow, _currentCalendar, prefs.selectedMasjidIdFlow, _masajidList) { l, t, c, mId, masajid -> 
            val masjid = masajid.find { it.id == mId } ?: masajid.firstOrNull() ?: MasjidRepository.defaultMasjid
            Triple(l, t, Triple(c, masjid, masajid))
        },
        repository.getAllLogs(),
        timingsFlow,
        combine(
            prefs.getAllCustomJammatTimes(),
            prefs.getCustomJumahAzan(),
            prefs.isSetupCompletedFlow,
            _isSyncingSheet,
            prefs.restoredTaqwaPointsFlow
        ) { cj, ja, setupDone, syncing, restoredPoints ->
            Triple(Pair(cj, ja), Pair(setupDone, syncing), restoredPoints)
        },
        combine(prefs.appsScriptUrlFlow, _lastSyncStatusMessage) { url, msg -> Pair(url, msg) }
    ) { (language, toggles, calMasjidPoints), allLogs, timings, (jammatPair, setupPair, restoredPoints), (appsScriptUrl, syncMsg) ->
        val (cal, selectedMasjid, masajid) = calMasjidPoints
        val (customJammat, customJumahAzan) = jammatPair
        val (isSetupCompleted, isSyncingSheet) = setupPair
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        val currentLog = allLogs.find { it.month == m && it.day == d }

        // Adjust timings according to the selected Masjid
        val adjustedTimings = timings?.applyMasjidOffsets(selectedMasjid)

        val effectiveTimings = if (adjustedTimings != null) {
            if (isFriday) {
                val jumahAzan = customJumahAzan ?: selectedMasjid.jumahAzanTime.ifBlank { "12:30" }
                adjustedTimings.copy(dhuhr = jumahAzan)
            } else {
                adjustedTimings
            }
        } else {
            AzanTiming(
                month = m,
                day = d,
                fajr = selectedMasjid.fajrAzanFixed ?: "05:40",
                sunrise = "06:45",
                dhuhr = if (isFriday) (customJumahAzan ?: selectedMasjid.jumahAzanTime.ifBlank { "12:30" }) else (selectedMasjid.zoharAzanFixed ?: "13:15"),
                asr = selectedMasjid.asrAzanFixed ?: "17:17",
                maghrib = selectedMasjid.maghribAzanFixed ?: "18:10",
                isha = selectedMasjid.ishaAzanFixed ?: "19:50"
            )
        }

        UIState(
            language = language,
            todayTimings = effectiveTimings,
            fajrEnabled = toggles[0],
            dhuhrEnabled = toggles[1],
            asrEnabled = toggles[2],
            maghribEnabled = toggles[3],
            ishaEnabled = toggles[4],
            selectedDate = cal,
            fajrPrayed = currentLog?.fajrPrayed ?: false,
            dhuhrPrayed = currentLog?.dhuhrPrayed ?: false,
            asrPrayed = currentLog?.asrPrayed ?: false,
            maghribPrayed = currentLog?.maghribPrayed ?: false,
            ishaPrayed = currentLog?.ishaPrayed ?: false,
            tahajjudPrayed = currentLog?.tahajjudPrayed ?: false,
            allLogs = allLogs,
            customJammatTimes = customJammat,
            customJumahAzan = customJumahAzan,
            selectedMasjid = selectedMasjid,
            allMasajid = masajid,
            restoredTaqwaPoints = restoredPoints,
            isSetupCompleted = isSetupCompleted,
            isSyncingSheet = isSyncingSheet,
            appsScriptUrl = appsScriptUrl,
            lastSyncStatusMessage = syncMsg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIState())

    fun setRestoredTaqwaPoints(points: Int) {
        viewModelScope.launch {
            prefs.setRestoredTaqwaPoints(points)
        }
    }

    fun selectMasjid(masjidId: String) {
        viewModelScope.launch {
            prefs.setSelectedMasjidId(masjidId)
        }
    }

    fun setAppsScriptUrl(url: String) {
        viewModelScope.launch {
            prefs.setAppsScriptUrl(url)
        }
    }

    suspend fun testAppsScriptConnection(url: String): Pair<Boolean, String> {
        return GoogleSheetMasjidSync.testAppsScriptConnection(url)
    }

    fun updatePrayerLocally(
        prayerName: String,
        newAzanTime: String,
        newJammatTime: String
    ) {
        val cal = _currentCalendar.value
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        viewModelScope.launch {
            val curMasjid = uiState.value.selectedMasjid
            val all = MasjidRepository.getAllMasajid().toMutableList()
            val existingIdx = all.indexOfFirst { it.id == curMasjid.id }
            val updatedMasjid = when (prayerName.lowercase()) {
                "fajr" -> curMasjid.copy(fajrAzanFixed = newAzanTime, fajrJammatFixed = newJammatTime)
                "zohar", "dhuhr" -> curMasjid.copy(zoharAzanFixed = newAzanTime, zoharJammatFixed = newJammatTime)
                "jumah", "jum'ah" -> curMasjid.copy(jumahAzanTime = newAzanTime, jumahJammatTime = newJammatTime)
                "asr" -> curMasjid.copy(asrAzanFixed = newAzanTime, asrJammatFixed = newJammatTime)
                "maghrib" -> curMasjid.copy(maghribAzanFixed = newAzanTime, maghribJammatFixed = newJammatTime)
                "isha" -> curMasjid.copy(ishaAzanFixed = newAzanTime, ishaJammatFixed = newJammatTime)
                else -> curMasjid
            }
            if (existingIdx != -1) {
                all[existingIdx] = updatedMasjid
            } else {
                all.add(0, updatedMasjid)
            }
            MasjidRepository.setDynamicMasajid(all)
            _masajidList.value = all
            val newCsv = GoogleSheetMasjidSync.buildCsv(all)
            prefs.setCachedGoogleSheetCsv(newCsv)
            prefs.addLocalAdminEditedMasjid(updatedMasjid.id)

            if (prayerName.equals("Jumah", ignoreCase = true) || prayerName.equals("Jum'ah", ignoreCase = true)) {
                prefs.setCustomJumahAzan(newAzanTime)
                prefs.setCustomJammatTime("jumah", newJammatTime)
            } else {
                val dbName = if (prayerName.equals("Zohar", ignoreCase = true)) "dhuhr" else prayerName
                repository.updatePrayerTime(m, d, dbName, newAzanTime, applyToAll = true)
                prefs.setCustomJammatTime(dbName, newJammatTime)
            }
        }
    }

    suspend fun syncMasjidToSheet(masjid: MasjidItem): Pair<Boolean, String> {
        val currentUrl = prefs.appsScriptUrlFlow.firstOrNull()?.trim() ?: ""
        if (currentUrl.isBlank()) {
            return Pair(false, "APPS_SCRIPT_NOT_SET")
        }

        // Try fast updateAll in one request first
        val (allSuccess, allMsg) = GoogleSheetMasjidSync.updateAllMasjidTimings(masjid, currentUrl)
        if (allSuccess) {
            _lastSyncStatusMessage.value = "Updated all timings successfully"
            return Pair(true, allMsg)
        }

        if (allMsg != "OLD_SCRIPT_FORMAT") {
            _lastSyncStatusMessage.value = allMsg
            return Pair(false, allMsg)
        }

        // Fallback for older deployed scripts: update prayer by prayer
        val prayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Jumah")
        for (prayer in prayers) {
            val (azan, jammat) = when (prayer) {
                "Fajr" -> masjid.fajrAzanFixed to masjid.fajrJammatFixed
                "Dhuhr" -> masjid.zoharAzanFixed to masjid.zoharJammatFixed
                "Asr" -> masjid.asrAzanFixed to masjid.asrJammatFixed
                "Maghrib" -> masjid.maghribAzanFixed to masjid.maghribJammatFixed
                "Isha" -> masjid.ishaAzanFixed to masjid.ishaJammatFixed
                "Jumah" -> masjid.jumahAzanTime to masjid.jumahJammatTime
                else -> "" to ""
            }
            val (success, msg) = GoogleSheetMasjidSync.updateRemoteGoogleSheet(
                masjidId = masjid.id,
                prayerName = prayer,
                azanTime = azan ?: "",
                jammatTime = jammat ?: "",
                webAppUrl = currentUrl
            )
            if (!success) {
                _lastSyncStatusMessage.value = msg
                return Pair(false, msg)
            }
        }
        _lastSyncStatusMessage.value = "Google Sheet updated successfully"
        return Pair(true, "All prayer timings updated in Google Sheet")
    }

    fun saveOrUpdateMasjid(id: String, name: String, address: String, photoUrl: String) {
        viewModelScope.launch {
            val all = MasjidRepository.getAllMasajid().toMutableList()
            val cleanId = id.trim()
            val existingIdx = all.indexOfFirst { it.id == cleanId }
            val directPhoto = GoogleSheetMasjidSync.extractGoogleDriveDirectUrl(photoUrl)
            val updated = if (existingIdx != -1) {
                all[existingIdx].copy(
                    name = name.trim(),
                    area = address.trim(),
                    photoUrl = directPhoto
                )
            } else {
                MasjidItem(
                    id = cleanId,
                    name = name.trim(),
                    area = address.trim(),
                    city = "Solapur",
                    state = "Maharashtra",
                    photoUrl = directPhoto,
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
            }
            if (existingIdx != -1) {
                all[existingIdx] = updated
            } else {
                all.add(0, updated)
            }
            MasjidRepository.setDynamicMasajid(all)
            _masajidList.value = all
            val newCsv = GoogleSheetMasjidSync.buildCsv(all)
            prefs.setCachedGoogleSheetCsv(newCsv)
            prefs.setSelectedMasjidId(updated.id)
            prefs.addLocalAdminEditedMasjid(updated.id)
        }
    }

    fun getGoogleSheetCsv(): String {
        return GoogleSheetMasjidSync.buildCsv(MasjidRepository.getAllMasajid())
    }

    private data class AlarmScheduleState(
        val f: Boolean,
        val d: Boolean,
        val a: Boolean,
        val m: Boolean,
        val i: Boolean,
        val timings: AzanTiming?
    )

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
            launch {
                uiState.map { state ->
                    AlarmScheduleState(
                        f = state.fajrEnabled,
                        d = state.dhuhrEnabled,
                        a = state.asrEnabled,
                        m = state.maghribEnabled,
                        i = state.ishaEnabled,
                        timings = state.todayTimings
                    )
                }
                .distinctUntilChanged()
                .collect { s ->
                    s.timings?.let { t ->
                        try {
                            if (s.f) alarmScheduler.scheduleAzan("Fajr", t.fajr) else alarmScheduler.cancelAzan("Fajr")
                            if (s.d) alarmScheduler.scheduleAzan("Dhuhr", t.dhuhr) else alarmScheduler.cancelAzan("Dhuhr")
                            if (s.a) alarmScheduler.scheduleAzan("Asr", t.asr) else alarmScheduler.cancelAzan("Asr")
                            if (s.m) alarmScheduler.scheduleAzan("Maghrib", t.maghrib) else alarmScheduler.cancelAzan("Maghrib")
                            if (s.i) alarmScheduler.scheduleAzan("Isha", t.isha) else alarmScheduler.cancelAzan("Isha")
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            prefs.setLanguage(lang)
        }
    }

    fun toggleAzan(name: String, enabled: Boolean) {
        viewModelScope.launch {
            prefs.setAzanToggle(name, enabled)
            if (!enabled) {
                alarmScheduler.cancelAzan(name)
            }
        }
    }
}

class AzanViewModelFactory(
    private val context: Context,
    private val repository: AzanRepository,
    private val prefs: PreferencesRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AzanViewModel(context, repository, prefs, alarmScheduler) as T
    }
}
