package com.myapplication.panthraa.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.StudentClass
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AppUserRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
    private val readGuard: SupabaseReadGuard = SupabaseReadGuard.shared,
) {
    suspend fun getAllUsers(
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<AppUser> {
        return readGuard.read(
            key = "people:v=$CACHE_SCHEMA_VERSION:all",
            groups = setOf(PEOPLE_GROUP),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc("get_app_users_public")
                .decodeList<AppUser>()
        }
    }

    suspend fun getStudentClasses(studentId: String): List<StudentClass> {
        return client.postgrest.rpc(
            function = "get_student_classes",
            parameters = buildJsonObject {
                put("p_student_id", studentId)
            },
        ).decodeList<StudentClass>()
    }

    suspend fun joinClassByCode(studentId: String, classCode: String): StudentClass {
        val cleanCode = classCode.trim()
        if (cleanCode.isBlank()) {
            error("Class code is required.")
        }

        client.postgrest.rpc(
            function = "join_class_by_code",
            parameters = buildJsonObject {
                put("p_student_id", studentId)
                put("p_class_code", cleanCode)
            },
        )

        return getStudentClasses(studentId).firstOrNull {
            it.classCode.equals(cleanCode, ignoreCase = true)
        } ?: error("Class code not found.")
    }

    suspend fun getProfessorClasses(professorId: String): List<ProfessorClass> {
        return client.postgrest.rpc(
            function = "get_professor_classes",
            parameters = buildJsonObject {
                put("p_professor_id", professorId)
            },
        ).decodeList<ProfessorClass>()
    }

    suspend fun createProfessorClass(professorId: String, subjectName: String): ProfessorClass {
        val cleanSubject = subjectName.trim()
        if (cleanSubject.isBlank()) {
            error("Subject name is required.")
        }

        return client.postgrest.rpc(
            function = "create_professor_class",
            parameters = buildJsonObject {
                put("p_professor_id", professorId)
                put("p_subject_name", cleanSubject)
            },
        ).decodeAs<ProfessorClass>()
    }

    suspend fun updateProfilePicture(context: Context, userId: String, imageUri: Uri): String {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(imageUri) ?: error("Unsupported image type.")
        val extension = when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> error("Only JPEG, PNG, or WEBP images are allowed.")
        }

        val size = getFileSize(context, imageUri)
        if (size > MAX_PROFILE_IMAGE_BYTES) {
            error("Profile picture must be 1MB or smaller.")
        }

        val bytes = resolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: error("Unable to read selected image.")
        if (bytes.size > MAX_PROFILE_IMAGE_BYTES) {
            error("Profile picture must be 1MB or smaller.")
        }
        val path = "profiles/$userId/avatar_${System.currentTimeMillis()}.$extension"

        client.storage.from("profile-pictures").upload(path, bytes) {
            contentType = ContentType.parse(mimeType)
        }

        val publicUrl = client.storage.from("profile-pictures").publicUrl(path)
        client.postgrest.rpc(
            function = "update_app_user_profile_picture",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_profile_picture_url", publicUrl)
            },
        )
        invalidatePeople()

        return publicUrl
    }

    suspend fun updateFullName(userId: String, fullName: String): String {
        val current = getAllUsers(CachePolicy.FORCE_REFRESH).firstOrNull { it.id == userId } ?: error("User not found")
        updateProfileDetails(
            userId, fullName, current.course,
            current.year, current.section, current.track, current.bio, current.phoneNumber
        )
        return fullName
    }

    suspend fun updateIdNumber(userId: String, idNumber: String): String {
        error("ID number cannot be changed.")
    }

    suspend fun updateProfileDetails(
        userId: String,
        fullName: String,
        course: String?,
        year: String?,
        section: String?,
        track: String?,
        bio: String?,
        phoneNumber: String?
    ): String {
        val cleanName = fullName.trim()
        if (cleanName.isBlank()) error("Full name is required.")

        Log.d(TAG, "Updating profile details through RPC. userId=$userId")

        client.postgrest.rpc(
            function = "update_app_user_extended_profile",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_full_name", cleanName)
                put("p_course", course?.trim())
                put("p_year", year?.trim())
                put("p_section", section?.trim())
                put("p_track", track?.trim())
                put("p_bio", bio?.trim())
                put("p_phone_number", phoneNumber?.trim())
            },
        )
        invalidatePeople()

        return cleanName
    }

    private fun invalidatePeople() {
        readGuard.invalidateGroup(PEOPLE_GROUP)
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
        private const val MAX_PROFILE_IMAGE_BYTES = 1_048_576L
        private const val TAG = "AppUserRepository"
        private const val CACHE_SCHEMA_VERSION = 1
        private const val ONE_MINUTE_MILLIS = 60_000L
        private const val PEOPLE_GROUP = "people"

        fun readableError(throwable: Throwable): String {
            Log.e(TAG, "User feature error", throwable)
            val message = throwable.message.orEmpty()
            return when {
                message.isConnectivityMessage() -> "Refresh failed. Showing last saved data."
                message.contains("1MB", ignoreCase = true) -> "Profile picture must be 1MB or smaller."
                message.contains("JPEG", ignoreCase = true) -> "Only JPEG, PNG, or WEBP images are allowed."
                message.contains("Full name", ignoreCase = true) -> "Full name is required."
                message.contains("ID number", ignoreCase = true) -> "ID number is required."
                message.contains("Class code", ignoreCase = true) -> "Class code is required."
                message.contains("Subject name", ignoreCase = true) -> "Subject name is required."
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
