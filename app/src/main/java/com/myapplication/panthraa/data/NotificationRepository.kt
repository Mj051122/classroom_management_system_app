package com.myapplication.panthraa.data

import android.util.Log
import com.myapplication.panthraa.model.AdminNotification
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
private data class NotificationReadRow(
    @SerialName("notification_id")
    val notificationId: Long,
)

@Serializable
private data class NotificationReadInsert(
    @SerialName("user_id")
    val userId: String,
    @SerialName("notification_id")
    val notificationId: Long,
    @SerialName("read_at")
    val readAt: String,
)

class NotificationRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
    private val readGuard: SupabaseReadGuard = SupabaseReadGuard.shared,
) {
    suspend fun getNotifications(
        userId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<AdminNotification> {
        return readGuard.read(
            key = "notifications:v=$CACHE_SCHEMA_VERSION:user=$userId",
            groups = setOf(notificationsGroup(userId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            val notifications = client.postgrest
                .from("admin_notifications")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<AdminNotification>()

            val readRows = client.postgrest
                .from("notification_reads")
                .select(columns = Columns.list("notification_id")) {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<NotificationReadRow>()

            val readIds = readRows.map { it.notificationId }.toSet()
            notifications.map { notification ->
                notification.copy(isRead = notification.id in readIds)
            }
        }
    }

    suspend fun markAsRead(userId: String, notificationId: Long) {
        runCatching {
            client.postgrest
                .from("notification_reads")
                .upsert(
                    NotificationReadInsert(
                        userId = userId,
                        notificationId = notificationId,
                        readAt = Instant.now().toString(),
                    ),
                ) {
                    onConflict = "user_id,notification_id"
                    ignoreDuplicates = true
                }
        }
        readGuard.invalidateGroup(notificationsGroup(userId))
    }

    fun invalidateNotifications(userId: String) {
        readGuard.invalidateGroup(notificationsGroup(userId))
    }

    companion object {
        private const val TAG = "NotificationRepository"
        private const val CACHE_SCHEMA_VERSION = 1
        private const val ONE_MINUTE_MILLIS = 60_000L

        private fun notificationsGroup(userId: String): String {
            return "notifications:user=$userId"
        }

        fun readableError(throwable: Throwable): String {
            Log.e(TAG, "Notification error", throwable)
            val message = throwable.message.orEmpty()
            val restException = throwable as? RestException
            val restText = listOfNotNull(
                restException?.error,
                restException?.description,
                message,
            ).joinToString(" ")
            return when {
                throwable is HttpRequestTimeoutException || restText.isConnectivityMessage() ->
                    "Refresh failed. Showing last saved data."
                message.isNotBlank() -> "Something went wrong while loading. Please try again."
                else -> "Something went wrong while loading. Please try again."
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
