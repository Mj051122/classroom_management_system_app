package com.myapplication.panthraa.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.myapplication.panthraa.model.TaskReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

class TaskRepository {
    suspend fun getTaskReminders(context: Context, userId: String): List<TaskReminder> = withContext(Dispatchers.IO) {
        TaskDatabase(context).readableDatabase.use { db ->
            db.query(
                TABLE_TASK_REMINDERS,
                null,
                "user_id = ?",
                arrayOf(userId),
                null,
                null,
                "reminder_date ASC, reminder_time ASC, created_at DESC",
            ).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(
                            TaskReminder(
                                id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                                userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                                kind = cursor.getString(cursor.getColumnIndexOrThrow("kind")),
                                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                                details = cursor.getString(cursor.getColumnIndexOrThrow("details")),
                                reminderDate = cursor.getString(cursor.getColumnIndexOrThrow("reminder_date")),
                                reminderTime = cursor.getString(cursor.getColumnIndexOrThrow("reminder_time")),
                                themeColor = cursor.getString(cursor.getColumnIndexOrThrow("theme_color")),
                                isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1,
                                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                            )
                        )
                    }
                }
            }
        }
    }

    suspend fun createTaskReminder(
        context: Context,
        userId: String,
        kind: String,
        title: String,
        details: String,
        reminderDate: String,
        reminderTime: String?,
        themeColor: String = "#0034DE",
    ): TaskReminder = withContext(Dispatchers.IO) {
        val cleanKind = kind.trim().lowercase()
        val cleanTitle = title.trim()
        val cleanDetails = details.trim()
        val cleanDate = reminderDate.trim()
        val cleanTime = reminderTime?.trim().orEmpty()

        if (cleanKind !in setOf("note", "schedule")) {
            error("Reminder type is invalid.")
        }
        if (cleanTitle.isBlank()) {
            error("Title is required.")
        }
        if (cleanDate.isBlank()) {
            error("Reminder date is required.")
        }

        val reminder = TaskReminder(
            id = UUID.randomUUID().toString(),
            userId = userId,
            kind = cleanKind,
            title = cleanTitle,
            details = cleanDetails,
            reminderDate = cleanDate,
            reminderTime = cleanTime.ifBlank { null },
            themeColor = themeColor,
            isCompleted = false,
            createdAt = Instant.now().toString(),
        )

        TaskDatabase(context).writableDatabase.use { db ->
            db.insertOrThrow(
                TABLE_TASK_REMINDERS,
                null,
                ContentValues().apply {
                    put("id", reminder.id)
                    put("user_id", reminder.userId)
                    put("kind", reminder.kind)
                    put("title", reminder.title)
                    put("details", reminder.details)
                    put("reminder_date", reminder.reminderDate)
                    put("reminder_time", reminder.reminderTime)
                    put("theme_color", reminder.themeColor)
                    put("is_completed", if (reminder.isCompleted) 1 else 0)
                    put("created_at", reminder.createdAt)
                },
            )
        }

        reminder
    }

    suspend fun deleteTaskReminder(context: Context, reminderId: String) = withContext(Dispatchers.IO) {
        TaskDatabase(context).writableDatabase.use { db ->
            db.delete(TABLE_TASK_REMINDERS, "id = ?", arrayOf(reminderId))
        }
    }

    suspend fun toggleTaskCompletion(context: Context, reminderId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        TaskDatabase(context).writableDatabase.use { db ->
            val values = ContentValues().apply {
                put("is_completed", if (isCompleted) 1 else 0)
            }
            db.update(TABLE_TASK_REMINDERS, values, "id = ?", arrayOf(reminderId))
        }
    }

    private class TaskDatabase(context: Context) : SQLiteOpenHelper(
        context.applicationContext,
        DATABASE_NAME,
        null,
        DATABASE_VERSION,
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS $TABLE_TASK_REMINDERS (
                    id TEXT PRIMARY KEY,
                    user_id TEXT NOT NULL,
                    kind TEXT NOT NULL,
                    title TEXT NOT NULL,
                    details TEXT NOT NULL DEFAULT '',
                    reminder_date TEXT NOT NULL,
                    reminder_time TEXT,
                    theme_color TEXT NOT NULL DEFAULT '#0034DE',
                    is_completed INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_task_reminders_user_date ON $TABLE_TASK_REMINDERS(user_id, reminder_date, reminder_time)"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE $TABLE_TASK_REMINDERS ADD COLUMN theme_color TEXT NOT NULL DEFAULT '#0034DE'")
                db.execSQL("ALTER TABLE $TABLE_TASK_REMINDERS ADD COLUMN is_completed INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "panthraa_tasks.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_TASK_REMINDERS = "task_reminders"
        private const val TAG = "TaskRepository"

        fun readableError(throwable: Throwable): String {
            Log.e(TAG, "Task feature error", throwable)
            val message = throwable.message.orEmpty()
            return when {
                message.isConnectivityMessage() -> "Refresh failed. Showing last saved data."
                message.contains("Title", ignoreCase = true) -> "Title is required."
                message.contains("Reminder date", ignoreCase = true) -> "Reminder date is required."
                message.contains("invalid", ignoreCase = true) -> "Reminder type is invalid."
                message.isNotBlank() -> "Debug error: $message"
                else -> "Debug error: ${throwable::class.simpleName ?: "Unknown error"}"
            }
        }

        private fun String.isConnectivityMessage(): Boolean {
            val text = lowercase()
            return listOf(
                "unable to resolve host",
                "no address associated with hostname",
                "failed to connect",
                "connect timed out",
                "request timed out",
                "unknownhost",
                "unresolvedaddress",
                "network is unreachable",
            ).any { marker -> text.contains(marker) }
        }
    }
}
