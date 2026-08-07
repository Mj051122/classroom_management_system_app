package com.myapplication.panthraa.ui

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image as ImageIcon
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.panthraa.data.OfflineImageCache
import com.myapplication.panthraa.model.StudentClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StudentClassCard(
    classItem: StudentClass,
    onOpenClass: () -> Unit,
    onViewClassmates: () -> Unit,
) {
    val accent = themeAccentColor(classItem.themeColor)
    val deepAccent = darkerClassAccent(accent)
    val progress = if (classItem.totalAssignments > 0) {
        (classItem.completedAssignments.toFloat() / classItem.totalAssignments.toFloat()) * 100f
    } else {
        classItem.progressPercentage.toFloat()
    }.coerceIn(0f, 100f)
    val bitmap = rememberImageBitmap(imageUrl = classItem.coverImageUrl, imageUri = null)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenClass),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accent,
                            deepAccent,
                            Color(0xFF0F172A),
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
                    center = Offset(size.width * 0.94f, size.height * 0.10f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.07f),
                    radius = size.width * 0.20f,
                    center = Offset(size.width * 0.06f, size.height * 1.02f),
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.06f),
                    topLeft = Offset(0f, size.height * 0.58f),
                    size = Size(size.width, size.height * 0.42f),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = classItem.displaySubjectCode.ifBlank { "Subject" },
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(
                            text = classItem.displayClassName.ifBlank { "Class name" },
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        classYearLabel(classItem.yearLevel)?.let { label ->
                            Text(
                                text = label,
                                color = Color.White.copy(alpha = 0.78f),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        compactClassScheduleLabel(
                            days = classItem.scheduleDays,
                            startTime = classItem.scheduleStartTime,
                            endTime = classItem.scheduleEndTime,
                        )?.let { schedule ->
                            Text(
                                text = schedule,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .border(1.dp, Color.White.copy(alpha = 0.24f), RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Class cover image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Text(
                                text = classItem.displaySubjectCode.take(3).ifBlank { "CLS" }.uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProfessorAvatar(
                        imageUrl = classItem.professorPhotoUrl,
                        name = classItem.displayProfessorName,
                        modifier = Modifier.size(44.dp),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        Text(
                            text = classItem.displayProfessorName.ifBlank { "Professor" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Professor",
                            color = Color.White.copy(alpha = 0.72f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    TextButton(
                        onClick = onViewClassmates,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            containerColor = Color.White.copy(alpha = 0.16f),
                            contentColor = Color.White,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "${classItem.studentCount.coerceAtLeast(0)}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(start = 5.dp),
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Completion",
                            color = Color.White.copy(alpha = 0.80f),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = "${progress.toInt()}% completed",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = Color.White,
                        trackColor = Color.Black.copy(alpha = 0.24f),
                    )
                    Text(
                        text = "${classItem.completedAssignments} / ${classItem.totalAssignments} submissions",
                        color = Color.White.copy(alpha = 0.76f),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun ClassCardPreview(
    className: String,
    subjectCode: String,
    joinCode: String,
    yearLevel: String? = null,
    coverImageUrl: String?,
    coverImageUri: Uri?,
    themeColor: String,
    professorName: String,
    professorPhotoUrl: String?,
    studentCount: Int,
    progressPercentage: Int,
    modifier: Modifier = Modifier,
    scheduleLabel: String? = null,
) {
    ClassCardContent(
        className = className,
        subjectCode = subjectCode,
        joinCode = joinCode,
        yearLevel = yearLevel,
        coverImageUrl = coverImageUrl,
        coverImageUri = coverImageUri,
        themeColor = themeColor,
        professorName = professorName,
        professorPhotoUrl = professorPhotoUrl,
        professorRole = "Professor",
        studentCount = studentCount,
        progressPercentage = progressPercentage,
        completedAssignments = 0,
        totalAssignments = 0,
        showJoinCode = true,
        scheduleLabel = scheduleLabel,
        onViewClassmates = null,
        modifier = modifier.fillMaxWidth(),
        footer = {
            val progress = progressPercentage.toFloat().coerceIn(0f, 100f)
            val footerTextColor = readableContentColor(themeAccentColor(themeColor))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Completion",
                    color = footerTextColor.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = "0/0 completed",
                    color = footerTextColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = Color.White,
                trackColor = Color.Black.copy(alpha = 0.22f),
            )
        }
    )
}

@Composable
fun ProfessorClassCard(
    classItem: com.myapplication.panthraa.model.ProfessorClass,
    status: com.myapplication.panthraa.model.AssignmentStatus?,
    onOpenClass: () -> Unit,
    onViewStudents: () -> Unit,
) {
    val submittedCount = status?.submittedCount ?: 0
    val missingCount = status?.missingCount ?: 0
    val totalStudents = status?.totalStudents ?: classItem.studentCount.coerceAtLeast(0)

    ClassCardContent(
        className = classItem.displayClassName,
        subjectCode = classItem.displaySubjectCode,
        joinCode = classItem.displayJoinCode,
        yearLevel = classItem.yearLevel,
        coverImageUrl = classItem.coverImageUrl,
        coverImageUri = null,
        themeColor = classItem.themeColor,
        professorName = classItem.professorName,
        professorPhotoUrl = classItem.professorPhotoUrl,
        professorRole = "Professor",
        studentCount = totalStudents,
        progressPercentage = 0,
        completedAssignments = 0,
        totalAssignments = 0,
        showJoinCode = true,
        scheduleLabel = compactClassScheduleLabel(
            days = classItem.scheduleDays,
            startTime = classItem.scheduleStartTime,
            endTime = classItem.scheduleEndTime,
        ),
        onViewClassmates = onViewStudents,
        viewButtonText = "View students",
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenClass),
        footer = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "Created ${classItem.createdAt?.take(10) ?: ""}",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelMedium,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssignmentStatusPill(count = missingCount, color = Color(0xFFFF4D57))
                    AssignmentStatusPill(count = submittedCount, color = Color(0xFFD7FF62))
                }
            }
        }
    )
}

@Composable
private fun AssignmentStatusPill(
    count: Int,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp).padding(end = 2.dp))
            Text(
                text = count.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(24.dp, 4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color)
        )
    }
}

@Composable
fun ClassCardContent(
    className: String,
    subjectCode: String,
    joinCode: String,
    yearLevel: String?,
    coverImageUrl: String?,
    coverImageUri: Uri?,
    themeColor: String,
    professorName: String,
    professorPhotoUrl: String?,
    professorRole: String,
    studentCount: Int,
    progressPercentage: Int,
    completedAssignments: Int,
    totalAssignments: Int,
    showJoinCode: Boolean,
    scheduleLabel: String? = null,
    onViewClassmates: (() -> Unit)? = null,
    viewButtonText: String = "View classmates",
    modifier: Modifier = Modifier,
    footer: @Composable () -> Unit,
) {
    val accent = themeAccentColor(themeColor)
    val contentColor = readableContentColor(accent)
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = accent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ClassCover(
                imageUrl = coverImageUrl,
                imageUri = coverImageUri,
                subjectCode = subjectCode,
                accent = accent,
                onViewClassmates = onViewClassmates,
                viewButtonText = viewButtonText,
            )

            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ClassMetaPill(text = subjectCode.ifBlank { "SUBJECT" })
                        classYearLabel(yearLevel)?.let { label ->
                            ClassMetaPill(text = label, muted = true)
                        }
                    }
                    if (showJoinCode) {
                        Text(
                            text = "Join $joinCode",
                            color = contentColor.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(joinCode))
                                    android.widget.Toast.makeText(context, "Join code copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = className.ifBlank { "Class name" },
                    color = contentColor,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                scheduleLabel?.let { schedule ->
                    Text(
                        text = schedule,
                        color = contentColor.copy(alpha = 0.92f),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfessorAvatar(
                        imageUrl = professorPhotoUrl,
                        name = professorName,
                        modifier = Modifier.size(52.dp),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                    ) {
                        Text(
                            text = professorName.ifBlank { "Professor" },
                            color = contentColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = professorRole.ifBlank { "Professor" },
                            color = contentColor.copy(alpha = 0.78f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.People,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = studentCount.coerceAtLeast(0).toString(),
                            modifier = Modifier.padding(start = 4.dp),
                            color = contentColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                footer()
            }
        }
    }
}

@Composable
private fun ClassMetaPill(
    text: String,
    muted: Boolean = false,
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White.copy(alpha = if (muted) 0.76f else 0.92f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        color = Color(0xFF111827),
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.labelMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun classYearLabel(yearLevel: String?): String? {
    return when (yearLevel?.trim()?.lowercase()) {
        "first" -> "First Year"
        "second" -> "Second Year"
        "third" -> "Third Year"
        "fourth" -> "Fourth Year"
        else -> null
    }
}

@Composable
private fun ClassCover(
    imageUrl: String?,
    imageUri: Uri?,
    subjectCode: String,
    accent: Color,
    onViewClassmates: (() -> Unit)? = null,
    viewButtonText: String = "View classmates",
) {
    val bitmap = rememberImageBitmap(imageUrl = imageUrl, imageUri = imageUri)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Class cover image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ImageIcon,
                    contentDescription = null,
                    tint = accent.copy(alpha = 0.72f),
                    modifier = Modifier.size(30.dp),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subjectCode.ifBlank { "Cover image" },
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (onViewClassmates != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                androidx.compose.material3.TextButton(
                    onClick = onViewClassmates,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        containerColor = Color.White.copy(alpha = 0.9f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = viewButtonText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfessorAvatar(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
) {
    val bitmap = rememberImageBitmap(imageUrl = imageUrl, imageUri = null)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .border(1.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "$name profile photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "P",
                color = Color(0xFF111827),
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun rememberImageBitmap(imageUrl: String?, imageUri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    var bitmap by remember(imageUrl, imageUri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl, imageUri) {
        bitmap = null
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                OfflineImageCache.loadBitmap(
                    context = context,
                    imageUrl = imageUrl,
                    imageUri = imageUri,
                )?.asImageBitmap()
            }.getOrNull()
        }
    }

    return bitmap
}

fun themeAccentColor(themeColor: String): Color {
    return when (themeColor.trim().lowercase()) {
        "purple" -> Color(0xFF6D28D9)
        "green" -> Color(0xFF047857)
        "orange" -> Color(0xFFC2410C)
        "red" -> Color(0xFFBE123C)
        "dark" -> Color(0xFF334155)
        "pink" -> Color(0xFFBE185D)
        "teal" -> Color(0xFF0F766E)
        "yellow" -> Color(0xFFA16207)
        "cyan" -> Color(0xFF0E7490)
        "indigo" -> Color(0xFF4338CA)
        else -> Color(0xFF1D4ED8)
    }
}

internal fun darkerClassAccent(color: Color): Color {
    return Color(
        red = (color.red * 0.68f).coerceIn(0f, 1f),
        green = (color.green * 0.68f).coerceIn(0f, 1f),
        blue = (color.blue * 0.68f).coerceIn(0f, 1f),
        alpha = 1f,
    )
}
