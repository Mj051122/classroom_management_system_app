package com.myapplication.panthraa.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.myapplication.panthraa.model.ClassAnnouncement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.ContentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AnnouncementRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
    private val readGuard: SupabaseReadGuard = SupabaseReadGuard.shared,
) {
    suspend fun getStudentAnnouncements(
        studentId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<ClassAnnouncement> {
        return readGuard.read(
            key = "announcements:v=$CACHE_SCHEMA_VERSION:user=$studentId:role=student",
            groups = setOf(announcementsGroup(studentId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_student_announcements",
                parameters = buildJsonObject {
                    put("p_student_id", studentId)
                },
            ).decodeList<ClassAnnouncement>()
        }
    }

    suspend fun getProfessorAnnouncements(
        professorId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<ClassAnnouncement> {
        return readGuard.read(
            key = "announcements:v=$CACHE_SCHEMA_VERSION:user=$professorId:role=professor",
            groups = setOf(announcementsGroup(professorId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_professor_announcements",
                parameters = buildJsonObject {
                    put("p_professor_id", professorId)
                },
            ).decodeList<ClassAnnouncement>()
        }
    }

    suspend fun toggleAnnouncementReadStatus(studentId: String, announcementId: String, isRead: Boolean) {
        client.postgrest.rpc(
            function = "toggle_announcement_read_status",
            parameters = buildJsonObject {
                put("p_student_id", studentId)
                put("p_announcement_id", announcementId)
                put("p_is_read", isRead)
            },
        )
        invalidateAnnouncements(studentId)
    }

    suspend fun createProfessorAnnouncement(
        context: Context,
        professorId: String,
        title: String,
        subtitle: String,
        content: String,
        targetYears: List<String>,
        imageUri: Uri? = null,
    ): ClassAnnouncement {
        val cleanTitle = title.trim()
        val cleanSubtitle = subtitle.trim()
        val cleanContent = content.trim()

        if (cleanTitle.isBlank()) {
            error("Announcement title is required.")
        }
        if (cleanContent.isBlank()) {
            error("Announcement message is required.")
        }

        val imageUrl = imageUri?.let { uploadAnnouncementImage(context, professorId, it) }

        val announcement = client.postgrest.rpc(
            function = "create_announcement",
            parameters = buildJsonObject {
                put("p_professor_id", professorId)
                put("p_title", cleanTitle)
                put("p_subtitle", cleanSubtitle)
                put("p_content", cleanContent)
                put("p_image_url", imageUrl.orEmpty())
                put(
                    "p_target_years",
                    kotlinx.serialization.json.JsonArray(
                        targetYears.map { kotlinx.serialization.json.JsonPrimitive(it) }
                    )
                )
            },
        ).decodeAs<ClassAnnouncement>()
        invalidateAnnouncements(professorId)
        return announcement
    }

    suspend fun updateProfessorAnnouncement(
        context: Context,
        professorId: String,
        announcementId: String,
        title: String,
        subtitle: String,
        content: String,
        targetYears: List<String>,
        imageUri: Uri? = null,
        existingImageUrl: String? = null,
    ): ClassAnnouncement {
        val cleanTitle = title.trim()
        val cleanSubtitle = subtitle.trim()
        val cleanContent = content.trim()

        if (cleanTitle.isBlank()) {
            error("Announcement title is required.")
        }
        if (cleanContent.isBlank()) {
            error("Announcement message is required.")
        }

        val imageUrl = imageUri?.let { uploadAnnouncementImage(context, professorId, it) }
            ?: existingImageUrl.orEmpty()

        val announcement = client.postgrest.rpc(
            function = "update_announcement",
            parameters = buildJsonObject {
                put("p_professor_id", professorId)
                put("p_announcement_id", announcementId)
                put("p_title", cleanTitle)
                put("p_subtitle", cleanSubtitle)
                put("p_content", cleanContent)
                put("p_image_url", imageUrl)
                put(
                    "p_target_years",
                    kotlinx.serialization.json.JsonArray(
                        targetYears.map { kotlinx.serialization.json.JsonPrimitive(it) }
                    )
                )
            },
        ).decodeAs<ClassAnnouncement>()
        invalidateAnnouncements(professorId)
        return announcement
    }

    suspend fun deleteProfessorAnnouncement(professorId: String, announcementId: String) {
        client.postgrest.rpc(
            function = "delete_announcement",
            parameters = buildJsonObject {
                put("p_professor_id", professorId)
                put("p_announcement_id", announcementId)
            },
        )
        invalidateAnnouncements(professorId)
    }

    fun invalidateAnnouncements(userId: String) {
        readGuard.invalidateGroup(announcementsGroup(userId))
    }

    private suspend fun uploadAnnouncementImage(context: Context, professorId: String, imageUri: Uri): String {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(imageUri) ?: error("Unsupported announcement image type.")
        val extension = when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> error("Only JPEG, PNG, or WEBP announcement images are allowed.")
        }

        val size = getFileSize(context, imageUri)
        if (size > MAX_ANNOUNCEMENT_IMAGE_BYTES) {
            error("Announcement image must be 3MB or smaller.")
        }

        val bytes = resolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: error("Unable to read selected announcement image.")
        if (bytes.size > MAX_ANNOUNCEMENT_IMAGE_BYTES) {
            error("Announcement image must be 3MB or smaller.")
        }

        val path = "announcements/$professorId/announcement_${System.currentTimeMillis()}.$extension"
        client.storage.from(ANNOUNCEMENT_IMAGE_BUCKET).upload(path, bytes) {
            contentType = ContentType.parse(mimeType)
        }

        return client.storage.from(ANNOUNCEMENT_IMAGE_BUCKET).publicUrl(path)
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex >= 0 && cursor.moveToFirst()) {
                return cursor.getLong(sizeIndex)
            }
        }
        return context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
    }

    companion object {
        private const val TAG = "AnnouncementRepository"
        private const val ANNOUNCEMENT_IMAGE_BUCKET = "announcement-images"
        private const val MAX_ANNOUNCEMENT_IMAGE_BYTES = 3_145_728L
        private const val CACHE_SCHEMA_VERSION = 2
        private const val ONE_MINUTE_MILLIS = 60_000L

        private fun announcementsGroup(userId: String): String {
            return "announcements:user=$userId"
        }

        fun readableError(throwable: Throwable): String {
            Log.e(TAG, "Announcement error", throwable)
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
                restException?.statusCode == 404 ||
                    restText.contains("Could not find the function", ignoreCase = true) ||
                    restText.contains("schema cache", ignoreCase = true) ||
                    restText.contains("PGRST202", ignoreCase = true) ->
                    "Supabase RPC is missing or outdated. Run the announcement SQL schema."
                message.isNotBlank() -> "Error: $message"
                else -> "Unknown error: ${throwable::class.simpleName}"
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
