package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskReminder(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val kind: String = "",
    val title: String = "",
    val details: String = "",
    @SerialName("reminder_date")
    val reminderDate: String = "",
    @SerialName("reminder_time")
    val reminderTime: String? = null,
    @SerialName("theme_color")
    val themeColor: String = "#0034DE",
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
) {
    val isNote: Boolean
        get() = kind.equals("note", ignoreCase = true)
}
