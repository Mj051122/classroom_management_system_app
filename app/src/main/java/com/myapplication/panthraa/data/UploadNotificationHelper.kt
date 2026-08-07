package com.myapplication.panthraa.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.myapplication.panthraa.model.PendingAssignment
import java.time.LocalDate
import java.time.LocalTime

object UploadNotificationHelper {
    private const val CHANNEL_ID = "new_uploads"
    private const val BADGE_NOTIFICATION_ID = 2001
    private const val BADGE_TAG = "upload_badge"
    private const val PREFS_NAME = "upload_notification_prefs"
    private const val SEEN_IDS_KEY = "seen_assignment_ids"
    private const val NOTIFICATION_GROUP = "new_uploads_group"
    private const val NOTIFICATION_BASE_ID = 3000

    fun detectNewUploads(context: Context, currentAssignments: List<PendingAssignment>): List<PendingAssignment> {
        val activeAssignments = currentAssignments.filterNot { isExpired(it) }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val seenIds = prefs.getStringSet(SEEN_IDS_KEY, emptySet()) ?: emptySet()
        val newAssignments = activeAssignments.filter { it.assignmentId !in seenIds }
        if (newAssignments.isNotEmpty()) {
            val updatedSeen = (seenIds + currentAssignments.map { it.assignmentId }).toSet()
            prefs.edit().putStringSet(SEEN_IDS_KEY, updatedSeen).apply()
        }
        return newAssignments
    }

    private fun isExpired(assignment: PendingAssignment): Boolean {
        val endDate = assignment.endDate?.take(10)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        } ?: return false
        val endTime = assignment.endTime?.take(5)?.let {
            runCatching { LocalTime.parse(it) }.getOrNull()
        }
        val now = LocalDate.now()
        return endDate.isBefore(now) || (endDate == now && endTime != null && LocalTime.now().isAfter(endTime))
    }

    fun showUploadNotifications(context: Context, newAssignments: List<PendingAssignment>) {
        if (newAssignments.isEmpty()) return
        ensureChannel(context)
        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?: Intent().setClassName(context.packageName, "com.myapplication.panthraa.MainActivity")
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        newAssignments.forEachIndexed { index, assignment ->
            val className = assignment.className.ifBlank { assignment.subjectCode.ifBlank { "Class" } }
            val pointsLabel = if (assignment.targetPoints > 0) " · ${assignment.targetPoints} pts" else ""
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(className)
                .setContentText("${assignment.title}$pointsLabel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(NOTIFICATION_GROUP)
                .build()
            NotificationManagerCompat.from(context).notify("new_upload_${assignment.assignmentId}", NOTIFICATION_BASE_ID + index, notification)
        }
    }

    fun updateBadge(context: Context, newCount: Int) {
        if (newCount > 0) {
            showBadge(context, newCount)
        } else {
            clearBadge(context)
        }
    }

    private fun showBadge(context: Context, count: Int) {
        ensureChannel(context)
        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?: Intent().setClassName(context.packageName, "com.myapplication.panthraa.MainActivity")
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(
                if (count == 1) "1 new upload"
                else "$count new uploads"
            )
            .setContentText("Tap to view")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setNumber(count)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(BADGE_TAG, BADGE_NOTIFICATION_ID, notification)
    }

    fun clearBadge(context: Context) {
        NotificationManagerCompat.from(context).cancel(BADGE_TAG, BADGE_NOTIFICATION_ID)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "New uploads",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "New assignments, materials, and uploads from your classes"
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
