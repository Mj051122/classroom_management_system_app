package com.myapplication.panthraa.data.session

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.myapplication.panthraa.auth.UserRole

/**
 * Stores the logged-in user's credentials at rest using EncryptedSharedPreferences
 * (AES-256, backed by the Android keystore) so the app can silently re-login on launch.
 *
 * All reads/writes are dispatched off the main thread by the caller because keystore
 * initialization can be slow on first access.
 */
class SessionStore(context: Context) {
    private val appContext = context.applicationContext
    private val lock = Any()
    private var prefs: SharedPreferences? = null

    fun saveCredentials(idNumber: String, password: String, role: UserRole): Boolean = synchronized(lock) {
        val safePrefs = preferencesLocked() ?: return@synchronized false
        try {
            val committed = safePrefs.edit()
                .putString(KEY_ID_NUMBER, idNumber)
                .putString(KEY_PASSWORD, password)
                .putString(KEY_ROLE, role.value)
                .commit()
            if (!committed) {
                Log.e(TAG, "Credential write was not committed")
            }
            committed
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to save credentials", t)
            false
        }
    }

    fun getCredentials(): Credentials? = synchronized(lock) {
        val safePrefs = preferencesLocked() ?: return@synchronized null
        try {
            val idNumber = safePrefs.getString(KEY_ID_NUMBER, null)
            val password = safePrefs.getString(KEY_PASSWORD, null)
            val roleValue = safePrefs.getString(KEY_ROLE, null)
            if (idNumber.isNullOrBlank() || password.isNullOrBlank() || roleValue.isNullOrBlank()) {
                return@synchronized null
            }
            val role = UserRole.entries.firstOrNull { it.value.equals(roleValue, ignoreCase = true) }
                ?: return@synchronized null
            Credentials(idNumber = idNumber, password = password, role = role)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to read credentials; resetting the encrypted session", t)
            resetPreferencesLocked()
            null
        }
    }

    fun clear() {
        synchronized(lock) {
            val safePrefs = prefs
            if (safePrefs == null) {
                deletePreferencesFile()
                return@synchronized
            }

            val cleared = try {
                safePrefs.edit().clear().commit()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to clear credentials", t)
                false
            }
            if (!cleared) {
                prefs = null
                deletePreferencesFile()
            }
        }
    }

    private fun preferencesLocked(): SharedPreferences? {
        prefs?.let { return it }
        return openPreferences(recoverOnFailure = true).also { prefs = it }
    }

    private fun resetPreferencesLocked() {
        prefs = null
        deletePreferencesFile()
        prefs = openPreferences(recoverOnFailure = false)
    }

    private fun openPreferences(recoverOnFailure: Boolean): SharedPreferences? {
        return try {
            createPreferences()
        } catch (firstFailure: Throwable) {
            Log.e(TAG, "Failed to open encrypted session storage", firstFailure)
            if (!recoverOnFailure) {
                null
            } else {
                Log.w(TAG, "Resetting unreadable encrypted session storage")
                deletePreferencesFile()
                try {
                    createPreferences()
                } catch (retryFailure: Throwable) {
                    Log.e(TAG, "Failed to reopen encrypted session storage", retryFailure)
                    null
                }
            }
        }
    }

    private fun createPreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            appContext,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun deletePreferencesFile() {
        runCatching { appContext.deleteSharedPreferences(FILE_NAME) }
            .onFailure { Log.e(TAG, "Failed to delete encrypted session storage", it) }
    }

    companion object {
        private const val TAG = "SessionStore"
        private const val FILE_NAME = "panthraa_session"
        private const val KEY_ID_NUMBER = "id_number"
        private const val KEY_PASSWORD = "password"
        private const val KEY_ROLE = "role"
    }
}

data class Credentials(
    val idNumber: String,
    val password: String,
    val role: UserRole,
)
