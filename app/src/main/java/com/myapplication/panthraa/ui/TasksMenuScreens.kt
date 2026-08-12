package com.myapplication.panthraa.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.content.Intent
import android.net.Uri
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

internal enum class TaskMode {
    Calendar,
    Notepad,
    Announcements,
}

internal data class AnnouncementTargetOption(
    val label: String,
    val value: String?,
)

internal val AnnouncementTargetOptions = listOf(
    AnnouncementTargetOption("All years", null),
    AnnouncementTargetOption("First Year", "first"),
    AnnouncementTargetOption("Second Year", "second"),
    AnnouncementTargetOption("Third Year", "third"),
    AnnouncementTargetOption("Fourth Year", "fourth"),
)

internal val TaskDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

@Composable
fun TasksScreen(
    currentUser: AppUser,
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onLoadTaskReminders: (AppUser) -> Unit,
    onCreateTaskReminder: (String, String, String, String, String?, String) -> Unit,
    onDeleteTaskReminder: (String) -> Unit,
    onToggleTaskCompletion: (String, Boolean) -> Unit,
    onLoadAnnouncements: (AppUser) -> Unit = {},
    onRefreshAnnouncements: (AppUser) -> Unit = {},
    onToggleAnnouncementReadStatus: (String, Boolean) -> Unit = { _, _ -> },
    onCreateAnnouncement: (String, String, String, List<String>, Uri?) -> Unit = { _, _, _, _, _ -> },
    onUpdateAnnouncement: (String, String, String, String, List<String>, Uri?, String?) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteAnnouncement: (String) -> Unit = {},
) {
    var showAnnouncementDialog by remember { mutableStateOf(false) }
    var editingAnnouncement by remember { mutableStateOf<ClassAnnouncement?>(null) }
    val isStudent = currentUser.role.equals("student", ignoreCase = true)
    val isProfessor = currentUser.role.equals("professor", ignoreCase = true)
    val internetRequired = uiState.isOfflineMode || uiState.connectivityStatus != ConnectivityStatus.Online

    LaunchedEffect(currentUser.id) {
        onLoadAnnouncements(currentUser)
    }

    PanthraaPullRefresh(
        isRefreshing = RefreshSurface.Announcements in uiState.refreshingSurfaces,
        onRefresh = { onRefreshAnnouncements(currentUser) },
        modifier = Modifier.fillMaxSize(),
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding() + 16.dp,
            bottom = innerPadding.calculateBottomPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "News Feed",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "Class announcements and updates",
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
                if (isProfessor) {
                    Button(
                        onClick = { showAnnouncementDialog = true },
                        enabled = !internetRequired && !uiState.isCreatingAnnouncement,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PanthraaBlue),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("Post", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }

        if (internetRequired && isProfessor) {
            item { InternetRequiredHint() }
        }

        item {
            AnnouncementListContent(
                announcements = uiState.classAnnouncements,
                isLoading = uiState.isLoadingAnnouncements,
                isStudent = isStudent,
                isProfessor = isProfessor,
                isMutating = uiState.isUpdatingAnnouncement || uiState.isDeletingAnnouncement,
                onToggleReadStatus = onToggleAnnouncementReadStatus,
                onEditAnnouncement = { editingAnnouncement = it },
                onDeleteAnnouncement = onDeleteAnnouncement,
            )
        }
    }
    }

    if (showAnnouncementDialog) {
        CreateAnnouncementDialog(
            onDismiss = { showAnnouncementDialog = false },
            onSubmit = { title, subtitle, content, targetYears, imageUri, _ ->
                onCreateAnnouncement(title, subtitle, content, targetYears, imageUri)
                showAnnouncementDialog = false
            },
            isSubmitting = uiState.isCreatingAnnouncement
        )
    }

    editingAnnouncement?.let { announcement ->
        CreateAnnouncementDialog(
            announcement = announcement,
            onDismiss = { editingAnnouncement = null },
            onSubmit = { title, subtitle, content, targetYears, imageUri, existingImageUrl ->
                onUpdateAnnouncement(announcement.id, title, subtitle, content, targetYears, imageUri, existingImageUrl)
                editingAnnouncement = null
            },
            isSubmitting = uiState.isUpdatingAnnouncement,
        )
    }
}

@Composable
internal fun TaskCalendarView(
    reminders: List<TaskReminder>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = selectedDate.withDayOfMonth(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.dayOfWeek.value % 7 // 0 for Sunday
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B)
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            val totalCells = ((daysInMonth + firstDayOfWeek) / 7 + 1) * 7
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(max = 300.dp),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(totalCells) { index ->
                    val dayOfMonth = index - firstDayOfWeek + 1
                    if (dayOfMonth in 1..daysInMonth) {
                        val cellDate = currentMonth.withDayOfMonth(dayOfMonth)
                        val isSelected = cellDate == selectedDate
                        val dateString = cellDate.toString()
                        val dayTasks = reminders.filter { it.reminderDate == dateString }
                        val isToday = cellDate == LocalDate.now(PhilippineZoneId)
                        
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFFEAF0FF) else Color.Transparent)
                                .border(
                                    width = if (isToday && !isSelected) 1.dp else 0.dp,
                                    color = if (isToday && !isSelected) Color(0xFFCBD5E1) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(cellDate) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    color = if (isSelected) Color(0xFF0034DE) else Color(0xFF334155),
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                if (dayTasks.isNotEmpty()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        dayTasks.take(3).forEach { task ->
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Color(android.graphics.Color.parseColor(task.themeColor))
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
internal fun TaskModeToggle(
    selectedMode: TaskMode,
    onModeSelected: (TaskMode) -> Unit,
    isStudent: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val modes = TaskMode.entries
        modes.forEach { mode ->
            val selected = selectedMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) Color.White else Color.Transparent)
                    .clickable(onClick = { onModeSelected(mode) })
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (mode) {
                        TaskMode.Calendar -> "Schedule"
                        TaskMode.Notepad -> "Notes"
                        TaskMode.Announcements -> "Announcements"
                    },
                    color = if (selected) Color(0xFF1D4ED8) else Color(0xFF64748B),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
internal fun AddTaskTypeDialog(
    isProfessor: Boolean,
    onDismiss: () -> Unit,
    onSelect: (TaskMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = "Add",
                subtitle = "Choose what to create.",
                icon = Icons.Filled.Add,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DialogOptionCard(
                    title = "Note",
                    subtitle = "Save a quick reminder.",
                    icon = Icons.Outlined.Lightbulb,
                    tint = Color(0xFF7C3AED),
                    onClick = { onSelect(TaskMode.Notepad) },
                )
                DialogOptionCard(
                    title = "Schedule",
                    subtitle = "Create a dated plan.",
                    icon = Icons.Filled.Event,
                    tint = PanthraaBlue,
                    onClick = { onSelect(TaskMode.Calendar) },
                )
                if (isProfessor) {
                    DialogOptionCard(
                        title = "Announcement",
                        subtitle = "Post an update to students.",
                        icon = Icons.Outlined.Insights,
                        tint = Color(0xFF0F766E),
                        onClick = { onSelect(TaskMode.Announcements) },
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            DialogCancelButton(onClick = onDismiss)
        },
    )
}

@Composable
internal fun AddTaskReminderDialog(
    mode: TaskMode,
    today: LocalDate,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onCreateTaskReminder: (String, String, String, String, String?, String) -> Unit,
) {
    var title by remember(mode) { mutableStateOf("") }
    var details by remember(mode) { mutableStateOf("") }
    var reminderDate by remember(mode) { mutableStateOf(today.toString()) }
    var reminderTime by remember(mode) { mutableStateOf("") }
    
    val colors = listOf("#0034DE", "#8B5CF6", "#10B981", "#F59E0B", "#EF4444", "#EC4899", "#64748B")
    var selectedColor by remember { mutableStateOf(colors[0]) }
    
    val isNote = mode == TaskMode.Notepad

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = if (isNote) "Add note" else "Add schedule",
                subtitle = if (isNote) "Save a quick note." else "Create a dated reminder.",
                icon = if (isNote) Icons.Outlined.Lightbulb else Icons.Filled.Event,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (isNote) "Note title" else "Schedule title") },
                    singleLine = true,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (isNote) "Note" else "Details") },
                    minLines = if (isNote) 3 else 2,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                if (!isNote) {
                    OutlinedTextField(
                        value = reminderDate,
                        onValueChange = { reminderDate = it.take(10) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Reminder date") },
                        placeholder = { Text(today.toString()) },
                        singleLine = true,
                        enabled = !isSaving,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it.take(5) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Time, optional") },
                        placeholder = { Text("08:30") },
                        singleLine = true,
                        enabled = !isSaving,
                        shape = RoundedCornerShape(16.dp),
                        colors = modernTextFieldColors(),
                    )
                }
                Text("Color tag", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colors.forEach { colorHex ->
                        val color = Color(android.graphics.Color.parseColor(colorHex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = 2.dp,
                                    color = if (selectedColor == colorHex) Color(0xFF111827) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = if (isSaving) "Saving..." else "Save",
                onClick = {
                    onCreateTaskReminder(
                        if (isNote) "note" else "schedule",
                        title,
                        details,
                        reminderDate,
                        reminderTime.ifBlank { null },
                        selectedColor
                    )
                },
                enabled = title.trim().isNotEmpty() &&
                    reminderDate.trim().isNotEmpty() &&
                    !isSaving,
            )
        },
        dismissButton = {
            DialogCancelButton(onClick = onDismiss, enabled = !isSaving)
        },
    )
}

@Composable
internal fun AddAnnouncementDialog(
    isPosting: Boolean,
    onDismiss: () -> Unit,
    onPostAnnouncement: (String, String, String?) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTarget by remember { mutableStateOf(AnnouncementTargetOptions.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = "Post announcement",
                subtitle = "Share an update with students.",
                icon = Icons.Outlined.Insights,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") },
                    singleLine = true,
                    enabled = !isPosting,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Announcement") },
                    minLines = 4,
                    enabled = !isPosting,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                Text(
                    text = "Post to",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    AnnouncementTargetOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(enabled = !isPosting) { selectedTarget = option }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = selectedTarget.value == option.value,
                                onClick = { selectedTarget = option },
                                enabled = !isPosting,
                            )
                            Text(
                                text = option.label,
                                color = Color(0xFF111827),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            DialogPrimaryButton(
                text = if (isPosting) "Posting..." else "Post",
                onClick = {
                    onPostAnnouncement(
                        title.trim(),
                        content.trim(),
                        selectedTarget.value,
                    )
                },
                enabled = title.trim().isNotEmpty() && content.trim().isNotEmpty() && !isPosting,
            )
        },
        dismissButton = {
            DialogCancelButton(onClick = onDismiss, enabled = !isPosting)
        },
    )
}

@Composable
internal fun SavedTaskReminderList(
    title: String,
    emptyText: String,
    reminders: List<TaskReminder>,
    isLoading: Boolean,
    isDeleting: Boolean,
    onDeleteTaskReminder: (String) -> Unit,
    onToggleTaskCompletion: (String, Boolean) -> Unit,
) {
    val sortedReminders = reminders.sortedWith(
        compareBy<TaskReminder> { it.isCompleted }
            .thenBy { it.reminderDate.ifBlank { "9999-12-31" } }
            .thenBy { it.reminderTime.orEmpty() }
            .thenBy { it.title },
    )

    Text(
        text = title,
        color = Color(0xFF111827),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    when {
        isLoading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        sortedReminders.isEmpty() -> TaskEmptyState(emptyText)
        else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            sortedReminders.forEach { reminder ->
                TaskReminderCard(
                    reminder = reminder,
                    isDeleting = isDeleting,
                    onDelete = onDeleteTaskReminder,
                    onToggleCompletion = onToggleTaskCompletion,
                )
            }
        }
    }
}

@Composable
internal fun TaskReminderCard(
    reminder: TaskReminder,
    isDeleting: Boolean,
    onDelete: (String) -> Unit,
    onToggleCompletion: ((String, Boolean) -> Unit)? = null,
) {
    val isNote = reminder.isNote
    val accentColor = Color(android.graphics.Color.parseColor(reminder.themeColor))
    val typeBackground = if (reminder.isCompleted) Color(0xFFE2E8F0) else accentColor.copy(alpha = 0.1f)
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted) Color(0xFFF1F5F9) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (reminder.isCompleted) 0.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(if (reminder.isCompleted) Color(0xFFCBD5E1) else accentColor)
            )
            Column(
                modifier = Modifier.padding(16.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                        if (onToggleCompletion != null && !isNote) {
                            androidx.compose.material3.Checkbox(
                                checked = reminder.isCompleted,
                                onCheckedChange = { onToggleCompletion(reminder.id, it) },
                                modifier = Modifier.padding(end = 8.dp).size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = reminder.title,
                                color = if (reminder.isCompleted) Color(0xFF94A3B8) else Color(0xFF1E293B),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textDecoration = if (reminder.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                            )
                            if (!isNote) {
                                Text(
                                    text = reminderLabel(reminder),
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isNote) "Note" else "Schedule",
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(typeBackground)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = if (reminder.isCompleted) Color(0xFF64748B) else readableContentColor(typeBackground, dark = accentColor),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
                if (reminder.details.isNotBlank()) {
                    Text(
                        text = reminder.details,
                        color = if (reminder.isCompleted) Color(0xFF94A3B8) else Color(0xFF475569),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp, start = if (onToggleCompletion != null && !isNote) 32.dp else 0.dp),
                        textDecoration = if (reminder.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    )
                }
                if (reminder.id.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            text = "Delete",
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isDeleting, onClick = { onDelete(reminder.id) })
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (isDeleting) Color(0xFF9CA3AF) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun TaskEmptyState(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

internal fun reminderLabel(reminder: TaskReminder): String {
    val date = runCatching {
        LocalDate.parse(reminder.reminderDate).format(TaskDateFormatter)
    }.getOrDefault(reminder.reminderDate.ifBlank { "No date" })
    val time = reminder.reminderTime?.takeIf { it.isNotBlank() } ?: "Any time"
    return "$date - $time"
}

@Composable
fun MenuScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onPickImage: () -> Unit,
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
    onLogout: () -> Unit,
) {
    var showSettings by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    val internetRequired = uiState.isOfflineMode || uiState.connectivityStatus != ConnectivityStatus.Online

    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(
                top = innerPadding.calculateTopPadding() + if (showSettings) 0.dp else 16.dp,
                bottom = innerPadding.calculateBottomPadding() + if (showSettings) 0.dp else 16.dp,
                start = if (showSettings) 0.dp else 16.dp,
                end = if (showSettings) 0.dp else 16.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showSettings) {
            SettingsContent(
                uiState = uiState,
                onBack = { showSettings = false },
                onPickImage = onPickImage,
                onSaveProfile = onSaveProfile,
                internetRequired = internetRequired,
            )
        } else {
            uiState.currentUser?.let { user ->
                SettingsProfileCard(
                    user = user,
                    isUploading = uiState.isUploadingProfilePicture,
                    onAction = { showSettings = true },
                    showActionIcon = true,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Log out")
            }
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
}

@Composable
internal fun SettingsContent(
    uiState: MainUiState,
    onBack: () -> Unit,
    onPickImage: () -> Unit,
    onSaveProfile: (String, String?, String?, String?, String?, String?, String?) -> Unit,
    internetRequired: Boolean,
) {
    val user = uiState.currentUser ?: return
    val isProfessor = user.role.equals("professor", ignoreCase = true)
    var nameDraft by remember(user.id, user.fullName) { mutableStateOf(user.fullName) }
    var bioDraft by remember(user.id, user.bio) { mutableStateOf(user.bio ?: "") }
    var phoneDraft by remember(user.id, user.phoneNumber) { mutableStateOf(user.phoneNumber ?: "") }
    var courseDraft by remember(user.id, user.course) { mutableStateOf(user.course ?: "") }
    var yearDraft by remember(user.id, user.year) { mutableStateOf(normalizeProfileYear(user.year)) }
    var sectionDraft by remember(user.id, user.section) { mutableStateOf(user.section ?: "") }
    var trackDraft by remember(user.id, user.track) { mutableStateOf(user.track ?: "") }
    
    var showEditFields by remember(user.id) { mutableStateOf(false) }
    val isSaving = uiState.isUpdatingProfileDetails
    val isBusy = internetRequired || isSaving || uiState.isUploadingProfilePicture

    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(34.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827),
                )
            }
            Text(
                text = "settings",
                color = Color(0xFF111827),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        ProfilePreviewCard(
            user = user,
            isUploading = uiState.isUploadingProfilePicture,
            isBusy = isBusy,
            internetRequired = internetRequired,
            onPickImage = onPickImage,
            onEditProfile = { showEditFields = !showEditFields },
        )

        if (showEditFields) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProfileTextField(
                    label = "Full name",
                    value = nameDraft,
                    onValueChange = { nameDraft = it },
                    enabled = !internetRequired && !isSaving,
                )

                ProfileTextField(
                    label = "Bio",
                    value = bioDraft,
                    onValueChange = { bioDraft = it },
                    enabled = !internetRequired && !isSaving,
                )
                ProfileTextField(
                    label = "Phone Number",
                    value = phoneDraft,
                    onValueChange = { phoneDraft = it },
                    enabled = !internetRequired && !isSaving,
                )
                ProfileTextField(
                    label = "Department",
                    value = courseDraft,
                    onValueChange = { courseDraft = it },
                    enabled = !internetRequired && !isSaving,
                    placeholder = if (isProfessor) "e.g. CCSICT" else null,
                )
                if (!isProfessor) {
                    ProfileYearSelector(
                        value = yearDraft,
                        onValueChange = { yearDraft = it },
                        enabled = !internetRequired && !isSaving,
                    )
                    ProfileTextField(
                        label = "Section",
                        value = sectionDraft,
                        onValueChange = { sectionDraft = it },
                        enabled = !internetRequired && !isSaving,
                    )
                    ProfileTextField(
                        label = "Track",
                        value = trackDraft,
                        onValueChange = { trackDraft = it },
                        enabled = !internetRequired && !isSaving,
                    )
                }
                if (internetRequired) {
                    InternetRequiredHint()
                }
                PrimaryButton(
                    text = if (isSaving) "Saving..." else "Save changes",
                    onClick = {
                        val cleanDepartment = courseDraft.trim()
                        onSaveProfile(
                            nameDraft, 
                            cleanDepartment.takeIf { it.isNotBlank() }, 
                            if (isProfessor) user.year else yearDraft.takeIf { it.isNotBlank() },
                            if (isProfessor) user.section else sectionDraft.takeIf { it.isNotBlank() },
                            if (isProfessor) user.track else trackDraft.takeIf { it.isNotBlank() },
                            bioDraft.takeIf { it.isNotBlank() }, 
                            phoneDraft.takeIf { it.isNotBlank() }
                        )
                        showEditFields = false
                    },
                    enabled = nameDraft.trim().isNotEmpty() && !isBusy,
                    isLoading = isSaving,
                )
            }
        }
    }
}

@Composable
internal fun ProfilePreviewCard(
    user: AppUser,
    isUploading: Boolean,
    isBusy: Boolean,
    internetRequired: Boolean,
    onPickImage: () -> Unit,
    onEditProfile: () -> Unit,
) {
    val roleLabel = if (user.role.equals("professor", ignoreCase = true)) "Professor" else "Student"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center,
        ) {
            Avatar(
                imageUrl = user.profilePictureUrl,
                name = user.fullName,
                modifier = Modifier.size(116.dp),
                initialFontSize = 34.sp,
                placeholderColor = Color(0xFF08C86B),
                showInitial = true,
                initialColor = Color.White,
                showBorder = false,
            )
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(34.dp), strokeWidth = 2.dp)
            }
            Image(
                painter = painterResource(R.drawable.edit_photo),
                contentDescription = "Edit photo",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(46.dp)
                    .clickable(enabled = !isBusy, onClick = onPickImage),
                contentScale = ContentScale.Fit,
            )
        }
        if (internetRequired) {
            InternetRequiredHint()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = user.fullName.ifBlank { "$roleLabel name" },
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Image(
                painter = painterResource(R.drawable.edit_name),
                contentDescription = "Edit name",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(enabled = !isBusy, onClick = onEditProfile),
                contentScale = ContentScale.Fit,
            )
        }
        Text(
            text = user.idNumber.ifBlank { "${roleLabel.lowercase()} id number" },
            color = Color.Black,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = user.email.ifBlank { "" },
            color = Color(0xFF64748B),
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        
        Spacer(modifier = Modifier.size(4.dp))
        
        Text(
            text = AcademicLine(user)?.uppercase() ?: user.role.uppercase(),
            color = Color(0xFF5B3FDB),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        if (!user.bio.isNullOrBlank()) {
            Text(
                text = user.bio,
                color = Color(0xFF475569),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
            )
        }

        if (!user.phoneNumber.isNullOrBlank()) {
            Text(
                text = user.phoneNumber,
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
internal fun ProfileYearSelector(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Year",
            color = Color(0xFF111827),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfessorClassYearOptions.chunked(2).forEach { options ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    options.forEach { option ->
                        val selected = value.equals(option.value, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable(enabled = enabled) { onValueChange(option.value) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) Color(0xFF5B3FDB) else Color.White,
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (selected) Color(0xFF5B3FDB) else Color(0xFFD1D5DB),
                            ),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = option.label,
                                    color = if (selected) Color.White else Color(0xFF111827),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                    if (options.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    placeholder: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = Color(0xFF111827),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = enabled,
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF111827),
                unfocusedTextColor = Color(0xFF111827),
                disabledTextColor = Color(0xFF111827),
                focusedBorderColor = Color(0xFF5B3FDB),
                unfocusedBorderColor = Color(0xFFD1D5DB),
                disabledBorderColor = Color(0xFFD1D5DB),
                cursorColor = Color(0xFF5B3FDB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
            ),
        )
    }
}

@Composable
internal fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5B3FDB),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFCCC6F4),
            disabledContentColor = Color(0xFF4C1D95),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF1EEFF),
            contentColor = Color(0xFF4C1D95),
            disabledContainerColor = Color(0xFFF5F3FF),
            disabledContentColor = Color(0xFF4C1D95),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = Color(0xFF4C1D95),
            )
        } else {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
        }
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun SettingsProfileCard(
    user: AppUser,
    isUploading: Boolean,
    onAction: () -> Unit,
    showActionIcon: Boolean,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAction)
            .padding(vertical = 8.dp),
    ) {
         val compact = maxWidth < 340.dp
        val avatarSize = if (compact) 72.dp else 92.dp
        val nameSize = if (compact) 14.sp else 17.sp
        val courseSize = if (compact) 21.sp else 27.sp

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Avatar(
                    imageUrl = user.profilePictureUrl,
                    name = user.fullName,
                    modifier = Modifier
                        .size(avatarSize)
                        .clickable(onClick = onAction),
                    initialFontSize = if (compact) 28.sp else 34.sp,
                    placeholderColor = Color(0xFF08C86B),
                    initialColor = Color.White,
                    showBorder = false,
                )
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(if (compact) 28.dp else 34.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (compact) 12.dp else 18.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = user.fullName,
                        modifier = Modifier.weight(1f),
                        color = Color.Black,
                        fontSize = nameSize,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 9.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                    )
                    Text(
                        text = user.idNumber,
                        color = Color.Black,
                        fontSize = nameSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = user.email,
                    color = Color(0xFF64748B),
                    fontSize = if (compact) 12.sp else 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = user.role.uppercase(),
                    fontSize = courseSize,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF7A2F),
                                Color(0xFFFF2E2E),
                                Color(0xFF8A2FA8),
                            ),
                        ),
                        fontSize = courseSize,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
            }

            if (showActionIcon) {
                IconButton(
                    onClick = onAction,
                    enabled = !isUploading,
                    modifier = Modifier.size(if (compact) 42.dp else 50.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Edit profile",
                        modifier = Modifier.size(if (compact) 34.dp else 42.dp),
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProfileIdentity(user: AppUser) {
    Text(
        text = "${user.fullName} . ${user.idNumber}",
        color = Color(0xFF0F172A),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
    )
    AcademicLine(user)?.let { line ->
        Text(
            text = line,
            color = Color(0xFF64748B),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

internal fun AcademicLine(user: AppUser): String? {
    val yearSection = profileAcademicLabel(user.year, user.section)
    return listOfNotNull(user.course, yearSection, user.track)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { null }
}

internal fun profileAcademicLabel(year: String?, section: String?): String? {
    return listOfNotNull(profileYearLabel(year), section?.trim()?.takeIf { it.isNotBlank() })
        .joinToString(" ")
        .ifBlank { null }
}

internal fun profileYearLabel(year: String?): String? {
    return when (normalizeProfileYear(year)) {
        "first" -> "First Year"
        "second" -> "Second Year"
        "third" -> "Third Year"
        "fourth" -> "Fourth Year"
        else -> year?.trim()?.takeIf { it.isNotBlank() }
    }
}

internal fun profileYearShortLabel(year: String?): String? {
    return when (normalizeProfileYear(year)) {
        "first" -> "1st"
        "second" -> "2nd"
        "third" -> "3rd"
        "fourth" -> "4th"
        else -> year?.trim()?.takeIf { it.isNotBlank() }
    }
}

internal fun normalizeProfileYear(year: String?): String {
    return when (year?.trim()?.lowercase(Locale.getDefault())) {
        "first", "first year", "1", "1st", "1st year" -> "first"
        "second", "second year", "2", "2nd", "2nd year" -> "second"
        "third", "third year", "3", "3rd", "3rd year" -> "third"
        "fourth", "fourth year", "4", "4th", "4th year" -> "fourth"
        else -> ""
    }
}

@Composable
internal fun PlaceholderScreen(
    title: String,
    onLogout: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title)
        if (onLogout != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onLogout) {
                Text("Log out")
            }
        }
    }
}

@Composable
internal fun ClassmatesPage(
    currentUser: AppUser,
    classItem: StudentClass,
    classmates: List<com.myapplication.panthraa.model.Classmate>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
) {
    PanthraaPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Classmates",
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!isLoading && classmates.isNotEmpty()) {
                Text(
                    text = "${classmates.size} students",
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PanthraaLoadingAnimation(size = 192.dp)
            }
        } else if (classmates.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No classmates found.", color = Color.Gray)
            }
        } else {
            val sortedClassmates = remember(classmates, currentUser.id) {
                classmates.sortedByDescending { it.id == currentUser.id }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                gridItems(sortedClassmates, key = { it.id }) { classmate ->
                    val isCurrentUser = classmate.id == currentUser.id
                    val cardColor = if (classmate.hasSubmittedRecent) Color(0xFFE8F8EC) else Color(0xFFFFECEC)
                    val borderColor = if (classmate.hasSubmittedRecent) Color(0xFF2E7D32) else Color(0xFFC62828)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, borderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Avatar(
                                imageUrl = classmate.photoUrl,
                                name = classmate.name,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (if (isCurrentUser) "You (" else "") + classmate.name.ifBlank { "Unknown" } + (if (isCurrentUser) ")" else ""),
                                color = Color.Black,
                                fontWeight = if (isCurrentUser) FontWeight.ExtraBold else FontWeight.SemiBold,
                                fontSize = 14.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
internal fun ViewStudentsPage(
    classItem: ProfessorClass,
    uiState: MainUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
) {
    PanthraaPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .border(BorderStroke(1.dp, Color(0xFFE5E7EB))),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Text(
                text = classItem.displayClassName,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (uiState.isLoadingProfessorStudents) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PanthraaLoadingAnimation(size = 192.dp)
            }
        } else if (uiState.professorStudents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No students enrolled yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.professorStudents, key = { it.id }) { student ->
                    val cardColor = if (student.hasSubmittedRecent) Color(0xFFE8F8EC) else Color(0xFFFFECEC)
                    val borderColor = if (student.hasSubmittedRecent) Color(0xFF2E7D32) else Color(0xFFC62828)
                    val academic = profileAcademicLabel(student.year, student.section)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = BorderStroke(1.dp, borderColor),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(
                                imageUrl = student.photoUrl,
                                name = student.name,
                                modifier = Modifier.size(48.dp),
                                placeholderColor = Color(0xFFE5E7EB),
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                            ) {
                                Text(
                                    text = student.name.ifBlank { "Unknown Name" },
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                )
                                Text(
                                    text = "ID: ${student.idNumber.ifBlank { "N/A" }}",
                                    color = Color.DarkGray,
                                    fontSize = 13.sp,
                                )
                                if (academic != null) {
                                    Text(
                                        text = academic,
                                        color = Color(0xFF4B5563),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Text(
                                text = "${student.completedAssignments}/${student.totalAssignments} submitted",
                                color = if (student.completedAssignments >= student.totalAssignments && student.totalAssignments > 0) Color(0xFF047857) else Color(0xFFBE123C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
internal fun AnnouncementListContent(
    announcements: List<ClassAnnouncement>,
    isLoading: Boolean,
    isStudent: Boolean,
    isProfessor: Boolean,
    isMutating: Boolean,
    onToggleReadStatus: (String, Boolean) -> Unit,
    onEditAnnouncement: (ClassAnnouncement) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
) {
    if (isLoading && announcements.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            PanthraaLoadingAnimation(size = 144.dp)
        }
        return
    }
    
    if (announcements.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No announcements yet.", color = Color.Gray)
        }
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        announcements.forEach { announcement ->
            AnnouncementCard(
                announcement = announcement,
                isStudent = isStudent,
                isProfessor = isProfessor,
                isMutating = isMutating,
                onToggleReadStatus = { isRead ->
                    onToggleReadStatus(announcement.id, isRead)
                },
                onEdit = { onEditAnnouncement(announcement) },
                onDelete = { onDeleteAnnouncement(announcement.id) },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
internal fun AnnouncementCard(
    announcement: ClassAnnouncement,
    isStudent: Boolean,
    isProfessor: Boolean,
    isMutating: Boolean,
    onToggleReadStatus: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val unread = isStudent && !announcement.isRead
    val backgroundColor = if (unread) Color(0xFFFFF1F2) else Color.White
    val strokeColor = if (unread) Color(0xFFFDA4AF) else Color(0xFFD8DEE9)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, strokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Avatar(
                    imageUrl = announcement.professorProfilePicUrl,
                    name = announcement.professorName,
                    modifier = Modifier.size(44.dp),
                    placeholderColor = Color(0xFFE2E8F0),
                    initialFontSize = 14.sp,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = announcement.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${announcement.professorName} - ${formatAnnouncementTime(announcement.createdAt)}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    formatAnnouncementEditedTime(announcement.updatedAt)?.let { editedLabel ->
                        Text(
                            text = editedLabel,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            androidx.compose.material3.HorizontalDivider(color = strokeColor.copy(alpha = 0.7f))

            if (announcement.subtitle.isNotBlank()) {
                Text(
                    text = announcement.subtitle,
                    color = Color(0xFF334155),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(
                text = announcement.content,
                fontSize = 14.sp,
                color = Color(0xFF0F172A),
                lineHeight = 17.sp,
            )
            AnnouncementLinks(content = announcement.content)

            AnnouncementImage(imageUrl = announcement.imageUrl)

            if (isStudent) {
                Button(
                    onClick = { onToggleReadStatus(!announcement.isRead) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (announcement.isRead) Color(0xFF475569) else Color(0xFF1D4ED8)
                    )
                ) {
                    Text(
                        text = if (announcement.isRead) "Mark as Unread" else "Mark as Read",
                        color = Color.White
                    )
                }
            }
            if (isProfessor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onEdit,
                        enabled = !isMutating,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0034DE)),
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("Edit", color = Color.White, modifier = Modifier.padding(start = 6.dp))
                    }
                    Button(
                        onClick = { showDeleteDialog = true },
                        enabled = !isMutating,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C)),
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("Delete", color = Color.White, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isMutating) showDeleteDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = DialogSurface,
            title = {
                DialogHeader(
                    title = "Delete announcement?",
                    subtitle = "This removes it from the feed.",
                    icon = Icons.Filled.Delete,
                    tint = Color(0xFFBE123C),
                )
            },
            text = {
                Text(
                    text = "This removes the announcement for all targeted students.",
                    color = Color(0xFF334155),
                )
            },
            confirmButton = {
                DialogPrimaryButton(
                    text = if (isMutating) "Deleting..." else "Delete",
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    enabled = !isMutating,
                    color = Color(0xFFBE123C),
                )
            },
            dismissButton = {
                DialogCancelButton(onClick = { showDeleteDialog = false }, enabled = !isMutating)
            },
        )
    }
}

@Composable
internal fun AnnouncementImage(imageUrl: String?) {
    var showFullImage by remember { mutableStateOf(false) }
    val modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFEAF0FF))

    if (!imageUrl.isNullOrBlank()) {
        coil.compose.AsyncImage(
            model = imageUrl,
            contentDescription = "Announcement image",
            modifier = modifier.clickable { showFullImage = true },
            contentScale = ContentScale.Crop,
        )

        if (showFullImage) {
            Dialog(
                onDismissRequest = { showFullImage = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.94f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    coil.compose.AsyncImage(
                        model = imageUrl,
                        contentDescription = "Full announcement image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 620.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Fit,
                    )
                    IconButton(
                        onClick = { showFullImage = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f)),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close image preview",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    } else {
        Image(
            painter = painterResource(R.drawable.splash_announcement),
            contentDescription = "Announcement",
            modifier = modifier.padding(18.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
internal fun AnnouncementLinks(content: String) {
    val uriHandler = LocalUriHandler.current
    val links = remember(content) {
        UrlRegex.findAll(content)
            .map { it.value.trimEnd('.', ',', ')', ']') }
            .distinct()
            .toList()
    }
    if (links.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        links.forEach { link ->
            Text(
                text = link,
                color = PanthraaBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFEAF0FF))
                    .clickable { uriHandler.openUri(link) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}

internal val UrlRegex = Regex("""https?://[^\s]+""")

internal fun formatAnnouncementTime(rawDate: String): String {
    return parseServerDateTimeInPhilippines(rawDate)
        ?.format(DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.getDefault()))
        ?: rawDate
}

internal fun formatAnnouncementEditedTime(rawDate: String?): String? {
    val cleanDate = rawDate?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return "(Edited ${formatAnnouncementTime(cleanDate)})"
}

@Composable
internal fun OutlinedButtonCompat(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEAF0FF),
            contentColor = PanthraaBlue,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun CreateAnnouncementDialog(
    announcement: ClassAnnouncement? = null,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, List<String>, Uri?, String?) -> Unit,
    isSubmitting: Boolean
) {
    var title by remember(announcement?.id) { mutableStateOf(announcement?.title.orEmpty()) }
    var subtitle by remember(announcement?.id) { mutableStateOf(announcement?.subtitle.orEmpty()) }
    var content by remember(announcement?.id) { mutableStateOf(announcement?.content.orEmpty()) }
    var selectedImageUri by remember(announcement?.id) { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember(announcement?.id) { mutableStateOf(announcement?.imageUrl) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            existingImageUrl = null
        }
    }
    
    // Target Years Selection State
    var isAllYear by remember(announcement?.id) { mutableStateOf(announcement?.targetYears.isNullOrEmpty()) }
    var selectedYears by remember(announcement?.id) { mutableStateOf(announcement?.targetYears?.toSet().orEmpty()) }

    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DialogSurface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                DialogHeader(
                    title = if (announcement == null) "Create announcement" else "Edit announcement",
                    subtitle = "Post an update to the class feed.",
                    icon = Icons.Outlined.Insights,
                )

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Message") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(16.dp),
                    colors = modernTextFieldColors(),
                )

                val previewModel: Any? = selectedImageUri ?: existingImageUrl
                if (previewModel != null) {
                    coil.compose.AsyncImage(
                        model = previewModel,
                        contentDescription = "Announcement image preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEAF0FF)),
                        contentScale = ContentScale.Crop,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButtonCompat(
                        text = if (previewModel == null) "Add image" else "Change image",
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.weight(1f),
                    )
                    if (previewModel != null) {
                        OutlinedButtonCompat(
                            text = "Remove",
                            onClick = {
                                selectedImageUri = null
                                existingImageUrl = null
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Text(
                    text = "Target Audience",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                )

                // Custom Card "Radio/Checkbox" Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // All Year Card
                    val allYearBg = if (isAllYear) Color(0xFF0034DE) else Color.White
                    val allYearBorder = if (isAllYear) Color(0xFF0034DE) else Color(0xFFCBD5E1)
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isAllYear = true
                                selectedYears = emptySet()
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = allYearBg),
                        border = BorderStroke(1.dp, allYearBorder)
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "All Year",
                                color = if (isAllYear) Color.White else Color(0xFF334155),
                                fontWeight = if (isAllYear) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Multi-select specific years
                    val yearOptions = listOf("first", "second", "third", "fourth")
                    val yearLabels = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        yearOptions.forEachIndexed { index, yearVal ->
                            val isSelected = selectedYears.contains(yearVal)
                            val bg = if (isSelected) Color(0xFF0034DE) else Color.White
                            val border = if (isSelected) Color(0xFF0034DE) else Color(0xFFCBD5E1)
                            
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        isAllYear = false
                                        val newSet = selectedYears.toMutableSet()
                                        if (isSelected) {
                                            newSet.remove(yearVal)
                                            if (newSet.isEmpty()) isAllYear = true // Revert if all unselected
                                        } else {
                                            newSet.add(yearVal)
                                        }
                                        selectedYears = newSet
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = bg),
                                border = BorderStroke(1.dp, border)
                            ) {
                                Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = yearLabels[index].replace(" Year", ""),
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DialogCancelButton(
                            onClick = onDismiss,
                            enabled = !isSubmitting,
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DialogPrimaryButton(
                            text = if (isSubmitting) {
                                if (announcement == null) "Posting..." else "Saving..."
                            } else {
                                if (announcement == null) "Post" else "Save"
                            },
                            onClick = {
                                val targetYears = if (isAllYear) emptyList() else selectedYears.toList()
                                onSubmit(title, subtitle, content, targetYears, selectedImageUri, existingImageUrl)
                            },
                            enabled = title.isNotBlank() && content.isNotBlank() && !isSubmitting,
                        )
                    }
                }
            }
        }
    }
}
