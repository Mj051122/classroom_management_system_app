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
import androidx.compose.ui.draw.drawBehind
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

internal val PhilippineZoneId: ZoneId = ZoneId.of("Asia/Manila")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PanthraaPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indicatorTopPadding: Dp = 18.dp,
    refreshThreshold: Dp = 104.dp,
    content: @Composable () -> Unit,
) {
    val refreshState = rememberPullToRefreshState()
    val pullProgress = refreshState.distanceFraction.coerceIn(0f, 1.25f)
    val maxContentPull = 92.dp
    val refreshingContentOffset = 76.dp
    val targetContentOffset = when {
        isRefreshing -> refreshingContentOffset
        pullProgress > 0f -> maxContentPull * pullProgress.coerceIn(0f, 1f)
        else -> 0.dp
    }
    val contentOffset by animateDpAsState(
        targetValue = targetContentOffset,
        animationSpec = tween(
            durationMillis = if (isRefreshing || pullProgress == 0f) 180 else 0,
        ),
        label = "pull_refresh_content_offset",
    )
    val loaderVisible = isRefreshing || pullProgress > 0.08f
    val loaderAlpha by animateFloatAsState(
        targetValue = if (loaderVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 120),
        label = "pull_refresh_loader_alpha",
    )

    Box(
        modifier = modifier
            .background(Color(0xFFF8FBFF))
            .pullToRefresh(
                isRefreshing = isRefreshing,
                state = refreshState,
                enabled = enabled,
                threshold = refreshThreshold,
                onRefresh = onRefresh,
            ),
    ) {
        Box(modifier = Modifier.graphicsLayer { translationY = contentOffset.toPx() }) {
            content()
        }
        if (loaderVisible || loaderAlpha > 0.01f) {
            PanthraaPullRefreshIndicator(
                isRefreshing = isRefreshing,
                pullProgress = pullProgress,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = indicatorTopPadding)
                    .graphicsLayer {
                        alpha = loaderAlpha
                        val loaderScale = if (isRefreshing) 1f else 0.72f + (pullProgress.coerceAtMost(1f) * 0.28f)
                        scaleX = loaderScale
                        scaleY = loaderScale
                    },
            )
        }
    }
}

@Composable
internal fun PanthraaPullRefreshIndicator(
    isRefreshing: Boolean,
    pullProgress: Float,
    modifier: Modifier = Modifier,
) {
    val progress = pullProgress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(96.dp)
            .height(76.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isRefreshing || progress >= 0.92f) {
            PanthraaLoadingAnimation(size = 72.dp)
        } else {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(alpha = 0.94f))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(24.dp)) {
                    val strokeWidth = 3.dp.toPx()
                    drawArc(
                        color = PanthraaBlue.copy(alpha = 0.85f),
                        startAngle = -90f,
                        sweepAngle = 300f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
            }
        }
    }
}

@Composable
internal fun OfflineReviewBanner(
    uiState: MainUiState,
) {
    val showBanner = uiState.isOfflineMode ||
        uiState.connectivityStatus == ConnectivityStatus.Connecting ||
        uiState.lastOnlineRefreshFailed
    if (!showBanner) return

    val background = when {
        uiState.connectivityStatus == ConnectivityStatus.Online -> Color(0xFFFFF7ED)
        uiState.connectivityStatus == ConnectivityStatus.Connecting -> Color(0xFFFFF7ED)
        else -> Color(0xFFFFECEC)
    }
    val textColor = when {
        uiState.connectivityStatus == ConnectivityStatus.Online -> Color(0xFF9A3412)
        uiState.connectivityStatus == ConnectivityStatus.Connecting -> Color(0xFF9A3412)
        else -> Color(0xFFB91C1C)
    }
    val message = when {
        uiState.connectivityStatus == ConnectivityStatus.Connecting -> "Connecting..."
        uiState.lastOnlineRefreshFailed && uiState.connectivityStatus == ConnectivityStatus.Online ->
            "Refresh failed. Showing last saved data."
        else -> "Offline mode: showing last saved data."
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun ConnectivityDot(
    status: ConnectivityStatus,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color.White)
            .padding(3.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(connectivityStatusColor(status)),
        )
    }
}

@Composable
internal fun ConnectivityStatusPill(
    status: ConnectivityStatus,
    modifier: Modifier = Modifier,
    onDarkBackground: Boolean = false,
) {
    val textColor = if (onDarkBackground) Color.White else Color(0xFF0F172A)
    val background = if (onDarkBackground) Color.White.copy(alpha = 0.16f) else Color(0xFFF8FAFC)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 11.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(connectivityStatusColor(status)),
        )
        Text(
            text = connectivityStatusText(status),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

internal fun connectivityStatusColor(status: ConnectivityStatus): Color {
    return when (status) {
        ConnectivityStatus.Online -> Color(0xFF22C55E)
        ConnectivityStatus.Offline -> Color(0xFFEF4444)
        ConnectivityStatus.Connecting -> Color(0xFFF97316)
    }
}

internal fun connectivityStatusText(status: ConnectivityStatus): String {
    return when (status) {
        ConnectivityStatus.Online -> "Online"
        ConnectivityStatus.Offline -> "Offline"
        ConnectivityStatus.Connecting -> "Connecting"
    }
}

@Composable
internal fun InternetRequiredHint(modifier: Modifier = Modifier) {
    Text(
        text = "Internet required.",
        color = Color(0xFFB91C1C),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(top = 4.dp),
    )
}

internal enum class NoticeType {
    INFO,
    WARNING,
    ERROR,
    OFFLINE,
    LOCKED,
    EXPIRED,
}

internal data class NoticeTone(
    val background: Color,
    val border: Color,
    val content: Color,
    val icon: ImageVector,
)

internal fun noticeTone(type: NoticeType): NoticeTone {
    return when (type) {
        NoticeType.INFO -> NoticeTone(Color(0xFFEAF0FF), Color(0xFFBFDBFE), PanthraaBlue, Icons.Filled.Description)
        NoticeType.WARNING -> NoticeTone(Color(0xFFFFFBEB), Color(0xFFFBBF24), Color(0xFF92400E), Icons.Filled.Event)
        NoticeType.ERROR -> NoticeTone(Color(0xFFFEE2E2), Color(0xFFFCA5A5), Color(0xFFB91C1C), Icons.Filled.Close)
        NoticeType.OFFLINE -> NoticeTone(Color(0xFFFFECEC), Color(0xFFFCA5A5), Color(0xFFB91C1C), Icons.Filled.Close)
        NoticeType.LOCKED -> NoticeTone(Color(0xFFEFF6FF), Color(0xFF93C5FD), Color(0xFF1E40AF), Icons.Filled.Event)
        NoticeType.EXPIRED -> NoticeTone(Color(0xFF374151), Color(0xFF1F2937), Color.White, Icons.Filled.Close)
    }
}

@Composable
internal fun PanthraaStatusNotice(
    type: NoticeType,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    actionEnabled: Boolean = true,
    onAction: (() -> Unit)? = null,
) {
    val tone = noticeTone(type)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = tone.background,
        border = BorderStroke(1.dp, tone.border.copy(alpha = 0.75f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(11.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tone.content.copy(alpha = if (type == NoticeType.EXPIRED) 0.18f else 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tone.icon,
                    contentDescription = null,
                    tint = tone.content,
                    modifier = Modifier.size(17.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = tone.content,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = message,
                    color = if (type == NoticeType.EXPIRED) Color(0xFFE5E7EB) else tone.content,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                )
            }
            if (actionText != null && onAction != null) {
                TextButton(
                    onClick = onAction,
                    enabled = actionEnabled,
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    Text(actionText, color = tone.content, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
internal fun PanthraaIconAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = PanthraaBlue,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else Color(0xFFCBD5E1),
            modifier = Modifier.size(22.dp),
        )
    }
}

/** Shared brand accent so the dashboard matches the landing screen's #0034DE. */
internal val PanthraaBlue = Color(0xFF2563EB)
internal val DialogSurface = Color(0xFFFFFFFF)
internal val DialogSoftBlue = Color(0xFFEAF0FF)

internal fun panthraaSoftBackground() = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF8FBFF),
        Color(0xFFEFF4FF),
        Color(0xFFF7F2FF),
        Color(0xFFEFFAF7),
        Color(0xFFFAFCFF),
    ),
    start = Offset(0f, 0f),
    end = Offset(900f, 1500f),
)

internal fun Modifier.panthraaScreenBackground(): Modifier = this
    .background(panthraaSoftBackground())
    .drawBehind {
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(PanthraaBlue.copy(alpha = 0.09f), Color.Transparent),
                center = Offset(size.width * 0.08f, size.height * 0.02f),
                radius = size.width * 0.86f,
            ),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF0F766E).copy(alpha = 0.07f), Color.Transparent),
                center = Offset(size.width * 1.02f, size.height * 0.34f),
                radius = size.width * 0.72f,
            ),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6D28D9).copy(alpha = 0.055f), Color.Transparent),
                center = Offset(size.width * 0.18f, size.height * 0.88f),
                radius = size.width * 0.78f,
            ),
        )
    }

internal enum class AttendanceButtonState {
    NotChecked,
    Present,
    Absent,
}

internal data class AttendanceButtonStyle(
    val state: AttendanceButtonState,
    val background: Color,
    val content: Color,
    val border: Color,
    val label: String,
)

internal fun attendanceButtonStyle(status: String?): AttendanceButtonStyle {
    return when (status?.trim()?.lowercase()) {
        "present" -> AttendanceButtonStyle(
            state = AttendanceButtonState.Present,
            background = Color(0xFFDCFCE7),
            content = Color(0xFF047857),
            border = Color(0xFF86EFAC),
            label = "Present",
        )
        "absent" -> AttendanceButtonStyle(
            state = AttendanceButtonState.Absent,
            background = Color(0xFFFEE2E2),
            content = Color(0xFFBE123C),
            border = Color(0xFFFCA5A5),
            label = "Absent",
        )
        else -> AttendanceButtonStyle(
            state = AttendanceButtonState.NotChecked,
            background = Color(0xFFF1F5F9),
            content = Color(0xFF64748B),
            border = Color(0xFFCBD5E1),
            label = "Attendance",
        )
    }
}

@Composable
internal fun DialogHeader(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    tint: Color = PanthraaBlue,
    @DrawableRes iconRes: Int? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(tint),
                    modifier = Modifier.size(26.dp),
                )
            } else if (icon != null) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
            )
            Text(
                text = subtitle,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
internal fun DialogPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    color: Color = PanthraaBlue,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun DialogCancelButton(
    text: String = "Cancel",
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(text, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DialogSurface,
        title = {
            DialogHeader(
                title = "Log out and clear cache?",
                subtitle = "Saved offline data will be removed from this device.",
                icon = Icons.Filled.Logout,
                tint = Color(0xFFBE123C),
            )
        },
        text = {
            Text(
                text = "You'll need to sign in and reconnect to download your offline data again.",
                color = Color(0xFF334155),
            )
        },
        confirmButton = {
            DialogPrimaryButton(
                text = "Log out",
                onClick = onConfirm,
                enabled = true,
                color = Color(0xFFBE123C),
            )
        },
        dismissButton = {
            DialogCancelButton(
                text = "Stay signed in",
                onClick = onDismiss,
            )
        },
    )
}

@Composable
internal fun DialogOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = tint,
            )
        }
    }
}

@Composable
internal fun modernTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF0F172A),
    unfocusedTextColor = Color(0xFF0F172A),
    focusedLabelColor = PanthraaBlue,
    unfocusedLabelColor = Color(0xFF64748B),
    focusedBorderColor = PanthraaBlue,
    unfocusedBorderColor = Color(0xFFD8DEE9),
    cursorColor = PanthraaBlue,
)

@Composable
internal fun PanthraaLoadingAnimation(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_session))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(size),
    )
}

@Composable
internal fun DashboardOverview(
    user: AppUser,
    uiState: MainUiState,
) {
    val isStudent = user.role.equals("student", ignoreCase = true)
    val classCount = if (isStudent) uiState.studentClasses.size else uiState.professorClasses.size
    val notesCount = uiState.taskReminders.count { it.isNote }
    val scheduleCount = uiState.taskReminders.count { !it.isNote }
    val progressText = if (isStudent) {
        val progress = uiState.studentClasses
            .map { it.progressPercentage.coerceIn(0, 100) }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.toInt() ?: 0
        "$progress%"
    } else {
        uiState.assignmentStatuses.values.sumOf { it.missingCount }.toString()
    }
    val progressLabel = if (isStudent) "avg progress" else "missing"

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Dashboard",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
            )
            Text(
                text = "Welcome back, ${user.fullName.ifBlank { "Student" }}",
                color = Color(0xFF334155),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                DashboardStatCard(
                    label = "Classes",
                    value = classCount.toString(),
                    modifier = Modifier.width(100.dp)
                )
            }
            item {
                DashboardStatCard(
                    label = progressLabel,
                    value = progressText,
                    modifier = Modifier.width(100.dp)
                )
            }
            item {
                DashboardStatCard(
                    label = "Notes",
                    value = notesCount.toString(),
                    modifier = Modifier.width(100.dp)
                )
            }
            item {
                DashboardStatCard(
                    label = "Schedules",
                    value = scheduleCount.toString(),
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}

@Composable
internal fun DashboardStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
            )
            Text(
                text = label,
                color = Color(0xFF475569),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun DashboardSectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
internal fun DashboardClassesPreview(
    user: AppUser,
    uiState: MainUiState,
) {
    val isStudent = user.role.equals("student", ignoreCase = true)

    when {
        uiState.isLoadingClasses -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PanthraaLoadingAnimation(size = 144.dp)
        }
        isStudent && uiState.studentClasses.isEmpty() -> DashboardEmptyCard("No classes yet.")
        !isStudent && uiState.professorClasses.isEmpty() -> DashboardEmptyCard("No classes created yet.")
        isStudent -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.studentClasses.take(3).forEach { classItem ->
                DashboardClassRow(
                    title = classItem.displayClassName,
                    subtitle = "${classItem.displaySubjectCode} - ${classItem.progressPercentage.coerceIn(0, 100)}%",
                    trailing = "${classItem.studentCount} students",
                )
            }
        }
        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.professorClasses.take(3).forEach { classItem ->
                val status = uiState.assignmentStatuses[classItem.id]
                DashboardClassRow(
                    title = classItem.displayClassName,
                    subtitle = "code: ${classItem.displayJoinCode}",
                    trailing = "${status?.missingCount ?: 0} missing",
                )
            }
        }
    }
}

@Composable
internal fun DashboardClassRow(
    title: String,
    subtitle: String,
    trailing: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD8DEE9)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF475569),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = trailing,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
internal fun DashboardTasksPreview(uiState: MainUiState) {
    val reminders = uiState.taskReminders.filterNot { it.isCompleted }.sortedWith(
        compareBy<TaskReminder> { it.reminderDate.ifBlank { "9999-12-31" } }
            .thenBy { it.reminderTime.orEmpty() }
            .thenBy { it.title },
    )

    when {
        uiState.isLoadingTaskReminders -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PanthraaLoadingAnimation(size = 144.dp)
        }
        reminders.isEmpty() -> DashboardEmptyCard("No saved notes or schedules.")
        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            reminders.take(3).forEach { reminder ->
                DashboardTaskRow(reminder)
            }
        }
    }
}

@Composable
internal fun DashboardTaskRow(reminder: TaskReminder) {
    val accent = Color(android.graphics.Color.parseColor(reminder.themeColor))
    Card(
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reminder.title,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${if (reminder.isNote) "Note" else "Schedule"} - ${reminderLabel(reminder)}",
                color = Color(0xFF475569),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun DashboardEmptyCard(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD8DEE9)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = text,
            color = Color(0xFF64748B),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
internal fun PanthraaEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PanthraaBlue.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = PanthraaBlue.copy(alpha = 0.5f),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = Color(0xFF64748B),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = PanthraaBlue),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = actionLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
internal fun UserHeader(
    user: AppUser,
    isUploading: Boolean,
    connectivityStatus: ConnectivityStatus,
    internetRequired: Boolean,
    onPickImage: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Avatar(
                    imageUrl = user.profilePictureUrl,
                    name = user.fullName,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = !internetRequired, onClick = onPickImage),
                )
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                }
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                ProfileIdentity(user = user)
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(user.role.replaceFirstChar { it.uppercase() }) },
                    )
                    ConnectivityStatusPill(status = connectivityStatus)
                }
            }
        }
    }
}

@Composable
fun UserListScreen(users: List<AppUser>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users, key = { it.id }) { user ->
            UserListItem(user = user)
        }
    }
}

@Composable
internal fun UserListItem(user: AppUser) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                imageUrl = user.profilePictureUrl,
                name = user.fullName,
                modifier = Modifier.size(48.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(text = user.fullName, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Text(text = user.idNumber, color = Color(0xFF64748B), style = MaterialTheme.typography.bodySmall)
            }
            AssistChip(
                onClick = {},
                label = { Text(user.role.replaceFirstChar { it.uppercase() }) },
            )
        }
    }
}

@Composable
internal fun Avatar(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    initialFontSize: TextUnit = 18.sp,
    placeholderColor: Color = Color(0xFFE2E8F0),
    showInitial: Boolean = true,
    initialColor: Color = Color(0xFF64748B),
    showBorder: Boolean = true,
) {
    var bitmap by remember(imageUrl) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    val context = LocalContext.current
    val avatarModifier = if (showBorder) {
        modifier
            .clip(CircleShape)
            .background(placeholderColor)
            .border(1.dp, Color(0xFFD8DEE9), CircleShape)
    } else {
        modifier
            .clip(CircleShape)
            .background(placeholderColor)
    }

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
        modifier = avatarModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = "$name profile picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else if (showInitial) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = initialColor,
                fontWeight = FontWeight.Bold,
                fontSize = initialFontSize,
            )
        }
    }
}
