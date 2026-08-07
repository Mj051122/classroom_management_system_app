package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassJoinRequest(
    val id: String = "",
    @SerialName("class_id")
    val classId: String = "",
    @SerialName("class_name")
    val className: String = "",
    @SerialName("subject_code")
    val subjectCode: String = "",
    @SerialName("student_id")
    val studentId: String = "",
    @SerialName("student_id_number")
    val idNumber: String = "",
    @SerialName("student_name")
    val name: String = "",
    @SerialName("student_profile_picture_url")
    val photoUrl: String? = null,
    val course: String? = null,
    val year: String? = null,
    val section: String? = null,
    val track: String? = null,
    @SerialName("requested_at")
    val requestedAt: String? = null,
)
