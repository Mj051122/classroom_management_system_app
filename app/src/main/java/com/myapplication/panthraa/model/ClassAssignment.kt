package com.myapplication.panthraa.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AssignmentType(val value: String) {
    MATERIAL("material"),
    TASK("task"),
    QUIZ("quiz"),
    EXAM("exam");
}

@Serializable
data class ClassAssignment(
    val id: String = "",
    @SerialName("class_id")
    val classId: String = "",
    @SerialName("professor_id")
    val professorId: String = "",
    val title: String = "",
    val instructions: String = "",
    val category: String = "lecture",
    @SerialName("target_points")
    val targetPoints: Int = 100,
    @SerialName("has_submitted")
    val hasSubmitted: Boolean = false,
    @SerialName("submission_text")
    val submissionText: String? = null,
    @SerialName("submitted_at")
    val submittedAt: String? = null,
    @SerialName("edit_attempts")
    val editAttempts: Int? = null,
    @SerialName("submitted_count")
    val submittedCount: Int = 0,
    @SerialName("total_students")
    val totalStudents: Int = 0,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    @SerialName("start_time")
    val startTime: String? = null,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("assignment_type")
    val assignmentType: String = "task",
    @SerialName("submission_format")
    val submissionFormat: String = "pdf",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("attendance_status")
    val attendanceStatus: String? = null,
    val score: Int? = null,
    @SerialName("requires_file")
    val requiresFile: Boolean = true,
    @SerialName("file_url")
    val fileUrl: String? = null,
    @SerialName("submission_file_url")
    val submissionFileUrl: String? = null,
)

@Serializable
data class AssignmentSubmission(
    val id: String = "",
    @SerialName("assignment_id")
    val assignmentId: String = "",
    @SerialName("student_id")
    val studentId: String = "",
    @SerialName("student_name")
    val studentName: String = "",
    @SerialName("id_number")
    val idNumber: String = "",
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("response_text")
    val responseText: String = "",
    @SerialName("submitted_at")
    val submittedAt: String? = null,
    @SerialName("edit_attempts")
    val editAttempts: Int = 0,
    val score: Int? = null,
    @SerialName("target_points")
    val targetPoints: Int = 100,
    val category: String = "lecture",
    @SerialName("raw_percent")
    val rawPercent: Double = 0.0,
    @SerialName("converted_grade")
    val convertedGrade: Double = 37.5,
    @SerialName("submission_file_url")
    val submissionFileUrl: String? = null,
)

@Serializable
data class AssignmentStatus(
    @SerialName("class_id")
    val classId: String = "",
    @SerialName("assignment_id")
    val assignmentId: String? = null,
    @SerialName("assignment_title")
    val assignmentTitle: String? = null,
    @SerialName("submitted_count")
    val submittedCount: Int = 0,
    @SerialName("missing_count")
    val missingCount: Int = 0,
    @SerialName("ungraded_count")
    val ungradedCount: Int = 0,
    @SerialName("total_students")
    val totalStudents: Int = 0,
)

@Serializable
data class PendingAssignment(
    @SerialName("assignment_id")
    val assignmentId: String = "",
    @SerialName("class_id")
    val classId: String = "",
    val title: String = "",
    val instructions: String = "",
    val category: String = "lecture",
    @SerialName("target_points")
    val targetPoints: Int = 100,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    @SerialName("start_time")
    val startTime: String? = null,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("assignment_type")
    val assignmentType: String = "task",
    @SerialName("submission_format")
    val submissionFormat: String = "pdf",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("class_name")
    val className: String = "",
    @SerialName("subject_code")
    val subjectCode: String = "",
    @SerialName("theme_color")
    val themeColor: String = "blue",
    @SerialName("requires_file")
    val requiresFile: Boolean = true,
    @SerialName("professor_name")
    val professorName: String = "",
)

@Serializable
data class StudentGrade(
    @SerialName("submission_id")
    val submissionId: String = "",
    @SerialName("assignment_id")
    val assignmentId: String = "",
    @SerialName("class_id")
    val classId: String = "",
    val title: String = "",
    val score: Int? = null,
    @SerialName("target_points")
    val targetPoints: Int = 100,
    val category: String = "lecture",
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    @SerialName("start_time")
    val startTime: String? = null,
    @SerialName("end_time")
    val endTime: String? = null,
    @SerialName("raw_percent")
    val rawPercent: Double? = null,
    @SerialName("converted_grade")
    val convertedGrade: Double? = null,
    @SerialName("submitted_at")
    val submittedAt: String? = null,
    @SerialName("class_name")
    val className: String = "",
    @SerialName("subject_code")
    val subjectCode: String = "",
    @SerialName("professor_name")
    val professorName: String = "",
    @SerialName("has_submitted")
    val hasSubmitted: Boolean = false,
    @SerialName("submission_text")
    val submissionText: String? = null,
)
