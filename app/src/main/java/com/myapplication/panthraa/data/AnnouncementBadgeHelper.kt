package com.myapplication.panthraa.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object AnnouncementBadgeHelper {
    private const val CHANNEL_ID = "announcement_badge"
    private const val NOTIFICATION_ID = 1001
    private const val TAG = "announcement_badge"

    fun updateBadge(context: Context, unreadCount: Int) {
        if (unreadCount > 0) {
            showBadge(context, unreadCount)
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
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(
                if (count == 1) "1 unread announcement"
                else "$count unread announcements"
            )
            .setContentText("Tap to view")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setNumber(count)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(TAG, NOTIFICATION_ID, notification)
    }

    fun clearBadge(context: Context) {
        NotificationManagerCompat.from(context).cancel(TAG, NOTIFICATION_ID)
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Announcements",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Unread announcement badge count"
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
