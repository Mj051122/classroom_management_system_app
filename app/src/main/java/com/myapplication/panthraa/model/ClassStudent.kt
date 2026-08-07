package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassStudent(
    val id: String = "",
    @SerialName("id_number")
    val idNumber: String = "",
    val name: String = "",
    @SerialName("photo_url")
    val photoUrl: String? = null,
    val year: String? = null,
    val section: String? = null,
    @SerialName("completed_assignments")
    val completedAssignments: Int = 0,
    @SerialName("total_assignments")
    val totalAssignments: Int = 0,
    @SerialName("has_submitted_recent")
    val hasSubmittedRecent: Boolean = false,
)
