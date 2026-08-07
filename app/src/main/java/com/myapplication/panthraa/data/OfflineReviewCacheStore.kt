package com.myapplication.panthraa.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.AssignmentStatus
import com.myapplication.panthraa.model.ClassAnnouncement
import com.myapplication.panthraa.model.ClassAssignment
import com.myapplication.panthraa.model.PendingAssignment
import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.StudentClass
import com.myapplication.panthraa.model.StudentGrade
import com.myapplication.panthraa.model.TaskReminder
import com.myapplication.panthraa.widget.PanthraaScheduleWidgetUpdater
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OfflineReviewCacheStore(context: Context) {
    private val appContext = context.applicationContext
    private val lock = Any()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private var prefs: SharedPreferences? = null

    fun saveUser(user: AppUser) {
        updateCache(commitSynchronously = true) { current ->
            (current ?: OfflineReviewCache()).copy(
                user = user,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
    }

    fun getLastUser(): AppUser? = readCache()?.user

    fun getCacheForUser(userId: String): OfflineReviewCache? {
        val cache = readCache() ?: return null
        return cache.takeIf { it.user?.id == userId }
    }

    fun saveClassSnapshot(
        user: AppUser,
        studentClasses: List<StudentClass>,
        professorClasses: List<ProfessorClass>,
        assignmentStatuses: Map<String, AssignmentStatus>,
    ) {
        val updated = updateCache { current ->
            val base = if (current?.user?.id == user.id) current else OfflineReviewCache()
            base.copy(
                user = user,
                studentClasses = studentClasses,
                professorClasses = professorClasses,
                assignmentStatuses = assignmentStatuses,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
        if (updated != null) {
            PanthraaScheduleWidgetUpdater.refreshAndSchedule(appContext)
        }
    }

    fun saveAssignmentsSnapshot(user: AppUser, classId: String, assignments: List<ClassAssignment>) {
        updateCache { current ->
            val base = if (current?.user?.id == user.id) current else OfflineReviewCache(user = user)
            base.copy(
                user = user,
                classAssignments = base.classAssignments.filterNot { it.classId == classId } + assignments,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
    }

    fun saveTaskRemindersSnapshot(user: AppUser, taskReminders: List<TaskReminder>) {
        updateCache { current ->
            val base = if (current?.user?.id == user.id) current else OfflineReviewCache(user = user)
            base.copy(
                user = user,
                taskReminders = taskReminders,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
    }

    fun saveAnnouncementsSnapshot(user: AppUser, announcements: List<ClassAnnouncement>) {
        updateCache { current ->
            val base = if (current?.user?.id == user.id) current else OfflineReviewCache(user = user)
            base.copy(
                user = user,
                classAnnouncements = announcements,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
    }

    fun savePendingAssignmentsSnapshot(user: AppUser, pendingAssignments: List<PendingAssignment>) {
        updateCache { current ->
            val base = if (current?.user?.id == user.id) current else OfflineReviewCache(user = user)
            base.copy(
                user = user,
                pendingAssignments = pendingAssignments,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
    }

    fun saveStudentGradesSnapshot(user: AppUser, studentGrades: List<StudentGrade>) {
        updateCache { current ->
            val base = if (current?.user?.id == user.id) current else OfflineReviewCache(user = user)
            base.copy(
                user = user,
                studentGrades = studentGrades,
                savedAtMillis = System.currentTimeMillis(),
            )
        }
    }

    fun clear() {
        synchronized(lock) {
            val safePrefs = prefs
            if (safePrefs == null) {
                deletePreferencesFile()
            } else {
                val cleared = try {
                    safePrefs.edit().clear().commit()
                } catch (t: Throwable) {
                    Log.e(TAG, "Failed to clear offline review cache", t)
                    false
                }
                if (!cleared) {
                    prefs = null
                    deletePreferencesFile()
                }
            }
        }
        PanthraaScheduleWidgetUpdater.refreshAndSchedule(appContext)
    }

    private fun readCache(): OfflineReviewCache? = synchronized(lock) {
        val safePrefs = preferencesLocked() ?: return@synchronized null
        try {
            readCacheLocked(safePrefs)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to read offline review cache; resetting it", t)
            resetPreferencesLocked()
            null
        }
    }

    private fun updateCache(
        commitSynchronously: Boolean = false,
        block: (OfflineReviewCache?) -> OfflineReviewCache,
    ): OfflineReviewCache? = synchronized(lock) {
        var safePrefs = preferencesLocked() ?: return@synchronized null
        val current = try {
            readCacheLocked(safePrefs)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to read offline review cache; resetting it", t)
            resetPreferencesLocked()
            safePrefs = preferencesLocked() ?: return@synchronized null
            null
        }

        return@synchronized try {
            val next = block(current)
            val editor = safePrefs.edit().putString(KEY_CACHE, json.encodeToString(next))
            val persisted = if (commitSynchronously) {
                editor.commit()
            } else {
                editor.apply()
                true
            }
            if (!persisted) {
                Log.e(TAG, "Offline review cache write was not committed")
                null
            } else {
                next
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to update offline review cache", t)
            null
        }
    }

    private fun readCacheLocked(safePrefs: SharedPreferences): OfflineReviewCache? {
        val raw = safePrefs.getString(KEY_CACHE, null) ?: return null
        return json.decodeFromString<OfflineReviewCache>(raw)
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
            Log.e(TAG, "Failed to open offline review cache", firstFailure)
            if (!recoverOnFailure) {
                null
            } else {
                Log.w(TAG, "Resetting unreadable offline review cache")
                deletePreferencesFile()
                try {
                    createPreferences()
                } catch (retryFailure: Throwable) {
                    Log.e(TAG, "Failed to reopen offline review cache", retryFailure)
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
            .onFailure { Log.e(TAG, "Failed to delete offline review cache", it) }
    }

    companion object {
        private const val TAG = "OfflineReviewCache"
        private const val FILE_NAME = "panthraa_offline_review_cache"
        private const val KEY_CACHE = "last_user_cache"
    }
}

@Serializable
data class OfflineReviewCache(
    val user: AppUser? = null,
    val studentClasses: List<StudentClass> = emptyList(),
    val professorClasses: List<ProfessorClass> = emptyList(),
    val classAssignments: List<ClassAssignment> = emptyList(),
    val assignmentStatuses: Map<String, AssignmentStatus> = emptyMap(),
    val taskReminders: List<TaskReminder> = emptyList(),
    val classAnnouncements: List<ClassAnnouncement> = emptyList(),
    val pendingAssignments: List<PendingAssignment> = emptyList(),
    val studentGrades: List<StudentGrade> = emptyList(),
    val savedAtMillis: Long = 0L,
)
