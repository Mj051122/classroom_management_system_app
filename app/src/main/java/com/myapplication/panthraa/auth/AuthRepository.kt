package com.myapplication.panthraa.auth

import com.myapplication.panthraa.data.SupabaseClientProvider
import com.myapplication.panthraa.model.AppUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
) {
    suspend fun login(idNumber: String, password: String, role: UserRole): AppUser {
        return client.postgrest.rpc(
            function = "login_app_user",
            parameters = buildJsonObject {
                put("p_id_number", idNumber.trim())
                put("p_password", password.trim())
                put("p_role", role.value.lowercase())
            },
        ).decodeSingleOrNull<AppUser>() ?: error("Invalid ID number, password, or role.")
    }

    suspend fun register(
        idNumber: String,
        fullName: String,
        password: String,
        role: UserRole,
    ) {
        client.postgrest.rpc(
            function = "register_app_user",
            parameters = buildJsonObject {
                put("p_id_number", idNumber.trim())
                put("p_full_name", fullName.trim())
                put("p_role", role.value)
                put("p_password", password.trim())
            },
        )
    }

    suspend fun signOut() = Unit
}
