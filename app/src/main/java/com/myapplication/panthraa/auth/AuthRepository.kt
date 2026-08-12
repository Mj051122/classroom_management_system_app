package com.myapplication.panthraa.auth

import com.myapplication.panthraa.data.SupabaseClientProvider
import com.myapplication.panthraa.model.AppUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
) {
    suspend fun signIn(email: String, password: String): AppUser {
        client.auth.signInWith(Email) {
            this.email = email.normalizedEmail()
            this.password = password
        }
        return currentProfileOrNull()
            ?: error("This email account is not connected to an app profile yet.")
    }

    suspend fun beginRegistration(email: String, password: String, idNumber: String) {
        client.postgrest.rpc(
            function = "check_id_number_available",
            parameters = buildJsonObject {
                put("p_id_number", idNumber.trim())
            },
        ).decodeAs<Boolean>()
        client.auth.signUpWith(Email) {
            this.email = email.normalizedEmail()
            this.password = password
        }
    }

    suspend fun completeRegistration(
        email: String,
        otp: String,
        idNumber: String,
        fullName: String,
        role: UserRole,
    ): AppUser {
        client.auth.verifyEmailOtp(
            type = OtpType.Email.SIGNUP,
            email = email.normalizedEmail(),
            token = otp.trim(),
        )
        return client.postgrest.rpc(
            function = "complete_email_registration",
            parameters = buildJsonObject {
                put("p_id_number", idNumber.trim())
                put("p_full_name", fullName.trim())
                put("p_role", role.value)
            },
        ).decodeSingleOrNull<AppUser>()
            ?: error("Could not create your app profile.")
    }

    suspend fun requestPasswordReset(email: String) {
        client.auth.resetPasswordForEmail(email = email.normalizedEmail())
    }

    suspend fun verifyPasswordResetCode(email: String, otp: String) {
        client.auth.verifyEmailOtp(
            type = OtpType.Email.RECOVERY,
            email = email.normalizedEmail(),
            token = otp.trim(),
        )
    }

    suspend fun updatePassword(newPassword: String) {
        client.auth.updateUser {
            password = newPassword
        }
        client.auth.signOut()
    }

    suspend fun currentProfileOrNull(): AppUser? {
        if (client.auth.currentUserOrNull() == null) return null
        return client.postgrest.rpc(
            function = "current_authenticated_app_user",
        ).decodeSingleOrNull<AppUser>()
    }

    suspend fun restoreAuthenticatedProfileOrNull(): AppUser? {
        client.auth.loadFromStorage()
        return currentProfileOrNull()
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    private fun String.normalizedEmail(): String = trim().lowercase()
}
