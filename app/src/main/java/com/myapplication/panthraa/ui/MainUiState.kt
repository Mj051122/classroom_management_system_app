package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.AdminNotification
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.AssignmentComment
import com.myapplication.panthraa.model.AssignmentSubmission
import com.myapplication.panthraa.model.AssignmentStatus
import com.myapplication.panthraa.model.AttendanceStudent
import com.myapplication.panthraa.model.ClassAssignment
import com.myapplication.panthraa.model.ClassJoinRequest
import com.myapplication.panthraa.model.ConnectivityStatus
import com.myapplication.panthraa.model.PendingAssignment
import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.StudentClass
import com.myapplication.panthraa.model.StudentGrade
import com.myapplication.panthraa.model.StudentJoinRequest
import com.myapplication.panthraa.model.TaskReminder
import com.myapplication.panthraa.model.ClassAnnouncement

enum class RefreshSurface {
    Dashboard,
    Classes,
    Assignments,
    PendingAssignments,
    Grades,
    Announcements,
    People,
    Classmates,
    Students,
    Submissions,
    Attendance,
    JoinRequests,
}

data class MainUiState(
    val currentUser: AppUser? = null,
    val users: List<AppUser> = emptyList(),
    val studentClasses: List<StudentClass> = emptyList(),
    val professorClasses: List<ProfessorClass> = emptyList(),
    val classAssignments: List<ClassAssignment> = emptyList(),
    val assignmentSubmissions: List<AssignmentSubmission> = emptyList(),
    val classmates: List<com.myapplication.panthraa.model.Classmate> = emptyList(),
    val professorStudents: List<com.myapplication.panthraa.model.ClassStudent> = emptyList(),
    val attendanceStudents: List<AttendanceStudent> = emptyList(),
    val attendanceError: String? = null,
    val classJoinRequests: List<ClassJoinRequest> = emptyList(),
    val studentJoinRequests: List<StudentJoinRequest> = emptyList(),
    val assignmentStatuses: Map<String, AssignmentStatus> = emptyMap(),
    val pendingAssignments: List<PendingAssignment> = emptyList(),
    val studentGrades: List<StudentGrade> = emptyList(),
    val professorGradeMonitorEnrollments: List<ProfessorGradeMonitorEnrollment> = emptyList(),
    val professorGradeMonitorSubmissions: List<ProfessorGradeMonitorSubmission> = emptyList(),
    val taskReminders: List<TaskReminder> = emptyList(),
    val classAnnouncements: List<ClassAnnouncement> = emptyList(),
    val notifications: List<AdminNotification> = emptyList(),
    val isUploadingProfilePicture: Boolean = false,
    val isUpdatingProfileDetails: Boolean = false,
    val isLoadingUsers: Boolean = false,
    val isLoadingClasses: Boolean = false,
    val isLoadingClassmates: Boolean = false,
    val isLoadingProfessorStudents: Boolean = false,
    val isLoadingAttendance: Boolean = false,
    val isRecordingAttendance: Boolean = false,
    val isLoadingJoinRequests: Boolean = false,
    val isUpdatingJoinRequest: Boolean = false,
    val isLoadingStudentJoinRequests: Boolean = false,
    val isJoiningClass: Boolean = false,
    val isCreatingClass: Boolean = false,
    val isUpdatingClass: Boolean = false,
    val isDeletingSubject: Boolean = false,
    val isLoadingAssignments: Boolean = false,
    val isLoadingAssignmentSubmissions: Boolean = false,
    val assignmentComments: List<AssignmentComment> = emptyList(),
    val commentsAssignmentId: String? = null,
    val isLoadingAssignmentComments: Boolean = false,
    val isPostingComment: Boolean = false,
    val isDeletingComment: Boolean = false,
    val isHidingComment: Boolean = false,
    val isScoringSubmission: Boolean = false,
    val isCreatingAssignment: Boolean = false,
    val isUpdatingAssignment: Boolean = false,
    val isSubmittingAssignment: Boolean = false,
    val isDeletingAssignment: Boolean = false,
    val isLoadingPendingAssignments: Boolean = false,
    val isLoadingStudentGrades: Boolean = false,
    val isLoadingProfessorGradeMonitor: Boolean = false,
    val isCreatingAnnouncement: Boolean = false,
    val isUpdatingAnnouncement: Boolean = false,
    val isDeletingAnnouncement: Boolean = false,
    val isLoadingTaskReminders: Boolean = false,
    val isLoadingAnnouncements: Boolean = false,
    val isLoadingNotifications: Boolean = false,
    val isPostingAnnouncement: Boolean = false,
    val isSavingTaskReminder: Boolean = false,
    val isDeletingTaskReminder: Boolean = false,
    val connectivityStatus: ConnectivityStatus = ConnectivityStatus.Connecting,
    val isOfflineMode: Boolean = false,
    val isRefreshingOnlineData: Boolean = false,
    val refreshingSurfaces: Set<RefreshSurface> = emptySet(),
    val lastOnlineRefreshFailed: Boolean = false,
    val showPeople: Boolean = false,
    val message: String? = null,
)

data class ProfessorGradeMonitorEnrollment(
    val classId: String = "",
    val className: String = "",
    val subjectCode: String = "",
    val yearLevel: String = "",
    val section: String = "",
    val track: String = "",
    val department: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val idNumber: String = "",
    val photoUrl: String? = null,
)

data class ProfessorGradeMonitorSubmission(
    val classId: String = "",
    val className: String = "",
    val subjectCode: String = "",
    val assignmentId: String = "",
    val assignmentTitle: String = "",
    val assignmentType: String = "task",
    val category: String = "lecture",
    val submissionId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val idNumber: String = "",
    val photoUrl: String? = null,
    val responseText: String = "",
    val submittedAt: String? = null,
    val editAttempts: Int = 0,
    val score: Int? = null,
    val targetPoints: Int = 100,
    val rawPercent: Double = 0.0,
    val convertedGrade: Double = 37.5,
    val submissionFileUrl: String? = null,
)
