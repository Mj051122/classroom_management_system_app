package com.myapplication.panthraa.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myapplication.panthraa.R
import com.myapplication.panthraa.data.OfflineImageCache
import com.myapplication.panthraa.model.AppUser
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
import com.myapplication.panthraa.model.TaskReminder
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlin.math.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private sealed class BottomTab(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int,
) {
    data object Home : BottomTab("home", "Dashboard", R.drawable.ic_nav_dashboard)
    data object Classes : BottomTab("classes", "Classes", R.drawable.ic_nav_classes)
    data object Tasks : BottomTab("tasks", "News Feed", R.drawable.ic_nav_newsfeed)
}

@Composable
private fun PanthraaSnackbarHost(
    hostState: SnackbarHostState,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
    ) { snackbarData ->
        PanthraaSnackbar(snackbarData = snackbarData)
    }
}

@Composable
private fun PanthraaSnackbar(
    snackbarData: SnackbarData,
) {
    val message = snackbarData.visuals.message
    val title = snackbarTitleFor(message)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { snackbarData.dismiss() },
            ),
        color = Color(0xFFFEFEFF).copy(alpha = 0.98f),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.80f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = message,
                color = Color(0xFF475569),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun snackbarTitleFor(message: String): String {
    val normalized = message.lowercase(Locale.getDefault())
    return when {
        listOf("required", "failed", "error", "offline", "invalid").any { it in normalized } ->
            "Needs attention"
        listOf("saved", "created", "posted", "updated", "approved", "sent", "recorded", "deleted").any { it in normalized } ->
            "Done"
        else -> "Notification"
    }
}

@Composable
private fun PanthraaBottomBar(
    tabs: List<BottomTab>,
    currentRoute: String?,
    badgeCounts: Map<String, Int>,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFFCFDFF).copy(alpha = 0.95f),
            shadowElevation = 16.dp,
            border = BorderStroke(1.dp, Color(0xFFE4EAF3).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(28.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 7.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                tabs.forEach { tab ->
                    PanthraaBottomBarItem(
                        tab = tab,
                        selected = (currentRoute ?: BottomTab.Home.route) == tab.route,
                        badgeCount = badgeCounts[tab.route] ?: 0,
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PanthraaBottomBarItem(
    tab: BottomTab,
    selected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = PanthraaBlue
    val inactiveColor = Color(0xFF94A3B8)
    val contentColor by animateColorAsState(
        targetValue = if (selected) activeColor else inactiveColor,
        animationSpec = tween(300),
        label = "tab_color",
    )
    val bgAlpha by animateFloatAsState(
        targetValue = if (selected) 0.12f else 0f,
        animationSpec = tween(300),
        label = "tab_bg",
    )
    val showBadge = !selected && badgeCount > 0

    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(activeColor.copy(alpha = bgAlpha))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = tab.label,
                modifier = Modifier.size(22.dp),
                tint = contentColor,
            )
            if (showBadge) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .offset(x = 1.dp, y = (-1).dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE11D48))
                )
            }
        }
        Text(
            text = tab.label,
            modifier = Modifier.padding(top = 2.dp),
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun MainScreen(
    currentUser: AppUser,
    networkStatus: ConnectivityStatus,
    startedOffline: Boolean,
    openScheduleSignal: Int = 0,
    onLogout: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val tabs = listOf(BottomTab.Home, BottomTab.Classes, BottomTab.Tasks)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var pendingProfileImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var pendingDashboardClassOpen by remember { mutableStateOf<StudentClass?>(null) }
    var pendingDashboardAssignmentOpen by remember { mutableStateOf<PendingAssignment?>(null) }
    var pendingDashboardProfessorYearOpen by remember { mutableStateOf<String?>(null) }
    var pendingDashboardProfessorClassOpen by remember { mutableStateOf<ProfessorClass?>(null) }
    var pendingDashboardProfessorUploadOpen by remember { mutableStateOf(false) }
    var dashboardRootResetToken by remember { mutableStateOf(0) }
    var classesRootResetToken by remember { mutableStateOf(0) }
    var tasksRootResetToken by remember { mutableStateOf(0) }
    var showLogoutConfirm by remember(currentUser.id) { mutableStateOf(false) }
    var barVisible by remember { mutableStateOf(true) }
    val barScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source == NestedScrollSource.UserInput) {
                    if (available.y < -5f) {
                        barVisible = false
                    } else if (available.y > 5f) {
                        barVisible = true
                    }
                }
                return Offset.Zero
            }
        }
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            pendingProfileImageUri = uri
        }
    }
    fun navigateFromDashboard(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = false
        }
    }

    fun resetToDashboardRoot() {
        pendingDashboardClassOpen = null
        pendingDashboardAssignmentOpen = null
        pendingDashboardProfessorYearOpen = null
        pendingDashboardProfessorClassOpen = null
        pendingDashboardProfessorUploadOpen = false
        dashboardRootResetToken += 1
        navController.navigate(BottomTab.Home.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    if (pendingProfileImageUri != null) {
        ProfileImageCropDialog(
            uri = pendingProfileImageUri!!,
            onDismiss = { pendingProfileImageUri = null },
            onSave = { uri ->
                pendingProfileImageUri = null
                viewModel.updateProfilePicture(context, uri)
            }
        )
    }

    LaunchedEffect(currentUser.id) {
        viewModel.setCurrentUser(currentUser)
    }

    LaunchedEffect(currentUser.id, startedOffline) {
        viewModel.initializeOfflineSupport(
            context = context,
            user = currentUser,
            connectivityStatus = networkStatus,
            startedOffline = startedOffline,
        )
    }

    LaunchedEffect(networkStatus) {
        viewModel.setConnectivityStatus(context, networkStatus)
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    if (showLogoutConfirm) {
        LogoutConfirmationDialog(
            onDismiss = { showLogoutConfirm = false },
            onConfirm = {
                showLogoutConfirm = false
                onLogout()
            },
        )
    }

    Scaffold(
        snackbarHost = { PanthraaSnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().nestedScroll(barScrollConnection)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
            ) {
                OfflineReviewBanner(
                    uiState = uiState,
                )
                val screenPadding = if (barVisible) {
                    PaddingValues(bottom = innerPadding.calculateBottomPadding() + 92.dp)
                } else {
                    PaddingValues(bottom = innerPadding.calculateBottomPadding())
                }
                NavHost(
                    navController = navController,
                    startDestination = BottomTab.Home.route,
                    modifier = Modifier.weight(1f),
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None },
                ) {
            composable(BottomTab.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    innerPadding = screenPadding,
                    openScheduleSignal = openScheduleSignal,
                    onPickImage = { imagePicker.launch("image/*") },
                    onLogout = { showLogoutConfirm = true },
                    onSaveProfile = viewModel::updateProfileDetails,
                    onLoadClasses = { user -> viewModel.loadClasses(user) },
                    onLoadTaskReminders = { user -> viewModel.loadTaskReminders(context, user) },
                    onLoadPendingAssignments = { user -> viewModel.loadPendingAssignments(user) },
                    onLoadStudentGrades = { user -> viewModel.loadStudentGrades(user) },
                    onLoadProfessorGradeMonitor = { user -> viewModel.loadProfessorGradeMonitor(user) },
                    onLoadAnnouncements = { user -> viewModel.loadAnnouncements(user) },
                    onRefreshDashboard = { viewModel.refreshDashboard(context) },
                    onRefreshPendingAssignments = { user -> viewModel.refreshPendingAssignments(user) },
                    onRefreshStudentGrades = { user -> viewModel.refreshStudentGrades(user) },
                    onRefreshProfessorGradeMonitor = { user -> viewModel.refreshProfessorGradeMonitor(user) },
                    onLoadJoinRequests = viewModel::loadClassJoinRequests,
                    onRefreshJoinRequests = viewModel::refreshClassJoinRequests,
                    onApproveJoinRequest = viewModel::approveClassJoinRequest,
                    onRejectJoinRequest = viewModel::rejectClassJoinRequest,
                    onOpenClassesTab = {
                        navigateFromDashboard(BottomTab.Classes.route)
                    },
                    onOpenNewsFeed = {
                        navigateFromDashboard(BottomTab.Tasks.route)
                    },
                    onOpenPendingAssignment = { pending ->
                        val classItem = uiState.studentClasses.firstOrNull { it.id == pending.classId }
                        if (classItem != null) {
                            viewModel.preparePendingAssignment(pending)
                            pendingDashboardClassOpen = classItem
                            pendingDashboardAssignmentOpen = pending
                            navigateFromDashboard(BottomTab.Classes.route)
                        }
                    },
                    onOpenClass = { classItem ->
                        pendingDashboardClassOpen = classItem
                        navigateFromDashboard(BottomTab.Classes.route)
                    },
                    onOpenProfessorYear = { yearValue ->
                        pendingDashboardProfessorYearOpen = yearValue
                        navigateFromDashboard(BottomTab.Classes.route)
                    },
                    onOpenProfessorClass = { classItem ->
                        pendingDashboardProfessorClassOpen = classItem
                        pendingDashboardProfessorUploadOpen = false
                        navigateFromDashboard(BottomTab.Classes.route)
                    },
                    onOpenProfessorUpload = { classItem ->
                        pendingDashboardProfessorClassOpen = classItem
                        pendingDashboardProfessorUploadOpen = true
                        navigateFromDashboard(BottomTab.Classes.route)
                    },
                    dashboardRootResetToken = dashboardRootResetToken,
                )
            }
            composable(BottomTab.Classes.route) {
                ClassesScreen(
                    uiState = uiState,
                    innerPadding = screenPadding,
                    currentUser = uiState.currentUser ?: currentUser,
                    onLoadClasses = { user -> viewModel.loadClasses(user) },
                    onLoadAssignments = viewModel::loadClassAssignments,
                    onLoadClassmates = viewModel::loadClassmates,
                    onLoadProfessorStudents = viewModel::loadProfessorStudents,
                    onLoadAttendance = viewModel::loadAssignmentAttendance,
                    onLoadJoinRequests = viewModel::loadClassJoinRequests,
                    onRefreshClasses = viewModel::refreshClasses,
                    onRefreshAssignments = viewModel::refreshClassAssignments,
                    onRefreshClassmates = viewModel::refreshClassmates,
                    onRefreshProfessorStudents = viewModel::refreshProfessorStudents,
                    onRefreshAttendance = viewModel::refreshAssignmentAttendance,
                    onRefreshJoinRequests = viewModel::refreshClassJoinRequests,
                    onJoinClass = viewModel::joinClass,
                    onApproveJoinRequest = viewModel::approveClassJoinRequest,
                    onRejectJoinRequest = viewModel::rejectClassJoinRequest,
                    onCreateAssignment = { classId, title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, fileUri ->
                        viewModel.createClassAssignment(classId, title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, fileUri)
                    },
                    onUpdateAssignment = { assignmentId, title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, fileUri ->
                        viewModel.updateClassAssignment(assignmentId, title, instructions, category, targetPoints, startDate, endDate, startTime, endTime, assignmentType, submissionFormat, requiresFile, fileUri)
                    },
                    onLoadAssignmentSubmissions = viewModel::loadAssignmentSubmissions,
                    onRefreshAssignmentSubmissions = viewModel::refreshAssignmentSubmissions,
                    onRecordAttendance = viewModel::recordAttendanceByQr,
                    onGradeSubmission = viewModel::gradeAssignmentSubmission,
                    onSubmitAssignment = viewModel::submitAssignment,
                    onRecordMaterialView = viewModel::recordMaterialView,
                    onDeleteAssignment = viewModel::deleteClassAssignment,
                    classToOpenFromDashboard = pendingDashboardClassOpen,
                    onDashboardClassOpenConsumed = { pendingDashboardClassOpen = null },
                    assignmentToOpenFromDashboard = pendingDashboardAssignmentOpen,
                    onDashboardAssignmentOpenConsumed = { pendingDashboardAssignmentOpen = null },
                    professorYearToOpenFromDashboard = pendingDashboardProfessorYearOpen,
                    onDashboardProfessorYearOpenConsumed = { pendingDashboardProfessorYearOpen = null },
                    professorClassToOpenFromDashboard = pendingDashboardProfessorClassOpen,
                    openProfessorUploadFromDashboard = pendingDashboardProfessorUploadOpen,
                    onDashboardProfessorClassOpenConsumed = {
                        pendingDashboardProfessorClassOpen = null
                        pendingDashboardProfessorUploadOpen = false
                    },
                    dashboardRootResetToken = dashboardRootResetToken,
                    onCreateClass = { className, subjectCode, joinCode, yearLevel, department, section, track, coverImageUri, themeColor, scheduleDays, scheduleStartTime, scheduleEndTime ->
                        viewModel.createClass(
                            context = context,
                            className = className,
                            subjectCode = subjectCode,
                            joinCode = joinCode,
                            yearLevel = yearLevel,
                            department = department,
                            section = section,
                            track = track,
                            coverImageUri = coverImageUri,
                            themeColor = themeColor,
                            scheduleDays = scheduleDays,
                            scheduleStartTime = scheduleStartTime,
                            scheduleEndTime = scheduleEndTime,
                        )
                    },
                )
            }
            composable(BottomTab.Tasks.route) {
                TasksScreen(
                    currentUser = uiState.currentUser ?: currentUser,
                    uiState = uiState,
                    innerPadding = screenPadding,
                    onLoadTaskReminders = { user -> viewModel.loadTaskReminders(context, user) },
                    onCreateTaskReminder = { kind, title, details, reminderDate, reminderTime, themeColor ->
                        viewModel.createTaskReminder(
                            context = context,
                            kind = kind,
                            title = title,
                            details = details,
                            reminderDate = reminderDate,
                            reminderTime = reminderTime,
                            themeColor = themeColor,
                        )
                    },
                    onDeleteTaskReminder = { reminderId -> viewModel.deleteTaskReminder(context, reminderId) },
                    onToggleTaskCompletion = { reminderId, isCompleted -> 
                        viewModel.toggleTaskCompletion(context, reminderId, isCompleted) 
                    },
                    onLoadAnnouncements = { user -> viewModel.loadAnnouncements(user) },
                    onRefreshAnnouncements = { user -> viewModel.refreshAnnouncements(user) },
                    onToggleAnnouncementReadStatus = viewModel::toggleAnnouncementReadStatus,
                    onCreateAnnouncement = { title, subtitle, content, targetYears, imageUri ->
                        viewModel.createProfessorAnnouncement(context, title, subtitle, content, targetYears, imageUri)
                    },
                    onUpdateAnnouncement = { announcementId, title, subtitle, content, targetYears, imageUri, existingImageUrl ->
                        viewModel.updateProfessorAnnouncement(context, announcementId, title, subtitle, content, targetYears, imageUri, existingImageUrl)
                    },
                    onDeleteAnnouncement = viewModel::deleteProfessorAnnouncement,
                )
            }
        }
        }

        val isStudent = (uiState.currentUser ?: currentUser).role.equals("student", ignoreCase = true)
        val navBadgeCounts = remember(uiState.pendingAssignments, uiState.classAnnouncements, isStudent) {
            buildMap {
                if (isStudent) {
                    val active = uiState.pendingAssignments.count {
                        pendingAssignmentDeadlineUi(it).primary != "Overdue"
                    }
                    if (active > 0) put(BottomTab.Classes.route, active)
                    val unread = uiState.classAnnouncements.count { !it.isRead }
                    if (unread > 0) put(BottomTab.Tasks.route, unread)
                }
            }
        }

        AnimatedVisibility(
            visible = barVisible,
            enter = slideInVertically(animationSpec = tween(300)) { it },
            exit = slideOutVertically(animationSpec = tween(300)) { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            PanthraaBottomBar(
                tabs = tabs,
                currentRoute = currentRoute,
                badgeCounts = navBadgeCounts,
            onTabSelected = { tab ->
                if (tab.route == currentRoute) {
                    when (tab) {
                        BottomTab.Home -> dashboardRootResetToken += 1
                        BottomTab.Classes -> classesRootResetToken += 1
                        BottomTab.Tasks -> tasksRootResetToken += 1
                    }
                } else {
                    if (tab == BottomTab.Home) {
                        resetToDashboardRoot()
                    } else {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        )
    }
    }
}
}
