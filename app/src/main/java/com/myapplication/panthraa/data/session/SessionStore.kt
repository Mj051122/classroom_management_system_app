package com.myapplication.panthraa.data.session

import android.content.Context
import android.util.Log

/**
 * Clears credentials saved by the prototype login flow.
 *
 * Supabase Auth now persists its own session securely. This class deliberately
 * does not read or write a password, so a raw password cannot be retained by
 * the app after the email-auth migration.
 */
class SessionStore(context: Context) {
    private val appContext = context.applicationContext

    fun clear() {
        runCatching { appContext.deleteSharedPreferences(FILE_NAME) }
            .onFailure { Log.e(TAG, "Failed to clear legacy credential storage", it) }
    }

    companion object {
        private const val TAG = "SessionStore"
        private const val FILE_NAME = "panthraa_session"
    }
}
