package com.myapplication.panthraa.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.ProfessorClass
import kotlin.random.Random

@Composable
fun CreateClassScreen(
    currentUser: AppUser,
    isCreating: Boolean,
    currentClassCount: Int,
    initialYearLevel: String? = null,
    lockedYearLevel: String? = null,
    initialDepartment: String = "",
    lockedDepartment: String? = null,
    initialSection: String = "",
    lockedSection: String? = null,
    initialTrack: String = "",
    lockedTrack: String? = null,
    initialSubjectCode: String = "",
    existingClasses: List<ProfessorClass> = emptyList(),
    onBack: () -> Unit,
    onCreateClass: (String, String, String, String, String, String, String?, Uri?, String, List<String>, String, String) -> Unit,
) {
    var className by remember { mutableStateOf("") }
    var subjectCode by remember(initialSubjectCode) { mutableStateOf(initialSubjectCode.uppercase()) }
    var joinCode by remember { mutableStateOf("") }
    var yearLevel by remember(initialYearLevel, lockedYearLevel) {
        mutableStateOf(lockedYearLevel ?: initialYearLevel.orEmpty())
    }
    var department by remember(initialDepartment, lockedDepartment) {
        mutableStateOf(lockedDepartment ?: initialDepartment)
    }
    var section by remember(initialSection, lockedSection) {
        mutableStateOf(lockedSection ?: initialSection)
    }
    var track by remember(initialTrack, lockedTrack) {
        mutableStateOf(lockedTrack ?: initialTrack)
    }
    var coverImageUri by remember { mutableStateOf<Uri?>(null) }
    var themeColor by remember { mutableStateOf("blue") }
    var selectedScheduleDays by remember { mutableStateOf<Set<String>>(emptySet()) }
    var scheduleStartTime by remember { mutableStateOf("") }
    var scheduleEndTime by remember { mutableStateOf("") }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var submittedClassCount by remember { mutableStateOf<Int?>(null) }
    val scheduleSummary = compactClassScheduleLabel(
        days = selectedScheduleDays,
        startTime = scheduleStartTime,
        endTime = scheduleEndTime,
    )
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            coverImageUri = uri
        }
    }

    LaunchedEffect(currentClassCount, isCreating) {
        val startCount = submittedClassCount
        if (startCount != null && !isCreating && currentClassCount > startCount) {
            onBack()
        }
    }

    if (showScheduleDialog) {
        ClassScheduleDialog(
            selectedDays = selectedScheduleDays,
            startTime = scheduleStartTime,
            endTime = scheduleEndTime,
            onDismiss = { showScheduleDialog = false },
            onSave = { days, start, end ->
                selectedScheduleDays = days.toSet()
                scheduleStartTime = start
                scheduleEndTime = end
                validationError = null
                showScheduleDialog = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 112.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, enabled = !isCreating) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827),
                )
            }
            Text(
                text = "Create Class",
                color = Color(0xFF111827),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Class Information",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Year level",
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    classYearOptions.forEach { option ->
                        YearChoiceChip(
                            label = option.label,
                            selected = yearLevel == option.value,
                            enabled = !isCreating && lockedYearLevel == null,
                            onClick = {
                                yearLevel = option.value
                                validationError = null
                            },
                        )
                    }
                }
                if (lockedYearLevel != null) {
                    Text(
                        text = "${classYearLabel(lockedYearLevel)} is selected from the year page.",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AcademicDropdownPicker(
                        label = "Course",
                        value = department,
                        placeholder = "Select course",
                        options = fixedCourseOptions,
                        enabled = !isCreating && lockedDepartment == null,
                        addTitle = "Add course",
                        addLabel = "Course name",
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            department = it
                            validationError = null
                        },
                    )
                    AcademicDropdownPicker(
                        label = "Section",
                        value = section,
                        placeholder = "Select section",
                        options = fixedSectionOptions,
                        enabled = !isCreating && lockedSection == null,
                        addTitle = "Add section",
                        addLabel = "Section name",
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            section = it
                            validationError = null
                        },
                    )
                }
                OutlinedTextField(
                    value = track,
                    onValueChange = { track = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Track (optional)") },
                    placeholder = { Text("Mobile Development") },
                    singleLine = true,
                    enabled = !isCreating && lockedTrack == null && lockedSection == null,
                    colors = createClassTextFieldColors(),
                )
                OutlinedTextField(
                    value = className,
                    onValueChange = { className = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Class name") },
                    placeholder = { Text("Data Structures") },
                    singleLine = true,
                    enabled = !isCreating,
                    colors = createClassTextFieldColors(),
                )
                OutlinedTextField(
                    value = subjectCode,
                    onValueChange = { subjectCode = it.uppercase() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Subject code") },
                    placeholder = { Text("IT221") },
                    singleLine = true,
                    enabled = !isCreating,
                    colors = createClassTextFieldColors(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { value ->
                            joinCode = value.filter { it.isDigit() }.take(6)
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Join code") },
                        placeholder = { Text("123456") },
                        singleLine = true,
                        enabled = !isCreating,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = createClassTextFieldColors(),
                    )
                    OutlinedButton(
                        onClick = {
                            joinCode = Random.nextInt(100000, 1000000).toString()
                            validationError = null
                        },
                        enabled = !isCreating,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Generate")
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isCreating) { showScheduleDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = if (scheduleSummary == null) Color(0xFFFFFBEB) else Color(0xFFECFDF5),
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (scheduleSummary == null) Color(0xFFF59E0B).copy(alpha = 0.35f) else Color(0xFF0F766E).copy(alpha = 0.28f),
                    ),
                    shape = RoundedCornerShape(18.dp),
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (scheduleSummary == null) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Filled.Event,
                                contentDescription = null,
                                tint = if (scheduleSummary == null) Color(0xFFD97706) else Color(0xFF0F766E),
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Text(
                                text = if (scheduleSummary == null) "Set schedule (mandatory)" else "Schedule set",
                                color = Color(0xFF111827),
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Text(
                                text = scheduleSummary ?: "Choose days and one time range before creating.",
                                color = if (scheduleSummary == null) Color(0xFF92400E) else Color(0xFF0F766E),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(
                            text = "Edit",
                            color = Color(0xFF0B5CFF),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(22.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Appearance",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Button(
                    onClick = { coverPicker.launch("image/*") },
                    enabled = !isCreating,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                ) {
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        text = if (coverImageUri == null) "Upload cover picture" else "Change cover picture",
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (coverImageUri != null) {
                    TextButton(onClick = { coverImageUri = null }, enabled = !isCreating) {
                        Text("Remove cover picture")
                    }
                }

                Text(
                    text = "Theme color",
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    themePresets.forEach { preset ->
                        ThemeSwatch(
                            name = preset,
                            selected = themeColor == preset,
                            enabled = !isCreating,
                            onClick = { themeColor = preset },
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Preview class card",
                color = Color(0xFF111827),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            ClassCardPreview(
                className = className.ifBlank { "Data Structures" },
                subjectCode = subjectCode.ifBlank { "IT221" },
                joinCode = joinCode.ifBlank { "123456" },
                yearLevel = yearLevel,
                coverImageUrl = null,
                coverImageUri = coverImageUri,
                themeColor = themeColor,
                professorName = currentUser.fullName.ifBlank { "Faculty" },
                professorPhotoUrl = currentUser.profilePictureUrl,
                studentCount = 0,
                progressPercentage = 0,
                scheduleLabel = scheduleSummary,
            )
        }

        validationError?.let { error ->
            Text(
                text = error,
                color = Color(0xFFB91C1C),
                fontWeight = FontWeight.SemiBold,
            )
        }

        Button(
            onClick = {
                val cleanScheduleDays = orderedClassScheduleDays(selectedScheduleDays)
                val cleanScheduleStartTime = normalizeClassScheduleTimeInput(scheduleStartTime)
                val cleanScheduleEndTime = normalizeClassScheduleTimeInput(scheduleEndTime)
                val conflictClass = findScheduleConflict(
                    existingClasses = existingClasses,
                    yearLevel = yearLevel,
                    department = department,
                    section = section,
                    track = track,
                    scheduleDays = cleanScheduleDays,
                    scheduleStartTime = scheduleStartTime,
                    scheduleEndTime = scheduleEndTime,
                )
                val error = validateCreateClassInput(className, subjectCode, joinCode, yearLevel, department, section, track)
                    ?: validateCreateClassSchedule(selectedScheduleDays, scheduleStartTime, scheduleEndTime)
                    ?: conflictClass?.let { "Schedule conflicts with ${it.displayClassName} for the same year, course, section, and track." }
                if (error != null) {
                    validationError = error
                } else if (cleanScheduleStartTime == null || cleanScheduleEndTime == null) {
                    validationError = "Enter a valid schedule time range."
                } else {
                    validationError = null
                    submittedClassCount = currentClassCount
                    onCreateClass(
                        className,
                        subjectCode,
                        joinCode,
                        yearLevel,
                        department,
                        section,
                        track.takeIf { it.isNotBlank() },
                        coverImageUri,
                        themeColor,
                        cleanScheduleDays,
                        cleanScheduleStartTime,
                        cleanScheduleEndTime,
                    )
                }
            },
            enabled = !isCreating && scheduleSummary != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B5CFF)),
        ) {
            if (isCreating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            Text(
                text = if (isCreating) "Creating..." else "Create class",
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ClassScheduleDialog(
    selectedDays: Set<String>,
    startTime: String,
    endTime: String,
    onDismiss: () -> Unit,
    onSave: (List<String>, String, String) -> Unit,
) {
    var draftDays by remember(selectedDays) { mutableStateOf(selectedDays) }
    var draftStartTime by remember(startTime) { mutableStateOf(startTime) }
    var draftEndTime by remember(endTime) { mutableStateOf(endTime) }
    val orderedDays = orderedClassScheduleDays(draftDays)
    val validationMessage = validateCreateClassSchedule(draftDays, draftStartTime, draftEndTime)
    val preview = compactClassScheduleLabel(orderedDays, draftStartTime, draftEndTime)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF0F172A),
        textContentColor = Color(0xFF334155),
        title = {
            Text(
                text = "Set schedule",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Choose the class days and one time range.",
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ClassScheduleDays.forEach { day ->
                        YearChoiceChip(
                            label = day.shortLabel,
                            selected = day.value in draftDays,
                            enabled = true,
                            onClick = {
                                draftDays = if (day.value in draftDays) {
                                    draftDays - day.value
                                } else {
                                    draftDays + day.value
                                }
                            },
                        )
                    }
                }
                if ("saturday" in draftDays) {
                    Text(
                        text = "Saturday will appear as Special schedule.",
                        color = Color(0xFF6D28D9),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        value = draftStartTime,
                        onValueChange = { draftStartTime = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Start") },
                        placeholder = { Text("8:00 AM") },
                        singleLine = true,
                        colors = createClassScheduleFieldColors(),
                    )
                    OutlinedTextField(
                        value = draftEndTime,
                        onValueChange = { draftEndTime = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("End") },
                        placeholder = { Text("12:30 PM") },
                        singleLine = true,
                        colors = createClassScheduleFieldColors(),
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = preview ?: validationMessage ?: "Schedule preview",
                        modifier = Modifier.padding(12.dp),
                        color = if (preview == null) Color(0xFF92400E) else Color(0xFF0F766E),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(orderedDays, draftStartTime.trim(), draftEndTime.trim()) },
                enabled = validationMessage == null,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF0034DE),
                    disabledContentColor = Color(0xFF94A3B8),
                ),
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF64748B),
                ),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun YearChoiceChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val background = when {
        selected -> Color(0xFF111827)
        enabled -> Color.White
        else -> Color(0xFFE5E7EB)
    }
    val textColor = if (selected) Color.White else Color(0xFF334155)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = if (selected) Color(0xFF111827) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(999.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun AcademicDropdownPicker(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    enabled: Boolean,
    addTitle: String,
    addLabel: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    val visibleOptions = remember(options, value) {
        fixedOptionsWithCurrentValue(options, value)
    }
    val accent = Color(0xFF0B5CFF)
    val shape = RoundedCornerShape(14.dp)
    val selectedValue = value.trim()
    val hasValue = selectedValue.isNotBlank()

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(shape)
                .background(if (enabled) Color.White else Color(0xFFF1F5F9))
                .border(
                    width = 1.dp,
                    color = when {
                        hasValue -> accent.copy(alpha = 0.34f)
                        enabled -> Color(0xFFD8E1F1)
                        else -> Color(0xFFE2E8F0)
                    },
                    shape = shape,
                )
                .clickable(enabled = enabled) { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = label,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                )
                Text(
                    text = selectedValue.ifBlank { placeholder },
                    color = if (hasValue) Color(0xFF0F172A) else Color(0xFF64748B),
                    fontWeight = if (hasValue) FontWeight.ExtraBold else FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = if (enabled) Color(0xFF64748B) else Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White),
        ) {
            visibleOptions.forEach { option ->
                val selected = option.equals(selectedValue, ignoreCase = true)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (selected) accent else Color(0xFF0F172A),
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    enabled = enabled,
                )
            }
            DropdownMenuItem(
                text = {
                    Text(
                        text = "+ Custom",
                        color = accent,
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                onClick = {
                    expanded = false
                    showAddDialog = true
                },
                enabled = enabled,
            )
        }
    }

    if (showAddDialog) {
        AddAcademicOptionDialog(
            title = addTitle,
            label = addLabel,
            onDismiss = { showAddDialog = false },
            onAdd = { option ->
                onValueChange(option)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddAcademicOptionDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
) {
    var draft by remember { mutableStateOf("") }
    val cleanDraft = draft.trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                singleLine = true,
                colors = createClassTextFieldColors(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(cleanDraft) },
                enabled = cleanDraft.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun ThemeSwatch(
    name: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val color = themeAccentColor(name)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) Color(0xFF111827) else Color(0xFFE5E7EB),
                    shape = CircleShape,
                )
                .clickable(enabled = enabled, onClick = onClick),
        )
        Text(
            text = name.replaceFirstChar { it.uppercase() },
            color = Color(0xFF334155),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private fun validateCreateClassInput(
    className: String,
    subjectCode: String,
    joinCode: String,
    yearLevel: String,
    department: String,
    section: String,
    track: String,
): String? {
    return when {
        className.trim().isBlank() -> "Class name is required."
        subjectCode.trim().isBlank() -> "Subject code is required."
        yearLevel.trim() !in classYearOptions.map { it.value } -> "Year level is required."
        department.trim().isBlank() -> "Course is required."
        section.trim().isBlank() -> "Section is required."
        joinCode.trim().isBlank() -> "Join code is required."
        !Regex("^\\d{6}$").matches(joinCode.trim()) -> "Join code must be exactly 6 digits."
        else -> null
    }
}

private fun validateCreateClassSchedule(
    selectedDays: Collection<String>,
    startTime: String,
    endTime: String,
): String? {
    val orderedDays = orderedClassScheduleDays(selectedDays)
    return when {
        orderedDays.isEmpty() -> "Set schedule is required."
        normalizeClassScheduleTimeInput(startTime) == null -> "Enter a valid start time."
        normalizeClassScheduleTimeInput(endTime) == null -> "Enter a valid end time."
        !isClassScheduleRangeValid(startTime, endTime) -> "Schedule end time must be after the start time."
        else -> null
    }
}

private fun findScheduleConflict(
    existingClasses: List<ProfessorClass>,
    yearLevel: String,
    department: String,
    section: String,
    track: String,
    scheduleDays: Collection<String>,
    scheduleStartTime: String,
    scheduleEndTime: String,
): ProfessorClass? {
    val cleanDays = orderedClassScheduleDays(scheduleDays)
    if (cleanDays.isEmpty()) return null

    return existingClasses.firstOrNull { classItem ->
        sameScheduleCohortValue(classItem.yearLevel, yearLevel) &&
            sameScheduleCohortValue(classItem.department, department) &&
            sameScheduleCohortValue(classItem.section, section) &&
            sameScheduleCohortValue(classItem.track, track) &&
            orderedClassScheduleDays(classItem.scheduleDays).any { it in cleanDays } &&
            classScheduleRangesOverlap(
                scheduleStartTime,
                scheduleEndTime,
                classItem.scheduleStartTime,
                classItem.scheduleEndTime,
            )
    }
}

private fun sameScheduleCohortValue(left: String?, right: String?): Boolean {
    return left.orEmpty().trim().equals(right.orEmpty().trim(), ignoreCase = true)
}

private val themePresets = listOf("blue", "purple", "green", "orange", "red", "dark", "pink", "teal", "yellow", "cyan", "indigo")

private data class ClassYearOption(
    val value: String,
    val label: String,
)

private val classYearOptions = listOf(
    ClassYearOption("first", "First Year"),
    ClassYearOption("second", "Second Year"),
    ClassYearOption("third", "Third Year"),
    ClassYearOption("fourth", "Fourth Year"),
)

private val fixedCourseOptions = listOf(
    "BSIT",
    "BSCS",
    "BSIS",
    "BSCrim",
    "BSHM",
    "BSTM",
    "BSA",
    "BSCpE",
    "BSBA",
)

private val fixedSectionOptions = listOf(
    "1A",
    "1B",
    "1C",
    "2A",
    "2B",
    "2C",
    "3A",
    "3B",
    "3C",
)

private fun classYearLabel(value: String?): String {
    return classYearOptions.firstOrNull { it.value == value }?.label ?: "Unassigned"
}

private fun fixedOptionsWithCurrentValue(options: List<String>, currentValue: String): List<String> {
    val cleanValue = currentValue.trim()
    return if (cleanValue.isBlank() || options.any { it.equals(cleanValue, ignoreCase = true) }) {
        options
    } else {
        options + cleanValue
    }
}

@Composable
private fun createClassTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF0F172A),
    disabledTextColor = Color(0xFF334155),
    focusedLabelColor = Color(0xFF0B5CFF),
    unfocusedLabelColor = Color(0xFF334155),
    disabledLabelColor = Color(0xFF64748B),
    focusedPlaceholderColor = Color(0xFF94A3B8),
    unfocusedPlaceholderColor = Color(0xFF94A3B8),
    disabledPlaceholderColor = Color(0xFF94A3B8),
    cursorColor = Color(0xFF0B5CFF),
    focusedBorderColor = Color(0xFF0B5CFF),
    unfocusedBorderColor = Color(0xFFCBD5E1),
    disabledBorderColor = Color(0xFFE2E8F0),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color(0xFFF1F5F9),
)

@Composable
private fun createClassScheduleFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF0F172A),
    disabledTextColor = Color(0xFF64748B),
    focusedLabelColor = Color(0xFF0034DE),
    unfocusedLabelColor = Color(0xFF475569),
    disabledLabelColor = Color(0xFF94A3B8),
    focusedPlaceholderColor = Color(0xFF94A3B8),
    unfocusedPlaceholderColor = Color(0xFF94A3B8),
    disabledPlaceholderColor = Color(0xFFCBD5E1),
    cursorColor = Color(0xFF0034DE),
    focusedBorderColor = Color(0xFF0034DE),
    unfocusedBorderColor = Color(0xFF94A3B8),
    disabledBorderColor = Color(0xFFCBD5E1),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color(0xFFF1F5F9),
)
