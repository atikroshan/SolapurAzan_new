package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "azan_prefs")

class PreferencesRepository(private val context: Context) {

    private val KEY_LANGUAGE = stringPreferencesKey("language")
    private val KEY_RESTORED_TAQWA_POINTS = androidx.datastore.preferences.core.intPreferencesKey("restored_taqwa_points")
    private val KEY_SETUP_COMPLETED = booleanPreferencesKey("setup_completed")
    private val KEY_CACHED_SHEET_CSV = stringPreferencesKey("cached_sheet_csv")
    private val KEY_ADMIN_OVERRIDE_MASAJID = stringSetPreferencesKey("admin_override_masajid_ids")
    private val KEY_APPS_SCRIPT_URL = stringPreferencesKey("apps_script_url")
    private val KEY_TAQWA_SYNC_USER = stringPreferencesKey("taqwa_sync_user")

    val taqwaSyncUserFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_TAQWA_SYNC_USER] ?: ""
    }

    suspend fun setTaqwaSyncUser(user: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TAQWA_SYNC_USER] = user.trim()
        }
    }

    val isSetupCompletedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SETUP_COMPLETED] ?: true
    }

    suspend fun setSetupCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SETUP_COMPLETED] = completed
        }
    }

    val cachedGoogleSheetCsvFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_CACHED_SHEET_CSV]
    }

    suspend fun setCachedGoogleSheetCsv(csv: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CACHED_SHEET_CSV] = csv
        }
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "en"
    }

    val restoredTaqwaPointsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_RESTORED_TAQWA_POINTS] ?: 0
    }

    suspend fun setRestoredTaqwaPoints(points: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_RESTORED_TAQWA_POINTS] = points
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = lang
        }
    }

    fun isAzanEnabled(name: String): Flow<Boolean> {
        val key = booleanPreferencesKey("azan_enabled_${name.lowercase()}")
        return context.dataStore.data.map { prefs ->
            prefs[key] ?: true
        }
    }

    suspend fun setAzanToggle(name: String, enabled: Boolean) {
        val key = booleanPreferencesKey("azan_enabled_${name.lowercase()}")
        context.dataStore.edit { prefs ->
            prefs[key] = enabled
        }
    }

    fun getCustomJammatTime(name: String): Flow<String?> {
        val key = stringPreferencesKey("jammat_time_${name.lowercase()}")
        return context.dataStore.data.map { prefs ->
            prefs[key]
        }
    }

    suspend fun setCustomJammatTime(name: String, time24: String) {
        val key = stringPreferencesKey("jammat_time_${name.lowercase()}")
        context.dataStore.edit { prefs ->
            prefs[key] = time24
        }
    }

    fun getCustomJumahAzan(): Flow<String?> {
        val key = stringPreferencesKey("azan_time_jumah")
        return context.dataStore.data.map { prefs ->
            prefs[key]
        }
    }

    suspend fun setCustomJumahAzan(time24: String) {
        val key = stringPreferencesKey("azan_time_jumah")
        context.dataStore.edit { prefs ->
            prefs[key] = time24
        }
    }

    fun getAllCustomJammatTimes(): Flow<Map<String, String>> {
        return context.dataStore.data.map { prefs ->
            val map = mutableMapOf<String, String>()
            listOf("fajr", "dhuhr", "jumah", "asr", "maghrib", "isha").forEach { prayer ->
                val key = stringPreferencesKey("jammat_time_$prayer")
                prefs[key]?.let { if (it.isNotEmpty()) map[prayer] = it }
            }
            map
        }
    }

    val selectedMasjidIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[stringPreferencesKey("selected_masjid_id")] ?: "100111111"
    }

    suspend fun setSelectedMasjidId(masjidId: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("selected_masjid_id")] = masjidId
        }
    }

    val localAdminEditedMasajidFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[KEY_ADMIN_OVERRIDE_MASAJID] ?: emptySet()
    }

    suspend fun addLocalAdminEditedMasjid(masjidId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_ADMIN_OVERRIDE_MASAJID] ?: emptySet()
            prefs[KEY_ADMIN_OVERRIDE_MASAJID] = current + masjidId
        }
    }

    suspend fun clearLocalAdminEditedMasjid(masjidId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_ADMIN_OVERRIDE_MASAJID] ?: emptySet()
            prefs[KEY_ADMIN_OVERRIDE_MASAJID] = current - masjidId
        }
    }

    suspend fun clearAllLocalAdminOverrides() {
        context.dataStore.edit { prefs ->
            prefs[KEY_ADMIN_OVERRIDE_MASAJID] = emptySet()
        }
    }

    val appsScriptUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_APPS_SCRIPT_URL]?.ifBlank { null } ?: GoogleSheetMasjidSync.APPS_SCRIPT_WEBAPP_URL
    }

    suspend fun setAppsScriptUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_APPS_SCRIPT_URL] = url.trim()
        }
    }
}
