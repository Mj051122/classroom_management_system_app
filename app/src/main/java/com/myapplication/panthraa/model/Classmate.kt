package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Classmate(
    val id: String = "",
    val name: String = "",
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("has_submitted_recent")
    val hasSubmittedRecent: Boolean = false,
)
