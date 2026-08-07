package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    @SerialName("id_number")
    val idNumber: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val role: String = "",
    val course: String? = null,
    val year: String? = null,
    val section: String? = null,
    val track: String? = null,
)
