package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceStudent(
    @SerialName("assignment_id")
    val assignmentId: String = "",
    @SerialName("student_id")
    val studentId: String = "",
    @SerialName("id_number")
    val idNumber: String = "",
    val name: String = "",
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("attendance_date")
    val attendanceDate: String? = null,
    val status: String = "absent",
    @SerialName("attendance_id")
    val attendanceId: String? = null,
    @SerialName("recorded_at")
    val recordedAt: String? = null,
)

@Serializable
data class RecordAttendanceResult(
    @SerialName("student_id")
    val studentId: String = "",
    @SerialName("id_number")
    val idNumber: String = "",
    val name: String = "",
    @SerialName("already_recorded")
    val alreadyRecorded: Boolean = false,
    val message: String = "",
)
