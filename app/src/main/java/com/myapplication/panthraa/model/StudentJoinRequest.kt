package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentJoinRequest(
    val id: String = "",
    @SerialName("class_id")
    val classId: String = "",
    @SerialName("class_name")
    val className: String = "",
    @SerialName("subject_code")
    val subjectCode: String = "",
    @SerialName("theme_color")
    val themeColor: String? = null,
    val status: String = "",
    @SerialName("rejection_reason")
    val rejectionReason: String? = null,
    @SerialName("requested_at")
    val requestedAt: String? = null,
    @SerialName("decided_at")
    val decidedAt: String? = null,
)