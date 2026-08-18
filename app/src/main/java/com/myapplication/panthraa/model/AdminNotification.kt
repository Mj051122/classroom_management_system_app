package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminNotification(
    val id: Long,
    val audience: String = "",
    val title: String,
    val message: String,
    @SerialName("created_at")
    val createdAt: String,
    val isRead: Boolean = false,
)
