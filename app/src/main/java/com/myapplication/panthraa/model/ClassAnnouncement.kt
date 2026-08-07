package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassAnnouncement(
    val id: String,
    @SerialName("target_years")
    val targetYears: List<String> = emptyList(),
    val title: String,
    val subtitle: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("professor_name")
    val professorName: String,
    @SerialName("professor_profile_pic_url")
    val professorProfilePicUrl: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("is_read")
    val isRead: Boolean = false
)
