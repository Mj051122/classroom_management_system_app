package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentClass(
    val id: String = "",
    @SerialName("class_code")
    val classCode: String = "",
    @SerialName("subject_name")
    val subjectName: String = "",
    @SerialName("class_name")
    val className: String = "",
    @SerialName("subject_code")
    val subjectCode: String = "",
    @SerialName("join_code")
    val joinCode: String = "",
    @SerialName("year_level")
    val yearLevel: String? = null,
    val department: String? = null,
    val section: String? = null,
    val track: String? = null,
    @SerialName("schedule_days")
    val scheduleDays: List<String> = emptyList(),
    @SerialName("schedule_start_time")
    val scheduleStartTime: String? = null,
    @SerialName("schedule_end_time")
    val scheduleEndTime: String? = null,
    @SerialName("cover_image_url")
    val coverImageUrl: String? = null,
    @SerialName("theme_color")
    val themeColor: String = "blue",
    @SerialName("professor_initial")
    val professorInitial: String = "",
    @SerialName("professor_name")
    val professorName: String = "",
    @SerialName("professor_position")
    val professorPosition: String = "",
    @SerialName("professor_photo_url")
    val professorPhotoUrl: String? = null,
    @SerialName("student_count")
    val studentCount: Int = 0,
    @SerialName("progress_percentage")
    val progressPercentage: Int = 0,
    @SerialName("completed_assignments")
    val completedAssignments: Int = 0,
    @SerialName("total_assignments")
    val totalAssignments: Int = 0,
    @SerialName("graded_assignments")
    val gradedAssignments: Int = 0,
    @SerialName("average_score")
    val averageScore: Int = 0,
    @SerialName("room_id")
    val roomId: String? = null,
) {
    val displayClassName: String
        get() = className.ifBlank { subjectName.ifBlank { "Untitled class" } }

    val displaySubjectCode: String
        get() = subjectCode.ifBlank { classCode }

    val displayJoinCode: String
        get() = joinCode.ifBlank { classCode }

    val displayProfessorName: String
        get() = professorName.ifBlank { "Faculty" }

    val displayProfessorRole: String
        get() = professorPosition.ifBlank { "Faculty" }
}
