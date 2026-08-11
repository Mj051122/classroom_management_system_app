package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUser(
    val id: String = "",
    @SerialName("id_number")
    val idNumber: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    @SerialName("profile_picture_url")
    val profilePictureUrl: String? = null,
    val course: String? = null,
    @SerialName("year_level")
    val year: String? = null,
    val section: String? = null,
    val track: String? = null,
    val bio: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)
