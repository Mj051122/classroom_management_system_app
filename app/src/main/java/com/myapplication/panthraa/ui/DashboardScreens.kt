    package com.myapplication.panthraa.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.graphics.Bitmap
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import com.myapplication.panthraa.data.AnnouncementBadgeHelper
import com.myapplication.panthraa.data.UploadNotificationHelper
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
import kotlinx.coroutines.delay
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

@Composable
fun HomeScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    openScheduleSignal: Int = 0,
    onPickImage: () -> Unit,
    onLogout: () -> Unit = {},
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
    onLoadClasses: (AppUser) -> Unit,
    onLoadTaskReminders: (AppUser) -> Unit,
    onLoadPendingAssignments: (AppUser) -> Unit,
    onLoadStudentGrades: (AppUser) -> Unit,
    onLoadProfessorGradeMonitor: (AppUser) -> Unit,
    onLoadAnnouncements: (AppUser) -> Unit = {},
    onRefreshDashboard: () -> Unit,
    onRefreshPendingAssignments: (AppUser) -> Unit,
    onRefreshStudentGrades: (AppUser) -> Unit,
    onRefreshProfessorGradeMonitor: (AppUser) -> Unit,
    onLoadJoinRequests: (List<String>) -> Unit = {},
    onRefreshJoinRequests: (List<String>) -> Unit = {},
    onApproveJoinRequest: (String, List<String>) -> Unit = { _, _ -> },
    onRejectJoinRequest: (String, List<String>) -> Unit = { _, _ -> },
    onOpenClass: (StudentClass) -> Unit = {},
    onOpenTask: (TaskReminder) -> Unit = {},
    onOpenClassesTab: () -> Unit = {},
    onOpenNewsFeed: () -> Unit = {},
    onOpenPendingAssignment: (PendingAssignment) -> Unit = {},
    onOpenProfessorYear: (String) -> Unit = {},
    onOpenProfessorClass: (ProfessorClass) -> Unit = {},
    onOpenProfessorUpload: (ProfessorClass) -> Unit = {},
    dashboardRootResetToken: Int = 0,
) {
    val user = uiState.currentUser
    var showPendingAssignments by remember { mutableStateOf(false) }
    var showStudentGrades by remember { mutableStateOf(false) }
    var showProfessorGradeMonitor by remember { mutableStateOf(false) }

    LaunchedEffect(dashboardRootResetToken) {
        showPendingAssignments = false
        showStudentGrades = false
        showProfessorGradeMonitor = false
    }

    LaunchedEffect(user?.id) {
        if (user != null) {
            onLoadClasses(user)
            onLoadTaskReminders(user)
            if (user.role.equals("student", ignoreCase = true)) {
                onLoadPendingAssignments(user)
                onLoadStudentGrades(user)
                onLoadAnnouncements(user)
            }
        }
    }

    val badgeContext = LocalContext.current
    val unreadAnnouncements = uiState.classAnnouncements.count { !it.isRead }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { }
    LaunchedEffect(user?.id) {
        if (user != null && Build.VERSION.SDK_INT >= 33) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            val granted = badgeContext.checkSelfPermission(permission)
            if (granted != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }
    LaunchedEffect(unreadAnnouncements) {
        AnnouncementBadgeHelper.updateBadge(badgeContext, unreadAnnouncements)
    }

    val isStudent = user?.role.equals("student", ignoreCase = true)
    if (user != null && isStudent && showPendingAssignments) {
        PendingAssignmentsScreen(
            uiState = uiState,
            innerPadding = innerPadding,
            onBack = { showPendingAssignments = false },
            onOpenAssignment = onOpenPendingAssignment,
            onLoad = { onLoadPendingAssignments(user) },
            onPullRefresh = { onRefreshPendingAssignments(user) },
        )
        return
    }

    if (user != null && isStudent && showStudentGrades) {
        StudentGradesScreen(
            uiState = uiState,
            innerPadding = innerPadding,
            onBack = { showStudentGrades = false },
            onLoad = { onLoadStudentGrades(user) },
            onPullRefresh = { onRefreshStudentGrades(user) },
        )
        return
    }

    if (user != null && !isStudent && showProfessorGradeMonitor) {
        ProfessorGradeMonitorScreen(
            uiState = uiState,
            innerPadding = innerPadding,
            onBack = { showProfessorGradeMonitor = false },
            onLoad = { onLoadProfessorGradeMonitor(user) },
            onPullRefresh = { onRefreshProfessorGradeMonitor(user) },
        )
        return
    }

    if (user != null && isStudent) {
        StudentHomeDashboard(
            user = user,
            uiState = uiState,
            innerPadding = innerPadding,
            openScheduleSignal = openScheduleSignal,
            onOpenClass = onOpenClass,
            onOpenTask = onOpenTask,
            onOpenClassesTab = onOpenClassesTab,
            onOpenNewsFeed = onOpenNewsFeed,
            onOpenPendingTasks = { showPendingAssignments = true },
            onOpenGrades = { showStudentGrades = true },
            onPickImage = onPickImage,
            onLogout = onLogout,
            onSaveProfile = onSaveProfile,
            onRefreshDashboard = onRefreshDashboard,
            dashboardRootResetToken = dashboardRootResetToken,
        )
        return
    }

    if (user != null) {
        ProfessorHomeDashboard(
            user = user,
            uiState = uiState,
            innerPadding = innerPadding,
            openScheduleSignal = openScheduleSignal,
            onPickImage = onPickImage,
            onLogout = onLogout,
            onSaveProfile = onSaveProfile,
            onRefreshDashboard = onRefreshDashboard,
            onOpenYear = onOpenProfessorYear,
            onOpenClass = onOpenProfessorClass,
            onOpenUpload = onOpenProfessorUpload,
            onOpenGradeMonitor = { showProfessorGradeMonitor = true },
            onLoadJoinRequests = onLoadJoinRequests,
            onRefreshJoinRequests = onRefreshJoinRequests,
            onApproveJoinRequest = onApproveJoinRequest,
            onRejectJoinRequest = onRejectJoinRequest,
            dashboardRootResetToken = dashboardRootResetToken,
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentAlignment = Alignment.Center,
    ) {
        PanthraaLoadingAnimation(size = 192.dp)
    }
}

// =================================================================================
// STUDENT DASHBOARD (redesigned) ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â matches the landing-screen aesthetic:
// soft gradients, rounded cards, blue (#0034DE) accent + coral/mint highlights.
// Professor dashboard uses the legacy layout above.
// =================================================================================

@Composable
internal fun StudentHomeDashboard(
    user: AppUser,
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    openScheduleSignal: Int = 0,
    onOpenClass: (StudentClass) -> Unit,
    onOpenTask: (TaskReminder) -> Unit,
    onOpenClassesTab: () -> Unit,
    onOpenNewsFeed: () -> Unit,
    onOpenPendingTasks: () -> Unit,
    onOpenGrades: () -> Unit,
    onPickImage: () -> Unit,
    onLogout: () -> Unit,
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
    onRefreshDashboard: () -> Unit,
    dashboardRootResetToken: Int = 0,
) {
    var showQrPreview by remember { mutableStateOf(false) }
    var showProfileDetails by remember { mutableStateOf(false) }
    var showSchedulePage by remember { mutableStateOf(false) }
    var scheduleClock by remember { mutableStateOf(LocalDateTime.now(PhilippineZoneId)) }
    val unreadAnnouncementCount = uiState.classAnnouncements.count { !it.isRead }
    val isRefreshing = RefreshSurface.Dashboard in uiState.refreshingSurfaces
    val todaySchedule = remember(uiState.studentClasses, scheduleClock) {
        studentDashboardTodaySchedule(uiState.studentClasses, scheduleClock)
    }

    LaunchedEffect(dashboardRootResetToken) {
        showQrPreview = false
        showProfileDetails = false
        showSchedulePage = false
    }

    LaunchedEffect(openScheduleSignal) {
        if (openScheduleSignal > 0) {
            showSchedulePage = true
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            scheduleClock = LocalDateTime.now(PhilippineZoneId)
        }
    }

    if (showQrPreview) {
        StudentProfilePreviewDialog(
            user = user,
            onDismiss = { showQrPreview = false },
        )
    }

    if (showProfileDetails) {
        StudentProfileDetailScreen(
            uiState = uiState,
            innerPadding = innerPadding,
            onBack = { showProfileDetails = false },
            onPickImage = onPickImage,
            onLogout = onLogout,
            onSaveProfile = onSaveProfile,
        )
        return
    }

    if (showSchedulePage) {
        StudentScheduleScreen(
            classes = uiState.studentClasses,
            innerPadding = innerPadding,
            onBack = { showSchedulePage = false },
            onOpenClass = onOpenClass,
        )
        return
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            onRefreshDashboard()
        },
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 8.dp,
            bottom = innerPadding.calculateBottomPadding() + 8.dp,
            start = 18.dp,
            end = 18.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            StudentPortalHeader(
                user = user,
                uiState = uiState,
                onProfileClick = { showProfileDetails = true },
            )
        }
        item {
            StudentDashboardActionGrid(
                user = user,
                uiState = uiState,
                onOpenProfile = { showProfileDetails = true },
                onOpenClasses = onOpenClassesTab,
                onOpenPendingTasks = onOpenPendingTasks,
                onOpenNewsFeed = onOpenNewsFeed,
                onOpenQr = { showQrPreview = true },
                onOpenSchedule = { showSchedulePage = true },
            )
        }
        if (unreadAnnouncementCount > 0) {
            item {
                StudentNewsFeedShortcut(
                    announcementCount = unreadAnnouncementCount,
                    highlighted = true,
                    onClick = onOpenNewsFeed,
                )
            }
        }
        item {
            StudentTodayScheduleCard(
                schedule = todaySchedule,
                onOpenClass = onOpenClass,
            )
        }
        item {
            StudentProgressHero(user = user, uiState = uiState)
        }
        item {
            StudentSectionHeader(title = "Subjects")
        }
        item {
            StudentSubjectShortcutGrid(
                uiState = uiState,
                onOpenClasses = onOpenClassesTab,
                onOpenPendingTasks = onOpenPendingTasks,
                onOpenGrades = onOpenGrades,
            )
        }
        if (unreadAnnouncementCount == 0) {
            item {
                StudentNewsFeedShortcut(
                    announcementCount = uiState.classAnnouncements.size,
                    highlighted = false,
                    onClick = onOpenNewsFeed,
                )
            }
        }
        }
}
}

@Composable
internal fun StudentPortalHeader(
    user: AppUser,
    uiState: MainUiState,
    onProfileClick: () -> Unit,
) {
    val dateLine = remember {
        LocalDate.now(PhilippineZoneId).format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
    }
    val program = buildString {
        append(user.course?.takeIf { it.isNotBlank() } ?: "Program")
        user.track?.takeIf { it.isNotBlank() }?.let { append(" - ").append(it) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0034DE),
                            Color(0xFF5B38F5),
                            Color(0xFF12A8A0),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 520f),
                    ),
                ),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f),
                    radius = size.width * 0.28f,
                    center = Offset(size.width * 0.94f, size.height * 0.12f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = size.width * 0.22f,
                    center = Offset(size.width * 0.05f, size.height * 0.95f),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = dateLine,
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Welcome, ${user.fullName.ifBlank { "Student" }}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Program",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = program,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    RoundedProfileImage(
                        imageUrl = user.profilePictureUrl,
                        name = user.fullName,
                        modifier = Modifier
                            .size(78.dp)
                            .clickable(onClick = onProfileClick),
                    )
                    ConnectivityStatusPill(
                        status = uiState.connectivityStatus,
                        onDarkBackground = true,
                    )
                }
            }
        }
    }
}

@Composable
internal fun RoundedProfileImage(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
) {
    var bitmap by remember(imageUrl) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    val context = LocalContext.current

    LaunchedEffect(imageUrl) {
        bitmap = null
        if (!imageUrl.isNullOrBlank()) {
            bitmap = withContext(Dispatchers.IO) {
                runCatching {
                    OfflineImageCache.loadBitmap(context, imageUrl)?.asImageBitmap()
                }.getOrNull()
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = "$name profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
            )
        }
    }
}

@Composable
internal fun StudentProfileDetailScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onLogout: () -> Unit,
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
) {
    val user = uiState.currentUser ?: return
    val internetRequired = uiState.isOfflineMode || uiState.connectivityStatus != ConnectivityStatus.Online
    val isSaving = uiState.isUpdatingProfileDetails
    val isBusy = internetRequired || isSaving || uiState.isUploadingProfilePicture
    var editMode by remember(user.id) { mutableStateOf(false) }
    var nameDraft by remember(user.id, user.fullName) { mutableStateOf(user.fullName) }
    var bioDraft by remember(user.id, user.bio) { mutableStateOf(user.bio.orEmpty()) }
    var phoneDraft by remember(user.id, user.phoneNumber) { mutableStateOf(user.phoneNumber.orEmpty()) }
    var courseDraft by remember(user.id, user.course) { mutableStateOf(user.course.orEmpty()) }
    var yearDraft by remember(user.id, user.year) { mutableStateOf(normalizeProfileYear(user.year)) }
    var sectionDraft by remember(user.id, user.section) { mutableStateOf(user.section.orEmpty()) }
    var trackDraft by remember(user.id, user.track) { mutableStateOf(user.track.orEmpty()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 0.dp,
            end = 0.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            StudentProfileHeader(
                user = user,
                editMode = editMode,
                isBusy = isBusy,
                onBack = onBack,
                onEditToggle = { editMode = !editMode },
                onPickImage = onPickImage,
            )
        }

        if (internetRequired) {
            item { InternetRequiredHint() }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                if (!editMode) {
                    StudentProfileInfoCards(user = user)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFBE123C),
                        ),
                        border = BorderStroke(1.dp, Color(0xFFBE123C).copy(alpha = 0.5f)),
                    ) {
                        Text("Log out", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    StudentProfileEditForm(
                        nameDraft = nameDraft,
                        courseDraft = courseDraft,
                        yearDraft = yearDraft,
                        sectionDraft = sectionDraft,
                        trackDraft = trackDraft,
                        phoneDraft = phoneDraft,
                        bioDraft = bioDraft,
                        isSaving = isSaving,
                        isBusy = isBusy,
                        onNameChange = { nameDraft = it },
                        onCourseChange = { courseDraft = it },
                        onYearChange = { yearDraft = it },
                        onSectionChange = { sectionDraft = it },
                        onTrackChange = { trackDraft = it },
                        onPhoneChange = { phoneDraft = it },
                        onBioChange = { bioDraft = it },
                        onSave = {
                            onSaveProfile(
                                nameDraft,
                                courseDraft.takeIf { it.isNotBlank() },
                                yearDraft.takeIf { it.isNotBlank() },
                                sectionDraft.takeIf { it.isNotBlank() },
                                trackDraft.takeIf { it.isNotBlank() },
                                bioDraft.takeIf { it.isNotBlank() },
                                phoneDraft.takeIf { it.isNotBlank() },
                            )
                            editMode = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun StudentProfileHeader(
    user: AppUser,
    editMode: Boolean,
    isBusy: Boolean,
    onBack: () -> Unit,
    onEditToggle: () -> Unit,
    onPickImage: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0034DE), Color(0xFF5B38F5), Color(0xFF0F766E)),
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 4.dp, end = 4.dp, bottom = 24.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onEditToggle) {
                        Text(
                            if (editMode) "Cancel" else "Edit",
                            color = Color.White.copy(alpha = 0.82f),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        RoundedProfileImage(
                            imageUrl = user.profilePictureUrl,
                            name = user.fullName,
                            modifier = Modifier.size(84.dp),
                        )
                        if (editMode) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .clickable(enabled = !isBusy, onClick = onPickImage),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.PhotoCamera, contentDescription = "Change photo", tint = Color(0xFF0034DE), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = user.fullName.ifBlank { "Student" },
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = user.role.replaceFirstChar { it.uppercase() },
                                    color = Color.White.copy(alpha = 0.80f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                )
                            }
                            Text(
                                text = user.idNumber.ifBlank { "" },
                                color = Color.White.copy(alpha = 0.64f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            )
                        }
                        Text(
                            text = AcademicLine(user) ?: user.role.replaceFirstChar { it.uppercase() },
                            color = Color.White.copy(alpha = 0.74f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun StudentProfileInfoCards(user: AppUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            ProfileInfoRow(icon = Icons.Filled.School, label = "ID Number", value = user.idNumber.ifBlank { "Not set" })
            ProfileDivider()
            ProfileInfoRow(icon = Icons.Filled.People, label = "Department", value = user.course.orEmpty().ifBlank { "Not set" })
            ProfileDivider()
            ProfileInfoRow(icon = Icons.Filled.Event, label = "Year Level", value = profileYearShortLabel(user.year) ?: "Not set")
            ProfileDivider()
            ProfileInfoRow(icon = Icons.Filled.Settings, label = "Section", value = user.section.orEmpty().ifBlank { "Not set" })
            ProfileDivider()
            ProfileInfoRow(icon = Icons.Filled.Add, label = "Track", value = user.track.orEmpty().ifBlank { "Not set" })
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            ProfileInfoRow(icon = Icons.Filled.Email, label = "Email", value = user.email.ifBlank { "Not set" })
            ProfileDivider()
            ProfileInfoRow(icon = Icons.Filled.PhotoCamera, label = "Phone", value = user.phoneNumber.orEmpty().ifBlank { "Not set" })
            ProfileDivider()
            ProfileInfoRow(icon = Icons.Outlined.Lightbulb, label = "Bio", value = user.bio.orEmpty().ifBlank { "No bio yet" })
        }
    }
}

@Composable
internal fun ProfileInfoRow(icon: ImageVector, label: String, value: String, iconTint: Color = Color(0xFF64748B)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
            Text(
                text = value,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun ProfileDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Color(0xFFF1F5F9),
        thickness = 1.dp,
    )
}

@Composable
internal fun StudentProfileEditForm(
    nameDraft: String,
    courseDraft: String,
    yearDraft: String,
    sectionDraft: String,
    trackDraft: String,
    phoneDraft: String,
    bioDraft: String,
    isSaving: Boolean,
    isBusy: Boolean,
    onNameChange: (String) -> Unit,
    onCourseChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onSectionChange: (String) -> Unit,
    onTrackChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Edit Information",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            ProfileTextField("Full name", nameDraft, onNameChange, !isBusy && !isSaving)
            ProfileTextField("Department", courseDraft, onCourseChange, !isBusy && !isSaving)
            ProfileYearSelector(yearDraft, onYearChange, !isBusy && !isSaving)
            ProfileTextField("Section", sectionDraft, onSectionChange, !isBusy && !isSaving)
            ProfileTextField("Track", trackDraft, onTrackChange, !isBusy && !isSaving)
            ProfileTextField("Phone Number", phoneDraft, onPhoneChange, !isBusy && !isSaving)
            ProfileTextField("Bio", bioDraft, onBioChange, !isBusy && !isSaving)
            PrimaryButton(
                text = if (isSaving) "Saving..." else "Save changes",
                onClick = onSave,
                enabled = nameDraft.trim().isNotEmpty() && !isBusy,
                isLoading = isSaving,
            )
        }
    }
}

@Composable
internal fun ProfessorProfileDetailScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onLogout: () -> Unit,
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
) {
    val user = uiState.currentUser ?: return
    val internetRequired = uiState.isOfflineMode || uiState.connectivityStatus != ConnectivityStatus.Online
    val isSaving = uiState.isUpdatingProfileDetails
    val isBusy = internetRequired || isSaving || uiState.isUploadingProfilePicture
    val classCount = uiState.professorClasses.size
    val studentCount = uiState.professorClasses.sumOf { it.studentCount.coerceAtLeast(0) }
    val requestCount = uiState.classJoinRequests.size
    val ungradedCount = uiState.assignmentStatuses.values.sumOf { it.ungradedCount.coerceAtLeast(0) }
    var editMode by remember(user.id) { mutableStateOf(false) }
    var nameDraft by remember(user.id, user.fullName) { mutableStateOf(user.fullName) }
    var bioDraft by remember(user.id, user.bio) { mutableStateOf(user.bio.orEmpty()) }
    var phoneDraft by remember(user.id, user.phoneNumber) { mutableStateOf(user.phoneNumber.orEmpty()) }
    var departmentDraft by remember(user.id, user.course) { mutableStateOf(user.course.orEmpty()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ProfessorProfileHeader(
                user = user,
                editMode = editMode,
                isBusy = isBusy,
                onBack = onBack,
                onEditToggle = { editMode = !editMode },
                onPickImage = onPickImage,
            )
        }

        item {
            ProfessorProfileStatsRow(
                classCount = classCount,
                studentCount = studentCount,
                requestCount = requestCount,
                ungradedCount = ungradedCount,
            )
        }

        if (internetRequired) {
            item { InternetRequiredHint() }
        }

        if (!editMode) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column {
                            ProfileInfoRow(icon = Icons.Filled.School, label = "ID Number", value = user.idNumber.ifBlank { "Not set" })
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Filled.People, label = "Department", value = user.course.orEmpty().ifBlank { "Not set" })
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Filled.Event, label = "Handled Classes", value = classCount.toString())
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Filled.Settings, label = "Students", value = studentCount.toString())
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Filled.Add, label = "Pending Requests", value = requestCount.toString())
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Outlined.Insights, label = "Ungraded Work", value = ungradedCount.toString())
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Column {
                            ProfileInfoRow(icon = Icons.Filled.Email, label = "Email", value = user.email.ifBlank { "Not set" })
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Filled.PhotoCamera, label = "Phone", value = user.phoneNumber.orEmpty().ifBlank { "Not set" })
                            ProfileDivider()
                            ProfileInfoRow(icon = Icons.Outlined.Lightbulb, label = "Bio", value = user.bio.orEmpty().ifBlank { "No bio yet" })
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color(0xFFD8E1F1)),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFFF8FBFF),
                                        Color(0xFFF6F4FF),
                                    ),
                                ),
                            ),
                    ) {
                        ProfessorProfileFormAmbientLayer(modifier = Modifier.matchParentSize())
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ProfileTextField("Full name", nameDraft, { nameDraft = it }, !internetRequired && !isSaving)
                            ProfileTextField(
                                label = "Department",
                                value = departmentDraft,
                                onValueChange = { departmentDraft = it },
                                enabled = !internetRequired && !isSaving,
                                placeholder = "e.g. CCSICT",
                            )
                            ProfileTextField("Phone Number", phoneDraft, { phoneDraft = it }, !internetRequired && !isSaving)
                            ProfileTextField("Bio", bioDraft, { bioDraft = it }, !internetRequired && !isSaving)
                            PrimaryButton(
                                text = if (isSaving) "Saving..." else "Save professor profile",
                                onClick = {
                                    val cleanDepartment = departmentDraft.trim()
                                    onSaveProfile(
                                        nameDraft,
                                        cleanDepartment.takeIf { it.isNotBlank() },
                                        user.year,
                                        user.section,
                                        user.track,
                                        bioDraft.takeIf { it.isNotBlank() },
                                        phoneDraft.takeIf { it.isNotBlank() },
                                    )
                                    editMode = false
                                },
                                enabled = nameDraft.trim().isNotEmpty() && !isBusy,
                                isLoading = isSaving,
                            )
                        }
                    }
                }
            }
        }

        if (!editMode) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFBE123C),
                    ),
                    border = BorderStroke(1.dp, Color(0xFFBE123C).copy(alpha = 0.5f)),
                ) {
                    Text("Log out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
internal fun ProfessorProfileHeader(
    user: AppUser,
    editMode: Boolean,
    isBusy: Boolean,
    onBack: () -> Unit,
    onEditToggle: () -> Unit,
    onPickImage: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0F766E)),
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 4.dp, end = 4.dp, bottom = 24.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onEditToggle) {
                        Text(
                            if (editMode) "Cancel" else "Edit",
                            color = Color.White.copy(alpha = 0.82f),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        RoundedProfileImage(
                            imageUrl = user.profilePictureUrl,
                            name = user.fullName,
                            modifier = Modifier.size(84.dp),
                        )
                        if (editMode) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .clickable(enabled = !isBusy, onClick = onPickImage),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.PhotoCamera, contentDescription = "Change photo", tint = Color(0xFF0F766E), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = user.fullName.ifBlank { "Professor" },
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = "Professor",
                                    color = Color.White.copy(alpha = 0.80f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                )
                            }
                            Text(
                                text = user.idNumber.ifBlank { "" },
                                color = Color.White.copy(alpha = 0.64f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            )
                        }
                        Text(
                            text = user.course.orEmpty().ifBlank { "Faculty" },
                            color = Color.White.copy(alpha = 0.74f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfessorProfileStatsRow(
    classCount: Int,
    studentCount: Int,
    requestCount: Int,
    ungradedCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ProfessorProfileStatCard(
            label = "Classes",
            value = classCount.toString(),
            icon = Icons.Filled.School,
            color = PanthraaBlue,
            modifier = Modifier.weight(1f),
        )
        ProfessorProfileStatCard(
            label = "Students",
            value = studentCount.toString(),
            icon = Icons.Filled.People,
            color = Color(0xFF0F766E),
            modifier = Modifier.weight(1f),
        )
        ProfessorProfileStatCard(
            label = "Requests",
            value = requestCount.toString(),
            icon = Icons.Filled.Add,
            color = Color(0xFF6D28D9),
            modifier = Modifier.weight(1f),
        )
        ProfessorProfileStatCard(
            label = "Ungraded",
            value = ungradedCount.toString(),
            icon = Icons.Outlined.Insights,
            color = Color(0xFFF97316),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun ProfessorProfileStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, color.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun ProfessorProfilePill(
    text: String,
    color: Color,
    container: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(container)
            .padding(horizontal = 9.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun ProfessorProfileHeroAmbientLayer(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 38.dp, y = (-46).dp)
                .size(152.dp)
                .background(PanthraaBlue.copy(alpha = 0.13f), CircleShape)
                .blur(28.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-48).dp, y = 34.dp)
                .size(132.dp)
                .background(Color(0xFF0F766E).copy(alpha = 0.11f), CircleShape)
                .blur(24.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 28.dp, y = 40.dp)
                .size(92.dp)
                .background(Color(0xFFF97316).copy(alpha = 0.09f), CircleShape)
                .blur(22.dp),
        )
    }
}

@Composable
internal fun ProfessorProfileFormAmbientLayer(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 44.dp, y = (-50).dp)
                .size(128.dp)
                .background(PanthraaBlue.copy(alpha = 0.09f), CircleShape)
                .blur(24.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-42).dp, y = 34.dp)
                .size(104.dp)
                .background(Color(0xFF0F766E).copy(alpha = 0.08f), CircleShape)
                .blur(22.dp),
        )
    }
}

@Composable
internal fun ProfessorProfileMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.82f),
                        color.copy(alpha = 0.08f),
                    ),
                ),
            )
            .border(1.dp, color.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = (-18).dp)
                .size(54.dp)
                .background(color.copy(alpha = 0.14f), CircleShape)
                .blur(16.dp),
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(color, CircleShape),
                )
                Text(
                    text = value,
                    color = color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = label,
                color = Color(0xFF475569),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun StudentDashboardActionGrid(
    user: AppUser,
    uiState: MainUiState,
    onOpenProfile: () -> Unit,
    onOpenClasses: () -> Unit,
    onOpenPendingTasks: () -> Unit,
    onOpenNewsFeed: () -> Unit,
    onOpenQr: () -> Unit,
    onOpenSchedule: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ProfQuickActionCard(
                title = "Student",
                subtitle = "Profile",
                iconRes = R.drawable.id_card,
                tint = PanthraaBlue,
                chip = Color(0xFFEAF0FF),
                onClick = onOpenProfile,
                modifier = Modifier.weight(1f),
            )
            ProfQuickActionCard(
                title = "QR Code",
                subtitle = "Student ID",
                icon = Icons.Filled.CheckCircle,
                tint = Color(0xFF6D28D9),
                chip = Color(0xFFF4F0FF),
                onClick = onOpenQr,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ProfQuickActionCard(
                title = "Schedule",
                subtitle = "My classes",
                icon = Icons.Filled.Event,
                tint = Color(0xFF0F766E),
                chip = Color(0xFFECFDF5),
                onClick = onOpenSchedule,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
internal fun StudentNewsFeedShortcut(
    announcementCount: Int,
    highlighted: Boolean,
    onClick: () -> Unit,
) {
    val container = if (highlighted) Color(0xFFFFF1F2) else Color.White
    val accent = if (highlighted) Color(0xFFE11D48) else PanthraaBlue
    val subtitle = if (highlighted) {
        "$announcementCount new announcement${if (announcementCount == 1) "" else "s"}"
    } else {
        "$announcementCount announcement${if (announcementCount == 1) "" else "s"}"
    }

    PortalActionCard(
        title = if (highlighted) "New Announcement" else "News Feed",
        subtitle = subtitle,
        icon = Icons.Outlined.Insights,
        tint = accent,
        chip = accent.copy(alpha = 0.12f),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        wide = true,
        containerColor = container,
        borderColor = if (highlighted) accent.copy(alpha = 0.35f) else Color.Transparent,
    )
}

@Composable
internal fun StudentSubjectShortcutGrid(
    uiState: MainUiState,
    onOpenClasses: () -> Unit,
    onOpenPendingTasks: () -> Unit,
    onOpenGrades: () -> Unit,
) {
    val overallGrade = remember(uiState.studentGrades) { buildOverallGradeSummary(uiState.studentGrades).finalGrade }
    val hasGrades = uiState.studentGrades.isNotEmpty()
    val gradeValue = when {
        uiState.isLoadingStudentGrades && !hasGrades -> "..."
        hasGrades -> formatGradeNumber(overallGrade)
        else -> "--"
    }
    val gradeTint = if (hasGrades) gradeTone(overallGrade) else Color(0xFF94A3B8)

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        PortalMiniCard(
            title = "Enrolled",
            value = uiState.studentClasses.size.toString(),
            icon = Icons.Filled.School,
            tint = Color(0xFF0F766E),
            onClick = onOpenClasses,
            modifier = Modifier.weight(1f),
        )
        PortalMiniCard(
            title = "Grades",
            value = gradeValue,
            icon = Icons.Filled.CheckCircle,
            iconRes = R.drawable.star,
            tint = gradeTint,
            onClick = onOpenGrades,
            modifier = Modifier.weight(1f),
        )
        PortalMiniCard(
            title = "Pending",
            value = uiState.pendingAssignments.size.toString(),
            icon = Icons.Filled.Edit,
            tint = Color(0xFFE11D48),
            onClick = onOpenPendingTasks,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun PortalActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    @DrawableRes iconRes: Int? = null,
    tint: Color,
    chip: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    wide: Boolean = false,
    containerColor: Color = Color.White,
    borderColor: Color = Color.Transparent,
) {
    Card(
        modifier = modifier
            .height(if (wide) 82.dp else 94.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(chip),
                contentAlignment = Alignment.Center,
            ) {
                if (iconRes != null) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(tint),
                    )
                } else {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (wide) 17.sp else 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
internal fun PortalMiniCard(
    title: String,
    value: String,
    icon: ImageVector,
    @DrawableRes iconRes: Int? = null,
    tint: Color,
    showValue: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(112.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                if (iconRes != null) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        colorFilter = ColorFilter.tint(tint),
                    )
                } else {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(19.dp))
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
                if (showValue) {
                    Text(
                        text = value,
                        color = tint,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
internal fun PendingAssignmentsScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onOpenAssignment: (PendingAssignment) -> Unit,
    onLoad: () -> Unit,
    onPullRefresh: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onLoad()
    }
    var selectedFilter by remember { mutableStateOf(PendingAssignmentFilter.All) }
    val filteredAssignments = remember(uiState.pendingAssignments, selectedFilter) {
        uiState.pendingAssignments
            .filter { assignment -> selectedFilter.matches(assignment) }
            .sortedWith(
                compareBy<PendingAssignment> { pendingAssignmentDeadlineSortKey(it) }
                    .thenBy { it.subjectCode.lowercase(Locale.getDefault()) }
                    .thenBy { it.title.lowercase(Locale.getDefault()) },
            )
    }

    PanthraaPullRefresh(
        isRefreshing = RefreshSurface.PendingAssignments in uiState.refreshingSurfaces,
        onRefresh = onPullRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }
                Column(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "Pending Tasks",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Assignments from all subjects",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
            PendingAssignmentTabs(
                selectedFilter = selectedFilter,
                assignments = uiState.pendingAssignments,
                onSelect = { selectedFilter = it },
                modifier = Modifier.padding(top = 14.dp),
            )
        }

        if (uiState.isLoadingPendingAssignments) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
        } else if (uiState.pendingAssignments.isEmpty()) {
            item {
                StudentEmptyCard(
                    title = "No pending assignments",
                    subtitle = "You're all caught up.",
                )
            }
        } else if (filteredAssignments.isEmpty()) {
            item {
                StudentEmptyCard(
                    title = "No ${selectedFilter.emptyLabel} pending",
                    subtitle = "Try another category.",
                )
            }
        } else {
            items(filteredAssignments, key = { it.assignmentId }) { assignment ->
                PendingAssignmentCard(
                    assignment = assignment,
                    onClick = { onOpenAssignment(assignment) },
                )
            }
        }
    }
}
}

internal enum class PendingAssignmentFilter(
    val tabLabel: String,
    val emptyLabel: String,
) {
    All("Pending", "tasks"),
    Task("Task", "tasks"),
    Quiz("Quiz", "quizzes"),
    Exam("Exam", "exams");

    fun matches(assignment: PendingAssignment): Boolean {
        val type = assignment.assignmentType.trim().lowercase(Locale.getDefault())
        return when (this) {
            All -> true
            Task -> type == "task" || type == "assignment"
            Quiz -> type == "quiz"
            Exam -> type == "exam"
        }
    }
}

@Composable
internal fun PendingAssignmentTabs(
    selectedFilter: PendingAssignmentFilter,
    assignments: List<PendingAssignment>,
    onSelect: (PendingAssignmentFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.86f))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PendingAssignmentFilter.entries.forEach { filter ->
            PendingAssignmentTab(
                label = filter.tabLabel,
                count = assignments.count { filter.matches(it) },
                selected = selectedFilter == filter,
                onClick = { onSelect(filter) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun PendingAssignmentTab(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pendingRed = Color(0xFFE11D48)
    val background = if (selected) pendingRed else Color.Transparent
    val content = if (selected) Color.White else Color(0xFF475569)
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = content,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = count.coerceAtMost(99).toString(),
                color = if (selected) pendingRed else Color(0xFF64748B),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) Color.White else Color(0xFFF1F5F9))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
internal fun PendingAssignmentCard(
    assignment: PendingAssignment,
    onClick: () -> Unit,
) {
    val pendingRed = Color(0xFFE11D48)
    val deadline = pendingAssignmentDeadlineUi(assignment)
    val deadlineColor = if (deadline.urgent) pendingRed else Color(0xFF0F172A)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (deadline.urgent) Color(0xFFFFC7D1) else Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (deadline.urgent) pendingRed else Color(0xFFFF8A9D)),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = assignment.subjectCode.ifBlank { assignment.className.ifBlank { "Subject" } },
                    color = pendingRed,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = assignment.title.ifBlank { "Untitled task" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                modifier = Modifier.width(76.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = deadline.label,
                    color = if (deadline.urgent) pendingRed else Color(0xFF94A3B8),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
                Text(
                    text = deadline.primary,
                    color = deadlineColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
                Text(
                    text = deadline.secondary,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

internal data class PendingAssignmentDeadlineUi(
    val label: String,
    val primary: String,
    val secondary: String,
    val urgent: Boolean,
)

internal fun pendingAssignmentDeadlineUi(
    assignment: PendingAssignment,
    today: LocalDate = LocalDate.now(PhilippineZoneId),
    currentTime: LocalTime = LocalTime.now(PhilippineZoneId),
): PendingAssignmentDeadlineUi {
    val dueDate = parsePendingAssignmentDate(assignment.endDate)
    val dueTime = parsePendingAssignmentTime(assignment.endTime)
    val timeText = dueTime?.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))

    if (dueDate == null) {
        return PendingAssignmentDeadlineUi(
            label = "DUE",
            primary = "No date",
            secondary = timeText ?: "Anytime",
            urgent = false,
        )
    }

    val isOverdue = dueDate.isBefore(today) || (dueDate == today && dueTime != null && dueTime.isBefore(currentTime))
    val isToday = dueDate == today
    val primary = when {
        isOverdue -> "Overdue"
        isToday -> "Today"
        dueDate == today.plusDays(1) -> "Tomorrow"
        else -> dueDate.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
    }
    val secondary = timeText ?: dueDate.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault()))

    return PendingAssignmentDeadlineUi(
        label = "DUE",
        primary = primary,
        secondary = secondary,
        urgent = isOverdue || isToday,
    )
}

internal fun pendingAssignmentDeadlineSortKey(assignment: PendingAssignment): Long {
    val dueDate = parsePendingAssignmentDate(assignment.endDate) ?: return Long.MAX_VALUE
    val dueTime = parsePendingAssignmentTime(assignment.endTime) ?: LocalTime.MAX
    return dueDate.toEpochDay() * 1440L + dueTime.hour * 60L + dueTime.minute
}

internal fun parsePendingAssignmentDate(rawDate: String?): LocalDate? {
    val clean = rawDate.orEmpty().trim()
    if (clean.isBlank()) return null
    return runCatching { LocalDate.parse(clean.take(10)) }.getOrNull()
}

internal fun parsePendingAssignmentTime(rawTime: String?): LocalTime? {
    val clean = rawTime.orEmpty().trim()
    if (clean.isBlank()) return null
    return runCatching { LocalTime.parse(clean.take(5)) }.getOrNull()
}

@Composable
internal fun ProfessorHomeDashboard(
    user: AppUser,
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    openScheduleSignal: Int = 0,
    onPickImage: () -> Unit,
    onLogout: () -> Unit,
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
    onRefreshDashboard: () -> Unit,
    onOpenYear: (String) -> Unit,
    onOpenClass: (ProfessorClass) -> Unit = {},
    onOpenUpload: (ProfessorClass) -> Unit = {},
    onOpenGradeMonitor: () -> Unit = {},
    onLoadJoinRequests: (List<String>) -> Unit = {},
    onRefreshJoinRequests: (List<String>) -> Unit = {},
    onApproveJoinRequest: (String, List<String>) -> Unit = { _, _ -> },
    onRejectJoinRequest: (String, List<String>) -> Unit = { _, _ -> },
    dashboardRootResetToken: Int = 0,
) {
    var showProfileDetails by remember { mutableStateOf(false) }
    var showQrPreview by remember { mutableStateOf(false) }
    var showUploadSubjectPicker by remember { mutableStateOf(false) }
    var showSchedulePage by remember { mutableStateOf(false) }
    var showReviewRequestsPage by remember { mutableStateOf(false) }

    LaunchedEffect(dashboardRootResetToken) {
        showProfileDetails = false
        showQrPreview = false
        showUploadSubjectPicker = false
        showSchedulePage = false
        showReviewRequestsPage = false
    }

    LaunchedEffect(openScheduleSignal) {
        if (openScheduleSignal > 0) {
            showSchedulePage = true
        }
    }

    if (showQrPreview) {
        ProfessorProfilePreviewDialog(
            user = user,
            onDismiss = { showQrPreview = false },
        )
    }

    if (showProfileDetails) {
        ProfessorProfileDetailScreen(
            uiState = uiState,
            innerPadding = innerPadding,
            onBack = { showProfileDetails = false },
            onPickImage = onPickImage,
            onLogout = onLogout,
            onSaveProfile = onSaveProfile,
        )
        return
    }

    val activeClasses = uiState.professorClasses
    if (showReviewRequestsPage) {
        ProfessorReviewRequestsScreen(
            uiState = uiState,
            classes = activeClasses,
            innerPadding = innerPadding,
            internetRequired = uiState.isOfflineMode || uiState.connectivityStatus != ConnectivityStatus.Online,
            onLoadJoinRequests = onLoadJoinRequests,
            onRefreshJoinRequests = onRefreshJoinRequests,
            onApproveRequest = onApproveJoinRequest,
            onRejectRequest = onRejectJoinRequest,
            onBack = { showReviewRequestsPage = false },
        )
        return
    }

    var scheduleClock by remember { mutableStateOf(LocalDateTime.now(PhilippineZoneId)) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            scheduleClock = LocalDateTime.now(PhilippineZoneId)
        }
    }
    if (showSchedulePage) {
        ProfessorScheduleScreen(
            classes = activeClasses,
            innerPadding = innerPadding,
            onBack = { showSchedulePage = false },
            onOpenClass = onOpenClass,
        )
        return
    }

    val todaySchedule = remember(activeClasses, scheduleClock) {
        professorDashboardTodaySchedule(activeClasses, scheduleClock)
    }
    val missingSubmissions = uiState.assignmentStatuses.values.sumOf { it.missingCount.coerceAtLeast(0) }
    val ungradedSubmissions = uiState.assignmentStatuses.values.sumOf { it.ungradedCount.coerceAtLeast(0) }
    val requestCount = uiState.classJoinRequests.size
    val scheduleCount = activeClasses.count {
        compactClassScheduleLabel(it.scheduleDays, it.scheduleStartTime, it.scheduleEndTime) != null
    }
    val uploadTarget = remember(activeClasses, uiState.assignmentStatuses) {
        activeClasses.maxWithOrNull(
            compareBy<ProfessorClass> { uiState.assignmentStatuses[it.id]?.missingCount ?: 0 }
                .thenBy { uiState.assignmentStatuses[it.id]?.ungradedCount ?: 0 }
                .thenBy { it.createdAt.orEmpty() },
        )
    }
    val missingTarget = remember(activeClasses, uiState.assignmentStatuses) {
        activeClasses.maxByOrNull { uiState.assignmentStatuses[it.id]?.missingCount ?: 0 } ?: uploadTarget
    }
    val classesTargetYear = remember(activeClasses) {
        activeClasses.firstOrNull()?.yearLevel ?: ProfessorClassYearOptions.first().value
    }
    val isRefreshing = RefreshSurface.Dashboard in uiState.refreshingSurfaces

    if (showUploadSubjectPicker) {
        ProfessorUploadSubjectPickerDialog(
            classes = activeClasses,
            onDismiss = { showUploadSubjectPicker = false },
            onSelectClass = { classItem ->
                showUploadSubjectPicker = false
                onOpenUpload(classItem)
            },
        )
    }

    PanthraaPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            onRefreshDashboard()
        },
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 14.dp,
            bottom = innerPadding.calculateBottomPadding() + 14.dp,
            start = 18.dp,
            end = 18.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ProfessorHeroHeader(
                user = user,
                uiState = uiState,
                onProfileClick = { showProfileDetails = true },
            )
        }
        item {
            ProfessorIdentityActionRow(
                onOpenProfile = { showProfileDetails = true },
                onOpenQr = { showQrPreview = true },
            )
        }
        item {
            ProfessorTodayScheduleCard(
                schedule = todaySchedule,
                onOpenClass = onOpenClass,
            )
        }
        item {
            StudentSectionHeader(title = "Work queue", subtitle = "Requests, grading, and missing work")
        }
        item {
            ProfessorTeachingActionGrid(
                requestCount = requestCount,
                missingSubmissions = missingSubmissions,
                ungradedSubmissions = ungradedSubmissions,
                onOpenRequests = { showReviewRequestsPage = true },
                onOpenMissing = { missingTarget?.let(onOpenClass) ?: onOpenYear(classesTargetYear) },
                onOpenGradeMonitor = onOpenGradeMonitor,
            )
        }
        item {
            StudentSectionHeader(title = "Class tools", subtitle = "Create, upload, and plan")
        }
        item {
            ProfessorDashboardActionGrid(
                classCount = activeClasses.size,
                scheduleCount = scheduleCount,
                onOpenClasses = { onOpenYear(classesTargetYear) },
                onOpenUpload = {
                    if (activeClasses.isEmpty()) {
                        onOpenYear(ProfessorClassYearOptions.first().value)
                    } else {
                        showUploadSubjectPicker = true
                    }
                },
                onOpenSchedule = { showSchedulePage = true },
            )
        }
        }
}
}

@Composable
internal fun ProfessorHeroHeader(
    user: AppUser,
    uiState: MainUiState,
    onProfileClick: () -> Unit,
) {
    val dateLine = remember {
        LocalDate.now(PhilippineZoneId).format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E3A8A),
                            Color(0xFF0F766E),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 520f),
                    ),
                ),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = size.width * 0.28f,
                    center = Offset(size.width * 0.94f, size.height * 0.12f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.width * 0.22f,
                    center = Offset(size.width * 0.05f, size.height * 0.95f),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = dateLine,
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Teaching dashboard",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = user.fullName.ifBlank { "Professor" },
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOfNotNull("Professor", user.course?.takeIf { it.isNotBlank() }).joinToString(" - "),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    RoundedProfileImage(
                        imageUrl = user.profilePictureUrl,
                        name = user.fullName,
                        modifier = Modifier
                            .size(78.dp)
                            .clickable(onClick = onProfileClick),
                    )
                    ConnectivityStatusPill(
                        status = uiState.connectivityStatus,
                        onDarkBackground = true,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfessorDashboardActionGrid(
    classCount: Int,
    scheduleCount: Int,
    onOpenClasses: () -> Unit,
    onOpenUpload: () -> Unit,
    onOpenSchedule: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ProfQuickActionCard(
                title = "Manage classes",
                subtitle = "$classCount active",
                icon = Icons.Filled.School,
                tint = Color(0xFF0F766E),
                chip = Color(0xFFECFDF5),
                onClick = onOpenClasses,
                modifier = Modifier.weight(1f),
            )
            ProfQuickActionCard(
                title = "Upload work",
                subtitle = if (classCount == 0) "Create class first" else "Choose class",
                icon = Icons.Filled.Add,
                tint = PanthraaBlue,
                chip = Color(0xFFEAF0FF),
                onClick = onOpenUpload,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ProfQuickActionCard(
                title = "View schedule",
                subtitle = "$scheduleCount scheduled",
                icon = Icons.Filled.Event,
                tint = Color(0xFF0F766E),
                chip = Color(0xFFECFDF5),
                onClick = onOpenSchedule,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun ProfessorTeachingActionGrid(
    requestCount: Int,
    missingSubmissions: Int,
    ungradedSubmissions: Int,
    onOpenRequests: () -> Unit,
    onOpenMissing: () -> Unit,
    onOpenGradeMonitor: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ProfQuickActionCard(
                title = "Review requests",
                subtitle = "$requestCount pending",
                icon = Icons.Filled.PersonAdd,
                tint = Color(0xFF6D28D9),
                chip = Color(0xFFF4F0FF),
                onClick = onOpenRequests,
                modifier = Modifier.weight(1f),
            )
            ProfQuickActionCard(
                title = "Check missing",
                subtitle = "$missingSubmissions submission${if (missingSubmissions == 1) "" else "s"}",
                icon = Icons.Filled.Close,
                tint = Color(0xFFE11D48),
                chip = Color(0xFFFFF1F2),
                onClick = onOpenMissing,
                modifier = Modifier.weight(1f),
            )
        }
        ProfQuickActionCard(
            title = "Grade monitor",
            subtitle = if (ungradedSubmissions > 0) {
                "$ungradedSubmissions ungraded"
            } else {
                "Student grades"
            },
            icon = Icons.Filled.Edit,
            tint = Color(0xFFF97316),
            chip = Color(0xFFFFF7ED),
            onClick = onOpenGradeMonitor,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
internal fun ProfessorScheduleScreen(
    classes: List<ProfessorClass>,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onOpenClass: (ProfessorClass) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 12.dp,
            end = 12.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Schedule",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Weekly teaching load",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        ClassScheduleDays.forEach { day ->
            val dayClasses = classes
                .filter { classItem -> day.value in orderedClassScheduleDays(classItem.scheduleDays) }
                .sortedWith(
                    compareBy<ProfessorClass> { parseClassScheduleTime(it.scheduleStartTime) ?: LocalTime.MAX }
                        .thenBy { it.displaySubjectCode }
                        .thenBy { it.displayClassName },
                )
            item(key = "schedule-${day.value}") {
                ProfessorScheduleDaySection(
                    day = day,
                    classes = dayClasses,
                    onOpenClass = onOpenClass,
                )
            }
        }
    }
}

@Composable
internal fun ProfessorScheduleDaySection(
    day: ClassScheduleDay,
    classes: List<ProfessorClass>,
    onOpenClass: (ProfessorClass) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (day.value == "saturday") Color(0xFFF4F0FF) else Color(0xFFEAF0FF))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = day.fullLabel,
                    color = if (day.value == "saturday") Color(0xFF6D28D9) else PanthraaBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
                if (day.value == "saturday") {
                    Text(
                        text = "Special schedule",
                        color = Color(0xFF6D28D9),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE2E8F0),
            )

            if (classes.isEmpty()) {
                Text(
                    text = "No classes scheduled.",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            } else {
                classes.forEachIndexed { index, classItem ->
                    if (index > 0) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    ProfessorScheduleClassRow(
                        classItem = classItem,
                        onOpenClass = { onOpenClass(classItem) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfessorScheduleClassRow(
    classItem: ProfessorClass,
    onOpenClass: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val scheduleLabel = compactClassScheduleLabel(
        days = classItem.scheduleDays,
        startTime = classItem.scheduleStartTime,
        endTime = classItem.scheduleEndTime,
    ) ?: "Schedule not set"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenClass)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = classItem.displaySubjectCode.take(3).ifBlank { "CLS" }.uppercase(Locale.getDefault()),
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = classItem.displayClassName,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = scheduleLabel,
                color = Color(0xFF0F766E),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open class",
            tint = Color(0xFF94A3B8),
        )
    }
}

@Composable
internal fun ProfessorTodayScheduleCard(
    schedule: ProfessorDashboardTodaySchedule,
    onOpenClass: (ProfessorClass) -> Unit,
) {
    DashboardTodayScheduleCard(
        dayLabel = schedule.dayLabel,
        scheduledCount = schedule.scheduledCount,
        palette = ProfessorDashboardSchedulePalette,
        rows = schedule.entries.map { entry ->
            professorDashboardScheduleRow(
                entry = entry,
                isCurrent = schedule.current?.classItem?.id == entry.classItem.id,
                onOpenClass = onOpenClass,
            )
        },
    )
}

internal fun professorDashboardScheduleRow(
    entry: ProfessorDashboardScheduleEntry,
    isCurrent: Boolean = false,
    onOpenClass: (ProfessorClass) -> Unit,
): DashboardScheduleRowData {
    val classItem = entry.classItem
    return DashboardScheduleRowData(
        subjectName = entry.subjectName,
        timeRange = entry.timeRange,
        subjectCode = entry.subjectCode,
        isCurrent = isCurrent,
        onClick = { onOpenClass(classItem) },
    )
}

internal fun studentDashboardScheduleRow(
    entry: StudentDashboardScheduleEntry,
    isCurrent: Boolean = false,
    onOpenClass: (StudentClass) -> Unit,
): DashboardScheduleRowData {
    val classItem = entry.classItem
    return DashboardScheduleRowData(
        subjectName = entry.subjectName,
        timeRange = entry.timeRange,
        subjectCode = entry.subjectCode,
        isCurrent = isCurrent,
        onClick = { onOpenClass(classItem) },
    )
}

internal data class DashboardScheduleRowData(
    val subjectName: String,
    val timeRange: String,
    val subjectCode: String,
    val isCurrent: Boolean,
    val onClick: () -> Unit,
)

internal data class DashboardSchedulePalette(
    val gradientColors: List<Color>,
    val currentAccent: Color,
)

internal val ProfessorDashboardSchedulePalette = DashboardSchedulePalette(
    gradientColors = listOf(
        Color(0xFF0F172A),
        Color(0xFF1E3A8A),
        Color(0xFF0F766E),
    ),
    currentAccent = Color(0xFF93C5FD),
)

internal val StudentDashboardSchedulePalette = DashboardSchedulePalette(
    gradientColors = listOf(
        Color(0xFF0034DE),
        Color(0xFF5B38F5),
        Color(0xFF12A8A0),
    ),
    currentAccent = Color(0xFF99F6E4),
)

@Composable
internal fun DashboardTodayScheduleCard(
    dayLabel: String,
    scheduledCount: Int,
    palette: DashboardSchedulePalette,
    rows: List<DashboardScheduleRowData>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 1.dp),
        shape = RoundedCornerShape(22.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = palette.gradientColors,
                        start = Offset(0f, 0f),
                        end = Offset(880f, 420f),
                    ),
                ),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f),
                    radius = size.width * 0.28f,
                    center = Offset(size.width * 0.95f, size.height * 0.04f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = size.width * 0.22f,
                    center = Offset(size.width * 0.02f, size.height * 0.98f),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        Text(
                            text = "Today's schedule",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = dashboardScheduleDisplayDay(dayLabel),
                            color = Color.White.copy(alpha = 0.72f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            lineHeight = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = dashboardScheduleCountText(scheduledCount),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (rows.isEmpty()) {
                    DashboardScheduleEmptyState(dayLabel = dayLabel)
                } else {
                    rows.forEach { row ->
                        DashboardTodayScheduleRow(
                            row = row,
                            palette = palette,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DashboardTodayScheduleRow(
    row: DashboardScheduleRowData,
    palette: DashboardSchedulePalette,
) {
    val accent = if (row.isCurrent) palette.currentAccent else Color.White.copy(alpha = 0.78f)
    val rowShape = RoundedCornerShape(14.dp)
    val rowBackground = if (row.isCurrent) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.12f)
    val rowBorder = if (row.isCurrent) {
        palette.currentAccent.copy(alpha = 0.38f)
    } else {
        Color.White.copy(alpha = 0.18f)
    }
    val rowModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 62.dp)
        .clip(rowShape)
        .background(rowBackground)
        .border(1.dp, rowBorder, rowShape)
        .clickable(onClick = row.onClick)
        .padding(horizontal = 12.dp, vertical = 9.dp)

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .width(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = if (row.isCurrent) 0.24f else 0.14f))
                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = row.timeRange,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            if (row.isCurrent) {
                Text(
                    text = "Now",
                    color = palette.currentAccent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp,
                    maxLines = 1,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = row.subjectName,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = row.subjectCode,
                color = Color.White.copy(alpha = 0.72f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open ${row.subjectName}",
            tint = accent,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
internal fun DashboardScheduleEmptyState(dayLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Event,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "No classes scheduled",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = dashboardScheduleDisplayDay(dayLabel),
                color = Color.White.copy(alpha = 0.72f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

internal fun dashboardScheduleDisplayDay(raw: String): String {
    val clean = raw.trim().lowercase(Locale.getDefault())
    return clean.replaceFirstChar { it.uppercase(Locale.getDefault()) }
}

internal fun dashboardScheduleCountText(count: Int): String {
    return if (count == 1) {
        "1 subject today"
    } else {
        "$count subjects today"
    }
}

internal fun professorDashboardScheduleTimeRange(start: LocalTime, end: LocalTime): String {
    val startPeriod = start.format(DateTimeFormatter.ofPattern("a", Locale.getDefault())).lowercase(Locale.getDefault())
    val endPeriod = end.format(DateTimeFormatter.ofPattern("a", Locale.getDefault())).lowercase(Locale.getDefault())
    val startText = start.format(DateTimeFormatter.ofPattern("h:mm", Locale.getDefault()))
    val endText = end.format(DateTimeFormatter.ofPattern("h:mm", Locale.getDefault()))
    return if (startPeriod == endPeriod) {
        "$startText - $endText$endPeriod"
    } else {
        "$startText$startPeriod - $endText$endPeriod"
    }
}

@Composable
internal fun ProfessorUploadSubjectPickerDialog(
    classes: List<ProfessorClass>,
    onDismiss: () -> Unit,
    onSelectClass: (ProfessorClass) -> Unit,
) {
    val sortedClasses = remember(classes) {
        classes.sortedWith(
            compareBy<ProfessorClass> { profileYearLabel(it.yearLevel).orEmpty() }
                .thenBy { it.department.orEmpty().lowercase(Locale.getDefault()) }
                .thenBy { it.section.orEmpty().lowercase(Locale.getDefault()) }
                .thenBy { it.displaySubjectCode.lowercase(Locale.getDefault()) },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = "Choose subject",
                subtitle = "Select where to upload.",
                icon = Icons.Filled.Add,
            )
        },
        text = {
            if (sortedClasses.isEmpty()) {
                Text(
                    text = "Create a class first before uploading.",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(sortedClasses, key = { it.id }) { classItem ->
                        ProfessorUploadSubjectOption(
                            classItem = classItem,
                            onClick = { onSelectClass(classItem) },
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            DialogCancelButton(text = "Close", onClick = onDismiss)
        },
    )
}

@Composable
internal fun ProfessorUploadSubjectOption(
    classItem: ProfessorClass,
    onClick: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val details = listOfNotNull(
        profileYearLabel(classItem.yearLevel),
        classItem.department?.trim()?.takeIf { it.isNotBlank() },
        classItem.section?.trim()?.takeIf { it.isNotBlank() },
        classItem.track?.trim()?.takeIf { it.isNotBlank() },
    ).joinToString(" - ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = classItem.displaySubjectCode.take(3).ifBlank { "SUB" }.uppercase(),
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = classItem.displaySubjectCode.ifBlank { classItem.displayClassName },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = classItem.displayClassName,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (details.isNotBlank()) {
                    Text(
                        text = details,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
internal fun ProfessorIdentityActionRow(
    onOpenProfile: () -> Unit,
    onOpenQr: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        ProfQuickActionCard(
            title = "Manage profile",
            subtitle = "Teaching identity",
            iconRes = R.drawable.id_card,
            tint = PanthraaBlue,
            chip = Color(0xFFEAF0FF),
            onClick = onOpenProfile,
            modifier = Modifier.weight(1f),
        )
        ProfQuickActionCard(
            title = "Professor ID",
            subtitle = "QR credential",
            icon = Icons.Filled.CheckCircle,
            tint = Color(0xFF6D28D9),
            chip = Color(0xFFF4F0FF),
            onClick = onOpenQr,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun ProfQuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    @DrawableRes iconRes: Int? = null,
    tint: Color,
    chip: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .heightIn(min = 82.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.86f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(chip),
                contentAlignment = Alignment.Center,
            ) {
                if (iconRes != null) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(tint),
                    )
                } else if (icon != null) {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfessorProfilePreviewDialog(
    user: AppUser,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DialogSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DialogHeader(
                    title = "Professor QR",
                    subtitle = "ID card for identification.",
                    icon = Icons.Filled.School,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(
                        imageUrl = user.profilePictureUrl,
                        name = user.fullName,
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .shadow(4.dp, CircleShape),
                        placeholderColor = Color(0xFFEAF0FF),
                        initialColor = PanthraaBlue,
                        initialFontSize = 26.sp,
                        showBorder = false,
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = user.fullName.ifBlank { "Professor" },
                            color = Color(0xFF0F172A),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = user.idNumber.ifBlank { "No ID number" },
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    StudentQrCard(
                        idNumber = user.idNumber,
                        modifier = Modifier.size(172.dp),
                    )
                }
                
                DialogPrimaryButton(text = "Done", onClick = onDismiss, enabled = true)
            }
        }
    }
}

@Composable
internal fun ProfessorYearOverview(
    classes: List<ProfessorClass>,
    onOpenYear: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ProfessorClassYearOptions.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowOptions.forEach { option ->
                    val yearClasses = classes.filter { it.yearLevel.equals(option.value, ignoreCase = true) }
                    ProfessorYearAccessCard(
                        option = option,
                        classCount = yearClasses.size,
                        onClick = { onOpenYear(option.value) },
                        modifier = Modifier
                            .weight(1f),
                        compact = true,
                    )
                }
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun ProfessorFocusCard(
    missingSubmissions: Int,
    ungradedSubmissions: Int,
    requestCount: Int,
    scheduleCount: Int,
    classCount: Int,
    onClick: () -> Unit,
) {
    val title = when {
        requestCount > 0 -> "$requestCount join request${if (requestCount == 1) "" else "s"} waiting"
        ungradedSubmissions > 0 -> "$ungradedSubmissions submission${if (ungradedSubmissions == 1) "" else "s"} ungraded"
        missingSubmissions > 0 -> "$missingSubmissions submissions need attention"
        classCount == 0 -> "Create your first class"
        else -> "Teaching load is organized"
    }
    val subtitle = when {
        requestCount > 0 -> "Review who can enter your classes."
        ungradedSubmissions > 0 -> "Open the class and review submitted work."
        missingSubmissions > 0 -> "Open a class to review the latest assignment status."
        classCount == 0 -> "Use the Classes tab to select a year and add a subject."
        else -> "You have $scheduleCount saved schedule${if (scheduleCount == 1) "" else "s"}."
    }
    val accent = when {
        requestCount > 0 -> Color(0xFF6D28D9)
        ungradedSubmissions > 0 -> Color(0xFFEAB308)
        missingSubmissions > 0 -> Color(0xFFE11D48)
        else -> Color(0xFF0F766E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Insights, contentDescription = null, tint = accent)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    lineHeight = 13.sp,
                )
            }
        }
    }
}

@Composable
internal fun ProfessorRecentClasses(
    classes: List<ProfessorClass>,
    statuses: Map<String, AssignmentStatus>,
    joinRequests: List<ClassJoinRequest>,
    isLoading: Boolean,
    onOpenClass: (ProfessorClass) -> Unit,
) {
    when {
        isLoading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PanthraaLoadingAnimation(size = 144.dp)
        }
        classes.isEmpty() -> PanthraaEmptyState(
            icon = Icons.Outlined.CreateNewFolder,
            title = "No classes yet",
            subtitle = "Create classes from the Classes tab to start managing your subjects.",
        )
        else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            classes.take(4).forEach { classItem ->
                val status = statuses[classItem.id]
                val requestCount = joinRequests.count { it.classId == classItem.id }
                ProfessorRecentClassRow(
                    classItem = classItem,
                    status = status,
                    requestCount = requestCount,
                    onClick = { onOpenClass(classItem) },
                )
            }
        }
    }
}

@Composable
internal fun ProfessorRecentClassRow(
    classItem: ProfessorClass,
    status: AssignmentStatus?,
    requestCount: Int,
    onClick: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val missing = status?.missingCount ?: 0
    val ungraded = status?.ungradedCount ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = classItem.displaySubjectCode.take(3).ifBlank { "CLS" }.uppercase(),
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = classItem.displayClassName,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${profileYearLabel(classItem.yearLevel) ?: "Unassigned"} - ${classItem.studentCount} students",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                if (requestCount > 0) {
                    ProfessorActionBadge("$requestCount request${if (requestCount == 1) "" else "s"}", Color(0xFF6D28D9), Color(0xFFF4F0FF))
                }
                if (missing > 0) {
                    ProfessorActionBadge("$missing missing", Color(0xFFE11D48), Color(0xFFFFF1F2))
                }
                if (ungraded > 0) {
                    ProfessorActionBadge("$ungraded ungraded", Color(0xFF854D0E), Color(0xFFFEF9C3))
                }
                if (requestCount == 0 && missing == 0 && ungraded == 0) {
                    ProfessorActionBadge("Up to date", Color(0xFF047857), Color(0xFFECFDF5))
                }
            }
        }
    }
}

@Composable
internal fun ProfessorActionBadge(
    text: String,
    contentColor: Color,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .padding(horizontal = 9.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Hero greeting card: time-of-day greeting + avatar + role, in a gradient surface. */
@Composable
internal fun StudentHeroHeader(
    user: AppUser,
    uiState: MainUiState,
    onProfileClick: () -> Unit,
) {
    val hour = remember { LocalTime.now(PhilippineZoneId).hour }
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
    val dateLine = remember {
        LocalDate.now(PhilippineZoneId).format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0034DE),
                        Color(0xFF5B3FDB),
                        Color(0xFF8A2FA8),
                    ),
                ),
            )
            .padding(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = dateLine,
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$greeting,",
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = user.fullName.ifBlank { "Student" },
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(contentAlignment = Alignment.Center) {
                    Avatar(
                        imageUrl = user.profilePictureUrl,
                        name = user.fullName,
                        modifier = Modifier
                            .size(64.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                            .clickable(onClick = onProfileClick),
                        placeholderColor = Color.White.copy(alpha = 0.25f),
                        initialColor = Color.White,
                        initialFontSize = 22.sp,
                        showBorder = false,
                    )
                    if (uiState.isUploadingProfilePicture) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(70.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeroPill(text = user.role.replaceFirstChar { it.uppercase() })
                }
                ConnectivityStatusPill(
                    status = uiState.connectivityStatus,
                    onDarkBackground = true,
                )
            }
        }
    }
}

@Composable
internal fun StudentProfilePreviewDialog(
    user: AppUser,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DialogSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DialogHeader(
                    title = "Student QR",
                    subtitle = "Use this for attendance scanning.",
                    icon = Icons.Filled.School,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(
                        imageUrl = user.profilePictureUrl,
                        name = user.fullName,
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .shadow(4.dp, CircleShape),
                        placeholderColor = Color(0xFFEAF0FF),
                        initialColor = PanthraaBlue,
                        initialFontSize = 26.sp,
                        showBorder = false,
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = user.fullName.ifBlank { "Student" },
                            color = Color(0xFF0F172A),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            text = user.idNumber.ifBlank { "No ID number" },
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    StudentQrCard(
                        idNumber = user.idNumber,
                        modifier = Modifier.size(172.dp),
                    )
                }
                
                DialogPrimaryButton(text = "Done", onClick = onDismiss, enabled = true)
            }
        }
    }
}

@Composable
internal fun StudentQrCard(
    idNumber: String,
    modifier: Modifier = Modifier,
) {
    val bitmap = remember(idNumber) {
        idNumber.takeIf { it.isNotBlank() }?.let { generateQrBitmap(it, 512) }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Student QR code",
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "No ID number",
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
internal fun StudentTodayScheduleCard(
    schedule: StudentDashboardTodaySchedule,
    onOpenClass: (StudentClass) -> Unit,
) {
    DashboardTodayScheduleCard(
        dayLabel = schedule.dayLabel,
        scheduledCount = schedule.scheduledCount,
        palette = StudentDashboardSchedulePalette,
        rows = schedule.entries.map { entry ->
            studentDashboardScheduleRow(
                entry = entry,
                isCurrent = schedule.current?.classItem?.id == entry.classItem.id,
                onOpenClass = onOpenClass,
            )
        },
    )
}

internal fun generateQrBitmap(value: String, size: Int): Bitmap {
    val matrix = QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, size, size)
    return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
        for (x in 0 until size) {
            for (y in 0 until size) {
                setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
    }
}

@Composable
internal fun HeroPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Headline progress: animated ring showing average completion across all classes. */
@Composable
internal fun StudentProgressHero(user: AppUser, uiState: MainUiState) {
    val progress = uiState.studentClasses
        .map { it.progressPercentage.coerceIn(0, 100) }
        .takeIf { it.isNotEmpty() }
        ?.average()
        ?.toInt() ?: 0
    val animated by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(durationMillis = 900),
        label = "progress",
    )
    val submitted = uiState.studentClasses.sumOf { it.completedAssignments }
    val total = uiState.studentClasses.sumOf { it.totalAssignments }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(96.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 12.dp.toPx()
                    val diameter = size.minDimension - stroke
                    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    val arcSize = Size(diameter, diameter)
                    drawArc(
                        color = Color(0xFFE2E8F0),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = PanthraaBlue,
                        startAngle = -90f,
                        sweepAngle = 360f * animated,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$progress%",
                        color = PanthraaBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                    )
                    Text(
                        text = "done",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Overall progress",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
                Text(
                    text = "Average completion across your ${uiState.studentClasses.size} classes.",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    lineHeight = 13.sp,
                )
                if (total > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "  $submitted / $total submissions",
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

/** Redesigned 2x2 stat grid with icons + accent tints. */
@Composable
internal fun StudentStatGrid(user: AppUser, uiState: MainUiState) {
    val classCount = uiState.studentClasses.size
    val notesCount = uiState.taskReminders.count { it.isNote }
    val scheduleCount = uiState.taskReminders.count { !it.isNote }
    val pending = uiState.pendingAssignments.count { pendingAssignmentDeadlineUi(it).primary != "Overdue" }

    val stats = listOf(
        StatTile("Classes", classCount.toString(), Icons.Filled.School, PanthraaBlue, Color(0xFFEAF0FF)),
        StatTile("Pending", pending.toString(), Icons.Outlined.Insights, Color(0xFFE11D48), Color(0xFFFFF1F2)),
        StatTile("Notes", notesCount.toString(), Icons.Outlined.Lightbulb, Color(0xFF6D28D9), Color(0xFFF4F0FF)),
        StatTile("Schedules", scheduleCount.toString(), Icons.Filled.Event, Color(0xFF0F766E), Color(0xFFECFDF5)),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        stats.forEach { stat ->
            StudentStatCard(stat = stat, modifier = Modifier.weight(1f))
        }
    }
}

internal data class StatTile(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val tint: Color,
    val chip: Color,
)

@Composable
internal fun StudentStatCard(stat: StatTile, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(stat.chip),
                contentAlignment = Alignment.Center,
            ) {
                Icon(stat.icon, contentDescription = stat.label, tint = stat.tint, modifier = Modifier.size(18.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    text = stat.value,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                )
                Text(
                    text = stat.label,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * The single most important next action: a pending assignment, else the nearest upcoming
 * schedule, else the newest note. One-tap entry into it.
 */
@Composable
internal fun StudentNextActionCard(
    user: AppUser,
    uiState: MainUiState,
    onOpenClass: (StudentClass) -> Unit,
    onOpenTask: (TaskReminder) -> Unit,
) {
    val activePendingAssignments = uiState.pendingAssignments
        .filter { pendingAssignmentDeadlineUi(it).primary != "Overdue" }
    val nextAssignment = activePendingAssignments
        .minByOrNull { pendingAssignmentDeadlineSortKey(it) }
    val nextAssignmentClass = nextAssignment?.let { assignment ->
        uiState.studentClasses.firstOrNull { it.id == assignment.classId }
    }
    val upcomingSchedule = uiState.taskReminders
        .filter { !it.isNote }
        .sortedWith(
            compareBy<TaskReminder> { it.reminderDate.ifBlank { "9999-12-31" } }
                .thenBy { it.reminderTime.orEmpty() },
        )
        .firstOrNull()
    val newestNote = uiState.taskReminders.firstOrNull { it.isNote }

    val accent: Color
    val icon: ImageVector
    val title: String
    val subtitle: String
    val cta: (() -> Unit)?

    when {
        nextAssignmentClass != null -> {
            accent = Color(0xFFE11D48); icon = Icons.Filled.Edit
            val pendingCount = activePendingAssignments.count { it.classId == nextAssignmentClass.id }
            title = "${pendingCount} assignment${if (pendingCount > 1) "s" else ""} to submit"
            subtitle = nextAssignmentClass.displayClassName
            cta = { onOpenClass(nextAssignmentClass) }
        }
        upcomingSchedule != null -> {
            accent = Color(0xFF0F766E); icon = Icons.Filled.Event
            title = upcomingSchedule.title
            subtitle = "Upcoming Ãƒâ€šÃ‚Â· ${reminderLabel(upcomingSchedule)}"
            cta = { onOpenTask(upcomingSchedule) }
        }
        newestNote != null -> {
            accent = Color(0xFF6D28D9); icon = Icons.Outlined.Lightbulb
            title = newestNote.title
            subtitle = "Note Ãƒâ€šÃ‚Â· ${reminderLabel(newestNote)}"
            cta = { onOpenTask(newestNote) }
        }
        else -> {
            accent = PanthraaBlue; icon = Icons.Outlined.Insights
            title = "You're all caught up"
            subtitle = "No pending work right now."
            cta = null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (cta != null) Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { cta() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (cta != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Open",
                    tint = accent,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Composable
internal fun StudentSectionHeader(title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
internal fun StudentClassesPreview(
    user: AppUser,
    uiState: MainUiState,
    onOpenClass: (StudentClass) -> Unit,
) {
    when {
        uiState.isLoadingClasses -> Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) { PanthraaLoadingAnimation(size = 144.dp) }
        uiState.studentClasses.isEmpty() -> PanthraaEmptyState(
            icon = Icons.Outlined.MenuBook,
            title = "No classes yet",
            subtitle = "Join a class from the Classes tab to get started.",
        )
        else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            uiState.studentClasses.take(3).forEach { classItem ->
                StudentClassPreviewRow(classItem = classItem, onClick = { onOpenClass(classItem) })
            }
        }
    }
}

@Composable
internal fun StudentClassPreviewRow(classItem: StudentClass, onClick: () -> Unit) {
    val accent = themeAccentColor(classItem.themeColor)
    val progress = classItem.progressPercentage.coerceIn(0, 100)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = classItem.displaySubjectCode.take(3).ifBlank { "CLS" }.uppercase(),
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = classItem.displayClassName,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = "  ${classItem.studentCount} students",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "$progress%",
                    color = accent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                )
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE2E8F0)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(accent),
                    )
                }
            }
        }
    }
}

@Composable
internal fun StudentTasksPreview(uiState: MainUiState, onOpenTask: (TaskReminder) -> Unit) {
    val reminders = uiState.taskReminders.filterNot { it.isCompleted }.sortedWith(
        compareBy<TaskReminder> { it.reminderDate.ifBlank { "9999-12-31" } }
            .thenBy { it.reminderTime.orEmpty() }
            .thenBy { it.title },
    )

    when {
        uiState.isLoadingTaskReminders -> Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) { PanthraaLoadingAnimation(size = 144.dp) }
        reminders.isEmpty() -> StudentEmptyCard(
            title = "Nothing scheduled",
            subtitle = "Add notes or schedules from the Tasks tab.",
        )
        else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            reminders.take(3).forEach { reminder ->
                StudentTaskChip(reminder = reminder, onClick = { onOpenTask(reminder) })
            }
        }
    }
}

@Composable
internal fun StudentTaskChip(reminder: TaskReminder, onClick: () -> Unit) {
    val accent = Color(android.graphics.Color.parseColor(reminder.themeColor))
    val tagLabel = if (reminder.isNote) "Note" else "Schedule"
    val tagBackground = accent.copy(alpha = 0.12f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 34.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = reminder.title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$tagLabel - ${reminderLabel(reminder)}",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(tagBackground)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = tagLabel,
                    color = readableContentColor(tagBackground, dark = accent),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
internal fun StudentEmptyCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
internal fun ProfessorGradeMonitorScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onLoad: () -> Unit,
    onPullRefresh: () -> Unit,
) {
    var selectedSubjectKey by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<String?>(null) }
    var selectedSection by remember { mutableStateOf<String?>(null) }
    var selectedTrack by remember { mutableStateOf<String?>(null) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    val students = remember(uiState.professorGradeMonitorEnrollments, uiState.professorGradeMonitorSubmissions) {
        buildProfessorGradeMonitorStudents(uiState.professorGradeMonitorEnrollments, uiState.professorGradeMonitorSubmissions)
    }
    val distinctYears = remember(uiState.professorGradeMonitorEnrollments) {
        uiState.professorGradeMonitorEnrollments.map { it.yearLevel }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val distinctSections = remember(uiState.professorGradeMonitorEnrollments) {
        uiState.professorGradeMonitorEnrollments.map { it.section }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val distinctTracks = remember(uiState.professorGradeMonitorEnrollments) {
        uiState.professorGradeMonitorEnrollments.map { it.track }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val distinctSubjects = remember(uiState.professorGradeMonitorEnrollments) {
        uiState.professorGradeMonitorEnrollments
            .map { it.subjectCode.ifBlank { it.className } }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
    val filteredStudents = remember(students, selectedYear, selectedSection, selectedTrack, selectedSubject) {
        students.mapNotNull { student ->
            val matching = student.subjects.filter { subject ->
                val enrollment = uiState.professorGradeMonitorEnrollments.firstOrNull {
                    it.classId == subject.classId
                }
                (selectedYear == null || enrollment?.yearLevel == selectedYear) &&
                    (selectedSection == null || enrollment?.section == selectedSection) &&
                    (selectedTrack == null || enrollment?.track == selectedTrack) &&
                    (selectedSubject == null || subject.subjectCode.ifBlank { subject.className } == selectedSubject)
            }
            if (matching.isEmpty()) null
            else student.copy(subjects = matching)
        }
    }
    val selectedSubjectEntry = filteredStudents
        .asSequence()
        .flatMap { student -> student.subjects.asSequence().map { subject -> student to subject } }
        .firstOrNull { (_, subject) -> subject.key == selectedSubjectKey }

    LaunchedEffect(Unit) { onLoad() }

    if (selectedSubjectEntry != null) {
        ProfessorGradeEvidenceScreen(
            student = selectedSubjectEntry.first,
            subject = selectedSubjectEntry.second,
            innerPadding = innerPadding,
            onBack = { selectedSubjectKey = null },
        )
        return
    }

    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri ->
        if (uri != null) {
            val csv = generateGradeCsv(filteredStudents)
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(csv.toByteArray(Charsets.UTF_8))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PanthraaPullRefresh(
            isRefreshing = RefreshSurface.Grades in uiState.refreshingSurfaces,
            onRefresh = onPullRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .panthraaScreenBackground(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 92.dp,
                start = 16.dp,
                end = 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Grade monitor",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "Student subjects, grades, and proof",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    if (filteredStudents.isNotEmpty()) {
                        IconButton(onClick = {
                            exportLauncher.launch("Panthraa_Grades.csv")
                        }) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = "Export grades",
                                tint = PanthraaBlue,
                            )
                        }
                    }
                }
            }

            if (students.isNotEmpty()) {
                item {
                    ProfessorGradeFilterBar(
                        distinctYears = distinctYears,
                        distinctSections = distinctSections,
                        distinctTracks = distinctTracks,
                        distinctSubjects = distinctSubjects,
                        selectedYear = selectedYear,
                        selectedSection = selectedSection,
                        selectedTrack = selectedTrack,
                        selectedSubject = selectedSubject,
                        onYearSelect = { selectedYear = it },
                        onSectionSelect = { selectedSection = it },
                        onTrackSelect = { selectedTrack = it },
                        onSubjectSelect = { selectedSubject = it },
                    )
                }
            }

            when {
                uiState.isLoadingProfessorGradeMonitor -> item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        PanthraaLoadingAnimation(size = 144.dp)
                    }
                }
                students.isEmpty() -> item {
                    PanthraaEmptyState(
                        icon = Icons.Outlined.FolderOff,
                        title = "No grade records yet",
                        subtitle = "Students will appear here once they submit assignments in your classes.",
                    )
                }
                filteredStudents.isEmpty() -> item {
                    PanthraaEmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "No students for these filters",
                        subtitle = "Try adjusting your filters to see more results.",
                    )
                }
                else -> {
                    item { ProfessorGradeMonitorHero(students = filteredStudents) }
                    item {
                        Text(
                            text = "Students",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                        )
                    }
                    items(filteredStudents, key = { it.key }) { student ->
                        ProfessorGradeStudentCard(
                            student = student,
                            onOpenSubject = { subject -> selectedSubjectKey = subject.key },
                        )
                    }
                }
            }
        }
    }
    }
}

private fun generateGradeCsv(
    students: List<ProfessorGradeMonitorStudentSummary>,
): String {
    val header = listOf(
        "Student Name", "ID Number", "Subject Code", "Subject Name",
        "Year Level", "Section", "Track", "Department",
        "Earned Points", "Max Points", "Raw Grade", "Final Grade",
        "Status", "Total Submissions", "Ungraded Items",
    )
    val rows = mutableListOf<List<String>>()
    for (student in students) {
        for (subject in student.subjects) {
            val comp = subject.computation
            rows.add(
                listOf(
                    escapeCsv(student.studentName),
                    escapeCsv(student.idNumber),
                    escapeCsv(subject.subjectCode),
                    escapeCsv(subject.className),
                    "", "", "", "",
                    comp?.earnedPoints?.toString().orEmpty(),
                    comp?.targetPoints?.toString().orEmpty(),
                    comp?.let { formatGrade(it.rawGrade) }.orEmpty(),
                    comp?.let { formatGrade(it.finalGrade) }.orEmpty(),
                    if (comp != null) "Graded" else "Incomplete",
                    subject.submissions.size.toString(),
                    subject.ungradedCount.toString(),
                )
            )
        }
    }
    return buildString {
        appendLine(header.joinToString(","))
        rows.forEach { appendLine(it.joinToString(",")) }
    }
}

private fun escapeCsv(value: String): String {
    return if (value.contains(',') || value.contains('"') || value.contains('\n')) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }
}

private fun formatGrade(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format("%.2f", value)
    }
}

@Composable
private fun ProfessorGradeFilterBar(
    distinctYears: List<String>,
    distinctSections: List<String>,
    distinctTracks: List<String>,
    distinctSubjects: List<String>,
    selectedYear: String?,
    selectedSection: String?,
    selectedTrack: String?,
    selectedSubject: String?,
    onYearSelect: (String?) -> Unit,
    onSectionSelect: (String?) -> Unit,
    onTrackSelect: (String?) -> Unit,
    onSubjectSelect: (String?) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterDropdownChip(
                    label = "Year",
                    options = distinctYears,
                    selected = selectedYear,
                    onSelect = onYearSelect,
                    modifier = Modifier.weight(1f),
                )
                FilterDropdownChip(
                    label = "Section",
                    options = distinctSections,
                    selected = selectedSection,
                    onSelect = onSectionSelect,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterDropdownChip(
                    label = "Track",
                    options = distinctTracks,
                    selected = selectedTrack,
                    onSelect = onTrackSelect,
                    modifier = Modifier.weight(1f),
                )
                FilterDropdownChip(
                    label = "Subject",
                    options = distinctSubjects,
                    selected = selectedSubject,
                    onSelect = onSubjectSelect,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FilterDropdownChip(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selected != null) "$label: $selected" else "$label: All"
    val isActive = selected != null

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = if (isActive) Color(0xFF0034DE).copy(alpha = 0.12f) else Color(0xFFF1F5F9),
            border = BorderStroke(
                1.dp,
                if (isActive) Color(0xFF0034DE).copy(alpha = 0.5f) else Color(0xFFCBD5E1),
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = displayText,
                    color = if (isActive) Color(0xFF0034DE) else Color(0xFF334155),
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isActive) Color(0xFF0034DE) else Color(0xFF64748B),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        "All",
                        fontWeight = if (!isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isActive) Color(0xFF0034DE) else Color(0xFF334155),
                        fontSize = 14.sp,
                    )
                },
                onClick = { onSelect(null); expanded = false },
            )
            options.forEach { option ->
                val isOptionSelected = option == selected
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            fontWeight = if (isOptionSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOptionSelected) Color(0xFF0034DE) else Color(0xFF0F172A),
                            fontSize = 14.sp,
                        )
                    },
                    onClick = { onSelect(option); expanded = false },
                )
            }
        }
    }
}

@Composable
internal fun ProfessorGradeMonitorHero(students: List<ProfessorGradeMonitorStudentSummary>) {
    val subjectCount = students.sumOf { it.subjects.size }
    val scoredSubjects = students.sumOf { student -> student.subjects.count { it.computation != null } }
    val evidenceCount = students.sumOf { student -> student.subjects.sumOf { it.submissions.size } }
    val ungradedCount = students.sumOf { student -> student.subjects.sumOf { it.ungradedCount } }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEAF0FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Insights, contentDescription = null, tint = PanthraaBlue)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Grade evidence board", color = Color(0xFF0F172A), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(
                        "Tap a subject grade to inspect scores and submissions.",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ProfessorGradeMonitorMetric("Students", students.size.toString(), PanthraaBlue, Modifier.weight(1f))
                ProfessorGradeMonitorMetric("Subjects", subjectCount.toString(), Color(0xFF0F766E), Modifier.weight(1f))
                ProfessorGradeMonitorMetric("Scored", scoredSubjects.toString(), Color(0xFFF97316), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ProfessorGradeMonitorMetric("Evidence", evidenceCount.toString(), Color(0xFF6D28D9), Modifier.weight(1f))
                ProfessorGradeMonitorMetric("Ungraded", ungradedCount.toString(), Color(0xFFE11D48), Modifier.weight(1f))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
internal fun ProfessorGradeMonitorMetric(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Text(text = value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        Text(text = label, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun ProfessorGradeStudentCard(
    student: ProfessorGradeMonitorStudentSummary,
    onOpenSubject: (ProfessorGradeMonitorSubjectSummary) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RoundedProfileImage(imageUrl = student.photoUrl, name = student.studentName, modifier = Modifier.size(46.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(student.studentName.ifBlank { "Student" }, color = Color(0xFF0F172A), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(student.idNumber.ifBlank { "No ID number" }, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
                Text("${student.subjects.size} subject${if (student.subjects.size == 1) "" else "s"}", color = PanthraaBlue, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
            student.subjects.forEach { subject ->
                ProfessorGradeSubjectRow(subject = subject, onClick = { onOpenSubject(subject) })
            }
        }
    }
}

@Composable
internal fun ProfessorGradeSubjectRow(subject: ProfessorGradeMonitorSubjectSummary, onClick: () -> Unit) {
    val computation = subject.computation
    val tone = computation?.let { gradeTone(it.finalGrade) } ?: Color(0xFF64748B)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, tone.copy(alpha = if (computation == null) 0.12f else 0.22f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(subject.subjectCode.ifBlank { "Subject" }, color = Color(0xFF0F172A), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subject.className.ifBlank { "Class" }, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${subject.submissions.size} submission${if (subject.submissions.size == 1) "" else "s"}" +
                        if (subject.ungradedCount > 0) " - ${subject.ungradedCount} ungraded" else "",
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    computation?.let { formatGradeNumber(it.finalGrade) } ?: "No grade",
                    color = tone,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (computation == null) 12.sp else 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text("View proof", color = PanthraaBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}

@Composable
internal fun ProfessorGradeEvidenceScreen(
    student: ProfessorGradeMonitorStudentSummary,
    subject: ProfessorGradeMonitorSubjectSummary,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
) {
    val computation = subject.computation
    val tone = computation?.let { gradeTone(it.finalGrade) } ?: Color(0xFF64748B)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.subjectCode.ifBlank { "Grade proof" },
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = student.studentName.ifBlank { "Student" },
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, tone.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        RoundedProfileImage(imageUrl = student.photoUrl, name = student.studentName, modifier = Modifier.size(48.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(student.studentName.ifBlank { "Student" }, color = Color(0xFF0F172A), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(subject.className.ifBlank { "Class" }, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        Text(
                            computation?.let { formatGradeNumber(it.finalGrade) } ?: "No grade",
                            color = tone,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (computation == null) 13.sp else 24.sp,
                        )
                    }
                    if (computation != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            GradeMetricPill("Raw", formatGradeNumber(computation.rawGrade), tone.copy(alpha = 0.1f), tone)
                            GradeMetricPill("Points", "${computation.earnedPoints}/${computation.targetPoints}", Color(0xFFF1F5F9), Color(0xFF334155))
                            GradeMetricPill("Records", subject.gradedSubmissions.size.toString(), Color(0xFFF1F5F9), Color(0xFF334155))
                        }
                    }
                }
            }
        }
        item {
            Text(
                text = "Scores and submissions",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )
        }
        if (subject.submissions.isEmpty()) {
            item { EmptyClassState("No submissions for this subject yet.") }
        } else {
            items(subject.submissions, key = { it.submissionId }) { submission ->
                ProfessorGradeEvidenceCard(submission = submission)
            }
        }
    }
}

@Composable
internal fun ProfessorGradeEvidenceCard(submission: ProfessorGradeMonitorSubmission) {
    val uriHandler = LocalUriHandler.current
    val score = submission.score
    val tone = score?.let { gradeTone(submission.convertedGrade) } ?: Color(0xFF64748B)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, tone.copy(alpha = 0.16f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(submission.assignmentTitle.ifBlank { "Assignment" }, color = Color(0xFF0F172A), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${assignmentTypeTitle(submission.assignmentType)} - ${assignmentCategoryLabel(submission.category)}",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                    )
                    Text(
                        "Submitted ${displaySubmissionDateTime(submission.submittedAt).ifBlank { "Not available" }}",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        when {
                            score != null -> "$score/${submission.targetPoints.coerceAtLeast(1)}"
                            submission.editAttempts > 0 -> "Grade cleared"
                            else -> "Pending"
                        },
                        color = tone, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp,
                    )
                    Text(score?.let { formatGradeNumber(submission.convertedGrade) } ?: "No score", color = Color(0xFF64748B), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
            if (submission.responseText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(10.dp),
                ) {
                    Text(
                        text = submission.responseText,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${submission.editAttempts} edit${if (submission.editAttempts == 1) "" else "s"}",
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                )
                if (!submission.submissionFileUrl.isNullOrBlank()) {
                    TextButton(onClick = { uriHandler.openUri(submission.submissionFileUrl) }) {
                        Text("Open file", color = PanthraaBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
internal fun StudentGradesScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onLoad: () -> Unit,
    onPullRefresh: () -> Unit,
) {
    var selectedSubjectKey by remember { mutableStateOf<String?>(null) }
    val summaries = remember(uiState.studentGrades) { buildStudentSubjectGradeSummaries(uiState.studentGrades) }
    val selectedSummary = summaries.firstOrNull { it.key == selectedSubjectKey }
    val classThemeMap = remember(uiState.studentClasses) {
        uiState.studentClasses.associate { it.id to themeAccentColor(it.themeColor) }
    }
    val overallSummary = remember(uiState.studentGrades) { buildOverallGradeSummary(uiState.studentGrades) }

    LaunchedEffect(Unit) {
        onLoad()
    }

    if (selectedSummary != null) {
        StudentGradeDetailsScreen(
            summary = selectedSummary,
            innerPadding = innerPadding,
            onBack = { selectedSubjectKey = null },
        )
        return
    }

    PanthraaPullRefresh(
        isRefreshing = RefreshSurface.Grades in uiState.refreshingSurfaces,
        onRefresh = onPullRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Grades",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Weighted performance across subjects",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        when {
            uiState.isLoadingStudentGrades -> item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PanthraaLoadingAnimation(size = 144.dp)
                }
            }
            uiState.studentGrades.isEmpty() -> item {
                PanthraaEmptyState(
                    icon = Icons.Outlined.Grade,
                    title = "No grade records yet",
                    subtitle = "Your graded assignments and computed grades will appear here.",
                )
            }
            else -> {
                item {
                    StudentGradeSummaryHero(
                        summary = overallSummary,
                        title = "Overall grade",
                        showBreakdown = false,
                    )
                }
                item {
                    StudentSectionHeader(
                        title = "Subjects",
                        subtitle = "Tap a card for score details",
                    )
                }
                summaries.chunked(2).forEach { rowSummaries ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowSummaries.forEach { summary ->
                                StudentSubjectGradeCard(
                                    summary = summary,
                                    modifier = Modifier.weight(1f),
                                    borderColor = classThemeMap[summary.classId],
                                    onClick = { selectedSubjectKey = summary.key },
                                )
                            }
                            if (rowSummaries.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
internal fun StudentGradeSummaryHero(
    summary: GradeComputationSummary,
    title: String = "General weighted average",
    showBreakdown: Boolean = true,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF0034DE), Color(0xFF5B38F5), Color(0xFF0F766E)),
                    ),
                )
                .padding(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.78f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = gradeStatusLabel(summary.finalGrade),
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
                Text(
                    text = formatGradeNumber(summary.finalGrade),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                )
                if (showBreakdown) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GradeMetricPill("Raw", formatGradeNumber(summary.rawGrade), Color.White.copy(alpha = 0.16f), Color.White)
                        GradeMetricPill("Lecture", formatGradeNumber(summary.lecturePercent), Color.White.copy(alpha = 0.16f), Color.White)
                        GradeMetricPill("Lab", formatGradeNumber(summary.laboratoryPercent), Color.White.copy(alpha = 0.16f), Color.White)
                    }
                }
                Text(
                    text = "${summary.earnedPoints}/${summary.targetPoints} points recorded",
                    color = Color.White.copy(alpha = 0.82f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
internal fun StudentSubjectGradeCard(
    summary: StudentSubjectGradeSummary,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    onClick: () -> Unit,
) {
    val tone = gradeTone(summary.finalGrade)
    val cardBorderColor = borderColor?.copy(alpha = 0.55f) ?: Color(0xFFD8E1F1)
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, cardBorderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = summary.className.ifBlank { "Class grade" },
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = summary.subjectCode.ifBlank { "" },
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = formatGradeNumber(summary.finalGrade),
                    color = tone,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    lineHeight = 26.sp,
                    maxLines = 1,
                )
                Text(
                    text = gradeStatusLabel(summary.finalGrade),
                    color = tone.copy(alpha = 0.65f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
internal fun StudentGradeDetailsScreen(
    summary: StudentSubjectGradeSummary,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.subjectCode.ifBlank { "Grade details" },
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = summary.className,
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        item {
            StudentGradeSummaryHero(
                summary = summary.toComputationSummary(),
                title = "Subject grade",
                showBreakdown = true,
            )
        }
        item { GradeCategorySection("Lecture", summary.lectureGrades) }
        item { GradeCategorySection("Laboratory", summary.laboratoryGrades) }
    }
}

@Composable
internal fun GradeCategorySection(title: String, grades: List<StudentGrade>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            )
            Text(
                text = "${grades.size} record${if (grades.size == 1) "" else "s"}",
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            )
        }
        if (grades.isEmpty()) {
            GradeCategoryEmptyCard(title = title)
        } else {
            grades.forEach { grade ->
                GradeBreakdownCard(grade = grade)
            }
        }
    }
}

@Composable
internal fun GradeCategoryEmptyCard(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp),
    ) {
        Text(
            text = "No $title grades yet",
            color = Color(0xFF64748B),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
internal fun GradeBreakdownCard(grade: StudentGrade) {
    val tone = gradeTone(grade.convertedGrade)
    val target = grade.targetPoints.coerceAtLeast(1)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, tone.copy(alpha = 0.18f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = grade.title.ifBlank { "Assignment" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${grade.score}/$target points",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
            Text(
                text = formatGradeNumber(grade.convertedGrade),
                color = tone,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
internal fun GradeMetricPill(
    label: String,
    value: String,
    background: Color,
    content: Color,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Text(
            text = label,
            color = content.copy(alpha = 0.72f),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
        )
        Text(
            text = value,
            color = content,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
        )
    }
}

internal data class GradeComputationSummary(
    val lecturePercent: Double,
    val laboratoryPercent: Double,
    val rawGrade: Double,
    val finalGrade: Double,
    val earnedPoints: Int,
    val targetPoints: Int,
)

internal data class ProfessorGradeMonitorStudentSummary(
    val key: String,
    val studentId: String,
    val studentName: String,
    val idNumber: String,
    val photoUrl: String?,
    val subjects: List<ProfessorGradeMonitorSubjectSummary>,
)

internal data class ProfessorGradeMonitorSubjectSummary(
    val key: String,
    val classId: String,
    val className: String,
    val subjectCode: String,
    val submissions: List<ProfessorGradeMonitorSubmission>,
    val gradedSubmissions: List<ProfessorGradeMonitorSubmission>,
    val ungradedCount: Int,
    val computation: GradeComputationSummary?,
)

internal data class StudentSubjectGradeSummary(
    val key: String,
    val classId: String,
    val className: String,
    val subjectCode: String,
    val professorName: String,
    val grades: List<StudentGrade>,
    val lectureGrades: List<StudentGrade>,
    val laboratoryGrades: List<StudentGrade>,
    val lecturePercent: Double,
    val laboratoryPercent: Double,
    val rawGrade: Double,
    val finalGrade: Double,
    val earnedPoints: Int,
    val targetPoints: Int,
) {
    fun toComputationSummary() = GradeComputationSummary(
        lecturePercent = lecturePercent,
        laboratoryPercent = laboratoryPercent,
        rawGrade = rawGrade,
        finalGrade = finalGrade,
        earnedPoints = earnedPoints,
        targetPoints = targetPoints,
    )
}

internal fun buildProfessorGradeMonitorStudents(
    enrollments: List<ProfessorGradeMonitorEnrollment>,
    submissions: List<ProfessorGradeMonitorSubmission>,
): List<ProfessorGradeMonitorStudentSummary> {
    val enrollmentKeys = enrollments.map { monitorStudentKey(it.studentId, it.idNumber, it.studentName) }
    val submissionKeys = submissions.map { monitorStudentKey(it.studentId, it.idNumber, it.studentName) }
    return (enrollmentKeys + submissionKeys)
        .distinct()
        .mapNotNull { studentKey ->
            val studentEnrollments = enrollments.filter { monitorStudentKey(it.studentId, it.idNumber, it.studentName) == studentKey }
            val studentSubmissions = submissions.filter { monitorStudentKey(it.studentId, it.idNumber, it.studentName) == studentKey }
            val firstEnrollment = studentEnrollments.firstOrNull()
            val firstSubmission = studentSubmissions.firstOrNull()
            val subjectKeys = (
                studentEnrollments.map { monitorSubjectKey(it.classId, it.subjectCode, it.className) } +
                    studentSubmissions.map { monitorSubjectKey(it.classId, it.subjectCode, it.className) }
                ).distinct()
            val subjects = subjectKeys.mapNotNull { subjectKey ->
                val enrollment = studentEnrollments.firstOrNull { monitorSubjectKey(it.classId, it.subjectCode, it.className) == subjectKey }
                val subjectSubmissions = studentSubmissions
                    .filter { monitorSubjectKey(it.classId, it.subjectCode, it.className) == subjectKey }
                    .sortedByDescending { it.submittedAt.orEmpty() }
                val subjectClassId = enrollment?.classId ?: subjectSubmissions.firstOrNull()?.classId.orEmpty()
                val subjectClassName = enrollment?.className ?: subjectSubmissions.firstOrNull()?.className.orEmpty()
                val subjectCode = enrollment?.subjectCode ?: subjectSubmissions.firstOrNull()?.subjectCode.orEmpty()
                if (subjectClassId.isBlank() && subjectClassName.isBlank() && subjectCode.isBlank()) {
                    null
                } else {
                    val graded = subjectSubmissions.filter { it.score != null }
                    ProfessorGradeMonitorSubjectSummary(
                        key = subjectKey,
                        classId = subjectClassId,
                        className = subjectClassName,
                        subjectCode = subjectCode,
                        submissions = subjectSubmissions,
                        gradedSubmissions = graded,
                        ungradedCount = subjectSubmissions.count { it.score == null },
                        computation = graded.takeIf { it.isNotEmpty() }?.let { computeWeightedGrade(it.toStudentGrades()) },
                    )
                }
            }.sortedBy { it.subjectCode.ifBlank { it.className } }

            if (subjects.isEmpty()) {
                null
            } else {
                ProfessorGradeMonitorStudentSummary(
                    key = studentKey,
                    studentId = firstEnrollment?.studentId ?: firstSubmission?.studentId.orEmpty(),
                    studentName = firstEnrollment?.studentName ?: firstSubmission?.studentName.orEmpty(),
                    idNumber = firstEnrollment?.idNumber ?: firstSubmission?.idNumber.orEmpty(),
                    photoUrl = firstEnrollment?.photoUrl ?: firstSubmission?.photoUrl,
                    subjects = subjects,
                )
            }
        }
        .sortedBy { it.studentName.ifBlank { it.idNumber }.lowercase(Locale.getDefault()) }
}

internal fun List<ProfessorGradeMonitorSubmission>.toStudentGrades(): List<StudentGrade> {
    return mapNotNull { submission ->
        val score = submission.score ?: return@mapNotNull null
        StudentGrade(
            submissionId = submission.submissionId,
            assignmentId = submission.assignmentId,
            classId = submission.classId,
            title = submission.assignmentTitle,
            score = score,
            targetPoints = submission.targetPoints.coerceAtLeast(1),
            category = submission.category,
            rawPercent = submission.rawPercent,
            convertedGrade = submission.convertedGrade,
            submittedAt = submission.submittedAt,
            className = submission.className,
            subjectCode = submission.subjectCode,
        )
    }
}

internal fun monitorStudentKey(studentId: String, idNumber: String, studentName: String): String {
    return studentId.ifBlank { idNumber.ifBlank { studentName } }.lowercase(Locale.getDefault())
}

internal fun monitorSubjectKey(classId: String, subjectCode: String, className: String): String {
    return classId.ifBlank { "$subjectCode:$className" }.lowercase(Locale.getDefault())
}

internal fun buildStudentSubjectGradeSummaries(grades: List<StudentGrade>): List<StudentSubjectGradeSummary> {
    return grades
        .groupBy { it.classId.ifBlank { "${it.subjectCode}:${it.className}" } }
        .map { (key, subjectGrades) ->
            val lectureGrades = subjectGrades.filter { it.category.equals("lecture", ignoreCase = true) }
            val laboratoryGrades = subjectGrades.filter { it.category.equals("laboratory", ignoreCase = true) }
            val computation = computeWeightedGrade(subjectGrades)
            StudentSubjectGradeSummary(
                key = key,
                classId = subjectGrades.firstOrNull()?.classId.orEmpty(),
                className = subjectGrades.firstOrNull()?.className.orEmpty(),
                subjectCode = subjectGrades.firstOrNull()?.subjectCode.orEmpty(),
                professorName = subjectGrades.firstOrNull()?.professorName.orEmpty(),
                grades = subjectGrades.sortedByDescending { it.submittedAt.orEmpty() },
                lectureGrades = lectureGrades.sortedByDescending { it.submittedAt.orEmpty() },
                laboratoryGrades = laboratoryGrades.sortedByDescending { it.submittedAt.orEmpty() },
                lecturePercent = computation.lecturePercent,
                laboratoryPercent = computation.laboratoryPercent,
                rawGrade = computation.rawGrade,
                finalGrade = computation.finalGrade,
                earnedPoints = computation.earnedPoints,
                targetPoints = computation.targetPoints,
            )
        }
        .sortedBy { it.subjectCode.ifBlank { it.className } }
}

internal fun buildOverallGradeSummary(grades: List<StudentGrade>): GradeComputationSummary {
    return computeWeightedGrade(grades)
}

internal fun computeWeightedGrade(grades: List<StudentGrade>): GradeComputationSummary {
    val lectureGrades = grades.filter { it.category.equals("lecture", ignoreCase = true) }
    val laboratoryGrades = grades.filter { it.category.equals("laboratory", ignoreCase = true) }
    val lecturePercent = categoryPercent(lectureGrades)
    val laboratoryPercent = categoryPercent(laboratoryGrades)
    val rawGrade = roundGrade(lecturePercent * 0.60 + laboratoryPercent * 0.40)
    val finalGrade = convertRawToFinal(rawGrade)
    return GradeComputationSummary(
        lecturePercent = lecturePercent,
        laboratoryPercent = laboratoryPercent,
        rawGrade = rawGrade,
        finalGrade = finalGrade,
        earnedPoints = grades.sumOf { it.score },
        targetPoints = grades.sumOf { it.targetPoints.coerceAtLeast(1) },
    )
}

internal fun categoryPercent(grades: List<StudentGrade>): Double {
    val target = grades.sumOf { it.targetPoints.coerceAtLeast(1) }
    if (target <= 0) return 0.0
    val earned = grades.sumOf { it.score }
    return pointsToPercent(earned, target)
}

internal fun pointsToPercent(earned: Int, target: Int): Double {
    return if (target <= 0) 0.0 else roundGrade(earned.toDouble() * 100.0 / target.toDouble())
}

internal fun convertRawToFinal(raw: Double): Double {
    return roundGrade(raw * 0.625 + 37.5)
}

internal fun roundGrade(value: Double): Double = round(value * 100.0) / 100.0

internal fun formatGradeNumber(value: Double): String {
    return String.format(Locale.getDefault(), "%.2f", value)
}

internal fun gradeTone(value: Double): Color {
    return when {
        value >= 85.0 -> Color(0xFF047857)
        value >= 75.0 -> Color(0xFFF97316)
        else -> Color(0xFFE11D48)
    }
}

internal fun gradeStatusContrastColor(value: Double): Color {
    return when {
        value >= 85.0 -> Color(0xFF065F46)
        value >= 75.0 -> Color(0xFF9A3412)
        else -> Color(0xFF9F1239)
    }
}

internal fun gradeStatusLabel(value: Double): String {
    return when {
        value >= 85.0 -> "Excellent"
        value >= 75.0 -> "Passed"
        else -> "Failed"
    }
}

internal fun assignmentCategoryLabel(category: String): String {
    return if (category.equals("laboratory", ignoreCase = true)) "Laboratory" else "Lecture"
}

internal fun assignmentTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "material" -> Color(0xFF0F766E)
        "task", "assignment" -> Color(0xFF0034DE)
        "quiz" -> Color(0xFF6D28D9)
        "exam" -> Color(0xFFBE123C)
        else -> Color(0xFF0034DE)
    }
}

@DrawableRes
internal fun assignmentTypeIconRes(type: String): Int {
    return when (type.lowercase()) {
        "material" -> R.drawable.material
        "quiz" -> R.drawable.quiz
        "exam" -> R.drawable.exam
        else -> R.drawable.task
    }
}

internal fun assignmentTypeLabel(type: String): String {
    return when (type.lowercase()) {
        "material" -> "MATERIAL"
        "task", "assignment" -> "TASK"
        "quiz" -> "QUIZ"
        "exam" -> "EXAM"
        else -> "TASK"
    }
}

internal fun assignmentTypeTitle(type: String): String {
    return when (type.lowercase()) {
        "material" -> "Material"
        "task", "assignment" -> "Task"
        "quiz" -> "Quiz"
        "exam" -> "Exam"
        else -> "Task"
    }
}

internal data class SubmissionFormatOption(
    val value: String,
    val label: String,
    val pickerMimeTypes: Array<String>,
    val extensions: Set<String>,
)

internal val SubmissionFormatOptions = listOf(
    SubmissionFormatOption("pdf", "PDF", arrayOf("application/pdf"), setOf("pdf")),
    SubmissionFormatOption(
        "docx",
        "Word",
        arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        setOf("doc", "docx"),
    ),
    SubmissionFormatOption(
        "pptx",
        "PPT",
        arrayOf("application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        setOf("ppt", "pptx"),
    ),
    SubmissionFormatOption(
        "xlsx",
        "Excel",
        arrayOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        setOf("xls", "xlsx"),
    ),
    SubmissionFormatOption("image", "Picture", arrayOf("image/*"), setOf("gif", "heic", "heif", "jpeg", "jpg", "png", "webp")),
    SubmissionFormatOption("zip", "ZIP", arrayOf("application/zip", "application/x-zip-compressed"), setOf("zip")),
)

internal fun normalizedSubmissionFormat(format: String?): String {
    return when (format?.trim()?.lowercase()) {
        "doc", "word", "docx" -> "docx"
        "ppt", "powerpoint", "pptx" -> "pptx"
        "xls", "excel", "xlsx" -> "xlsx"
        "gif", "heic", "heif", "jpg", "jpeg", "png", "webp", "image", "photo", "picture" -> "image"
        "zip", "compressed" -> "zip"
        else -> format?.trim()?.lowercase()?.takeIf { value ->
            SubmissionFormatOptions.any { it.value == value }
        } ?: "pdf"
    }
}

internal fun submissionFormatOption(format: String?): SubmissionFormatOption {
    val normalized = normalizedSubmissionFormat(format)
    return SubmissionFormatOptions.firstOrNull { it.value == normalized } ?: SubmissionFormatOptions.first()
}

internal fun submissionFormatLabel(format: String?): String = submissionFormatOption(format).label

internal fun acceptedFormatsText(option: SubmissionFormatOption): String {
    return option.extensions.sorted().joinToString(", ") { ".$it" }
}

internal data class SubmissionBlocker(
    val type: NoticeType,
    val title: String,
    val message: String,
)

internal fun submissionBlockingReason(
    isSubmitting: Boolean,
    locked: Boolean,
    expired: Boolean,
    internetRequired: Boolean,
    requiresFile: Boolean,
    hasValidFile: Boolean,
    selectedFileInvalid: Boolean,
    hasChanges: Boolean,
): SubmissionBlocker? {
    return when {
        isSubmitting -> SubmissionBlocker(
            NoticeType.INFO,
            "Saving submission",
            "Please wait while your file and notes are being saved.",
        )
        locked -> SubmissionBlocker(
            NoticeType.LOCKED,
            "Submission locked",
            "Submission is locked by your professor.",
        )
        expired -> SubmissionBlocker(
            NoticeType.EXPIRED,
            "Deadline passed",
            "The deadline has passed.",
        )
        internetRequired -> SubmissionBlocker(
            NoticeType.OFFLINE,
            "Internet required",
            "Connect to the internet to submit.",
        )
        selectedFileInvalid -> SubmissionBlocker(
            NoticeType.ERROR,
            "Unsupported file",
            "Choose a file that matches the required format.",
        )
        requiresFile && !hasValidFile -> SubmissionBlocker(
            NoticeType.WARNING,
            "Required file missing",
            "Attach the required file before submitting.",
        )
        !hasChanges -> SubmissionBlocker(
            NoticeType.INFO,
            "No changes to submit",
            "Edit your notes or choose a replacement file before updating.",
        )
        else -> null
    }
}

internal fun selectedFileName(context: android.content.Context, uri: Uri): String {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return uri.lastPathSegment.orEmpty()
}

internal fun submissionFileFormatError(
    context: android.content.Context,
    uri: Uri,
    requiredFormat: String?,
): String? {
    val option = submissionFormatOption(requiredFormat)
    val fileName = selectedFileName(context, uri)
    val extension = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
    val mimeType = context.contentResolver.getType(uri).orEmpty().lowercase()
    val matches = when (option.value) {
        "image" -> extension in option.extensions || mimeType.startsWith("image/")
        else -> extension in option.extensions || mimeType in option.pickerMimeTypes.map { it.lowercase() }
    }
    return if (matches) null else "This file type is not accepted. Required: ${option.label} (${acceptedFormatsText(option)})."
}

@Composable
internal fun StudentScheduleScreen(
    classes: List<StudentClass>,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onOpenClass: (StudentClass) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 12.dp,
            bottom = innerPadding.calculateBottomPadding() + 18.dp,
            start = 12.dp,
            end = 12.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Schedule",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "My classes",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        ClassScheduleDays.forEach { day ->
            val dayClasses = classes
                .filter { classItem -> day.value in orderedClassScheduleDays(classItem.scheduleDays) }
                .sortedWith(
                    compareBy<StudentClass> { parseClassScheduleTime(it.scheduleStartTime) ?: LocalTime.MAX }
                        .thenBy { it.displaySubjectCode }
                        .thenBy { it.displayClassName },
                )
            item(key = "schedule-${day.value}") {
                StudentScheduleDaySection(
                    day = day,
                    classes = dayClasses,
                    onOpenClass = onOpenClass,
                )
            }
        }
    }
}

@Composable
internal fun StudentScheduleDaySection(
    day: ClassScheduleDay,
    classes: List<StudentClass>,
    onOpenClass: (StudentClass) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (day.value == "saturday") Color(0xFFF4F0FF) else Color(0xFFEAF0FF))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = day.fullLabel,
                    color = if (day.value == "saturday") Color(0xFF6D28D9) else PanthraaBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                )
                if (day.value == "saturday") {
                    Text(
                        text = "Special schedule",
                        color = Color(0xFF6D28D9),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE2E8F0),
            )

            if (classes.isEmpty()) {
                Text(
                    text = "No classes scheduled.",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            } else {
                classes.forEachIndexed { index, classItem ->
                    if (index > 0) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    StudentScheduleClassRow(
                        classItem = classItem,
                        onOpenClass = { onOpenClass(classItem) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun StudentScheduleClassRow(
    classItem: StudentClass,
    onOpenClass: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val scheduleLabel = compactClassScheduleLabel(
        days = classItem.scheduleDays,
        startTime = classItem.scheduleStartTime,
        endTime = classItem.scheduleEndTime,
    ) ?: "Schedule not set"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenClass)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = classItem.displaySubjectCode.take(3).ifBlank { "CLS" }.uppercase(Locale.getDefault()),
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = classItem.displayClassName,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = scheduleLabel,
                color = Color(0xFF0F766E),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open",
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(16.dp),
        )
    }
}
