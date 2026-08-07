package com.myapplication.panthraa.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.myapplication.panthraa.MainActivity
import com.myapplication.panthraa.data.OfflineReviewCacheStore
import com.myapplication.panthraa.ui.DashboardScheduleEntry
import com.myapplication.panthraa.ui.PhilippineZoneId
import com.myapplication.panthraa.ui.professorDashboardTodaySchedule
import com.myapplication.panthraa.ui.studentDashboardTodaySchedule
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object PanthraaScheduleWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            CompactWidgetSize,
            MediumWidgetSize,
            LargeWidgetSize,
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = ScheduleWidgetRepository.load(context)
        provideContent {
            PanthraaScheduleWidgetContent(state = state)
        }
    }
}

class PanthraaScheduleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PanthraaScheduleWidget

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        PanthraaScheduleWidgetUpdater.refreshAndSchedule(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        PanthraaScheduleWidgetUpdater.cancel(context)
    }
}

internal sealed interface ScheduleWidgetState {
    data object NeedsSetup : ScheduleWidgetState
    data object NeedsSync : ScheduleWidgetState

    data class Ready(
        val roleLabel: String,
        val userName: String,
        val dayLabel: String,
        val classCount: Int,
        val scheduledCount: Int,
        val current: ScheduleWidgetEntry?,
        val next: ScheduleWidgetEntry?,
        val previous: ScheduleWidgetEntry?,
        val entries: List<ScheduleWidgetEntry>,
        val syncedLabel: String,
    ) : ScheduleWidgetState
}

internal data class ScheduleWidgetEntry(
    val id: String,
    val subjectName: String,
    val subjectCode: String,
    val timeRange: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isCurrent: Boolean,
)

internal object ScheduleWidgetRepository {
    fun load(
        context: Context,
        now: LocalDateTime = LocalDateTime.now(PhilippineZoneId),
    ): ScheduleWidgetState {
        val store = OfflineReviewCacheStore(context.applicationContext)
        val user = store.getLastUser() ?: return ScheduleWidgetState.NeedsSetup
        val cache = store.getCacheForUser(user.id) ?: return ScheduleWidgetState.NeedsSync
        val isProfessor = user.role.equals("professor", ignoreCase = true)
        val today = if (isProfessor) {
            professorDashboardTodaySchedule(cache.professorClasses, now)
        } else {
            studentDashboardTodaySchedule(cache.studentClasses, now)
        }
        val classCount = if (isProfessor) cache.professorClasses.size else cache.studentClasses.size
        val entries = today.entries.map { entry ->
            entry.toWidgetEntry(isCurrent = today.current?.classId == entry.classId)
        }
        val currentEntry = today.current
        val previous = if (currentEntry != null) {
            val idx = today.entries.indexOfFirst { it.classId == currentEntry.classId }
            if (idx > 0) today.entries[idx - 1]?.toWidgetEntry(isCurrent = false) else null
        } else {
            today.entries.lastOrNull { it.endTime.isBefore(now.toLocalTime()) }?.toWidgetEntry(isCurrent = false)
        }
        val firstName = user.fullName.substringBefore(" ").trim()
        return ScheduleWidgetState.Ready(
            roleLabel = if (isProfessor) "Professor" else "Student",
            userName = firstName,
            dayLabel = today.dayLabel,
            classCount = classCount,
            scheduledCount = today.scheduledCount,
            current = today.current?.toWidgetEntry(isCurrent = true),
            next = today.next?.toWidgetEntry(isCurrent = false),
            previous = previous,
            entries = entries,
            syncedLabel = syncedLabel(cache.savedAtMillis),
        )
    }

    fun nextRefreshDelayMillis(context: Context): Long? {
        val ready = load(context.applicationContext) as? ScheduleWidgetState.Ready ?: return null
        if (ready.classCount == 0) return null
        val entries = ready.entries.map { entry ->
            DashboardScheduleEntry(
                classItem = entry,
                classId = entry.id,
                subjectName = entry.subjectName,
                subjectCode = entry.subjectCode,
                startTime = entry.startTime,
                endTime = entry.endTime,
            )
        }
        return com.myapplication.panthraa.ui.dashboardScheduleRefreshDelayMillis(entries)
    }

    private fun <T> DashboardScheduleEntry<T>.toWidgetEntry(isCurrent: Boolean): ScheduleWidgetEntry {
        return ScheduleWidgetEntry(
            id = classId,
            subjectName = subjectName,
            subjectCode = subjectCode,
            timeRange = timeRange,
            startTime = startTime,
            endTime = endTime,
            isCurrent = isCurrent,
        )
    }

    private fun syncedLabel(savedAtMillis: Long): String {
        if (savedAtMillis <= 0L) return "Offline cache"
        val syncedAt = Instant.ofEpochMilli(savedAtMillis)
            .atZone(PhilippineZoneId)
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        return "Synced $syncedAt"
    }
}

@Composable
private fun PanthraaScheduleWidgetContent(state: ScheduleWidgetState) {
    val size = LocalSize.current
    val context = LocalContext.current
    val heightTier = when {
        size.height < 100.dp -> WidgetHeightTier.Single
        size.height < 190.dp -> WidgetHeightTier.Double
        size.height < 280.dp -> WidgetHeightTier.Triple
        else -> WidgetHeightTier.Full
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(WidgetBackground))
            .cornerRadius(24.dp)
            .clickable(
                actionStartActivity(
                    Intent(context, MainActivity::class.java).apply {
                        putExtra(MainActivity.EXTRA_OPEN_SCHEDULE, true)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                )
            )
            .padding(widgetPadding(heightTier)),
    ) {
        when (state) {
            ScheduleWidgetState.NeedsSetup -> WidgetEmptyContent(
                title = "Panthraa",
                message = "Open Panthraa to set up",
                isCompact = heightTier == WidgetHeightTier.Single,
            )
            ScheduleWidgetState.NeedsSync -> WidgetEmptyContent(
                title = "Panthraa Schedule",
                message = "Open app once to sync schedule",
                isCompact = heightTier == WidgetHeightTier.Single,
            )
            is ScheduleWidgetState.Ready -> when (heightTier) {
                WidgetHeightTier.Single -> SingleCellWidget(state)
                WidgetHeightTier.Double -> DoubleCellWidget(state)
                WidgetHeightTier.Triple -> TripleCellWidget(state)
                WidgetHeightTier.Full -> FullScheduleWidget(state)
            }
        }
    }
}

@Composable
private fun ColumnScope.SingleCellWidget(state: ScheduleWidgetState.Ready) {
    val accent = if (state.roleLabel == "Professor") ProfessorAccent else PanthraaBlue
    val entry = state.current ?: state.next
    if (entry == null) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = androidx.glance.layout.Alignment.CenterVertically,
        ) {
            Text(text = "No classes", style = widgetTextStyle(WidgetInk, 13, FontWeight.Bold), maxLines = 1)
            Spacer(GlanceModifier.height(2.dp))
            Text(text = state.dayLabel.titleCaseDay(), style = widgetTextStyle(MutedInk, 10, FontWeight.Medium), maxLines = 1)
        }
        return
    }
    val isNow = entry.isCurrent
    val bg = if (isNow) accent else HighlightBackground
    val labelColor = if (isNow) Color.White else accent
    val codeColor = if (isNow) Color.White else WidgetInk
    val timeColor = if (isNow) Color(0xCCFFFFFF) else MutedInk
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(bg))
            .cornerRadius(16.dp)
            .padding(10.dp),
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = androidx.glance.layout.Alignment.CenterVertically) {
            Text(
                text = if (isNow) "CURRENT" else "NEXT",
                style = widgetTextStyle(labelColor, 9, FontWeight.Bold),
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                text = entry.timeRange,
                style = widgetTextStyle(timeColor, 9, FontWeight.Medium),
                maxLines = 1,
            )
        }
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = entry.subjectCode,
            style = widgetTextStyle(codeColor, 15, FontWeight.Bold),
            maxLines = 1,
        )
    }
}

@Composable
private fun ColumnScope.DoubleCellWidget(state: ScheduleWidgetState.Ready) {
    val accent = if (state.roleLabel == "Professor") ProfessorAccent else PanthraaBlue
    val first = state.current ?: state.next
    val second = if (state.current != null) state.next else null

    if (first == null) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            NoClassesContent(state)
        }
        return
    }
    Column(modifier = GlanceModifier.fillMaxSize()) {
        WidgetEntryBlock(
            entry = first,
            accent = accent,
            modifier = GlanceModifier.defaultWeight(),
        )
        if (second != null) {
            Spacer(GlanceModifier.height(6.dp))
            WidgetEntryBlock(
                entry = second,
                accent = accent,
                modifier = GlanceModifier.defaultWeight(),
            )
        }
    }
}

@Composable
private fun ColumnScope.TripleCellWidget(state: ScheduleWidgetState.Ready) {
    val accent = if (state.roleLabel == "Professor") ProfessorAccent else PanthraaBlue
    val nowTime = LocalTime.now(PhilippineZoneId)

    if (state.entries.isEmpty()) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            NoClassesContent(state)
        }
        return
    }

    val currentEntry = state.entries.firstOrNull { e ->
        !nowTime.isBefore(e.startTime) && nowTime.isBefore(e.endTime)
    }
    val upcomingEntries = state.entries.filter { it.startTime.isAfter(nowTime) }
    val pastEntries = state.entries.filter { it.endTime.isBefore(nowTime) }
    val nextEntry = upcomingEntries.firstOrNull()
    val prevEntry = pastEntries.lastOrNull()

    val blocks = buildList<ScheduleWidgetEntry> {
        currentEntry?.let { add(it) }
        nextEntry?.let { if (it.id != currentEntry?.id) add(it) }
        prevEntry?.let { if (it.id != currentEntry?.id && it.id != nextEntry?.id) add(it) }
        for (e in state.entries) {
            if (size >= 3) break
            if (none { it.id == e.id }) add(e)
        }
    }

    Column(modifier = GlanceModifier.fillMaxSize()) {
        blocks.forEachIndexed { index, entry ->
            if (index > 0) Spacer(GlanceModifier.height(5.dp))
            WidgetEntryBlock(
                entry = entry.copy(isCurrent = entry.id == currentEntry?.id),
                accent = accent,
                modifier = GlanceModifier.defaultWeight(),
            )
        }
    }
}

@Composable
private fun ColumnScope.FullScheduleWidget(state: ScheduleWidgetState.Ready) {
    val accent = if (state.roleLabel == "Professor") ProfessorAccent else PanthraaBlue
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = "${state.userName}'s ${state.dayLabel.titleCaseDay()}",
            style = widgetTextStyle(WidgetInk, 12, FontWeight.Bold),
            maxLines = 1,
            modifier = GlanceModifier.defaultWeight(),
        )
        Text(
            text = state.roleLabel,
            style = widgetTextStyle(accent, 10, FontWeight.Bold),
            maxLines = 1,
        )
    }
    Spacer(GlanceModifier.height(6.dp))
    if (state.entries.isEmpty()) {
        NoClassesContent(state)
        WidgetFooter(state)
        return
    }
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        state.entries.take(6).forEach { entry ->
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = androidx.glance.layout.Alignment.CenterVertically,
            ) {
                Text(
                    text = formatLocalTime(entry.startTime),
                    style = widgetTextStyle(if (entry.isCurrent) accent else MutedInk, 10, FontWeight.Bold),
                    modifier = GlanceModifier.width(50.dp),
                )
                Spacer(GlanceModifier.width(6.dp))
                Column(
                    modifier = GlanceModifier.width(6.dp).height(6.dp)
                        .background(ColorProvider(if (entry.isCurrent) accent else DividerColor))
                        .cornerRadius(3.dp),
                ) {}
                Spacer(GlanceModifier.width(10.dp))
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(
                            ColorProvider(if (entry.isCurrent) HighlightBackground else Color.Transparent),
                        )
                        .cornerRadius(8.dp)
                        .padding(if (entry.isCurrent) 6.dp else 0.dp),
                ) {
                    Text(
                        text = entry.subjectCode,
                        style = widgetTextStyle(WidgetInk, 11, FontWeight.Bold),
                        maxLines = 1,
                    )
                    if (!entry.isCurrent) {
                        Text(
                            text = entry.timeRange,
                            style = widgetTextStyle(MutedInk, 9, FontWeight.Medium),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
    val remaining = (state.scheduledCount - 6).coerceAtLeast(0)
    if (remaining > 0) {
        Text(
            text = "+$remaining more",
            style = widgetTextStyle(MutedInk, 10, FontWeight.Medium),
            maxLines = 1,
            modifier = GlanceModifier.padding(top = 2.dp),
        )
    }
    WidgetFooter(state)
}

@Composable
private fun WidgetEntryBlock(
    entry: ScheduleWidgetEntry,
    accent: Color,
    modifier: GlanceModifier = GlanceModifier,
) {
    val isNow = entry.isCurrent
    val bg = if (isNow) accent else HighlightBackground
    val labelColor = if (isNow) Color.White else accent
    val codeColor = if (isNow) Color.White else WidgetInk
    val timeColor = if (isNow) Color(0xCCFFFFFF) else MutedInk
    val label = when {
        isNow -> "CURRENT"
        entry.endTime.isAfter(LocalTime.now(PhilippineZoneId)) -> "NEXT"
        else -> "PREVIOUS"
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorProvider(bg))
            .cornerRadius(14.dp)
            .padding(10.dp),
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = androidx.glance.layout.Alignment.CenterVertically) {
            Text(
                text = label,
                style = widgetTextStyle(labelColor, 9, FontWeight.Bold),
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                text = entry.timeRange,
                style = widgetTextStyle(timeColor, 9, FontWeight.Medium),
                maxLines = 1,
            )
        }
        Spacer(GlanceModifier.height(2.dp))
        Text(
            text = entry.subjectCode,
            style = widgetTextStyle(codeColor, 14, FontWeight.Bold),
            maxLines = 1,
        )
    }
}

@Composable
private fun ColumnScope.NoClassesContent(state: ScheduleWidgetState.Ready) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .defaultWeight()
            .background(ColorProvider(HighlightBackground))
            .cornerRadius(16.dp)
            .padding(12.dp),
        verticalAlignment = androidx.glance.layout.Alignment.CenterVertically,
    ) {
        Text(text = "No classes today", style = widgetTextStyle(WidgetInk, 13, FontWeight.Bold), maxLines = 1)
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = if (state.classCount == 0) "Open the app after joining or creating classes" else "Enjoy your ${state.dayLabel.titleCaseDay()}!",
            style = widgetTextStyle(MutedInk, 11, FontWeight.Medium),
            maxLines = 2,
        )
    }
}

@Composable
private fun WidgetEmptyContent(title: String, message: String, isCompact: Boolean) {
    Text(
        text = title,
        style = widgetTextStyle(WidgetInk, if (isCompact) 12 else 14, FontWeight.Bold),
        maxLines = 1,
    )
    Spacer(GlanceModifier.height(4.dp))
    Text(
        text = message,
        style = widgetTextStyle(MutedInk, if (isCompact) 10 else 11, FontWeight.Medium),
        maxLines = 2,
    )
}

@Composable
private fun WidgetFooter(state: ScheduleWidgetState.Ready) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = "${state.scheduledCount} today",
            style = widgetTextStyle(MutedInk, 9, FontWeight.Medium),
            maxLines = 1,
        )
        Spacer(GlanceModifier.defaultWeight())
        Text(
            text = state.syncedLabel,
            style = widgetTextStyle(MutedInk, 9, FontWeight.Medium),
            maxLines = 1,
        )
    }
}

private fun widgetTextStyle(color: Color, size: Int, weight: FontWeight): TextStyle {
    return TextStyle(
        color = ColorProvider(color),
        fontSize = size.sp,
        fontWeight = weight,
    )
}

private fun widgetPadding(tier: WidgetHeightTier) = when (tier) {
    WidgetHeightTier.Single -> 10.dp
    WidgetHeightTier.Double -> 12.dp
    WidgetHeightTier.Triple -> 12.dp
    WidgetHeightTier.Full -> 14.dp
}

private fun String.titleCaseDay(): String {
    val clean = trim().lowercase(Locale.getDefault())
    return clean.replaceFirstChar { it.uppercase(Locale.getDefault()) }
}

private fun formatLocalTime(time: LocalTime): String {
    return time.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
}

private enum class WidgetHeightTier {
    Single,
    Double,
    Triple,
    Full,
}

private val CompactWidgetSize = DpSize(160.dp, 80.dp)
private val MediumWidgetSize = DpSize(160.dp, 160.dp)
private val LargeWidgetSize = DpSize(160.dp, 290.dp)
private val PanthraaBlue = Color(0xFF0034DE)
private val ProfessorAccent = Color(0xFF0F766E)
private val WidgetBackground = Color(0xFFFFFFFF)
private val HighlightBackground = Color(0xFFF4F7FF)
private val DividerColor = Color(0xFFE2E8F0)
private val WidgetInk = Color(0xFF0F172A)
private val SecondaryInk = Color(0xFF334155)
private val MutedInk = Color(0xFF64748B)
