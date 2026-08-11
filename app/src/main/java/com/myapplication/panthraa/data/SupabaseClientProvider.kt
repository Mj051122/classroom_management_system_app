package com.myapplication.panthraa.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.seconds

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://ulxbeelcvbawkpcutaom.supabase.co"
    private const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_CUpuMbFx6QJMJNBYzdPlvQ_KtDOdgX9"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_PUBLISHABLE_KEY,
    ) {
        requestTimeout = 30.seconds
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
