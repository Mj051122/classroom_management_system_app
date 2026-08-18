package com.myapplication.panthraa.ui

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.content.Intent
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import androidx.annotation.DrawableRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myapplication.panthraa.R
import com.myapplication.panthraa.data.OfflineImageCache
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.AssignmentComment
import com.myapplication.panthraa.model.AssignmentSubmission
import com.myapplication.panthraa.model.AssignmentStatus
import com.myapplication.panthraa.model.AttendanceStudent
import com.myapplication.panthraa.model.ClassAnnouncement
import com.myapplication.panthraa.model.ClassAssignment
import com.myapplication.panthraa.model.ClassJoinRequest
import com.myapplication.panthraa.model.ConnectivityStatus
import com.myapplication.panthraa.model.PendingAssignment
import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.StudentClass
import com.myapplication.panthraa.model.StudentGrade
import com.myapplication.panthraa.model.StudentJoinRequest
import com.myapplication.panthraa.model.TaskReminder
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlin.math.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClassesScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    currentUser: AppUser,
    onLoadClasses: (AppUser) -> Unit,
    onLoadAssignments: (String) -> Unit,
    onLoadClassmates: (String) -> Unit,
    onLoadProfessorStudents: (String) -> Unit,
    onLoadAttendance: (String) -> Unit,
    onLoadJoinRequests: (List<String>) -> Unit,
    onRefreshClasses: () -> Unit,
    onRefreshAssignments: (String) -> Unit,
    onRefreshClassmates: (String) -> Unit,
    onRefreshProfessorStudents: (String) -> Unit,
    onRefreshAttendance: (String) -> Unit,
    onRefreshJoinRequests: (List<String>) -> Unit,
    onLoadStudentJoinRequests: () -> Unit = {},
    onJoinClass: (String) -> Unit,
    onApproveJoinRequest: (String, List<String>) -> Unit,
    onRejectJoinRequest: (String, List<String>, String) -> Unit,
    onCreateAssignment: (String, String, String, String, Int, String?, String?, String?, String?, String, String, Boolean, Boolean, Uri?) -> Unit,
    onUpdateAssignment: (String, String, String, String, Int, String?, String?, String?, String?, String, String, Boolean, Boolean, Uri?) -> Unit,
    onLoadAssignmentSubmissions: (String) -> Unit,
    onRefreshAssignmentSubmissions: (String) -> Unit,
    onLoadAssignmentComments: (String) -> Unit = {},
    onRefreshAssignmentComments: (String) -> Unit = {},
    onPostAssignmentComment: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteAssignmentComment: (String) -> Unit = {},
    onHideAssignmentComment: (String) -> Unit = {},
    onUnhideAssignmentComment: (String) -> Unit = {},
    onRecordAttendance: (String, String) -> Unit,
    onGradeSubmission: (String, Int, Int) -> Unit,
    onSubmitAssignment: (String, String, Uri?) -> Unit,
    onRecordMaterialView: (String) -> Unit,
    onDeleteAssignment: (String) -> Unit,
    classToOpenFromDashboard: StudentClass? = null,
    onDashboardClassOpenConsumed: () -> Unit = {},
    assignmentToOpenFromDashboard: PendingAssignment? = null,
    onDashboardAssignmentOpenConsumed: () -> Unit = {},
    professorYearToOpenFromDashboard: String? = null,
    onDashboardProfessorYearOpenConsumed: () -> Unit = {},
    professorClassToOpenFromDashboard: ProfessorClass? = null,
    openProfessorUploadFromDashboard: Boolean = false,
    onDashboardProfessorClassOpenConsumed: () -> Unit = {},
    dashboardRootResetToken: Int = 0,
    onCreateClass: (String, String, String, String, String, String, String?, Uri?, String, List<String>, String, String) -> Unit,
    onUpdateClass: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onDeleteClass: (String) -> Unit = {},
    onOpenPeople: () -> Unit = {},
) {
    var selectedClass by remember { mutableStateOf<StudentClass?>(null) }
    var viewingClassmatesFor by remember { mutableStateOf<StudentClass?>(null) }
    var viewingStudentsFor by remember { mutableStateOf<ProfessorClass?>(null) }
    var selectedProfessorClass by remember { mutableStateOf<ProfessorClass?>(null) }
    var selectedProfessorYear by remember { mutableStateOf<ProfessorYearOption?>(null) }
    var selectedProfessorDepartment by remember { mutableStateOf<ProfessorDepartmentGroup?>(null) }
    var selectedProfessorSection by remember { mutableStateOf<ProfessorSectionGroup?>(null) }
    var selectedProfessorSubject by remember { mutableStateOf<ProfessorSubjectGroup?>(null) }
    var viewingJoinRequestsFor by remember { mutableStateOf<ProfessorSubjectGroup?>(null) }
    var openUploadForSelectedProfessorClass by remember { mutableStateOf(false) }
    var selectedStudentAssignment by remember { mutableStateOf<ClassAssignment?>(null) }
    var selectedStudentAttendanceAssignment by remember { mutableStateOf<ClassAssignment?>(null) }
    var selectedProfessorAssignment by remember { mutableStateOf<ClassAssignment?>(null) }
    var selectedAttendanceAssignment by remember { mutableStateOf<ClassAssignment?>(null) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var createClassYear by remember { mutableStateOf<String?>(null) }
    var createClassDepartment by remember { mutableStateOf("") }
    var createClassSection by remember { mutableStateOf("") }
    var createClassTrack by remember { mutableStateOf("") }
    var createClassSubjectCode by remember { mutableStateOf("") }
    val internetRequired = uiState.isOfflineMode || uiState.connectivityStatus != ConnectivityStatus.Online

    LaunchedEffect(dashboardRootResetToken) {
        selectedClass = null
        viewingClassmatesFor = null
        viewingStudentsFor = null
        selectedProfessorClass = null
        selectedProfessorYear = null
        selectedProfessorDepartment = null
        selectedProfessorSection = null
        selectedProfessorSubject = null
        viewingJoinRequestsFor = null
        openUploadForSelectedProfessorClass = false
        selectedStudentAssignment = null
        selectedStudentAttendanceAssignment = null
        selectedProfessorAssignment = null
        selectedAttendanceAssignment = null
        showJoinDialog = false
        showCreateDialog = false
    }

    LaunchedEffect(currentUser.id, currentUser.role) {
        onLoadClasses(currentUser)
        if (currentUser.role.equals("student", ignoreCase = true)) {
            onLoadStudentJoinRequests()
        }
    }

    LaunchedEffect(classToOpenFromDashboard?.id) {
        val dashboardClass = classToOpenFromDashboard
        if (dashboardClass != null) {
            selectedStudentAssignment = null
            selectedProfessorAssignment = null
            selectedProfessorClass = null
            selectedProfessorYear = null
            selectedProfessorDepartment = null
            selectedProfessorSection = null
            selectedProfessorSubject = null
            viewingJoinRequestsFor = null
            selectedClass = dashboardClass
            assignmentToOpenFromDashboard?.let { pending ->
                selectedStudentAssignment = uiState.classAssignments
                    .firstOrNull { it.id == pending.assignmentId }
                    ?: pending.toStudentClassAssignment()
                onDashboardAssignmentOpenConsumed()
            }
            selectedStudentAttendanceAssignment = null
            selectedAttendanceAssignment = null
            onDashboardClassOpenConsumed()
        }
    }

    LaunchedEffect(professorYearToOpenFromDashboard) {
        val yearValue = professorYearToOpenFromDashboard
        if (yearValue != null && currentUser.role.equals("professor", ignoreCase = true)) {
            val option = ProfessorClassYearOptions.firstOrNull { it.value.equals(yearValue, ignoreCase = true) }
            if (option != null) {
                selectedClass = null
                viewingClassmatesFor = null
                viewingStudentsFor = null
                selectedProfessorClass = null
                selectedProfessorDepartment = null
                selectedProfessorSection = null
                selectedProfessorSubject = null
                viewingJoinRequestsFor = null
                selectedStudentAssignment = null
                selectedStudentAttendanceAssignment = null
                selectedProfessorAssignment = null
                selectedAttendanceAssignment = null
                selectedProfessorYear = option
            }
            onDashboardProfessorYearOpenConsumed()
        }
    }

    LaunchedEffect(professorClassToOpenFromDashboard?.id) {
        val dashboardClass = professorClassToOpenFromDashboard
        if (dashboardClass != null && currentUser.role.equals("professor", ignoreCase = true)) {
            selectedClass = null
            viewingClassmatesFor = null
            viewingStudentsFor = null
            selectedProfessorYear = null
            selectedProfessorDepartment = null
            selectedProfessorSection = null
            selectedProfessorSubject = null
            viewingJoinRequestsFor = null
            selectedStudentAssignment = null
            selectedStudentAttendanceAssignment = null
            selectedProfessorAssignment = null
            selectedAttendanceAssignment = null
            openUploadForSelectedProfessorClass = openProfessorUploadFromDashboard
            selectedProfessorClass = dashboardClass
            onDashboardProfessorClassOpenConsumed()
        }
    }

    if (showCreateDialog && currentUser.role.equals("professor", ignoreCase = true)) {
        CreateClassScreen(
            currentUser = currentUser,
            isCreating = uiState.isCreatingClass,
            currentClassCount = uiState.professorClasses.size,
            initialYearLevel = createClassYear,
            lockedYearLevel = createClassYear,
            initialDepartment = createClassDepartment,
            lockedDepartment = createClassDepartment.takeIf { it.isNotBlank() },
            initialSection = createClassSection,
            lockedSection = createClassSection.takeIf { it.isNotBlank() },
            initialTrack = createClassTrack,
            lockedTrack = createClassTrack.takeIf { it.isNotBlank() },
            initialSubjectCode = createClassSubjectCode,
            existingClasses = uiState.professorClasses,
            onBack = { if (!uiState.isCreatingClass) showCreateDialog = false },
            onCreateClass = onCreateClass,
        )
        return
    }

    if (viewingClassmatesFor != null) {
        ClassmatesPage(
            currentUser = currentUser,
            classItem = viewingClassmatesFor!!,
            classmates = uiState.classmates,
            isLoading = uiState.isLoadingClassmates,
            isRefreshing = RefreshSurface.Classmates in uiState.refreshingSurfaces,
            onRefresh = { onRefreshClassmates(viewingClassmatesFor!!.id) },
            onBack = { viewingClassmatesFor = null },
        )
        return
    }

    if (viewingStudentsFor != null) {
        ViewStudentsPage(
            classItem = viewingStudentsFor!!,
            uiState = uiState,
            isRefreshing = RefreshSurface.Students in uiState.refreshingSurfaces,
            onRefresh = { onRefreshProfessorStudents(viewingStudentsFor!!.id) },
            onBack = { viewingStudentsFor = null },
        )
        return
    }

    selectedClass?.let { classItem ->
        selectedStudentAttendanceAssignment?.let { assignment ->
            AttendanceScreen(
                classTitle = classItem.displayClassName,
                assignment = uiState.classAssignments.firstOrNull { it.id == assignment.id } ?: assignment,
                students = uiState.attendanceStudents,
                loadError = uiState.attendanceError,
                isLoading = uiState.isLoadingAttendance,
                isRecording = false,
                internetRequired = internetRequired,
                canMarkAttendance = false,
                viewerUser = currentUser,
                onLoadAttendance = onLoadAttendance,
                isRefreshing = RefreshSurface.Attendance in uiState.refreshingSurfaces,
                onRefreshAttendance = onRefreshAttendance,
                onRecordAttendance = { _, _ -> },
                onBack = { selectedStudentAttendanceAssignment = null },
            )
            return
        }

        selectedStudentAssignment?.let { assignment ->
            StudentAssignmentDetailPage(
                assignment = uiState.classAssignments.firstOrNull { it.id == assignment.id } ?: assignment,
                classItem = classItem,
                innerPadding = innerPadding,
                isSubmitting = uiState.isSubmittingAssignment,
                internetRequired = internetRequired,
                comments = uiState.assignmentComments,
                isLoadingComments = uiState.isLoadingAssignmentComments,
                isPostingComment = uiState.isPostingComment,
                isDeletingComment = uiState.isDeletingComment,
                isHidingComment = uiState.isHidingComment,
                currentUserId = currentUser.id,
                onSubmitAssignment = onSubmitAssignment,
                onRecordMaterialView = onRecordMaterialView,
                onLoadComments = onLoadAssignmentComments,
                onRefreshComments = onRefreshAssignmentComments,
                onPostComment = onPostAssignmentComment,
                onDeleteComment = onDeleteAssignmentComment,
                onHideComment = onHideAssignmentComment,
                onUnhideComment = onUnhideAssignmentComment,
                onOpenAttendance = { selectedStudentAttendanceAssignment = assignment },
                onBack = { selectedStudentAssignment = null },
            )
            return
        }

        StudentClassroomPage(
            classItem = classItem,
            assignments = uiState.classAssignments,
            isLoadingAssignments = uiState.isLoadingAssignments,
            isSubmittingAssignment = uiState.isSubmittingAssignment,
            internetRequired = internetRequired,
            onLoadAssignments = onLoadAssignments,
            isRefreshingAssignments = RefreshSurface.Assignments in uiState.refreshingSurfaces,
            onRefreshAssignments = onRefreshAssignments,
            onSubmitAssignment = onSubmitAssignment,
            onOpenAssignment = { selectedStudentAssignment = it },
            onOpenAttendance = { selectedStudentAttendanceAssignment = it },
            onViewClassmates = {
                viewingClassmatesFor = classItem
                onLoadClassmates(classItem.id)
            },
            onBack = {
                selectedStudentAssignment = null
                selectedStudentAttendanceAssignment = null
                selectedClass = null
            },
        )
        return
    }

    selectedProfessorClass?.let { classItem ->
        selectedAttendanceAssignment?.let { assignment ->
            AttendanceScreen(
                classTitle = classItem.displayClassName,
                assignment = uiState.classAssignments.firstOrNull { it.id == assignment.id } ?: assignment,
                students = uiState.attendanceStudents,
                loadError = uiState.attendanceError,
                isLoading = uiState.isLoadingAttendance,
                isRecording = uiState.isRecordingAttendance,
                internetRequired = internetRequired,
                onLoadAttendance = onLoadAttendance,
                isRefreshing = RefreshSurface.Attendance in uiState.refreshingSurfaces,
                onRefreshAttendance = onRefreshAttendance,
                onRecordAttendance = onRecordAttendance,
                onBack = { selectedAttendanceAssignment = null },
            )
            return
        }

        selectedProfessorAssignment?.let { assignment ->
            ProfessorAssignmentDetailPage(
                assignment = uiState.classAssignments.firstOrNull { it.id == assignment.id } ?: assignment,
                submissions = uiState.assignmentSubmissions,
                isLoadingSubmissions = uiState.isLoadingAssignmentSubmissions,
                isScoringSubmission = uiState.isScoringSubmission,
                isDeleting = uiState.isDeletingAssignment,
                isUpdating = uiState.isUpdatingAssignment,
                internetRequired = internetRequired,
                classItem = classItem,
                innerPadding = innerPadding,
                comments = uiState.assignmentComments,
                isLoadingComments = uiState.isLoadingAssignmentComments,
                isPostingComment = uiState.isPostingComment,
                isDeletingComment = uiState.isDeletingComment,
                isHidingComment = uiState.isHidingComment,
                currentUserId = currentUser.id,
                onLoadSubmissions = onLoadAssignmentSubmissions,
                isRefreshingSubmissions = RefreshSurface.Submissions in uiState.refreshingSurfaces,
                onRefreshSubmissions = onRefreshAssignmentSubmissions,
                onLoadComments = onLoadAssignmentComments,
                onRefreshComments = onRefreshAssignmentComments,
                onPostComment = onPostAssignmentComment,
                onDeleteComment = onDeleteAssignmentComment,
                onHideComment = onHideAssignmentComment,
                onUnhideComment = onUnhideAssignmentComment,
                onGradeSubmission = onGradeSubmission,
                onDeleteAssignment = onDeleteAssignment,
                onUpdateAssignment = onUpdateAssignment,
                onOpenAttendance = { selectedAttendanceAssignment = assignment },
                onBack = { selectedProfessorAssignment = null },
            )
            return
        }

            ProfessorClassroomPage(
                classItem = classItem,
                assignments = uiState.classAssignments,
                isLoadingAssignments = uiState.isLoadingAssignments,
                isCreatingAssignment = uiState.isCreatingAssignment,
                isUpdatingClass = uiState.isUpdatingClass,
                isDeletingSubject = uiState.isDeletingSubject,
                internetRequired = internetRequired,
                onLoadAssignments = onLoadAssignments,
                isRefreshingAssignments = RefreshSurface.Assignments in uiState.refreshingSurfaces,
                onRefreshAssignments = onRefreshAssignments,
                onCreateAssignment = onCreateAssignment,
                onOpenAssignment = { selectedProfessorAssignment = it },
                onOpenAttendance = { selectedAttendanceAssignment = it },
                onUpdateClass = onUpdateClass,
                onDeleteClass = onDeleteClass,
                openUploadOnStart = openUploadForSelectedProfessorClass,
                onUploadOpenConsumed = { openUploadForSelectedProfessorClass = false },
            onBack = {
                selectedProfessorAssignment = null
                selectedAttendanceAssignment = null
                openUploadForSelectedProfessorClass = false
                selectedProfessorClass = null
            },
        )
        return
    }

    viewingJoinRequestsFor?.let { subjectGroup ->
        ProfessorJoinRequestsPage(
            year = selectedProfessorYear,
            subjectGroup = subjectGroup,
            uiState = uiState,
            internetRequired = internetRequired,
            onLoadJoinRequests = onLoadJoinRequests,
            isRefreshing = RefreshSurface.JoinRequests in uiState.refreshingSurfaces,
            onRefreshJoinRequests = onRefreshJoinRequests,
            onApproveRequest = onApproveJoinRequest,
            onRejectRequest = onRejectJoinRequest,
            onBack = { viewingJoinRequestsFor = null },
        )
        return
    }

    selectedProfessorSubject?.let { subjectGroup ->
        ProfessorSubjectClassesPage(
            year = selectedProfessorYear,
            departmentGroup = selectedProfessorDepartment,
            sectionGroup = selectedProfessorSection,
            subjectGroup = subjectGroup,
            statuses = uiState.assignmentStatuses,
            internetRequired = internetRequired,
            onBack = { selectedProfessorSubject = null },
            onOpenClass = { selectedProfessorClass = it },
            onViewJoinRequests = { viewingJoinRequestsFor = subjectGroup },
            onViewStudents = { classItem ->
                viewingStudentsFor = classItem
                onLoadProfessorStudents(classItem.id)
            },
            onCreateClass = {
                createClassYear = selectedProfessorYear?.value
                createClassDepartment = selectedProfessorDepartment?.department.orEmpty()
                createClassSection = selectedProfessorSection?.section.orEmpty()
                createClassTrack = selectedProfessorSection?.track.orEmpty()
                createClassSubjectCode = subjectGroup.subjectCode
                showCreateDialog = true
            },
        )
        return
    }


    selectedProfessorSection?.let { sectionGroup ->
        ProfessorSectionSubjectsPage(
            year = selectedProfessorYear,
            departmentGroup = selectedProfessorDepartment,
            sectionGroup = sectionGroup,
            statuses = uiState.assignmentStatuses,
            pendingJoinRequestClassIds = uiState.classJoinRequests.map { it.classId }.toSet(),
            isLoadingClasses = uiState.isLoadingClasses,
            internetRequired = internetRequired,
            onBack = {
                selectedProfessorSection = null
            },
            onOpenSubject = { selectedProfessorSubject = it },
            onOpenClass = { selectedProfessorClass = it },
            onViewJoinRequests = { viewingJoinRequestsFor = it },
            onViewStudents = { classItem ->
                viewingStudentsFor = classItem
                onLoadProfessorStudents(classItem.id)
            },
            onCreateClass = {
                createClassYear = selectedProfessorYear?.value
                createClassDepartment = selectedProfessorDepartment?.department.orEmpty()
                createClassSection = sectionGroup.section
                createClassTrack = sectionGroup.track
                createClassSubjectCode = ""
                showCreateDialog = true
            },
        )
        return
    }

    selectedProfessorDepartment?.let { departmentGroup ->
        ProfessorDepartmentSectionsPage(
            year = selectedProfessorYear,
            departmentGroup = departmentGroup,
            isLoadingClasses = uiState.isLoadingClasses,
            internetRequired = internetRequired,
            onBack = {
                selectedProfessorSubject = null
                selectedProfessorSection = null
                selectedProfessorDepartment = null
            },
            onOpenSection = { selectedProfessorSection = it },
            onCreateClass = {
                createClassYear = selectedProfessorYear?.value
                createClassDepartment = departmentGroup.department
                createClassSection = ""
                createClassTrack = ""
                createClassSubjectCode = ""
                showCreateDialog = true
            },
        )
        return
    }

    selectedProfessorYear?.let { year ->
        ProfessorYearDepartmentsPage(
            year = year,
            classes = uiState.professorClasses.filter { it.yearLevel.equals(year.value, ignoreCase = true) },
            isLoadingClasses = uiState.isLoadingClasses,
            internetRequired = internetRequired,
            onBack = {
                selectedProfessorSubject = null
                selectedProfessorSection = null
                selectedProfessorDepartment = null
                selectedProfessorYear = null
            },
            onOpenDepartment = { selectedProfessorDepartment = it },
            onCreateClass = {
                createClassYear = year.value
                createClassDepartment = ""
                createClassSection = ""
                createClassTrack = ""
                createClassSubjectCode = ""
                showCreateDialog = true
            },
        )
        return
    }

    PanthraaPullRefresh(
        isRefreshing = RefreshSurface.Classes in uiState.refreshingSurfaces,
        onRefresh = onRefreshClasses,
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 12.dp,
            start = 12.dp,
            end = 12.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            val isProfessor = currentUser.role.equals("professor", ignoreCase = true)
            val isStudent = currentUser.role.equals("student", ignoreCase = true)
            val missingProfileFields = missingAcademicProfileFields(currentUser)
            val canJoinClass = isStudent &&
                missingProfileFields.isEmpty() &&
                !internetRequired &&
                !uiState.isJoiningClass
            ClassesRootHeader(
                isProfessor = isProfessor,
                isStudent = isStudent,
                professorClasses = uiState.professorClasses,
                canJoinClass = canJoinClass,
                canCreateClass = isProfessor && !internetRequired && !uiState.isCreatingClass,
                isJoiningClass = uiState.isJoiningClass,
                onJoinClass = { showJoinDialog = true },
                onCreateClass = {
                    createClassYear = null
                    createClassDepartment = ""
                    createClassSection = ""
                    createClassTrack = ""
                    createClassSubjectCode = ""
                    showCreateDialog = true
                },
                onOpenPeople = onOpenPeople,
            )
            if (internetRequired) {
                InternetRequiredHint()
            }
            if (isStudent && missingProfileFields.isNotEmpty()) {
                ProfileRequiredBeforeJoinCard(missingFields = missingProfileFields)
            }
        }

        if (currentUser.role.equals("student", ignoreCase = true)) {
            if (uiState.studentJoinRequests.isNotEmpty()) {
                item {
                    Text(
                        text = "Join requests",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    )
                }
                items(uiState.studentJoinRequests, key = { it.id }) { joinRequest ->
                    StudentJoinRequestCard(
                        joinRequest = joinRequest,
                    )
                }
            }
            if (!uiState.isLoadingClasses && uiState.studentClasses.isEmpty()) {
                item {
                    PanthraaEmptyState(
                        icon = Icons.Outlined.MenuBook,
                        title = "No classes yet",
                        subtitle = "Join a class using the invite code from your professor to see your subjects here.",
                    )
                }
            }
            items(uiState.studentClasses, key = { it.id }) { classItem ->
                StudentClassCard(
                    classItem = classItem,
                    onOpenClass = { selectedClass = classItem },
                    onViewClassmates = {
                        viewingClassmatesFor = classItem
                        onLoadClassmates(classItem.id)
                    },
                )
            }
        } else if (currentUser.role.equals("professor", ignoreCase = true)) {
            if (!uiState.isLoadingClasses && uiState.professorClasses.isEmpty()) {
                item {
                    PanthraaEmptyState(
                        icon = Icons.Outlined.CreateNewFolder,
                        title = "No classes yet",
                        subtitle = "Create your first class to start managing subjects, assignments, and student grades.",
                    )
                }
            } else {
                item {
                    ProfessorYearGrid(
                        classes = uiState.professorClasses,
                        onOpenYear = { selectedProfessorYear = it },
                    )
                }
            }
        }
    }
    }

    if (showJoinDialog) {
        ClassInputDialog(
            title = "Join class",
            label = "Join code",
            confirmText = if (uiState.isJoiningClass) "Joining..." else "Join",
            enabled = !uiState.isJoiningClass,
            onDismiss = { showJoinDialog = false },
            onConfirm = {
                onJoinClass(it)
                showJoinDialog = false
            },
        )
    }

}

internal data class ProfessorYearOption(
    val value: String,
    val label: String,
    val shortLabel: String,
    val accent: Color,
)

internal data class ProfessorSubjectGroup(
    val subjectCode: String,
    val classes: List<ProfessorClass>,
)

internal data class ProfessorDepartmentGroup(
    val department: String,
    val classes: List<ProfessorClass>,
)

internal data class ProfessorSectionGroup(
    val section: String,
    val track: String,
    val classes: List<ProfessorClass>,
) {
    val label: String
        get() = if (track.isBlank()) section else "$section - $track"
}

internal fun academicGroupLabel(value: String?): String {
    return value?.trim()?.takeIf { it.isNotBlank() } ?: "Unassigned"
}

internal fun academicGroupKey(value: String?): String {
    return academicGroupLabel(value).lowercase(Locale.getDefault())
}

internal fun missingAcademicProfileFields(user: AppUser): List<String> {
    return buildList {
        if (user.course.isNullOrBlank()) add("Department")
        if (normalizeProfileYear(user.year).isBlank()) add("Year level")
        if (user.section.isNullOrBlank()) add("Section")
    }
}

internal val ProfessorClassYearOptions = listOf(
    ProfessorYearOption("first", "First Year", "1", Color(0xFF1D4ED8)),
    ProfessorYearOption("second", "Second Year", "2", Color(0xFF6D28D9)),
    ProfessorYearOption("third", "Third Year", "3", Color(0xFF0F766E)),
    ProfessorYearOption("fourth", "Fourth Year", "4", Color(0xFFC2410C)),
)

@Composable
internal fun ClassesRootHeader(
    isProfessor: Boolean,
    isStudent: Boolean,
    professorClasses: List<ProfessorClass>,
    canJoinClass: Boolean,
    canCreateClass: Boolean,
    isJoiningClass: Boolean,
    onJoinClass: () -> Unit,
    onCreateClass: () -> Unit,
    onOpenPeople: () -> Unit = {},
) {
    val totalClasses = professorClasses.size
    val activeYears = ProfessorClassYearOptions.count { option ->
        professorClasses.any { it.yearLevel.equals(option.value, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = if (isProfessor) "Class management" else "Classes",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    lineHeight = 26.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (isProfessor) {
                        "Organize year levels, subjects, and sections."
                    } else {
                        "Join and open your active classes."
                    },
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (isStudent) {
                Button(
                    onClick = onJoinClass,
                    enabled = canJoinClass,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PanthraaBlue,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE2E8F0),
                        disabledContentColor = Color(0xFF94A3B8),
                    ),
                    modifier = Modifier.heightIn(min = 44.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isJoiningClass) "Joining" else "Join",
                        modifier = Modifier.padding(start = 6.dp),
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            } else if (isProfessor) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenPeople,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color(0xFF0F172A),
                        ),
                        modifier = Modifier.heightIn(min = 44.dp),
                    ) {
                        Icon(Icons.Outlined.PeopleAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "People",
                            modifier = Modifier.padding(start = 6.dp),
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Button(
                        onClick = onCreateClass,
                        enabled = canCreateClass,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PanthraaBlue,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFE2E8F0),
                            disabledContentColor = Color(0xFF94A3B8),
                        ),
                        modifier = Modifier.heightIn(min = 44.dp),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "New",
                            modifier = Modifier.padding(start = 6.dp),
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }

        if (isProfessor) {
            ProfessorClassManagementSummary(
                totalClasses = totalClasses,
                activeYears = activeYears,
            )
        }
    }
}

@Composable
internal fun ProfessorClassManagementSummary(
    totalClasses: Int,
    activeYears: Int,
    modifier: Modifier = Modifier,
) {
    val classLabel = if (totalClasses == 1) "1 class" else "$totalClasses classes"
    val yearLabel = "$activeYears of ${ProfessorClassYearOptions.size} years active"

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF08216B),
                            PanthraaBlue,
                            Color(0xFF0F766E),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(760f, 360f),
                    ),
                )
                .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "Academic structure",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Open a year to manage departments, sections, and subjects.",
                            color = Color.White.copy(alpha = 0.78f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeaderStatusChip(
                        label = classLabel,
                        icon = Icons.Filled.School,
                    )
                    HeaderStatusChip(
                        label = yearLabel,
                        icon = Icons.Filled.CheckCircle,
                    )
                }
            }
        }
    }
}

@Composable
internal fun HeaderStatusChip(
    label: String,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun ProfessorYearGrid(
    classes: List<ProfessorClass>,
    onOpenYear: (ProfessorYearOption) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProfessorClassYearOptions.forEach { option ->
            val count = classes.count { it.yearLevel.equals(option.value, ignoreCase = true) }
            ProfessorYearCard(
                option = option,
                classCount = count,
                onClick = { onOpenYear(option) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
internal fun ProfessorYearCard(
    option: ProfessorYearOption,
    classCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfessorYearAccessCard(
        option = option,
        classCount = classCount,
        onClick = onClick,
        modifier = modifier,
    )
}
 
@Composable
internal fun ProfessorYearAccessCard(
    option: ProfessorYearOption,
    classCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val hasClasses = classCount > 0
    val countText = if (hasClasses) {
        "$classCount ${if (classCount == 1) "class" else "classes"}"
    } else {
        "No classes"
    }
    val cardHeight = if (compact) 82.dp else 118.dp
    Card(
        modifier = modifier
            .height(cardHeight)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            width = 1.dp,
            color = if (hasClasses) option.accent.copy(alpha = 0.24f) else Color(0xFFE2E8F0),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        if (compact) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(option.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.shortLabel,
                        color = option.accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = option.label,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = countText,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open ${option.label}",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp),
                )
            }
            return@Card
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            option.accent.copy(alpha = if (hasClasses) 0.16f else 0.07f),
                            Color.White,
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(420f, 360f),
                    ),
                )
                .padding(14.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(option.accent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option.shortLabel,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(option.accent.copy(alpha = if (hasClasses) 0.14f else 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open",
                            tint = option.accent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = option.label,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = countText,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfessorYearDepartmentsPage(
    year: ProfessorYearOption,
    classes: List<ProfessorClass>,
    isLoadingClasses: Boolean,
    internetRequired: Boolean,
    onBack: () -> Unit,
    onOpenDepartment: (ProfessorDepartmentGroup) -> Unit,
    onCreateClass: () -> Unit,
) {
    val departmentGroups = remember(classes) {
        classes
            .groupBy { academicGroupKey(it.department) }
            .map { (_, groupedClasses) ->
                ProfessorDepartmentGroup(
                    department = academicGroupLabel(groupedClasses.firstOrNull()?.department),
                    classes = groupedClasses,
                )
            }
            .sortedBy { it.department.lowercase(Locale.getDefault()) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ProfessorPageHeader(
                title = "Choose department",
                subtitle = year.label,
                path = year.label,
                onBack = onBack,
                actionText = "Create class",
                actionEnabled = !internetRequired,
                onAction = onCreateClass,
            )
            if (internetRequired) {
                InternetRequiredHint()
            }
        }

        if (isLoadingClasses) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
        } else if (departmentGroups.isEmpty()) {
            item {
                EmptyClassState("No departments created for ${year.label} yet.")
            }
        } else {
            items(departmentGroups, key = { it.department.lowercase(Locale.getDefault()) }) { group ->
                ProfessorDepartmentCard(
                    departmentGroup = group,
                    onClick = { onOpenDepartment(group) },
                )
            }
        }
    }
}

@Composable
internal fun ProfessorDepartmentSectionsPage(
    year: ProfessorYearOption?,
    departmentGroup: ProfessorDepartmentGroup,
    isLoadingClasses: Boolean,
    internetRequired: Boolean,
    onBack: () -> Unit,
    onOpenSection: (ProfessorSectionGroup) -> Unit,
    onCreateClass: () -> Unit,
) {
    val sectionGroups = remember(departmentGroup.classes) {
        departmentGroup.classes
            .groupBy { academicGroupKey(it.section) to academicGroupKey(it.track) }
            .map { (_, groupedClasses) ->
                ProfessorSectionGroup(
                    section = academicGroupLabel(groupedClasses.firstOrNull()?.section),
                    track = groupedClasses.firstOrNull()?.track?.trim().orEmpty(),
                    classes = groupedClasses,
                )
            }
            .sortedWith(compareBy({ it.section.lowercase(Locale.getDefault()) }, { it.track.lowercase(Locale.getDefault()) }))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ProfessorPageHeader(
                title = "Choose section",
                subtitle = departmentGroup.department,
                path = listOfNotNull(year?.label, departmentGroup.department).joinToString(" - "),
                onBack = onBack,
                actionText = "Create class",
                actionEnabled = !internetRequired,
                onAction = onCreateClass,
            )
            if (internetRequired) {
                InternetRequiredHint()
            }
        }

        if (isLoadingClasses) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
        } else if (sectionGroups.isEmpty()) {
            item {
                EmptyClassState("No sections created for ${departmentGroup.department} yet.")
            }
        } else {
            items(sectionGroups, key = { "${it.section.lowercase(Locale.getDefault())}:${it.track.lowercase(Locale.getDefault())}" }) { group ->
                ProfessorSectionCard(
                    sectionGroup = group,
                    onClick = { onOpenSection(group) },
                )
            }
        }
    }
}

@Composable
internal fun ProfessorSectionSubjectsPage(
    year: ProfessorYearOption?,
    departmentGroup: ProfessorDepartmentGroup?,
    sectionGroup: ProfessorSectionGroup,
    statuses: Map<String, AssignmentStatus>,
    pendingJoinRequestClassIds: Set<String>,
    isLoadingClasses: Boolean,
    internetRequired: Boolean,
    onBack: () -> Unit,
    onOpenSubject: (ProfessorSubjectGroup) -> Unit,
    onOpenClass: (ProfessorClass) -> Unit,
    onViewJoinRequests: (ProfessorSubjectGroup) -> Unit,
    onViewStudents: (ProfessorClass) -> Unit,
    onCreateClass: () -> Unit,
) {
    val subjectGroups = remember(sectionGroup.classes) {
        sectionGroup.classes
            .groupBy { it.displaySubjectCode.ifBlank { "SUBJECT" }.uppercase() }
            .map { (subjectCode, groupedClasses) ->
                ProfessorSubjectGroup(subjectCode = subjectCode, classes = groupedClasses)
            }
            .sortedBy { it.subjectCode }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ProfessorPageHeader(
                title = "Class management",
                subtitle = listOfNotNull(departmentGroup?.department, sectionGroup.label).joinToString(" - "),
                path = listOfNotNull(year?.label, departmentGroup?.department, sectionGroup.label).joinToString(" - "),
                onBack = onBack,
                actionText = "Create class",
                actionEnabled = !internetRequired,
                onAction = onCreateClass,
            )
            if (internetRequired) {
                InternetRequiredHint()
            }
        }

        if (isLoadingClasses) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
        } else if (subjectGroups.isEmpty()) {
            item {
                EmptyClassState("No subjects created for ${sectionGroup.label} yet.")
            }
        } else {
            items(subjectGroups, key = { it.subjectCode }) { group ->
                ProfessorManagedSubjectCard(
                    subjectGroup = group,
                    statuses = statuses,
                    hasPendingRequests = group.classes.any { it.id in pendingJoinRequestClassIds },
                    onClick = { onOpenSubject(group) },
                    onOpenClass = onOpenClass,
                    onViewStudents = onViewStudents,
                    onViewJoinRequests = { onViewJoinRequests(group) },
                )
            }
        }
    }
}

@Composable
internal fun ProfessorSubjectClassesPage(
    year: ProfessorYearOption?,
    departmentGroup: ProfessorDepartmentGroup?,
    sectionGroup: ProfessorSectionGroup?,
    subjectGroup: ProfessorSubjectGroup,
    statuses: Map<String, AssignmentStatus>,
    internetRequired: Boolean,
    onBack: () -> Unit,
    onOpenClass: (ProfessorClass) -> Unit,
    onViewJoinRequests: () -> Unit,
    onViewStudents: (ProfessorClass) -> Unit,
    onCreateClass: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ProfessorPageHeader(
                title = "Class management",
                subtitle = listOfNotNull(departmentGroup?.department, sectionGroup?.label, subjectGroup.subjectCode).joinToString(" - "),
                path = listOfNotNull(year?.label, departmentGroup?.department, sectionGroup?.label, subjectGroup.subjectCode).joinToString(" - "),
                onBack = onBack,
                actionText = "Create class",
                actionEnabled = !internetRequired,
                onAction = onCreateClass,
                secondaryActionText = "Requests",
                secondaryActionEnabled = !internetRequired,
                onSecondaryAction = onViewJoinRequests,
            )
            if (internetRequired) {
                InternetRequiredHint()
            }
        }

        if (subjectGroup.classes.isEmpty()) {
            item {
                EmptyClassState("No classes under ${subjectGroup.subjectCode} yet.")
            }
        } else {
            items(subjectGroup.classes, key = { it.id }) { classItem ->
                ProfessorSubjectClassCard(
                    classItem = classItem,
                    status = statuses[classItem.id],
                    onOpenClass = { onOpenClass(classItem) },
                    onViewStudents = { onViewStudents(classItem) },
                )
            }
        }
    }
}

@Composable
internal fun ProfessorJoinRequestsPage(
    year: ProfessorYearOption?,
    subjectGroup: ProfessorSubjectGroup,
    uiState: MainUiState,
    internetRequired: Boolean,
    onLoadJoinRequests: (List<String>) -> Unit,
    isRefreshing: Boolean,
    onRefreshJoinRequests: (List<String>) -> Unit,
    onApproveRequest: (String, List<String>) -> Unit,
    onRejectRequest: (String, List<String>, String) -> Unit,
    onBack: () -> Unit,
) {
    val classIds = remember(subjectGroup.classes) { subjectGroup.classes.map { it.id } }
    val classIdSet = remember(classIds) { classIds.toSet() }
    val requests = uiState.classJoinRequests.filter { it.classId in classIdSet }

    LaunchedEffect(classIds.joinToString(",")) {
        onLoadJoinRequests(classIds)
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { onRefreshJoinRequests(classIds) },
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ProfessorPageHeader(
                title = "Pending requests",
                subtitle = "${year?.label ?: "Selected year"} - ${subjectGroup.subjectCode}",
                onBack = onBack,
            )
            if (internetRequired) {
                InternetRequiredHint()
            }
        }

        if (uiState.isLoadingJoinRequests) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
        } else if (requests.isEmpty()) {
            item {
                EmptyClassState("No pending requests for this subject.")
            }
        } else {
            items(requests, key = { it.id }) { request ->
                JoinRequestCard(
                    request = request,
                    classIdsToRefresh = classIds,
                    isUpdating = uiState.isUpdatingJoinRequest,
                    internetRequired = internetRequired,
                    onApproveRequest = onApproveRequest,
                    onRejectRequest = onRejectRequest,
                )
            }
        }
    }
}
}

@Composable
internal fun JoinRequestCard(
    request: ClassJoinRequest,
    classIdsToRefresh: List<String>,
    isUpdating: Boolean,
    internetRequired: Boolean,
    onApproveRequest: (String, List<String>) -> Unit,
    onRejectRequest: (String, List<String>, String) -> Unit,
) {
    val enabled = !internetRequired && !isUpdating
    var showRejectDialog by remember { mutableStateOf(false) }
    val detailRows = listOfNotNull(
        "Year" to (profileYearLabel(request.year) ?: "N/A"),
        "Course" to (cleanRequestDetail(request.course) ?: "N/A"),
        "Section" to (cleanRequestDetail(request.section) ?: "N/A"),
        cleanRequestDetail(request.track)?.let { "Track" to it },
    ).chunked(2)
    val requestedClassLabel = listOfNotNull(
        cleanRequestDetail(request.subjectCode),
        cleanRequestDetail(request.className),
    ).distinct().joinToString(" · ").ifBlank { "this class" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    imageUrl = request.photoUrl,
                    name = request.name,
                    modifier = Modifier.size(50.dp),
                    placeholderColor = Color(0xFFE5E7EB),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = request.name.ifBlank { "Unknown student" },
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "ID: ${request.idNumber.ifBlank { "N/A" }}",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (request.isIrregular) {
                            IrregularBadge()
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Student details",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                )
                detailRows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowItems.forEach { (label, value) ->
                            JoinRequestDetailChip(
                                label = label,
                                value = value,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Text(
                text = "Wants to join $requestedClassLabel",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { showRejectDialog = true },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE4E6),
                        contentColor = Color(0xFFBE123C),
                        disabledContainerColor = Color(0xFFF1F5F9),
                        disabledContentColor = Color(0xFF94A3B8),
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(17.dp))
                    Text("Reject", modifier = Modifier.padding(start = 6.dp), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onApproveRequest(request.id, classIdsToRefresh) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF047857),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE2E8F0),
                        disabledContentColor = Color(0xFF94A3B8),
                    ),
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(17.dp))
                    Text("Approve", modifier = Modifier.padding(start = 6.dp), fontWeight = FontWeight.Bold)
                }
            }

            if (internetRequired) {
                Text(
                    text = "Internet required.",
                    color = Color(0xFFB91C1C),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
        }
    }

    if (showRejectDialog) {
        RejectJoinRequestDialog(
            studentName = request.name.ifBlank { "this student" },
            isSubmitting = isUpdating,
            onDismiss = { showRejectDialog = false },
            onConfirm = { reason ->
                onRejectRequest(request.id, classIdsToRefresh, reason)
                showRejectDialog = false
            },
        )
    }
}

@Composable
internal fun RejectJoinRequestDialog(
    studentName: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                text = "Reject $studentName?",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "The student will see this reason on their side. You can leave it blank.",
                    color = Color(0xFF475569),
                    fontSize = 13.sp,
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it.take(300) },
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Reason (optional)") },
                    placeholder = { Text("e.g. Missing prerequisites") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason.trim()) },
                enabled = !isSubmitting,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBE123C),
                    contentColor = Color.White,
                ),
            ) {
                Text(if (isSubmitting) "Rejecting..." else "Reject", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting,
            ) {
                Text("Cancel", color = Color(0xFF475569), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
    )
}

@Composable
internal fun StudentJoinRequestCard(
    joinRequest: StudentJoinRequest,
) {
    val isPending = joinRequest.status.equals("pending", ignoreCase = true)
    val reason = joinRequest.rejectionReason?.takeIf { it.isNotBlank() }
    val className = listOfNotNull(
        joinRequest.subjectCode.takeIf { it.isNotBlank() },
        joinRequest.className.takeIf { it.isNotBlank() },
    ).distinct().joinToString(" · ").ifBlank { "this class" }
    val statusColor = if (isPending) Color(0xFFB45309) else Color(0xFFBE123C)
    val statusBackground = if (isPending) Color(0xFFFEF3C7) else Color(0xFFFFE4E6)
    val statusBorder = if (isPending) Color(0xFFFDE68A) else Color(0xFFFECDD3)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusBackground)
                        .border(1.dp, statusBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPending) Icons.Outlined.Insights else Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = statusColor,
                        )
                        Text(
                            text = if (isPending) "Waiting for approval" else "Request rejected",
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 5.dp),
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = className,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Sent ${joinRequest.requestedAt?.let { displayAssignmentCreatedAt(it) } ?: "recently"}",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (reason != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = "Professor's note",
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                    Text(
                        text = reason,
                        color = Color(0xFF0F172A),
                        fontSize = 13.sp,
                    )
                }
            }
            if (isPending) {
                Text(
                    text = "You'll be notified once the professor responds.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                )
            } else {
                Text(
                    text = "You can try joining again with the class invite code.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
internal fun JoinRequestDetailChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal fun cleanRequestDetail(value: String?): String? {
    return value?.trim()?.takeIf { it.isNotBlank() }
}

@Composable
internal fun ProfessorPageHeader(
    title: String,
    subtitle: String,
    path: String? = null,
    onBack: () -> Unit,
    actionText: String? = null,
    actionEnabled: Boolean = true,
    onAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    secondaryActionEnabled: Boolean = true,
    onSecondaryAction: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0034DE),
                            Color(0xFF5B38F5),
                            Color(0xFF0F766E),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 520f),
                    ),
                ),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.10f),
                    radius = size.width * 0.25f,
                    center = Offset(size.width * 0.92f, size.height * 0.10f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = size.width * 0.18f,
                    center = Offset(size.width * 0.02f, size.height * 0.95f),
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(38.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.76f),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (!path.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.14f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = path,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if ((actionText != null && onAction != null) || (secondaryActionText != null && onSecondaryAction != null)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                    if (actionText != null && onAction != null) {
                        Button(
                            onClick = onAction,
                            enabled = actionEnabled,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PanthraaBlue,
                            ),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Text(actionText, modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                        }
                    }
                    if (secondaryActionText != null && onSecondaryAction != null) {
                        Button(
                            onClick = onSecondaryAction,
                            enabled = secondaryActionEnabled,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.18f),
                                contentColor = Color.White,
                            ),
                        ) {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(secondaryActionText, modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
internal fun ProfessorDepartmentCard(
    departmentGroup: ProfessorDepartmentGroup,
    onClick: () -> Unit,
) {
    val classCount = departmentGroup.classes.size
    val students = departmentGroup.classes.sumOf { it.studentCount.coerceAtLeast(0) }
    val accent = themeAccentColor(departmentGroup.classes.firstOrNull()?.themeColor ?: "blue")
    ProfessorHierarchyCard(
        title = departmentGroup.department,
        subtitle = "$classCount ${if (classCount == 1) "class" else "classes"} - $students students",
        accent = accent,
        actionText = "Open department",
        onClick = onClick,
    )
}

@Composable
internal fun ProfessorSectionCard(
    sectionGroup: ProfessorSectionGroup,
    onClick: () -> Unit,
) {
    val classCount = sectionGroup.classes.size
    val students = sectionGroup.classes.sumOf { it.studentCount.coerceAtLeast(0) }
    val accent = themeAccentColor(sectionGroup.classes.firstOrNull()?.themeColor ?: "green")
    ProfessorHierarchyCard(
        title = sectionGroup.label,
        subtitle = "$classCount ${if (classCount == 1) "subject" else "subjects"} - $students students",
        accent = accent,
        actionText = "Open section",
        onClick = onClick,
    )
}

@Composable
internal fun ProfessorHierarchyCard(
    title: String,
    subtitle: String,
    accent: Color,
    actionText: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title.take(2).uppercase(),
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = actionText,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfessorManagedSubjectCard(
    subjectGroup: ProfessorSubjectGroup,
    statuses: Map<String, AssignmentStatus>,
    hasPendingRequests: Boolean,
    onClick: () -> Unit,
    onOpenClass: (ProfessorClass) -> Unit,
    onViewStudents: (ProfessorClass) -> Unit,
    onViewJoinRequests: () -> Unit,
) {
    val accent = themeAccentColor(subjectGroup.classes.firstOrNull()?.themeColor ?: "blue")
    val classCount = subjectGroup.classes.size
    val students = subjectGroup.classes.sumOf { it.studentCount.coerceAtLeast(0) }
    val subjectName = subjectGroup.classes.firstOrNull()?.displayClassName?.takeIf { it.isNotBlank() } ?: "Subject"

    val pulseAlpha = if (hasPendingRequests) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
        infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        ).value
    } else 1f

    val requestsBgColor = if (hasPendingRequests) Color(0xFFEF4444).copy(alpha = pulseAlpha) else Color(0xFFF1F5F9)
    val requestsContentColor = if (hasPendingRequests) Color.White else Color(0xFF94A3B8)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accent indicator
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent)
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subjectGroup.subjectCode,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onViewJoinRequests,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = requestsBgColor,
                                contentColor = requestsContentColor,
                            ),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Requests",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }

                Text(
                    text = subjectName,
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                
                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$classCount ${if (classCount == 1) "class" else "classes"}",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.People,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "$students",
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(accent.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "Open subject",
                            color = accent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfessorManagedClassRow(
    classItem: ProfessorClass,
    status: AssignmentStatus?,
    accent: Color,
    onOpenClass: () -> Unit,
    onViewStudents: () -> Unit,
) {
    val submittedCount = status?.submittedCount ?: 0
    val missingCount = status?.missingCount ?: 0
    val totalStudents = status?.totalStudents ?: classItem.studentCount.coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenClass),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(accent),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = classItem.displayClassName,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Text(
                        text = "Join ${classItem.displayJoinCode}",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1)),
                    )
                    Text(
                        text = "$totalStudents ${if (totalStudents == 1) "student" else "students"}",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    )
                    if (submittedCount > 0 || missingCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFCBD5E1)),
                        )
                        Text(
                            text = "$submittedCount sub, $missingCount miss",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
            TextButton(
                onClick = onViewStudents,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = accent.copy(alpha = 0.10f),
                    contentColor = accent,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "Students",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open class",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
internal fun DeleteSubjectWarningDialog(
    year: ProfessorYearOption?,
    departmentGroup: ProfessorDepartmentGroup?,
    sectionGroup: ProfessorSectionGroup,
    subjectGroup: ProfessorSubjectGroup,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val scopeLabel = listOfNotNull(
        year?.label,
        departmentGroup?.department,
        sectionGroup.label,
    ).joinToString(" - ")
    var remainingSeconds by remember(subjectGroup.subjectCode, scopeLabel) { mutableStateOf(5) }
    val classCount = subjectGroup.classes.size
    val studentCount = subjectGroup.classes.sumOf { it.studentCount.coerceAtLeast(0) }

    LaunchedEffect(subjectGroup.subjectCode, scopeLabel) {
        remainingSeconds = 5
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = "Delete ${subjectGroup.subjectCode}?",
                subtitle = "This action cannot be undone.",
                icon = Icons.Filled.Delete,
                tint = Color(0xFFBE123C),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "This will permanently delete this subject from $scopeLabel.",
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Affected: $classCount ${if (classCount == 1) "class" else "classes"} and $studentCount enrolled ${if (studentCount == 1) "student" else "students"}. Assignments, submissions, and pending join requests under those classes will also be removed.",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = when {
                    isDeleting -> "Deleting..."
                    remainingSeconds > 0 -> "Delete in ${remainingSeconds}s"
                    else -> "Delete subject"
                },
                onClick = onConfirm,
                enabled = remainingSeconds == 0 && !isDeleting,
                color = if (remainingSeconds == 0 && !isDeleting) Color(0xFFBE123C) else Color(0xFF94A3B8),
            )
        },
        dismissButton = {
            DialogCancelButton(onClick = onDismiss, enabled = !isDeleting)
        },
    )
}

@Composable
internal fun EmptyClassState(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF4B5563),
        )
    }
}

@Composable
internal fun ProfileRequiredBeforeJoinCard(missingFields: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.45f)),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Complete your academic profile before joining a class.",
                color = Color(0xFF92400E),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
            Text(
                text = "Missing: ${missingFields.joinToString(", ")}",
                color = Color(0xFFB45309),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
internal fun ClassInputDialog(
    title: String,
    label: String,
    confirmText: String,
    enabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var value by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = title,
                subtitle = "Enter the code shared by your professor.",
                icon = Icons.Filled.Add,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it.take(6) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(label) },
                    placeholder = { Text("6-digit join code") },
                    singleLine = true,
                    enabled = enabled,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                Text(
                    text = "You will be added only after your professor approves the request.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 13.sp,
                )
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = confirmText,
                onClick = { onConfirm(value.trim()) },
                enabled = value.trim().isNotEmpty() && enabled,
            )
        },
        dismissButton = {
            DialogCancelButton(onClick = onDismiss, enabled = enabled)
        },
    )
}

internal val ClassCardDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())

@Composable
internal fun ProfessorSubjectManagementHero(
    year: ProfessorYearOption?,
    departmentGroup: ProfessorDepartmentGroup?,
    sectionGroup: ProfessorSectionGroup?,
    subjectGroup: ProfessorSubjectGroup,
) {
    val accent = themeAccentColor(subjectGroup.classes.firstOrNull()?.themeColor ?: "blue")
    val classCount = subjectGroup.classes.size
    val students = subjectGroup.classes.sumOf { it.studentCount.coerceAtLeast(0) }
    val context = listOfNotNull(
        year?.label,
        departmentGroup?.department,
        sectionGroup?.label,
    ).joinToString(" - ")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            accent.copy(alpha = 0.10f),
                            Color.White,
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 420f),
                    ),
                ),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = accent.copy(alpha = 0.10f),
                    radius = size.width * 0.22f,
                    center = Offset(size.width * 0.94f, size.height * 0.02f),
                )
                drawCircle(
                    color = accent.copy(alpha = 0.06f),
                    radius = size.width * 0.16f,
                    center = Offset(size.width * 0.04f, size.height * 1.06f),
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(accent.copy(alpha = 0.14f))
                            .border(1.dp, accent.copy(alpha = 0.20f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = "Managing subject",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = subjectGroup.subjectCode,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (context.isNotBlank()) {
                            Text(
                                text = context,
                                color = Color(0xFF475569),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ProfessorClassStatTile(
                        label = if (classCount == 1) "Class" else "Classes",
                        value = classCount.toString(),
                        color = accent,
                        modifier = Modifier.weight(1f),
                    )
                    ProfessorClassStatTile(
                        label = if (students == 1) "Student" else "Students",
                        value = students.toString(),
                        color = Color(0xFF0F766E),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfessorSubjectClassCard(
    classItem: ProfessorClass,
    status: AssignmentStatus?,
    onOpenClass: () -> Unit,
    onViewStudents: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val submittedCount = status?.submittedCount ?: 0
    val missingCount = status?.missingCount ?: 0
    val totalStudents = status?.totalStudents ?: classItem.studentCount.coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenClass),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = classItem.displaySubjectCode.ifBlank { "Subject" },
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = classItem.displayClassName.ifBlank { "Class name" },
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Open class",
                        color = accent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$totalStudents students - ${submittedCount + missingCount} activities",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )

                TextButton(
                    onClick = onViewStudents,
                    modifier = Modifier.height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        containerColor = accent.copy(alpha = 0.12f),
                        contentColor = accent
                    )
                ) {
                    Text(
                        text = "Students",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfessorClassMetaLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
        Text(
            text = value,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
internal fun ProfessorMiniPill(
    text: String,
    color: Color,
) {
    val background = color
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = readableContentColor(background),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun ProfessorStatusCount(
    label: String,
    count: Int,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = count.toString(),
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
        )
        Text(
            text = label,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
        )
    }
}

@Composable
internal fun ProfessorClassStatTile(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.09f))
            .border(1.dp, color.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AssignmentStatusPill(
    count: Int,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.People, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp).padding(end = 2.dp))
            Text(
                text = count.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(width = 48.dp, height = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color),
        )
    }
}

@Composable
internal fun ProfessorClassroomPage(
    classItem: ProfessorClass,
    assignments: List<ClassAssignment>,
    isLoadingAssignments: Boolean,
    isCreatingAssignment: Boolean,
    isUpdatingClass: Boolean = false,
    isDeletingSubject: Boolean = false,
    internetRequired: Boolean,
    onLoadAssignments: (String) -> Unit,
    isRefreshingAssignments: Boolean,
    onRefreshAssignments: (String) -> Unit,
    onCreateAssignment: (String, String, String, String, Int, String?, String?, String?, String?, String, String, Boolean, Boolean, Uri?) -> Unit,
    onOpenAssignment: (ClassAssignment) -> Unit,
    onOpenAttendance: (ClassAssignment) -> Unit,
    openUploadOnStart: Boolean = false,
    onUploadOpenConsumed: () -> Unit = {},
    onUpdateClass: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onDeleteClass: (String) -> Unit = {},
    onBack: () -> Unit,
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var showSubjectEditor by remember { mutableStateOf(false) }
    val sortedUploads = remember(assignments) { assignments.sortedByNewestUpload() }
    val assignmentCount = remember(assignments) {
        assignments.count { !it.assignmentType.equals("material", ignoreCase = true) }
    }
    val materialCount = remember(assignments) {
        assignments.count { it.assignmentType.equals("material", ignoreCase = true) }
    }

    LaunchedEffect(classItem.id) {
        onLoadAssignments(classItem.id)
    }

    LaunchedEffect(classItem.id, openUploadOnStart, internetRequired) {
        if (openUploadOnStart) {
            if (!internetRequired) {
                showUploadDialog = true
            }
            onUploadOpenConsumed()
        }
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshingAssignments,
        onRefresh = { onRefreshAssignments(classItem.id) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .panthraaScreenBackground(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ProfessorClassroomHero(
                    classItem = classItem,
                    uploadCount = sortedUploads.size,
                    internetRequired = internetRequired,
                    isCreatingAssignment = isCreatingAssignment,
                    onBack = onBack,
                    onUpload = { showUploadDialog = true },
                    onEdit = { showSubjectEditor = true },
                )
            }

            if (internetRequired) {
                item {
                    PanthraaStatusNotice(
                        type = NoticeType.OFFLINE,
                        title = "Offline mode",
                        message = "Connect to the internet to upload work or manage attendance.",
                    )
                }
            }

            item {
                ProfessorClassFeedHeader(
                    uploadCount = sortedUploads.size,
                    assignmentCount = assignmentCount,
                    materialCount = materialCount,
                )
            }

            if (isLoadingAssignments) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 34.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        PanthraaLoadingAnimation(size = 144.dp)
                    }
                }
            } else if (sortedUploads.isEmpty()) {
                item {
                    ProfessorEmptyUploadsCard(
                        enabled = !internetRequired && !isCreatingAssignment,
                        onUpload = { showUploadDialog = true },
                    )
                }
            } else {
                itemsIndexed(sortedUploads, key = { _, assignment -> assignment.id }) { index, assignment ->
                    val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
                    val uploadNumber = sortedUploads.size - index
                    val attendanceAction: (() -> Unit)? = if (isMaterial) null else {
                        { onOpenAttendance(assignment) }
                    }
                    AssignmentSummaryCard(
                        assignment = assignment,
                        accent = assignmentTypeColor(assignment.assignmentType),
                        uploadNumber = uploadNumber,
                        submitted = null,
                        actionLabel = if (isMaterial) null else "Attendance",
                        actionEnabled = !internetRequired && !isMaterial,
                        onActionClick = attendanceAction,
                        onClick = { onOpenAssignment(assignment) },
                    )
                }
            }
        }
    }

    if (showUploadDialog) {
        UploadAssignmentDialog(
            isCreating = isCreatingAssignment,
            scheduleDays = classItem.scheduleDays,
            scheduleStartTime = classItem.scheduleStartTime,
            scheduleEndTime = classItem.scheduleEndTime,
            onDismiss = { showUploadDialog = false },
            onUpload = { title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, allowComments, fileUri ->
                onCreateAssignment(classItem.id, title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, allowComments, fileUri)
                showUploadDialog = false
            },
        )
    }

    if (showSubjectEditor) {
        ProfessorSubjectEditorDialog(
            classItem = classItem,
            isSaving = isUpdatingClass,
            isDeleting = isDeletingSubject,
            onDismiss = { showSubjectEditor = false },
            onSave = { className, subjectCode, section, track ->
                onUpdateClass(classItem.id, className, subjectCode, section, track)
                showSubjectEditor = false
            },
            onDelete = {
                onDeleteClass(classItem.id)
                showSubjectEditor = false
            },
        )
    }
}

@Composable
internal fun ProfessorSubjectEditorDialog(
    classItem: ProfessorClass,
    isSaving: Boolean,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onSave: (className: String, subjectCode: String, section: String, track: String) -> Unit,
    onDelete: () -> Unit,
) {
    var className by remember(classItem.id) { mutableStateOf(classItem.className) }
    var subjectCode by remember(classItem.id) { mutableStateOf(classItem.subjectCode) }
    var section by remember(classItem.id) { mutableStateOf(classItem.section.orEmpty()) }
    var track by remember(classItem.id) { mutableStateOf(classItem.track.orEmpty()) }
    var confirmingDelete by remember(classItem.id) { mutableStateOf(false) }
    var remainingSeconds by remember(classItem.id) { mutableStateOf(5) }
    val busy = isSaving || isDeleting
    val canSave = className.trim().isNotEmpty() && subjectCode.trim().isNotEmpty()

    LaunchedEffect(confirmingDelete) {
        if (confirmingDelete) {
            remainingSeconds = 5
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds -= 1
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            if (confirmingDelete) {
                DialogHeader(
                    title = "Delete ${classItem.displaySubjectCode}?",
                    subtitle = "This action cannot be undone.",
                    icon = Icons.Filled.Delete,
                    tint = Color(0xFFBE123C),
                )
            } else {
                DialogHeader(
                    title = "Edit subject",
                    subtitle = "Fix the class name, subject code, section, or track.",
                    icon = Icons.Filled.Edit,
                )
            }
        },
        text = {
            if (confirmingDelete) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "This will permanently delete ${classItem.displayClassName} and all of its assignments, submissions, and pending join requests. This cannot be undone.",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Enrolled: ${classItem.studentCount.coerceAtLeast(0)} ${if (classItem.studentCount == 1) "student" else "students"}.",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 520.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Class name") },
                        singleLine = true,
                        enabled = !busy,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                    OutlinedTextField(
                        value = subjectCode,
                        onValueChange = { subjectCode = it.take(12).uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Subject code") },
                        singleLine = true,
                        enabled = !busy,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it.take(30) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Section") },
                        placeholder = { Text("e.g. ICT 11 - A") },
                        singleLine = true,
                        enabled = !busy,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                    OutlinedTextField(
                        value = track,
                        onValueChange = { track = it.take(30) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Track") },
                        placeholder = { Text("e.g. ICT") },
                        singleLine = true,
                        enabled = !busy,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                    Text(
                        text = "The class code stays the same, so enrolled students are not affected.",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 8.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE2E8F0),
                    )
                    Text(
                        text = "DANGER ZONE",
                        color = Color(0xFFBE123C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                    )
                    OutlinedButton(
                        onClick = { confirmingDelete = true },
                        enabled = !busy,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBE123C)),
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Delete subject", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            if (confirmingDelete) {
                DialogPrimaryButton(
                    text = when {
                        isDeleting -> "Deleting..."
                        remainingSeconds > 0 -> "Delete in ${remainingSeconds}s"
                        else -> "Delete subject"
                    },
                    onClick = onDelete,
                    enabled = remainingSeconds == 0 && !busy,
                    color = if (remainingSeconds == 0 && !busy) Color(0xFFBE123C) else Color(0xFF94A3B8),
                )
            } else {
                DialogPrimaryButton(
                    text = if (isSaving) "Saving..." else "Save changes",
                    onClick = {
                        onSave(className.trim(), subjectCode.trim().uppercase(), section.trim(), track.trim())
                    },
                    enabled = canSave && !busy,
                )
            }
        },
        dismissButton = {
            DialogCancelButton(
                onClick = {
                    if (confirmingDelete) confirmingDelete = false else onDismiss()
                },
                enabled = !busy,
                text = if (confirmingDelete) "Back" else "Cancel",
            )
        },
    )
}

@Composable
internal fun ProfessorClassroomHero(
    classItem: ProfessorClass,
    uploadCount: Int,
    internetRequired: Boolean,
    isCreatingAssignment: Boolean,
    onBack: () -> Unit,
    onUpload: () -> Unit,
    onEdit: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val yearLabel = profileYearLabel(classItem.yearLevel) ?: "Unassigned year"
    val createdLabel = displayClassDate(classItem.createdAt).ifBlank { "Unknown date" }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp),
                )
            }
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit subject",
                    tint = accent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.88f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
            shape = RoundedCornerShape(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                accent.copy(alpha = 0.08f),
                                Color(0xFFF8FAFC),
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(720f, 440f),
                        ),
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = "Class workspace",
                            color = accent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = classItem.displayClassName.ifBlank { "Class name" },
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp,
                            lineHeight = 22.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = classItem.displaySubjectCode.ifBlank { "Subject" },
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF94A3B8)),
                            )
                            Text(
                                text = yearLabel,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(
                            text = "Created $createdLabel",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ProfessorClassroomMetric(
                            label = "Students",
                            value = classItem.studentCount.coerceAtLeast(0).toString(),
                            accent = PanthraaBlue,
                            modifier = Modifier.weight(1f),
                        )
                        ProfessorClassroomMetric(
                            label = "Uploads",
                            value = uploadCount.toString(),
                            accent = Color(0xFF0F766E),
                            modifier = Modifier.weight(1f),
                        )
                        ProfessorClassroomMetric(
                            label = "Join code",
                            value = classItem.displayJoinCode,
                            accent = accent,
                            isCode = true,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Button(
                        onClick = onUpload,
                        enabled = !internetRequired && !isCreatingAssignment,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PanthraaBlue,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCBD5E1),
                            disabledContentColor = Color.White,
                        ),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (isCreatingAssignment) "Uploading..." else "Upload work",
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfessorClassroomMetric(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    isCode: Boolean = false,
) {
    Box(
        modifier = modifier
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White.copy(alpha = 0.86f))
            .border(1.dp, accent.copy(alpha = 0.16f), RoundedCornerShape(15.dp))
            .padding(horizontal = 9.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = value,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (isCode) 13.sp else 19.sp,
                letterSpacing = if (isCode) 1.2.sp else 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun ProfessorClassFeedHeader(
    uploadCount: Int,
    assignmentCount: Int,
    materialCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "Class feed",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$assignmentCount assignments / $materialCount materials",
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFEAF0FF))
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Text(
                text = "$uploadCount total",
                color = PanthraaBlue,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
internal fun ProfessorEmptyUploadsCard(
    enabled: Boolean,
    onUpload: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFEAF0FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = PanthraaBlue,
                    modifier = Modifier.size(23.dp),
                )
            }
            Text(
                text = "No uploads yet",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            Text(
                text = "Add an assignment or learning material to start the class feed.",
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onUpload,
                enabled = enabled,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PanthraaBlue),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Upload work", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
internal fun AttendanceScreen(
    classTitle: String,
    assignment: ClassAssignment,
    students: List<AttendanceStudent>,
    loadError: String?,
    isLoading: Boolean,
    isRecording: Boolean,
    internetRequired: Boolean,
    canMarkAttendance: Boolean = true,
    viewerUser: AppUser? = null,
    onLoadAttendance: (String) -> Unit,
    isRefreshing: Boolean,
    onRefreshAttendance: (String) -> Unit,
    onRecordAttendance: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    var localError by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredStudents = remember(students, searchQuery) {
        val query = searchQuery.trim().lowercase(Locale.getDefault())
        if (query.isEmpty()) {
            students
        } else {
            students.filter { student ->
                student.name.lowercase(Locale.getDefault()).contains(query) ||
                    student.idNumber.lowercase(Locale.getDefault()).contains(query)
            }
        }
    }

    LaunchedEffect(assignment.id) {
        onLoadAttendance(assignment.id)
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { onRefreshAttendance(assignment.id) },
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp),
                ) {
                    Text(
                        text = "Attendance",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = assignment.title.ifBlank { classTitle },
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val present = students.count { it.status.equals("present", ignoreCase = true) }
                    Text(
                        text = "$present/${students.size} present for this activity",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = classTitle,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (canMarkAttendance) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Call a name or search by ID number") },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF64748B))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PanthraaBlue,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                            ),
                        )
                        Text(
                            text = "Call the student's name, find them in the list, then tap Mark present.",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                        )
                    } else {
                        Text(
                            text = "Read-only attendance monitoring for this activity.",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                    }
                    if (!canMarkAttendance && viewerUser != null) {
                        val studentAttendance = students.firstOrNull { it.studentId == viewerUser.id || it.idNumber == viewerUser.idNumber }
                        val status = studentAttendance?.status ?: ""
                        val expired = isAssignmentExpired(assignment)
                        if (status.equals("present", ignoreCase = true)) {
                            StudentAttendanceStatusCard(
                                status = "present",
                                className = classTitle,
                                date = studentAttendance?.attendanceDate
                            )
                        } else if (expired) {
                            StudentAttendanceStatusCard(
                                status = "absent",
                                className = classTitle,
                                date = null
                            )
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = "Not marked yet",
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                    )
                                    Text(
                                        text = "Your professor records attendance by calling names in class. If you attended but are still unmarked, tell your professor your name or ID number (${viewerUser.idNumber.ifBlank { "no ID on file" }}).",
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        lineHeight = 13.sp,
                                    )
                                }
                            }
                        }
                    }
                    if (internetRequired) {
                        InternetRequiredHint()
                    }
                    localError?.let {
                        Text(
                            text = it,
                            color = Color(0xFFB91C1C),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
        } else if (students.isEmpty()) {
            item { EmptyClassState(loadError ?: "No enrolled students yet.") }
        } else if (filteredStudents.isEmpty()) {
            item { EmptyClassState("No student matches \"$searchQuery\".") }
        } else {
            items(filteredStudents, key = { it.studentId }) { student ->
                AttendanceStudentCard(
                    student = student,
                    canMarkPresent = canMarkAttendance,
                    isRecording = isRecording,
                    onMarkPresent = { onRecordAttendance(assignment.id, student.idNumber) },
                )
            }
        }
    }
    }
}

@Composable
internal fun StudentAttendanceStatusCard(
    status: String,
    className: String,
    date: String?,
) {
    val isPresent = status.equals("present", ignoreCase = true)
    val accent = if (isPresent) Color(0xFF047857) else Color(0xFFBE123C)
    val bgColor = if (isPresent) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
    val icon = if (isPresent) Icons.Filled.CheckCircle else Icons.Filled.Close
    val statusLabel = if (isPresent) "Present" else "Absent"
    val message = if (isPresent) {
        "Your attendance has been recorded."
    } else {
        "You missed this attendance activity."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            bgColor,
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = statusLabel,
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                )
                Text(
                    text = message,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = className,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                date?.let {
                    Text(
                        text = it,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun AttendanceStudentCard(
    student: AttendanceStudent,
    canMarkPresent: Boolean = false,
    isRecording: Boolean = false,
    onMarkPresent: (() -> Unit)? = null,
) {
    val present = student.status.equals("present", ignoreCase = true)
    val canMark = canMarkPresent && !present && onMarkPresent != null && student.idNumber.isNotBlank()
    val cardColor = if (present) Color(0xFFE8F8EC) else if (canMark) Color.White else Color(0xFFFFECEC)
    val badgeColor = if (present) Color(0xFF047857) else Color(0xFFBE123C)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (canMark && !isRecording) Modifier.clickable(onClick = onMarkPresent!!) else Modifier),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = if (canMark) 0.18f else 0.28f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Avatar(
                imageUrl = student.photoUrl,
                name = student.name,
                modifier = Modifier.size(48.dp),
                placeholderColor = Color.White.copy(alpha = 0.85f),
                initialFontSize = 16.sp,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = student.name.ifBlank { "Student" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = student.idNumber.ifBlank { "No ID number" },
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
            if (present) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(badgeColor)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Present",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (canMark) PanthraaBlue else badgeColor)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = when {
                            isRecording -> "Marking..."
                            canMark -> "Mark present"
                            else -> "Absent"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun StudentClassroomPage(
    classItem: StudentClass,
    assignments: List<ClassAssignment>,
    isLoadingAssignments: Boolean,
    isSubmittingAssignment: Boolean,
    internetRequired: Boolean,
    onLoadAssignments: (String) -> Unit,
    isRefreshingAssignments: Boolean,
    onRefreshAssignments: (String) -> Unit,
    onSubmitAssignment: (String, String, Uri?) -> Unit,
    onOpenAssignment: (ClassAssignment) -> Unit,
    onOpenAttendance: (ClassAssignment) -> Unit,
    onViewClassmates: () -> Unit,
    onBack: () -> Unit,
) {
    LaunchedEffect(classItem.id) {
        onLoadAssignments(classItem.id)
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshingAssignments,
        onRefresh = { onRefreshAssignments(classItem.id) },
        modifier = Modifier.fillMaxSize(),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                )
            }
            Text(
                text = classItem.displayClassName,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = classItem.displaySubjectCode,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Button(
                onClick = onViewClassmates,
                enabled = !internetRequired,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PanthraaBlue),
            ) {
                Icon(Icons.Filled.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text("Classmates", color = Color.White, modifier = Modifier.padding(start = 6.dp))
            }
        }
        if (internetRequired) {
            InternetRequiredHint()
        }

        if (isLoadingAssignments) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PanthraaLoadingAnimation(size = 144.dp)
            }
        } else {
            val sortedUploads = remember(assignments) { assignments.sortedByNewestUpload() }

            sortedUploads.forEachIndexed { index, assignment ->
                val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
                val uploadNumber = sortedUploads.size - index
                val attendanceAction: (() -> Unit)? = if (isMaterial) null else {
                    { onOpenAttendance(assignment) }
                }
                AssignmentSummaryCard(
                    assignment = assignment,
                    accent = assignmentTypeColor(assignment.assignmentType),
                    uploadNumber = uploadNumber,
                    submitted = assignment.hasSubmitted,
                    actionLabel = if (isMaterial) null else "Attendance",
                    actionEnabled = !internetRequired && !isMaterial,
                    showAttendanceStatus = !isMaterial,
                    attendanceStatus = if (isMaterial) null else assignment.attendanceStatus,
                    onActionClick = attendanceAction,
                    onClick = { onOpenAssignment(assignment) },
                )
            }
            if (sortedUploads.isEmpty()) {
                EmptyClassState("No assignments uploaded yet.")
            }
        }
    }
}
}

internal data class ProfessorUploadCardTone(
    val label: String,
    val accent: Color,
    val background: Color,
)

internal fun professorUploadCardTone(assignment: ClassAssignment): ProfessorUploadCardTone {
    val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
    if (isMaterial) {
        return ProfessorUploadCardTone(
            label = "Material",
            accent = Color(0xFF0F766E),
            background = Color(0xFFF0FDFA),
        )
    }
    if (isAssignmentLocked(assignment)) {
        return ProfessorUploadCardTone(
            label = "Scheduled upload",
            accent = Color(0xFF2563EB),
            background = Color(0xFFEFF6FF),
        )
    }
    if (isAssignmentExpired(assignment)) {
        return ProfessorUploadCardTone(
            label = "Past due",
            accent = Color(0xFFE11D48),
            background = Color(0xFFFFF1F2),
        )
    }
    val endDate = assignment.endDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    if (endDate == LocalDate.now(PhilippineZoneId)) {
        return ProfessorUploadCardTone(
            label = "Due today",
            accent = Color(0xFFF97316),
            background = Color(0xFFFFF7ED),
        )
    }
    return ProfessorUploadCardTone(
        label = "Ongoing",
        accent = Color(0xFF16A34A),
        background = Color(0xFFECFDF5),
    )
}

@Composable
internal fun AssignmentSummaryCard(
    assignment: ClassAssignment,
    accent: Color,
    uploadNumber: Int? = null,
    submitted: Boolean?,
    actionLabel: String? = null,
    actionEnabled: Boolean = true,
    showAttendanceStatus: Boolean = false,
    attendanceStatus: String? = null,
    onActionClick: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val isStudentView = submitted != null
    val typeAccent = accent
    val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
    val professorTone = if (isStudentView) null else professorUploadCardTone(assignment)
    val locked = isStudentView && !isMaterial && isAssignmentLocked(assignment)
    val expired = isStudentView && !isMaterial && !locked && !(submitted ?: false) && isAssignmentExpired(assignment)

    val isOpen = isStudentView && !locked && !expired && !(submitted ?: false) && !isMaterial

    val statusAccent = when {
        !isStudentView -> professorTone?.accent ?: typeAccent
        locked -> Color(0xFF94A3B8)
        expired -> Color(0xFFE11D48)
        submitted == true -> Color(0xFF16A34A)
        isOpen -> PanthraaBlue
        else -> PanthraaBlue
    }
    val cardColor = when {
        !isStudentView -> Color.White
        locked -> Color(0xFFF8FAFC)
        expired -> Color(0xFFFFF1F2)
        submitted == true -> Color(0xFFECFDF5)
        else -> Color(0xFFEFF6FF)
    }
    val borderColor = if (isStudentView) statusAccent.copy(alpha = 0.34f) else Color(0xFFE2E8F0)
    val contentColor = Color(0xFF0F172A)
    val mutedContentColor = if (locked) Color(0xFF94A3B8) else Color(0xFF64748B)
    val attendanceStyle = if (showAttendanceStatus && !isMaterial) attendanceButtonStyle(attendanceStatus) else null
    val statusText = when {
        !isStudentView -> professorTone?.label.orEmpty()
        isMaterial -> "Learning material"
        locked -> "Locked"
        expired -> "Closed"
        submitted == true -> "Submitted"
        else -> "Not submitted"
    }
    val timeWindowText = if (!isStudentView && !isMaterial) assignmentTimeWindowLabel(assignment) else null
    val typeLabel = assignmentTypeLabel(assignment.assignmentType)
    val categoryPrefix = if (isMaterial) "" else "${assignmentCategoryLabel(assignment.category).uppercase()} / "
    val metaText = if (isMaterial) {
        "$typeLabel - ${displayAssignmentCreatedAt(assignment.createdAt).ifBlank { "Unknown date" }}"
    } else {
        "$categoryPrefix$typeLabel - ${displayAssignmentCreatedAt(assignment.createdAt).ifBlank { "Unknown date" }}"
    }
    val iconBackground = if (!isStudentView) {
        professorTone?.background ?: typeAccent.copy(alpha = 0.10f)
    } else {
        typeAccent.copy(alpha = if (isMaterial) 0.15f else 0.12f)
    }
    val iconBorder = if (!isStudentView) statusAccent.copy(alpha = 0.18f) else typeAccent.copy(alpha = if (isMaterial) 0.20f else 0.16f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackground)
                    .border(1.dp, iconBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(assignmentTypeIconRes(assignment.assignmentType)),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(typeAccent),
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = metaText,
                    color = mutedContentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = assignment.title.ifBlank { "Untitled assignment" },
                    color = if (locked) Color(0xFF94A3B8) else contentColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(statusAccent.copy(alpha = if (!isStudentView) 0.10f else 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = statusText,
                            color = statusAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (isStudentView && !isMaterial) {
                        val count = assignment.submittedCount.coerceAtLeast(if (assignment.hasSubmitted) 1 else 0)
                        Text(
                            text = "$count submitted",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                    timeWindowText?.let { window ->
                        Text(
                            text = window,
                            color = mutedContentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            ) {
                uploadNumber?.let { number ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (!isStudentView) Color(0xFFF8FAFC) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (!isStudentView) Color(0xFFE2E8F0) else statusAccent.copy(alpha = 0.30f),
                                shape = RoundedCornerShape(999.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "#$number",
                            color = if (!isStudentView) Color(0xFF64748B) else statusAccent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                    }
                }
                if (actionLabel != null && onActionClick != null && !isMaterial) {
                    val buttonBackground = attendanceStyle?.background ?: Color(0xFFEAF0FF)
                    val buttonContent = attendanceStyle?.content ?: PanthraaBlue
                    val buttonBorder = attendanceStyle?.border ?: PanthraaBlue.copy(alpha = 0.24f)
                    Button(
                        onClick = onActionClick,
                        enabled = actionEnabled,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonBackground,
                            contentColor = buttonContent,
                            disabledContainerColor = Color.White.copy(alpha = 0.70f),
                            disabledContentColor = buttonContent.copy(alpha = 0.60f),
                        ),
                        border = BorderStroke(1.dp, buttonBorder),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .width(if (attendanceStyle == null) 88.dp else 58.dp),
                    ) {
                        when (attendanceStyle?.state) {
                            AttendanceButtonState.NotChecked -> Text(
                                text = "-",
                                color = buttonContent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                            )
                            AttendanceButtonState.Present -> Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = actionLabel,
                                tint = buttonContent,
                                modifier = Modifier.size(22.dp),
                            )
                            AttendanceButtonState.Absent -> Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = actionLabel,
                                tint = buttonContent,
                                modifier = Modifier.size(22.dp),
                            )
                            null -> Text(
                                text = "Attendance",
                                color = buttonContent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun StudentAssignmentCard(
    assignment: ClassAssignment,
    isSubmitting: Boolean,
    onSubmitAssignment: (String, String, Uri?) -> Unit,
) {
    var showSubmissionDialog by remember(assignment.id) { mutableStateOf(false) }
    var answer by remember(assignment.id, assignment.submissionText) { mutableStateOf(assignment.submissionText.orEmpty()) }
    val submitted = assignment.hasSubmitted
    val locked = isAssignmentLocked(assignment)
    val isMaterial = assignment.assignmentType == "material"
    val cardColor = if (locked) Color(0xFFF1F5F9) else if (submitted) Color(0xFFE8F8EC) else Color(0xFFFFECEC)
    val borderColor = if (locked) Color(0xFFCBD5E1) else if (submitted) Color(0xFF2E7D32) else Color(0xFFC62828)
    val accentColor = assignmentTypeColor(assignment.assignmentType)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.92f),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, borderColor),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TypeLabelChip(assignmentType = assignment.assignmentType, accentColor = accentColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = assignment.title,
                                color = if (locked) Color(0xFF94A3B8) else Color.Black,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            if (locked) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFCBD5E1))
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                ) {
                                    Text(
                                        text = "Locked",
                                        color = Color(0xFF475569),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when {
                                        locked -> "Locked"
                                        isMaterial -> "Read-only"
                                        submitted -> "Submitted"
                                        else -> "Pending"
                                    },
                                    color = if (isMaterial && !locked) accentColor else borderColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                                if (submitted && assignment.editAttempts != null && assignment.editAttempts > 0) {
                                    Text(
                                        text = " - Edited ${assignment.editAttempts} time(s)",
                                        color = Color.DarkGray,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                            if (submitted) {
                                val displayTime = displaySubmissionDateTime(assignment.submittedAt).ifBlank { "Unknown time" }
                                Text(
                                    text = displayTime,
                                    color = Color.DarkGray,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    if (!locked && !isMaterial) {
                        IconButton(
                            onClick = {
                                answer = assignment.submissionText.orEmpty()
                                showSubmissionDialog = true
                            },
                            enabled = !isSubmitting,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = if (submitted) "Edit submission" else "Submit assignment",
                                tint = Color.Black,
                            )
                        }
                    }
                }
                if (assignment.instructions.isNotBlank()) {
                    Text(
                        text = assignment.instructions,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val completedCount = assignment.submittedCount.coerceAtLeast(if (assignment.hasSubmitted) 1 else 0)
                    val totalCount = maxOf(assignment.totalStudents, completedCount)
                    Text(
                        text = "$completedCount/$totalCount completed",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }

    if (showSubmissionDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSubmitting) showSubmissionDialog = false
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = DialogSurface,
            title = {
                DialogHeader(
                    title = if (submitted) "Edit submission" else "Submit assignment",
                    subtitle = assignment.title.ifBlank { "Assignment response" },
                    iconRes = assignmentTypeIconRes(assignment.assignmentType),
                    tint = assignmentTypeColor(assignment.assignmentType),
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (assignment.instructions.isNotBlank()) {
                        Text(
                            text = assignment.instructions,
                            color = Color(0xFF334155),
                            fontSize = 13.sp,
                            lineHeight = 15.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Your answer") },
                        minLines = 5,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                }
            },
            confirmButton = {
                DialogPrimaryButton(
                    text = when {
                        isSubmitting -> "Saving..."
                        submitted -> "Update"
                        else -> "Submit"
                    },
                    onClick = {
                        onSubmitAssignment(assignment.id, answer, null)
                        showSubmissionDialog = false
                    },
                    enabled = answer.trim().isNotEmpty() && !isSubmitting,
                )
            },
            dismissButton = {
                DialogCancelButton(onClick = { showSubmissionDialog = false }, enabled = !isSubmitting)
            },
        )
    }
}

@Composable
internal fun StudentAssignmentDetailPage(
    assignment: ClassAssignment,
    classItem: StudentClass? = null,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    isSubmitting: Boolean,
    internetRequired: Boolean,
    comments: List<AssignmentComment> = emptyList(),
    isLoadingComments: Boolean = false,
    isPostingComment: Boolean = false,
    isDeletingComment: Boolean = false,
    isHidingComment: Boolean = false,
    currentUserId: String = "",
    onSubmitAssignment: (String, String, Uri?) -> Unit,
    onRecordMaterialView: (String) -> Unit,
    onLoadComments: (String) -> Unit = {},
    onRefreshComments: (String) -> Unit = {},
    onPostComment: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteComment: (String) -> Unit = {},
    onHideComment: (String) -> Unit = {},
    onUnhideComment: (String) -> Unit = {},
    onOpenAttendance: () -> Unit,
    onBack: () -> Unit,
) {
    var answer by remember(assignment.id, assignment.submissionText) {
        mutableStateOf(assignment.submissionText.orEmpty())
    }
    var selectedSubmissionFileUri by remember(assignment.id) { mutableStateOf<Uri?>(null) }
    var selectedSubmissionFileName by remember(assignment.id) { mutableStateOf<String?>(null) }
    var submissionFileError by remember(assignment.id) { mutableStateOf<String?>(null) }
    var editorVisible by remember(assignment.id, assignment.hasSubmitted, assignment.submissionText) {
        mutableStateOf(!assignment.hasSubmitted)
    }
    val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
    val locked = isAssignmentLocked(assignment)
    val expired = !locked && isAssignmentExpired(assignment)
    val requiredFormat = submissionFormatOption(assignment.submissionFormat)
    val selectedSubmissionFileInvalid = submissionFileError != null && selectedSubmissionFileName != null && selectedSubmissionFileUri == null
    val hasExistingSubmissionFile = !assignment.submissionFileUrl.isNullOrBlank()
    val hasValidSubmissionFile = if (assignment.requiresFile) {
        selectedSubmissionFileUri != null || (hasExistingSubmissionFile && !selectedSubmissionFileInvalid)
    } else {
        !selectedSubmissionFileInvalid
    }
    val hasSubmissionChanges = !assignment.hasSubmitted ||
        selectedSubmissionFileUri != null ||
        answer.trim() != assignment.submissionText.orEmpty().trim()
    val submitBlocker = submissionBlockingReason(
        isSubmitting = isSubmitting,
        locked = locked,
        expired = expired,
        internetRequired = internetRequired,
        requiresFile = assignment.requiresFile,
        hasValidFile = hasValidSubmissionFile,
        selectedFileInvalid = selectedSubmissionFileInvalid,
        hasChanges = hasSubmissionChanges,
    )
    val context = LocalContext.current
    val submissionFilePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val formatError = submissionFileFormatError(context, uri, requiredFormat.value)
        if (formatError != null) {
            selectedSubmissionFileUri = null
            selectedSubmissionFileName = selectedFileName(context, uri).ifBlank { "Selected file" }
            submissionFileError = formatError
        } else {
            selectedSubmissionFileUri = uri
            selectedSubmissionFileName = selectedFileName(context, uri).ifBlank { "Selected ${requiredFormat.label} file" }
            submissionFileError = null
        }
    }

    var materialViewRemainingMillis by remember(assignment.id, assignment.hasSubmitted) { mutableStateOf<Long?>(null) }

    LaunchedEffect(assignment.id, assignment.hasSubmitted, isMaterial, internetRequired) {
        if (isMaterial && !assignment.hasSubmitted && !internetRequired) {
            val totalMillis = 10_000L
            val tickMillis = 100L
            materialViewRemainingMillis = totalMillis
            while ((materialViewRemainingMillis ?: 0L) > 0L) {
                delay(tickMillis)
                materialViewRemainingMillis = ((materialViewRemainingMillis ?: 0L) - tickMillis).coerceAtLeast(0L)
            }
            onRecordMaterialView(assignment.id)
        } else {
            if (materialViewRemainingMillis != 0L) materialViewRemainingMillis = null
        }
    }

    LaunchedEffect(assignment.id, internetRequired) {
        if (!internetRequired) {
            onLoadComments(assignment.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .verticalScroll(rememberScrollState())
            .padding(
                start = 14.dp,
                end = 14.dp,
                top = 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AssignmentDetailHeader(
            title = assignmentTypeTitle(assignment.assignmentType),
            onBack = onBack,
        )

        if (!isMaterial && classItem != null) {
            val scheduleLabel = compactClassScheduleLabel(
                days = classItem.scheduleDays,
                startTime = classItem.scheduleStartTime,
                endTime = classItem.scheduleEndTime,
            )
            if (scheduleLabel != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        Icons.Filled.Event,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = scheduleLabel,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        if (!isMaterial && assignment.endDate != null) {
            val dueDateText = displayClassDate(assignment.endDate)
            val dueTimeText = assignment.endTime?.let { formatAssignmentClockTime(it) }
            val deadlineLabel = if (dueTimeText != null) "$dueDateText at $dueTimeText" else dueDateText
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isAssignmentExpired(assignment)) Color(0xFFFEF2F2) else Color(0xFFF0FDF4))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    Icons.Filled.Event,
                    contentDescription = null,
                    tint = if (isAssignmentExpired(assignment)) Color(0xFFDC2626) else Color(0xFF16A34A),
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = if (isAssignmentExpired(assignment)) "Overdue · $deadlineLabel" else "Due $deadlineLabel",
                    color = if (isAssignmentExpired(assignment)) Color(0xFF991B1B) else Color(0xFF166534),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
        }

        AssignmentInstructionCard(assignment = assignment, materialViewRemainingMillis = materialViewRemainingMillis)

        if (assignment.fileUrl != null) {
            AssignmentFilePreview(
                fileUrl = assignment.fileUrl,
                fileName = assignment.title,
            )
        }

        if (!isMaterial) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(16.dp),
            ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Text(
                    text = "Your submission",
                    color = if (locked) Color(0xFF94A3B8) else Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                )

                if (locked) {
                    PanthraaStatusNotice(
                        type = NoticeType.LOCKED,
                        title = "Submission locked",
                        message = "Submission is locked until the window opens.",
                    )
                } else if (editorVisible) {
                    if (assignment.hasSubmitted) {
                        SubmissionMetadata(assignment = assignment)
                    }
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Notes (optional)") },
                        placeholder = { Text("Add a note for your professor.") },
                        minLines = 4,
                        readOnly = locked || expired,
                        enabled = !internetRequired && !isSubmitting && !locked && !expired,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = PanthraaBlue,
                            focusedLabelColor = PanthraaBlue,
                            cursorColor = PanthraaBlue,
                        ),
                    )
                    FileRequirementCard(
                        requiredFormat = requiredFormat,
                        requiresFile = assignment.requiresFile,
                        selectedFileName = selectedSubmissionFileName,
                        selectedFileInvalid = selectedSubmissionFileInvalid,
                        existingFileUrl = assignment.submissionFileUrl,
                        enabled = !internetRequired && !isSubmitting && !locked && !expired,
                        error = submissionFileError,
                        onPickFile = { submissionFilePicker.launch(requiredFormat.pickerMimeTypes) },
                        onClearSelectedFile = {
                            selectedSubmissionFileUri = null
                            selectedSubmissionFileName = null
                            submissionFileError = null
                        },
                    )
                    if (assignment.hasSubmitted) {
                        AssignmentScoreLine(
                            assignment = assignment,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Button(
                        onClick = {
                            if (submitBlocker == null) {
                                onSubmitAssignment(assignment.id, answer, selectedSubmissionFileUri)
                            }
                        },
                        enabled = submitBlocker == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PanthraaBlue,
                            disabledContainerColor = PanthraaBlue.copy(alpha = 0.72f),
                            disabledContentColor = Color.White,
                        ),
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isSubmitting) "Saving..." else if (assignment.hasSubmitted) "Update submission" else "Submit assignment",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    ViewAttendanceButton(
                        onClick = onOpenAttendance,
                        enabled = !internetRequired,
                    )
                    submitBlocker?.let { reason ->
                        PanthraaStatusNotice(
                            type = reason.type,
                            title = reason.title,
                            message = reason.message,
                        )
                    }
                } else {
                    SubmissionMetadata(assignment = assignment)
                    assignment.submissionFileUrl?.let { fileUrl ->
                        AssignmentFilePreview(
                            fileUrl = fileUrl,
                            fileName = "Submitted ${requiredFormat.label} file",
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Preview",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                        ) {
                            Text(
                                text = assignment.submissionText?.takeIf { it.isNotBlank() }
                                    ?: "Your submitted answer will appear here.",
                                color = if (assignment.submissionText.isNullOrBlank()) Color(0xFF94A3B8) else Color(0xFF0F172A),
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 18.sp,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AssignmentScoreLine(
                                assignment = assignment,
                                modifier = Modifier.weight(1f),
                            )
                            if (!locked && !expired) {
                                PanthraaIconAction(
                                    icon = Icons.Filled.Edit,
                                    contentDescription = "Edit submission",
                                    onClick = {
                                        answer = assignment.submissionText.orEmpty()
                                        editorVisible = true
                                    },
                                    enabled = !internetRequired && !isSubmitting,
                                )
                            }
                        }
                        if (internetRequired) {
                            InternetRequiredHint()
                        }
                    }
                }
            }
        }
        }

        AssignmentCommentsSection(
            assignment = assignment,
            comments = comments,
            currentUserId = currentUserId,
            isProfessorView = false,
            isLoading = isLoadingComments,
            isPosting = isPostingComment,
            isDeleting = isDeletingComment,
            isHiding = isHidingComment,
            internetRequired = internetRequired,
            onPostComment = onPostComment,
            onDeleteComment = onDeleteComment,
            onHideComment = onHideComment,
            onUnhideComment = onUnhideComment,
        )
    }
}

@Composable
internal fun ViewAttendanceButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0F766E),
            disabledContainerColor = Color(0xFFCBD5E1),
        ),
    ) {
        Icon(Icons.Filled.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        Text(
            text = "View Attendance",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun FileRequirementCard(
    requiredFormat: SubmissionFormatOption,
    requiresFile: Boolean,
    selectedFileName: String?,
    selectedFileInvalid: Boolean,
    existingFileUrl: String?,
    enabled: Boolean,
    error: String?,
    onPickFile: () -> Unit,
    onClearSelectedFile: () -> Unit,
) {
    val hasExistingFile = !existingFileUrl.isNullOrBlank()
    val hasSelectedFile = selectedFileName != null
    val hasAnyFile = hasExistingFile || hasSelectedFile
    val isPictureRequirement = requiredFormat.value == "image"
    val fileLabel = if (requiresFile) "Required file" else "Attachment"
    val requirementDescription = if (requiresFile) {
        if (isPictureRequirement) {
            "Attach an accepted picture before submitting."
        } else {
            "Attach an accepted ${requiredFormat.label} file before submitting."
        }
    } else ""
    val borderColor = when {
        selectedFileInvalid || error != null -> Color(0xFFEF4444)
        hasAnyFile -> PanthraaBlue.copy(alpha = 0.45f)
        else -> Color(0xFFCBD5E1)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (hasAnyFile && !selectedFileInvalid) Color(0xFFEAF0FF) else Color.White,
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fileLabel,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                    )
                    if (requirementDescription.isNotBlank()) {
                        Text(
                            text = requirementDescription,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                        )
                    }
                    Text(
                        text = "Accepted: ${acceptedFormatsText(requiredFormat)}",
                        color = PanthraaBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }

            if (hasExistingFile) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PanthraaBlue.copy(alpha = 0.08f))
                        .border(1.dp, PanthraaBlue.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = PanthraaBlue,
                        modifier = Modifier.size(18.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Current submitted file",
                            color = PanthraaBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = "Kept unless you choose a replacement.",
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                        )
                    }
                }
            }

            selectedFileName?.let { fileName ->
                val fileTone = if (selectedFileInvalid) Color(0xFFB91C1C) else PanthraaBlue
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(fileTone.copy(alpha = 0.08f))
                        .border(1.dp, fileTone.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (selectedFileInvalid) Icons.Filled.Close else Icons.Filled.Description,
                        contentDescription = null,
                        tint = fileTone,
                        modifier = Modifier.size(18.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasExistingFile) "Selected replacement" else "Selected file",
                            color = fileTone,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = fileName,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    PanthraaIconAction(
                        icon = Icons.Filled.Close,
                        contentDescription = "Remove selected file",
                        onClick = onClearSelectedFile,
                        enabled = enabled,
                        tint = Color(0xFF64748B),
                    )
                }
            }

            Button(
                onClick = onPickFile,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PanthraaBlue),
            ) {
                Icon(Icons.Filled.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text(
                    text = when {
                        hasSelectedFile -> if (isPictureRequirement) "Choose another picture" else "Choose another file"
                        hasExistingFile -> "Choose replacement"
                        else -> if (isPictureRequirement) "Choose picture" else "Choose file"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            error?.let {
                Text(
                    text = it,
                    color = Color(0xFFB91C1C),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
internal fun SubmissionMetadata(assignment: ClassAssignment) {
    val editCount = assignment.editAttempts ?: 0
    val updateTime = displaySubmissionDateTime(assignment.submittedAt).ifBlank { "Not available" }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "Last edited: $updateTime",
            color = Color.DarkGray,
            style = MaterialTheme.typography.labelMedium
        )
        if (editCount > 0) {
            Text(
                text = "Number of edits: $editCount",
                color = Color.DarkGray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
internal fun AssignmentScoreLine(
    assignment: ClassAssignment,
    modifier: Modifier = Modifier,
) {
    val targetPoints = assignment.targetPoints.coerceAtLeast(1)
    val earnedPoints = assignment.score?.coerceIn(0, targetPoints)
    val rawPercent = earnedPoints?.let { pointsToPercent(it, targetPoints) }
    val finalGrade = rawPercent?.let { convertRawToFinal(it) }
    val tone = finalGrade?.let { gradeTone(it) } ?: Color(0xFF64748B)
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(tone.copy(alpha = 0.09f))
            .border(1.dp, tone.copy(alpha = 0.18f), shape)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Score",
            color = Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = earnedPoints?.let { "$it/$targetPoints" } ?: "--/$targetPoints",
            color = tone,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun ProfessorAssignmentDetailPage(
    assignment: ClassAssignment,
    submissions: List<AssignmentSubmission>,
    isLoadingSubmissions: Boolean,
    isScoringSubmission: Boolean,
    isDeleting: Boolean,
    isUpdating: Boolean,
    internetRequired: Boolean,
    classItem: ProfessorClass? = null,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    comments: List<AssignmentComment> = emptyList(),
    isLoadingComments: Boolean = false,
    isPostingComment: Boolean = false,
    isDeletingComment: Boolean = false,
    isHidingComment: Boolean = false,
    currentUserId: String = "",
    onLoadSubmissions: (String) -> Unit,
    isRefreshingSubmissions: Boolean,
    onRefreshSubmissions: (String) -> Unit,
    onLoadComments: (String) -> Unit = {},
    onRefreshComments: (String) -> Unit = {},
    onPostComment: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteComment: (String) -> Unit = {},
    onHideComment: (String) -> Unit = {},
    onUnhideComment: (String) -> Unit = {},
    onGradeSubmission: (String, Int, Int) -> Unit,
    onDeleteAssignment: (String) -> Unit,
    onUpdateAssignment: (String, String, String, String, Int, String?, String?, String?, String?, String, String, Boolean, Boolean, Uri?) -> Unit,
    onOpenAttendance: () -> Unit,
    onBack: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedSubmission by remember { mutableStateOf<AssignmentSubmission?>(null) }
    var pendingScoreClose by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var scoreSaveWasRunning by remember { mutableStateOf(false) }
    val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
    val activeSubmission = selectedSubmission?.let { selected ->
        submissions.firstOrNull { it.id == selected.id } ?: selected
    }
    val ungradedSubmissionCount = submissions.count { it.score == null }
    val reviewSummaryText = when {
        submissions.isEmpty() -> "Waiting for work"
        ungradedSubmissionCount == 0 -> "All submitted work reviewed"
        ungradedSubmissionCount == 1 -> "1 submission needs a score"
        else -> "$ungradedSubmissionCount submissions need scores"
    }

    LaunchedEffect(assignment.id, isMaterial, internetRequired) {
        if (!internetRequired && !isMaterial) {
            onLoadSubmissions(assignment.id)
        }
        if (!internetRequired) {
            onLoadComments(assignment.id)
        }
    }

    LaunchedEffect(isScoringSubmission, submissions, pendingScoreClose) {
        if (isScoringSubmission) {
            scoreSaveWasRunning = true
            return@LaunchedEffect
        }
        val pending = pendingScoreClose
        if (scoreSaveWasRunning && pending != null) {
            val saved = submissions.any { submission ->
                submission.id == pending.first && submission.score == pending.second
            }
            scoreSaveWasRunning = false
            if (saved) {
                pendingScoreClose = null
                selectedSubmission = null
            }
        }
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshingSubmissions,
        onRefresh = {
            if (!isMaterial) onRefreshSubmissions(assignment.id)
            onRefreshComments(assignment.id)
        },
        modifier = Modifier.fillMaxSize(),
        enabled = !isMaterial,
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    top = 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 12.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
        AssignmentDetailHeader(
            title = assignmentTypeTitle(assignment.assignmentType),
            onBack = onBack,
            trailing = {
                IconButton(
                    onClick = { showEditDialog = true },
                    enabled = !internetRequired && !isUpdating && !isDeleting,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit ${assignmentTypeTitle(assignment.assignmentType).lowercase()}",
                        tint = PanthraaBlue,
                    )
                }
                IconButton(
                    onClick = { showDeleteDialog = true },
                    enabled = !internetRequired && !isDeleting,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = if (isMaterial) "Delete material" else "Delete ${assignmentTypeTitle(assignment.assignmentType).lowercase()}",
                        tint = Color(0xFFBE123C),
                    )
                }
                if (internetRequired) {
                    InternetRequiredHint()
                }
            },
        )

        AssignmentInstructionCard(assignment = assignment)

        if (assignment.fileUrl != null) {
            AssignmentFilePreview(
                fileUrl = assignment.fileUrl,
                fileName = assignment.title,
            )
        }

        if (!isMaterial) {
            Button(
                onClick = onOpenAttendance,
                enabled = !internetRequired,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                Text(
                    text = "Check Attendance",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Student submissions",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "${assignment.submittedCount}/${assignment.totalStudents} submitted - $reviewSummaryText",
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    when {
                        internetRequired -> PanthraaStatusNotice(
                            type = NoticeType.OFFLINE,
                            title = "Internet required",
                            message = "Connect to review and score student submissions.",
                        )
                        isLoadingSubmissions -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            PanthraaLoadingAnimation(size = 144.dp)
                        }
                        submissions.isEmpty() -> PanthraaStatusNotice(
                            type = NoticeType.INFO,
                            title = "No submissions yet",
                            message = "Student submissions will appear here once they upload their work.",
                        )
                        else -> {
                            if (isRefreshingSubmissions) {
                                PanthraaStatusNotice(
                                    type = NoticeType.INFO,
                                    title = "Refreshing submissions",
                                    message = "Keeping the current list visible while checking for updates.",
                                )
                            }
                            submissions.forEach { submission ->
                                AssignmentSubmissionCard(
                                    submission = submission,
                                    onClick = {
                                        if (!isScoringSubmission) selectedSubmission = submission
                                    },
                                )
                            }
                        }
                    }
                }
            }
}

        AssignmentCommentsSection(
            assignment = assignment,
            comments = comments,
            currentUserId = currentUserId,
            isProfessorView = true,
            isLoading = isLoadingComments,
            isPosting = isPostingComment,
            isDeleting = isDeletingComment,
            isHiding = isHidingComment,
            internetRequired = internetRequired,
            onPostComment = onPostComment,
            onDeleteComment = onDeleteComment,
            onHideComment = onHideComment,
            onUnhideComment = onUnhideComment,
        )
    }
}
}

    activeSubmission?.let { submission ->
        AssignmentSubmissionDetailDialog(
            submission = submission,
            isSavingScore = isScoringSubmission,
            internetRequired = internetRequired,
            onSaveScore = { score ->
                pendingScoreClose = submission.id to score
                onGradeSubmission(submission.id, score, submission.targetPoints)
            },
            onDismiss = {
                pendingScoreClose = null
                scoreSaveWasRunning = false
                selectedSubmission = null
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = DialogSurface,
            title = {
                DialogHeader(
                    title = if (isMaterial) "Delete material?" else "Delete ${assignmentTypeTitle(assignment.assignmentType).lowercase()}?",
                    subtitle = "This removes it for everyone.",
                    icon = Icons.Filled.Delete,
                    tint = Color(0xFFBE123C),
                )
            },
            text = {
                Text(
                    text = if (isMaterial) {
                        "This removes the material for everyone in the class."
                    } else {
                        "This removes the ${assignmentTypeTitle(assignment.assignmentType).lowercase()} and related submissions for everyone in the class."
                    },
                    color = Color(0xFF334155),
                )
            },
            confirmButton = {
                DialogPrimaryButton(
                    text = if (isDeleting) "Deleting..." else "Delete",
                    onClick = {
                        onDeleteAssignment(assignment.id)
                        showDeleteDialog = false
                        onBack()
                    },
                    enabled = !internetRequired && !isDeleting,
                    color = Color(0xFFBE123C),
                )
            },
            dismissButton = {
                DialogCancelButton(onClick = { showDeleteDialog = false }, enabled = !isDeleting)
            },
        )
    }

    if (showEditDialog) {
        UploadAssignmentDialog(
            isCreating = isUpdating,
            initialAssignment = assignment,
            scheduleDays = classItem?.scheduleDays.orEmpty(),
            scheduleStartTime = classItem?.scheduleStartTime,
            scheduleEndTime = classItem?.scheduleEndTime,
            onDismiss = { if (!isUpdating) showEditDialog = false },
            onUpload = { title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, allowComments, fileUri ->
                onUpdateAssignment(assignment.id, title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, allowComments, fileUri)
                showEditDialog = false
            },
        )
    }
}

@Composable
internal fun AssignmentCommentsSection(
    assignment: ClassAssignment,
    comments: List<AssignmentComment>,
    currentUserId: String,
    isProfessorView: Boolean,
    isLoading: Boolean,
    isPosting: Boolean,
    isDeleting: Boolean,
    isHiding: Boolean,
    internetRequired: Boolean,
    onPostComment: (String, String, String) -> Unit,
    onDeleteComment: (String) -> Unit,
    onHideComment: (String) -> Unit,
    onUnhideComment: (String) -> Unit,
) {
    var draft by remember(assignment.id) { mutableStateOf("") }
    var visibility by remember(assignment.id) { mutableStateOf("public") }
    var pendingDeleteCommentId by remember(assignment.id) { mutableStateOf<String?>(null) }
    val isStudentView = !isProfessorView
    val commentsEnabled = isProfessorView || assignment.allowComments
    val canPost = commentsEnabled && !internetRequired && !isPosting && draft.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Comments",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (comments.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PanthraaBlue.copy(alpha = 0.10f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "${comments.size}",
                            color = PanthraaBlue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                        )
                    }
                }
            }

            Text(
                text = if (isStudentView) {
                    "Public comments are visible to everyone in the class. \"Only professor\" comments are seen just by your professor."
                } else {
                    "Public comments are visible to students. Private comments are seen only by you and the author."
                },
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                lineHeight = 13.sp,
            )

            when {
                internetRequired -> PanthraaStatusNotice(
                    type = NoticeType.OFFLINE,
                    title = "Internet required",
                    message = "Connect to view and post comments.",
                )
                isLoading && comments.isEmpty() -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PanthraaLoadingAnimation(size = 72.dp)
                }
                comments.isEmpty() -> PanthraaStatusNotice(
                    type = NoticeType.INFO,
                    title = if (isStudentView && !assignment.allowComments) {
                        "Comments are off"
                    } else {
                        "No comments yet"
                    },
                    message = if (isStudentView && !assignment.allowComments) {
                        "The professor has turned off comments for this upload."
                    } else {
                        "Start the discussion below."
                    },
                )
                else -> {
                    if (isStudentView && !assignment.allowComments) {
                        PanthraaStatusNotice(
                            type = NoticeType.INFO,
                            title = "Comments are off",
                            message = "The professor has turned off new comments, but previous ones stay visible.",
                        )
                    }
                    comments.forEach { comment ->
                        AssignmentCommentRow(
                            comment = comment,
                            currentUserId = currentUserId,
                            isProfessorView = isProfessorView,
                            isDeleting = isDeleting,
                            isHiding = isHiding,
                            internetRequired = internetRequired,
                            onDelete = { pendingDeleteCommentId = comment.id },
                            onHide = { onHideComment(comment.id) },
                            onUnhide = { onUnhideComment(comment.id) },
                        )
                    }
                }
            }

            if (commentsEnabled && !internetRequired) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Add a comment") },
                    placeholder = {
                        Text(
                            if (isProfessorView) {
                                "Share a note with the class."
                            } else {
                                "Ask a question or share a note."
                            }
                        )
                    },
                    minLines = 2,
                    maxLines = 5,
                    enabled = !isPosting,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PanthraaBlue,
                        focusedLabelColor = PanthraaBlue,
                        cursorColor = PanthraaBlue,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isStudentView) {
                        CommentVisibilityChip(
                            label = "Public",
                            selected = visibility == "public",
                            enabled = !isPosting,
                            onSelect = { visibility = "public" },
                        )
                        CommentVisibilityChip(
                            label = "Only professor",
                            selected = visibility == "private",
                            enabled = !isPosting,
                            showLock = true,
                            onSelect = { visibility = "private" },
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            val content = draft.trim()
                            if (content.isNotEmpty()) {
                                onPostComment(assignment.id, content, if (isProfessorView) "public" else visibility)
                                draft = ""
                            }
                        },
                        enabled = canPost,
                        modifier = Modifier.height(42.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PanthraaBlue,
                            disabledContainerColor = Color(0xFFCBD5E1),
                        ),
                    ) {
                        if (isPosting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isPosting) "Posting..." else "Post",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }

    pendingDeleteCommentId?.let { commentId ->
        AlertDialog(
            onDismissRequest = { if (!isDeleting) pendingDeleteCommentId = null },
            title = { Text("Delete comment?") },
            text = { Text("This will permanently remove your comment for everyone. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingDeleteCommentId = null
                        onDeleteComment(commentId)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFBE123C)),
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { if (!isDeleting) pendingDeleteCommentId = null },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun AssignmentCommentRow(
    comment: AssignmentComment,
    currentUserId: String,
    isProfessorView: Boolean,
    isDeleting: Boolean,
    isHiding: Boolean,
    internetRequired: Boolean,
    onDelete: () -> Unit,
    onHide: () -> Unit,
    onUnhide: () -> Unit,
) {
    val isOwnComment = comment.authorId == currentUserId
    val canDelete = comment.id.isNotBlank() && isOwnComment
    val canHide = comment.id.isNotBlank() && isProfessorView && !isOwnComment
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Avatar(
            imageUrl = comment.authorPhotoUrl,
            name = comment.authorName,
            modifier = Modifier.size(36.dp),
            initialFontSize = 14.sp,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = comment.authorName.ifBlank { "Unknown" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (comment.authorRole.equals("professor", ignoreCase = true)) {
                    CommentTag(
                        text = "Faculty",
                        background = Color(0xFFEAF0FF),
                        content = PanthraaBlue,
                    )
                }
                if (comment.isPrivate) {
                    CommentTag(
                        text = "Private",
                        background = Color(0xFFFEF3C7),
                        content = Color(0xFF92400E),
                        showLock = true,
                    )
                }
                if (comment.isHidden && isProfessorView) {
                    CommentTag(
                        text = "Hidden",
                        background = Color(0xFFF1F5F9),
                        content = Color(0xFF475569),
                    )
                }
            }
            Text(
                text = displayAssignmentCreatedAt(comment.createdAt),
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            )
            Text(
                text = comment.content,
                color = Color(0xFF334155),
                fontSize = 13.sp,
                lineHeight = 17.sp,
            )
        }
        if (canDelete) {
            IconButton(
                onClick = onDelete,
                enabled = !isDeleting && !internetRequired,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete comment",
                    tint = Color(0xFFBE123C),
                    modifier = Modifier.size(17.dp),
                )
            }
        }
        if (canHide) {
            IconButton(
                onClick = if (comment.isHidden) onUnhide else onHide,
                enabled = !isHiding && !internetRequired,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = if (comment.isHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (comment.isHidden) "Unhide comment" else "Hide comment from everyone",
                    tint = if (comment.isHidden) Color(0xFF0F766E) else Color(0xFF92400E),
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}

@Composable
private fun CommentTag(
    text: String,
    background: Color,
    content: Color,
    showLock: Boolean = false,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (showLock) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(10.dp),
            )
        }
        Text(
            text = text,
            color = content,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun CommentVisibilityChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    showLock: Boolean = false,
    onSelect: () -> Unit,
) {
    val background = if (selected) PanthraaBlue.copy(alpha = 0.12f) else Color(0xFFF1F5F9)
    val border = if (selected) PanthraaBlue.copy(alpha = 0.50f) else Color(0xFFE2E8F0)
    val content = if (selected) PanthraaBlue else Color(0xFF64748B)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onSelect)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (showLock) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(12.dp),
            )
        }
        Text(
            text = label,
            color = content,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
        )
    }
}

@Composable
internal fun TypeLabelChip(
    assignmentType: String,
    accentColor: Color,
) {
    val label = assignmentTypeLabel(assignmentType)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(assignmentTypeIconRes(assignmentType)),
                contentDescription = null,
                colorFilter = ColorFilter.tint(accentColor),
                modifier = Modifier.size(9.dp),
            )
            Text(
                text = label,
                color = accentColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
internal fun AssignmentFilePreview(
    fileUrl: String,
    fileName: String,
) {
    val ctx = LocalContext.current
    val isPdf = remember(fileUrl, fileName) { isPdfFile(fileUrl, fileName) }
    val isImage = remember(fileUrl, fileName) { isImageFile(fileUrl, fileName) }
    var pdfState by remember(fileUrl) { mutableStateOf<PdfPreviewState>(PdfPreviewState.Loading) }

    LaunchedEffect(fileUrl, isPdf) {
        if (!isPdf) {
            pdfState = PdfPreviewState.NotPdf
            return@LaunchedEffect
        }
        pdfState = PdfPreviewState.Loading
        pdfState = withContext(Dispatchers.IO) {
            runCatching {
                PdfPreviewState.Ready(renderPdfPreviewPages(ctx, fileUrl))
            }.getOrElse { error ->
                PdfPreviewState.Error("Unable to preview this PDF. Try opening it externally.")
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = Color(0xFF0F766E),
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            isPdf -> "PDF Preview"
                            isImage -> "Picture Preview"
                            else -> "Attached File"
                        },
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = when {
                            isPdf -> "Scroll in-app. Open externally only when needed."
                            isImage -> "View the picture in-app. Open externally only when needed."
                            else -> fileName.ifBlank { "Open this attachment externally." }
                        },
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(
                    onClick = {
                        openFileExternally(ctx, fileUrl)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open file",
                        tint = Color(0xFF0F766E),
                    )
                }
            }
            if (isPdf) {
                PdfPreviewContent(
                    state = pdfState,
                    fileUrl = fileUrl,
                    onOpenExternally = { openFileExternally(ctx, fileUrl) },
                )
            } else if (isImage) {
                ImagePreviewContent(
                    fileUrl = fileUrl,
                    onOpenExternally = { openFileExternally(ctx, fileUrl) },
                )
            } else {
                PdfPreviewFallback(
                    message = "In-app preview is available for PDF and picture files. Open this file externally.",
                    onOpenExternally = { openFileExternally(ctx, fileUrl) },
                )
            }
        }
    }
}

@Composable
internal fun PdfPreviewContent(
    state: PdfPreviewState,
    fileUrl: String,
    onOpenExternally: () -> Unit,
) {
    when (state) {
        PdfPreviewState.Loading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = PanthraaBlue)
                Text("Loading PDF preview", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
        is PdfPreviewState.Ready -> {
            if (state.pages.isEmpty()) {
                PdfPreviewFallback(
                    message = "This PDF has no previewable pages.",
                    onOpenExternally = onOpenExternally,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(state.pages) { index, page ->
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                text = "Page ${index + 1}",
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                            )
                            Image(
                                bitmap = page.asImageBitmap(),
                                contentDescription = "PDF page ${index + 1}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.FillWidth,
                            )
                        }
                    }
                }
            }
        }
        is PdfPreviewState.Error -> PdfPreviewFallback(
            message = state.message,
            onOpenExternally = onOpenExternally,
        )
        PdfPreviewState.NotPdf -> PdfPreviewFallback(
            message = "Preview is available for PDF files only.",
            onOpenExternally = onOpenExternally,
        )
    }
}

@Composable
internal fun ImagePreviewContent(
    fileUrl: String,
    onOpenExternally: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        coil.compose.SubcomposeAsyncImage(
            model = fileUrl,
            contentDescription = "Picture preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = PanthraaBlue)
                    Text("Loading picture preview", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            },
            error = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Picture preview unavailable.", color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    TextButton(onClick = onOpenExternally) {
                        Text("Open", color = PanthraaBlue, fontWeight = FontWeight.Bold)
                    }
                }
            },
        )
    }
}

@Composable
internal fun PdfPreviewFallback(
    message: String,
    onOpenExternally: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onOpenExternally) {
            Text("Open", color = PanthraaBlue, fontWeight = FontWeight.Bold)
        }
    }
}

internal sealed interface PdfPreviewState {
    data object Loading : PdfPreviewState
    data object NotPdf : PdfPreviewState
    data class Ready(val pages: List<Bitmap>) : PdfPreviewState
    data class Error(val message: String) : PdfPreviewState
}

internal fun openFileExternally(context: android.content.Context, fileUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(fileUrl)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

internal fun isPdfFile(fileUrl: String, fileName: String): Boolean {
    val cleanUrl = fileUrl.substringBefore("?").lowercase(Locale.getDefault())
    val cleanName = fileName.substringBefore("?").lowercase(Locale.getDefault())
    return cleanUrl.endsWith(".pdf") || cleanName.endsWith(".pdf") || cleanUrl.contains(".pdf/")
}

internal fun isImageFile(fileUrl: String, fileName: String): Boolean {
    val cleanUrl = fileUrl.substringBefore("?").lowercase(Locale.getDefault())
    val cleanName = fileName.substringBefore("?").lowercase(Locale.getDefault())
    val extensions = listOf(".gif", ".heic", ".heif", ".jpeg", ".jpg", ".png", ".webp")
    return extensions.any { extension ->
        cleanUrl.endsWith(extension) || cleanName.endsWith(extension) || cleanUrl.contains("$extension/")
    }
}

internal fun renderPdfPreviewPages(context: android.content.Context, fileUrl: String): List<Bitmap> {
    val pdfFile = cachedPdfFile(context, fileUrl)
    ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
        PdfRenderer(descriptor).use { renderer ->
            return (0 until renderer.pageCount).map { pageIndex ->
                renderer.openPage(pageIndex).use { page ->
                    val targetWidth = 960
                    val scale = targetWidth.toFloat() / page.width.toFloat()
                    val targetHeight = (page.height * scale).toInt().coerceAtLeast(1)
                    Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888).also { bitmap ->
                        bitmap.eraseColor(android.graphics.Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    }
                }
            }
        }
    }
}

internal fun cachedPdfFile(context: android.content.Context, fileUrl: String): File {
    val cacheDir = File(context.applicationContext.cacheDir, "pdf_previews").apply { mkdirs() }
    val cached = File(cacheDir, "${sha256(fileUrl)}.pdf")
    if (cached.exists() && cached.length() > 0L) return cached

    val temp = File(cacheDir, "${cached.name}.tmp")
    val connection = URL(fileUrl).openConnection() as HttpURLConnection
    connection.connectTimeout = 15_000
    connection.readTimeout = 30_000
    try {
        connection.inputStream.use { input ->
            temp.outputStream().use { output -> input.copyTo(output) }
        }
        if (temp.length() <= 0L) error("Downloaded PDF is empty.")
        if (cached.exists()) cached.delete()
        if (!temp.renameTo(cached)) {
            temp.copyTo(cached, overwrite = true)
            temp.delete()
        }
        return cached
    } finally {
        connection.disconnect()
        if (temp.exists()) temp.delete()
    }
}

internal fun sha256(value: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

@Composable
internal fun AssignmentSubmissionCard(
    submission: AssignmentSubmission,
    onClick: () -> Unit,
) {
    val isGraded = submission.score != null
    val scoreTone = if (isGraded) Color(0xFF0F766E) else Color(0xFFB91C1C)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Avatar(
                imageUrl = submission.photoUrl,
                name = submission.studentName,
                modifier = Modifier.size(44.dp),
                placeholderColor = Color(0xFFE5E7EB),
                initialFontSize = 15.sp,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = submission.studentName.ifBlank { "Student" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${submission.idNumber.ifBlank { "No ID" }} - ${displaySubmissionDateTime(submission.submittedAt)}",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = submission.responseText.ifBlank {
                        if (!submission.submissionFileUrl.isNullOrBlank()) "File attached" else "No notes"
                    },
                    color = Color(0xFF334155),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(scoreTone.copy(alpha = 0.12f))
                    .padding(horizontal = 9.dp, vertical = 6.dp),
            ) {
                Text(
                    text = submission.score?.let { "$it/${submission.targetPoints.coerceAtLeast(1)}" }
                        ?: if (submission.editAttempts > 0) "Grade cleared" else "Needs score",
                    color = scoreTone,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open submission",
                tint = PanthraaBlue,
            )
        }
    }
}

@Composable
internal fun AssignmentSubmissionDetailDialog(
    submission: AssignmentSubmission,
    isSavingScore: Boolean,
    internetRequired: Boolean,
    onSaveScore: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var scoreDraft by remember(submission.id, submission.score) {
        mutableStateOf(submission.score?.toString().orEmpty())
    }
    val scoreValue = scoreDraft.toIntOrNull()
    val targetPoints = submission.targetPoints.coerceAtLeast(1)
    val scoreValid = scoreValue != null && scoreValue in 0..targetPoints
    val scoreChanged = scoreValid && scoreValue != submission.score
    val previewRaw = scoreValue?.let { pointsToPercent(it, targetPoints) }
    val previewConverted = previewRaw?.let { convertRawToFinal(it) }
    val scoreBlocker = when {
        internetRequired -> SubmissionBlocker(
            NoticeType.OFFLINE,
            "Internet required",
            "Connect to the internet to save this score.",
        )
        isSavingScore -> SubmissionBlocker(
            NoticeType.INFO,
            "Saving score",
            "Please wait while the score is being saved.",
        )
        scoreDraft.isBlank() -> SubmissionBlocker(
            NoticeType.WARNING,
            "Score required",
            "Enter earned points from 0 to $targetPoints.",
        )
        !scoreValid -> SubmissionBlocker(
            NoticeType.ERROR,
            "Score out of range",
            "Score must be between 0 and $targetPoints.",
        )
        !scoreChanged -> SubmissionBlocker(
            NoticeType.INFO,
            "No score changes",
            "Change the earned points before saving.",
        )
        else -> null
    }

    AlertDialog(
        onDismissRequest = { if (!isSavingScore) onDismiss() },
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = submission.studentName.ifBlank { "Student submission" },
                subtitle = submission.idNumber.ifBlank { "No ID number" },
                icon = Icons.Filled.People,
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Submitted: ${displaySubmissionDateTime(submission.submittedAt).ifBlank { "Not available" }}",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
                if (submission.editAttempts > 0) {
                    Text(
                        text = "Edited ${submission.editAttempts} time(s)",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
                OutlinedTextField(
                    value = scoreDraft,
                    onValueChange = { input -> scoreDraft = input.filter { it.isDigit() }.take(4) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Earned points") },
                    placeholder = { Text("0-$targetPoints") },
                    singleLine = true,
                    enabled = !internetRequired && !isSavingScore,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                Text(
                    text = submission.score?.let {
                        "Current: $it/$targetPoints points - ${formatGradeNumber(submission.rawPercent)} raw - ${formatGradeNumber(submission.convertedGrade)} final"
                    } ?: "No score yet. Target: $targetPoints points.",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
                if (scoreValid && previewRaw != null && previewConverted != null) {
                    Text(
                        text = "Preview: ${scoreValue ?: 0}/$targetPoints - ${formatGradeNumber(previewRaw)} raw - ${formatGradeNumber(previewConverted)} final",
                        color = gradeTone(previewConverted),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                    )
                }
                scoreBlocker?.let { reason ->
                    PanthraaStatusNotice(
                        type = reason.type,
                        title = reason.title,
                        message = reason.message,
                    )
                }
                submission.submissionFileUrl?.let { fileUrl ->
                    AssignmentFilePreview(
                        fileUrl = fileUrl,
                        fileName = "Submitted file",
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                ) {
                    Text(
                        text = submission.responseText.ifBlank { "No answer text." },
                        color = Color(0xFF0F172A),
                        lineHeight = 18.sp,
                    )
                }
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = if (isSavingScore) "Saving..." else "Save score",
                onClick = {
                    if (scoreBlocker == null) {
                        scoreValue?.let(onSaveScore)
                    }
                },
                enabled = scoreBlocker == null,
                color = Color(0xFFEAB308),
            )
        },
        dismissButton = {
            DialogCancelButton(text = "Close", onClick = onDismiss, enabled = !isSavingScore)
        },
    )
}

@Composable
internal fun AssignmentDetailHeader(
    title: String,
    onBack: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp, end = 8.dp),
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        trailing?.invoke()
    }
}

@Composable
internal fun AssignmentInstructionCard(assignment: ClassAssignment, materialViewRemainingMillis: Long? = null) {
    val isMaterial = assignment.assignmentType.equals("material", ignoreCase = true)
    if (isMaterial) {
        MaterialResourceCard(assignment = assignment, remainingMillis = materialViewRemainingMillis)
    } else {
        ScoredWorkInstructionCard(assignment = assignment)
    }
}

@Composable
internal fun MaterialResourceCard(assignment: ClassAssignment, remainingMillis: Long? = null) {
    val typeAccent = assignmentTypeColor(assignment.assignmentType)
    val totalMillis = 10_000L
    val isTimerRunning = remainingMillis != null && remainingMillis > 0L
    val secondsLeft = if (isTimerRunning) ((remainingMillis + 999L) / 1000L).coerceIn(1L, 10L) else 0L
    val progress = if (isTimerRunning) {
        (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)),
        border = BorderStroke(1.dp, typeAccent.copy(alpha = 0.24f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(typeAccent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(assignmentTypeIconRes(assignment.assignmentType)),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(typeAccent),
                        modifier = Modifier.size(30.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = assignment.title.ifBlank { "Untitled material" },
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Learning material",
                        color = typeAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
            }
            if (assignment.instructions.isNotBlank()) {
                Text(
                    text = assignment.instructions,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (isTimerRunning) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(typeAccent.copy(alpha = 0.10f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "$secondsLeft second${if (secondsLeft == 1L) "" else "s"} left to mark as viewed",
                            color = typeAccent,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                        )
                    }
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = typeAccent,
                        trackColor = typeAccent.copy(alpha = 0.16f),
                    )
                }
            } else if (assignment.hasSubmitted) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(typeAccent.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Viewed",
                        color = typeAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                    )
                }
            } else if (remainingMillis == 0L) {
                // Disappear
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(typeAccent.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Stay 10 seconds to mark as viewed",
                        color = typeAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                    )
                }
            }
            if (!assignment.createdAt.isNullOrBlank()) {
                Text(
                    text = "Uploaded ${displayClassDate(assignment.createdAt)}",
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
internal fun ScoredWorkInstructionCard(assignment: ClassAssignment) {
    val typeAccent = assignmentTypeColor(assignment.assignmentType)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(typeAccent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(assignmentTypeIconRes(assignment.assignmentType)),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(typeAccent),
                        modifier = Modifier.size(30.dp),
                    )
                }
                Text(
                    text = assignment.title.ifBlank { "Untitled ${assignmentTypeTitle(assignment.assignmentType).lowercase()}" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f),
                )
            }
            Text(
                text = assignment.instructions.ifBlank { "No instructions provided." },
                color = Color(0xFF334155),
                lineHeight = 18.sp,
                style = MaterialTheme.typography.bodyMedium,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(typeAccent.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "${assignmentCategoryLabel(assignment.category)} - ${assignment.targetPoints.coerceAtLeast(1)} points",
                    color = typeAccent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(PanthraaBlue.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Submit as ${submissionFormatLabel(assignment.submissionFormat)}",
                    color = PanthraaBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
            }
            if (!assignment.createdAt.isNullOrBlank()) {
                Text(
                    text = displayClassDate(assignment.createdAt),
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

internal fun nextClassSessionDate(
    today: LocalDate,
    scheduleDays: List<String>,
): LocalDate? {
    val dayValues = scheduleDays.mapNotNull { day ->
        when (day.lowercase(Locale.getDefault())) {
            "monday" -> DayOfWeek.MONDAY
            "tuesday" -> DayOfWeek.TUESDAY
            "wednesday" -> DayOfWeek.WEDNESDAY
            "thursday" -> DayOfWeek.THURSDAY
            "friday" -> DayOfWeek.FRIDAY
            "saturday" -> DayOfWeek.SATURDAY
            "sunday" -> DayOfWeek.SUNDAY
            else -> null
        }
    }.distinct()
    if (dayValues.isEmpty()) return null
    val todayValue = today.dayOfWeek
    val next = dayValues.minBy { (it.value - todayValue.value + 7) % 7 }
    return today.plusDays(((next.value - todayValue.value + 7) % 7).toLong())
}

internal fun normalizeTimeToSeconds(raw: String?): String? {
    return runCatching {
        LocalTime.parse(raw).let { String.format("%02d:%02d:00", it.hour, it.minute) }
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UploadAssignmentDialog(
    isCreating: Boolean,
    initialAssignment: ClassAssignment? = null,
    scheduleDays: List<String> = emptyList(),
    scheduleStartTime: String? = null,
    scheduleEndTime: String? = null,
    onDismiss: () -> Unit,
    onUpload: (String, String, String, Int, String?, String?, String?, String?, String, String, Boolean, Boolean, Uri?) -> Unit,
) {
    val isEditMode = initialAssignment != null
    var title by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.title.orEmpty()) }
    var instructions by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.instructions.orEmpty()) }
    var category by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.category ?: "lecture") }
    var assignmentType by remember(initialAssignment?.id) {
        mutableStateOf(
            when (initialAssignment?.assignmentType?.lowercase()) {
                "material" -> "material"
                "quiz" -> "quiz"
                "exam" -> "exam"
                else -> "task"
            },
        )
    }
    var submissionFormat by remember(initialAssignment?.id) {
        mutableStateOf(normalizedSubmissionFormat(initialAssignment?.submissionFormat))
    }
    var requiresFile by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.requiresFile ?: true) }
    var allowComments by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.allowComments ?: true) }
    var targetPointsDraft by remember(initialAssignment?.id) { mutableStateOf((initialAssignment?.targetPoints ?: 100).toString()) }
    val targetPoints = targetPointsDraft.toIntOrNull()
    val targetPointsValid = targetPoints != null && targetPoints > 0

    var startDate by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.startDate) }
    var endDate by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.endDate) }
    var startTime by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.startTime) }
    var endTime by remember(initialAssignment?.id) { mutableStateOf(initialAssignment?.endTime) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember(initialAssignment?.id) {
        mutableStateOf(if (initialAssignment?.fileUrl != null) "Current PDF attached" else null)
    }
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && it.moveToFirst()) {
                    selectedFileName = it.getString(nameIndex)
                }
            }
            if (selectedFileName == null) {
                selectedFileName = uri.lastPathSegment
            }
        }
    }

    val isMaterial = assignmentType.equals("material", ignoreCase = true)
    val typeAccent = assignmentTypeColor(assignmentType)
    val canUpload = title.trim().isNotEmpty() && (isMaterial || targetPointsValid) && !isCreating

    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    val timeDisplayFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

    if (showStartDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.let {
                runCatching { LocalDate.parse(it).toEpochDay() * 86400000L }.getOrNull()
            },
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        startDate = java.time.Instant.ofEpochMilli(millis).atZone(PhilippineZoneId).toLocalDate().toString()
                    }
                    showStartDatePicker = false
                    showStartTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.let {
                runCatching { LocalDate.parse(it).toEpochDay() * 86400000L }.getOrNull()
            },
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        endDate = java.time.Instant.ofEpochMilli(millis).atZone(PhilippineZoneId).toLocalDate().toString()
                    }
                    showEndDatePicker = false
                    showEndTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = state)
        }
    }

    if (showStartTimePicker) {
        val initialHour = startTime?.let {
            runCatching { LocalTime.parse(it).hour }.getOrNull()
        } ?: 8
        val initialMinute = startTime?.let {
            runCatching { LocalTime.parse(it).minute }.getOrNull()
        } ?: 0
        val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = false)
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = DialogSurface,
            title = { DialogHeader(title = "Start time", subtitle = "Set when this activity opens.", icon = Icons.Filled.Event) },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    startTime = String.format("%02d:%02d:00", state.hour, state.minute)
                    showStartTimePicker = false
                    showEndDatePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("Cancel") }
            },
        )
    }

    if (showEndTimePicker) {
        val initialHour = endTime?.let {
            runCatching { LocalTime.parse(it).hour }.getOrNull()
        } ?: 17
        val initialMinute = endTime?.let {
            runCatching { LocalTime.parse(it).minute }.getOrNull()
        } ?: 0
        val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = false)
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = DialogSurface,
            title = { DialogHeader(title = "End time", subtitle = "Set the deadline for this activity.", icon = Icons.Filled.Event) },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    endTime = String.format("%02d:%02d:00", state.hour, state.minute)
                    showEndTimePicker = false
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("Cancel") }
            },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = if (isEditMode) "Edit upload" else "Upload",
                subtitle = if (isMaterial) {
                    if (isEditMode) "Update this learning material." else "Add a learning material."
                } else {
                    if (isEditMode) "Update this activity." else "Add an activity for this class."
                },
                iconRes = assignmentTypeIconRes(assignmentType),
                tint = typeAccent,
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Type",
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("material", "task", "quiz", "exam").forEach { type ->
                        AssignmentTypeChip(
                            assignmentType = type,
                            label = assignmentTypeLabel(type),
                            selected = assignmentType == type,
                            color = assignmentTypeColor(type),
                            enabled = !isCreating,
                            onClick = { assignmentType = type },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (isMaterial) "Title" else "Title") },
                    singleLine = true,
                    enabled = !isCreating,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )

                if (isMaterial || isEditMode) {
                    Text(
                        text = if (isEditMode) "PDF file (optional replacement)" else "Upload PDF file",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = 1.dp,
                                color = if (selectedFileUri != null) typeAccent.copy(alpha = 0.4f) else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .background(if (selectedFileUri != null) typeAccent.copy(alpha = 0.05f) else Color.White)
                            .clickable(enabled = !isCreating) { filePickerLauncher.launch("application/pdf") }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selectedFileUri != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 12.dp),
                            ) {
                                Image(
                                    painter = painterResource(assignmentTypeIconRes(assignmentType)),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(typeAccent),
                                    modifier = Modifier.size(28.dp),
                                )
                                Text(
                                    text = selectedFileName ?: "Selected file",
                                    color = typeAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = 8.dp).weight(1f),
                                )
                                IconButton(
                                    onClick = {
                                        selectedFileUri = null
                                        selectedFileName = null
                                    },
                                    modifier = Modifier.size(24.dp),
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Remove file", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(assignmentTypeIconRes(assignmentType)),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color(0xFF94A3B8)),
                                    modifier = Modifier.size(30.dp),
                                )
                                Text(
                                text = if (isEditMode && initialAssignment?.fileUrl != null) {
                                    "Tap to replace the current PDF"
                                } else {
                                    "Tap to select a PDF file"
                                },
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (isMaterial) "Description (optional)" else "Instructions") },
                    minLines = 3,
                    enabled = !isCreating,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )

                if (!isMaterial) {
                    Text(
                        text = "Student submission format",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                    SubmissionFormatOptions.chunked(3).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowOptions.forEach { option ->
                                AssignmentCategoryChip(
                                    label = option.label,
                                    selected = submissionFormat == option.value,
                                    enabled = !isCreating,
                                    onClick = { submissionFormat = option.value },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(3 - rowOptions.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Text(
                        text = "Category",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AssignmentCategoryChip(
                            label = "Lecture",
                            selected = category == "lecture",
                            enabled = !isCreating,
                            onClick = { category = "lecture" },
                            modifier = Modifier.weight(1f),
                        )
                        AssignmentCategoryChip(
                            label = "Laboratory",
                            selected = category == "laboratory",
                            enabled = !isCreating,
                            onClick = { category = "laboratory" },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    OutlinedTextField(
                        value = targetPointsDraft,
                        onValueChange = { input -> targetPointsDraft = input.filter { it.isDigit() }.take(4) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Target points") },
                        placeholder = { Text("100 points") },
                        singleLine = true,
                        enabled = !isCreating,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Require file submission",
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                        Switch(
                            checked = requiresFile,
                            onCheckedChange = { requiresFile = it },
                            enabled = !isCreating,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF0034DE),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1),
                            ),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Allow student comments",
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                            )
                            Text(
                                text = if (isMaterial) {
                                    "Students can discuss this material at the bottom."
                                } else {
                                    "Students can ask questions under this activity."
                                },
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                lineHeight = 12.sp,
                            )
                        }
                        Switch(
                            checked = allowComments,
                            onCheckedChange = { allowComments = it },
                            enabled = !isCreating,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF0034DE),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCBD5E1),
                            ),
                        )
                    }

                    Text(
                        text = "Time window (optional)",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )

                    val currentDate = LocalDate.now(PhilippineZoneId)
                    val hasSchedule = scheduleDays.isNotEmpty() &&
                        scheduleStartTime != null && scheduleEndTime != null
                    val sessionStartTime = normalizeTimeToSeconds(scheduleStartTime) ?: "08:00:00"
                    val sessionEndTime = normalizeTimeToSeconds(scheduleEndTime) ?: "23:59:00"
                    val sessionDate = remember(scheduleDays) { nextClassSessionDate(currentDate, scheduleDays) }
                    val sessionActive = sessionDate != null &&
                        startDate == sessionDate.toString() &&
                        startTime == sessionStartTime &&
                        endTime == sessionEndTime
                    if (hasSchedule && sessionDate != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            AssignmentCategoryChip(
                                label = "Next class session",
                                selected = sessionActive,
                                enabled = !isCreating,
                                onClick = {
                                    startDate = sessionDate.toString()
                                    startTime = sessionStartTime
                                    endDate = sessionDate.toString()
                                    endTime = sessionEndTime
                                },
                                modifier = Modifier.weight(1f),
                            )
                            AssignmentCategoryChip(
                                label = "All-day",
                                selected = !sessionActive &&
                                    endDate == currentDate.plusDays(0).toString() &&
                                    endTime == "23:59:00",
                                enabled = !isCreating,
                                onClick = {
                                    startDate = currentDate.toString()
                                    startTime = "08:00:00"
                                    endDate = currentDate.toString()
                                    endTime = "23:59:00"
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        AssignmentCategoryChip(
                            label = "In 1 week",
                            selected = !sessionActive && endDate == currentDate.plusDays(7).toString() &&
                                endTime == "23:59:00" && startTime == "08:00:00",
                            enabled = !isCreating,
                            onClick = {
                                startDate = currentDate.toString()
                                startTime = "08:00:00"
                                endDate = currentDate.plusDays(7).toString()
                                endTime = "23:59:00"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                        )
                    } else {
                        val presetOptions = listOf(
                            0L to "Due today",
                            1L to "Due tomorrow",
                            3L to "In 3 days",
                            7L to "In 1 week",
                        )
                        presetOptions.chunked(2).forEach { rowOptions ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                rowOptions.forEach { (days, label) ->
                                    AssignmentCategoryChip(
                                        label = label,
                                        selected = endDate == currentDate.plusDays(days).toString() &&
                                            endTime == "23:59:00" && startTime == "08:00:00",
                                        enabled = !isCreating,
                                        onClick = {
                                            startDate = currentDate.toString()
                                            startTime = "08:00:00"
                                            endDate = currentDate.plusDays(days).toString()
                                            endTime = "23:59:00"
                                        },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                repeat(2 - rowOptions.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    val genericPresetActive = listOf(
                        0L, 1L, 3L, 7L,
                    ).any { days ->
                        endDate == currentDate.plusDays(days).toString() &&
                            endTime == "23:59:00" && startTime == "08:00:00"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AssignmentCategoryChip(
                            label = "Custom start/end",
                            selected = !sessionActive && !genericPresetActive && (startDate != null || endDate != null),
                            enabled = !isCreating,
                            onClick = { showStartDatePicker = true },
                            modifier = Modifier.weight(1f),
                        )
                        if (startDate != null || endDate != null) {
                            AssignmentCategoryChip(
                                label = "Clear",
                                selected = false,
                                enabled = !isCreating,
                                onClick = {
                                    startDate = null
                                    startTime = null
                                    endDate = null
                                    endTime = null
                                },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    if (startDate != null || endDate != null) {
                        val startLabel = listOfNotNull(
                            startDate?.let { runCatching { LocalDate.parse(it).format(dateFormatter) }.getOrNull() },
                            startTime?.let { runCatching { LocalTime.parse(it).format(timeDisplayFormatter) }.getOrNull() },
                        ).joinToString(" ")
                        val endLabel = listOfNotNull(
                            endDate?.let { runCatching { LocalDate.parse(it).format(dateFormatter) }.getOrNull() },
                            endTime?.let { runCatching { LocalTime.parse(it).format(timeDisplayFormatter) }.getOrNull() },
                        ).joinToString(" ")
                        Text(
                            text = buildString {
                                if (startDate != null) append("Opens $startLabel")
                                if (startDate != null && endDate != null) append("  ")
                                if (endDate != null) append("Due $endLabel")
                            },
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                        )
                    }
                }
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = if (isCreating) {
                    if (isEditMode) "Saving..." else "Uploading..."
                } else {
                    if (isEditMode) "Save changes" else "Upload"
                },
                onClick = {
                    val points = if (isMaterial) 100 else (targetPoints ?: 100)
                    val cleanStartDate = if (isMaterial) null else startDate
                    val cleanEndDate = if (isMaterial) null else endDate
                    val cleanStartTime = if (isMaterial) null else startTime
                    val cleanEndTime = if (isMaterial) null else endTime
                    onUpload(title, instructions, category, points, cleanStartDate, cleanEndDate, cleanStartTime, cleanEndTime, assignmentType, submissionFormat, requiresFile, allowComments, selectedFileUri)
                },
                enabled = canUpload,
            )
        },
        dismissButton = {
            DialogCancelButton(onClick = onDismiss, enabled = !isCreating)
        },
    )
}

@Composable
internal fun AssignmentCategoryChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) PanthraaBlue else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) PanthraaBlue else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(14.dp),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color(0xFF334155),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
internal fun AssignmentTypeChip(
    assignmentType: String,
    label: String,
    selected: Boolean,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) color else Color.White)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) color else color.copy(alpha = 0.30f),
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(assignmentTypeIconRes(assignmentType)),
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (selected) Color.White else color),
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                color = if (selected) Color.White else color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

internal fun parseServerDateTimeInPhilippines(rawDate: String?): ZonedDateTime? {
    val clean = rawDate.orEmpty()
    if (clean.isBlank()) return null
    return runCatching {
        ZonedDateTime.parse(clean).withZoneSameInstant(PhilippineZoneId)
    }.recoverCatching {
        LocalDateTime.parse(clean.take(19))
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(PhilippineZoneId)
    }.getOrNull()
}

internal fun displayClassDate(rawDate: String?): String {
    val clean = rawDate.orEmpty()
    if (clean.isBlank()) return ""
    if (clean.contains("T")) {
        parseServerDateTimeInPhilippines(clean)?.let {
            return it.toLocalDate().format(ClassCardDateFormatter)
        }
    }
    return runCatching {
        LocalDate.parse(clean.take(10)).format(ClassCardDateFormatter)
    }.getOrDefault(clean.take(10))
}

internal fun displayAssignmentCreatedAt(rawDate: String?): String {
    val clean = rawDate.orEmpty()
    if (clean.isBlank()) return ""
    return parseServerDateTimeInPhilippines(clean)
        ?.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.getDefault()))
        ?: displayClassDate(clean)
}

internal fun List<ClassAssignment>.sortedByNewestUpload(): List<ClassAssignment> {
    return sortedWith(
        compareByDescending<ClassAssignment> { assignmentCreatedAtMillis(it.createdAt) }
            .thenByDescending { it.id },
    )
}

internal fun assignmentCreatedAtMillis(rawDate: String?): Long {
    val clean = rawDate.orEmpty()
    if (clean.isBlank()) return Long.MIN_VALUE
    parseServerDateTimeInPhilippines(clean)?.let { return it.toInstant().toEpochMilli() }
    return runCatching {
        LocalDate.parse(clean.take(10))
            .atStartOfDay(PhilippineZoneId)
            .toInstant()
            .toEpochMilli()
    }.getOrDefault(Long.MIN_VALUE)
}

internal fun isAssignmentLocked(
    assignment: ClassAssignment,
    currentDate: LocalDate = LocalDate.now(PhilippineZoneId),
    currentTime: LocalTime = LocalTime.now(PhilippineZoneId),
): Boolean {
    assignment.startDate?.let { startDateStr ->
        val startDate = runCatching { LocalDate.parse(startDateStr) }.getOrNull() ?: return false
        if (currentDate < startDate) return true
        if (currentDate == startDate) {
            assignment.startTime?.let { startTimeStr ->
                val startTime = runCatching { LocalTime.parse(startTimeStr) }.getOrNull() ?: return false
                if (currentTime < startTime) return true
            }
        }
    }
    return false
}

internal fun isAssignmentExpired(
    assignment: ClassAssignment,
    currentDate: LocalDate = LocalDate.now(PhilippineZoneId),
    currentTime: LocalTime = LocalTime.now(PhilippineZoneId),
): Boolean {
    assignment.endDate?.let { endDateStr ->
        val endDate = runCatching { LocalDate.parse(endDateStr) }.getOrNull() ?: return false
        if (currentDate > endDate) return true
        if (currentDate == endDate) {
            assignment.endTime?.let { endTimeStr ->
                val endTime = runCatching { LocalTime.parse(endTimeStr) }.getOrNull() ?: return false
                if (currentTime > endTime) return true
            }
        }
    }
    return false
}

internal fun formatAssignmentClockTime(rawTime: String): String {
    return runCatching {
        LocalTime.parse(rawTime).format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
    }.getOrDefault(rawTime)
}

internal fun assignmentTimeWindowLabel(assignment: ClassAssignment): String? {
    val startDate = assignment.startDate
    val endDate = assignment.endDate
    val startText = startDate?.let { displayClassDate(it) }
    val endText = endDate?.let { displayClassDate(it) }
    val startTime = assignment.startTime?.let(::formatAssignmentClockTime)
    val endTime = assignment.endTime?.let(::formatAssignmentClockTime)

    if (startDate != null && endDate != null) {
        if (startDate == endDate) {
            val timeText = when {
                startTime != null && endTime != null -> " $startTime - $endTime"
                startTime != null -> " from $startTime"
                endTime != null -> " until $endTime"
                else -> ""
            }
            return "${startText.orEmpty()}$timeText".trim()
        }
        val start = listOfNotNull(startText, startTime).joinToString(" ")
        val end = listOfNotNull(endText, endTime).joinToString(" ")
        return "$start - $end".trim()
    }
    if (startDate != null) {
        return "Opens ${listOfNotNull(startText, startTime).joinToString(" ")}".trim()
    }
    if (endDate != null) {
        return "Due ${listOfNotNull(endText, endTime).joinToString(" ")}".trim()
    }
    return null
}

internal val SubmissionDateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.getDefault())

internal fun displaySubmissionDateTime(rawDate: String?): String {
    return parseServerDateTimeInPhilippines(rawDate)
        ?.format(SubmissionDateTimeFormatter)
        ?: rawDate.orEmpty().takeIf { it.isNotBlank() }.orEmpty()
}
