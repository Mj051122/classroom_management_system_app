package com.myapplication.panthraa.ui

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.myapplication.panthraa.model.AdminNotification
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun NotificationsScreen(
    uiState: MainUiState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    internetRequired: Boolean,
    onLoadNotifications: () -> Unit,
    onPullRefresh: () -> Unit,
    onMarkRead: (Long) -> Unit,
    onMarkUnread: (Long) -> Unit,
    onBack: () -> Unit,
) {
    var selectedNotification by remember { mutableStateOf<AdminNotification?>(null) }
    val notifications = uiState.notifications

    LaunchedEffect(Unit) {
        onLoadNotifications()
    }

    if (selectedNotification != null) {
        NotificationDetailsDialog(
            notification = selectedNotification!!,
            onDismiss = { selectedNotification = null },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 6.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PanthraaIconAction(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Notifications",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    lineHeight = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Updates from the school administration",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        PanthraaPullRefresh(
            isRefreshing = uiState.isLoadingNotifications,
            onRefresh = onPullRefresh,
            enabled = !internetRequired,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                uiState.isLoadingNotifications && notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        PanthraaLoadingAnimation(size = 144.dp)
                    }
                }

                notifications.isEmpty() -> {
                    PanthraaEmptyState(
                        icon = Icons.Outlined.NotificationsNone,
                        title = "No notifications yet",
                        subtitle = "Updates from the school administration will appear here.",
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = innerPadding.calculateTopPadding() + 8.dp,
                            bottom = innerPadding.calculateBottomPadding() + 24.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(
                            items = notifications,
                            key = { it.id },
                        ) { notification ->
                            var cardEntered by remember(notification.id) { mutableStateOf(false) }
                            LaunchedEffect(notification.id) { cardEntered = true }
                            NotificationCard(
                                notification = notification,
                                internetRequired = internetRequired,
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = tween(240, easing = LinearOutSlowInEasing),
                                        fadeOutSpec = tween(160, easing = FastOutLinearInEasing),
                                    )
                                    .aliveEntrance(entered = cardEntered, delayMillis = 0),
                                onOpen = {
                                    selectedNotification = notification
                                },
                                onMarkRead = {
                                    onMarkRead(notification.id)
                                },
                                onMarkUnread = {
                                    onMarkUnread(notification.id)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AdminNotification,
    internetRequired: Boolean,
    onOpen: () -> Unit,
    onMarkRead: () -> Unit,
    onMarkUnread: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unread = !notification.isRead
    val backgroundColor = if (unread) Color(0xFFEAF0FF) else Color.White
    val strokeColor = if (unread) Color(0xFFFECACA) else Color(0xFFD8DEE9)
    val titleColor = if (unread) Color(0xFF0F172A) else Color(0xFF334155)

    Surface(
        onClick = onOpen,
        enabled = !internetRequired,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, strokeColor),
        shadowElevation = if (unread) 2.dp else 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .size(10.dp)
                        .background(
                            color = if (unread) Color(0xFFEF4444) else Color.Transparent,
                            shape = CircleShape,
                        ),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = notification.title,
                        color = titleColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = notification.message,
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = notificationRelativeTime(notification.createdAt),
                        color = if (unread) Color(0xFFDC2626) else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = if (unread) onMarkRead else onMarkUnread,
                enabled = !internetRequired,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (unread) PanthraaBlue else Color(0xFFF1F5F9),
                    contentColor = if (unread) Color.White else Color(0xFF334155),
                ),
            ) {
                Text(
                    text = if (unread) "Mark as read" else "Mark as unread",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun NotificationDetailsDialog(
    notification: AdminNotification,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DialogSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(DialogSoftBlue),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = PanthraaBlue,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = notification.title,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            lineHeight = 20.sp,
                        )
                        Text(
                            text = notificationRelativeTime(notification.createdAt),
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    PanthraaIconAction(
                        icon = Icons.Filled.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text(
                    text = notification.message,
                    color = Color(0xFF334155),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PanthraaBlue),
                ) {
                    Text(
                        text = "Close",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

internal fun notificationRelativeTime(rawDate: String): String {
    val created = parseServerDateTimeInPhilippines(rawDate) ?: return rawDate
    val now = ZonedDateTime.now(PhilippineZoneId)
    val minutes = Duration.between(created, now).toMinutes().coerceAtLeast(0)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 24 * 60 -> "${minutes / 60}h ago"
        minutes < 7 * 24 * 60 -> "${minutes / (24 * 60)}d ago"
        else -> created.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
    }
}
