package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.StudentClass
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

internal data class DashboardScheduleEntry<T>(
    val classItem: T,
    val classId: String,
    val subjectName: String,
    val subjectCode: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    val timeRange: String
        get() = scheduleTimeRangeLabel(startTime, endTime)
}

internal data class DashboardTodaySchedule<T>(
    val dayLabel: String,
    val scheduledCount: Int,
    val current: DashboardScheduleEntry<T>?,
    val next: DashboardScheduleEntry<T>?,
    val entries: List<DashboardScheduleEntry<T>>,
)

internal typealias ProfessorDashboardScheduleEntry = DashboardScheduleEntry<ProfessorClass>
internal typealias ProfessorDashboardTodaySchedule = DashboardTodaySchedule<ProfessorClass>
internal typealias StudentDashboardScheduleEntry = DashboardScheduleEntry<StudentClass>
internal typealias StudentDashboardTodaySchedule = DashboardTodaySchedule<StudentClass>

internal fun professorDashboardTodaySchedule(
    classes: List<ProfessorClass>,
    now: LocalDateTime = LocalDateTime.now(PhilippineZoneId),
): ProfessorDashboardTodaySchedule {
    return dashboardTodaySchedule(
        classes = classes,
        now = now,
        classId = { it.id },
        subjectName = { it.displayClassName },
        subjectCode = { it.displaySubjectCode },
        scheduleDays = { it.scheduleDays },
        scheduleStartTime = { it.scheduleStartTime },
        scheduleEndTime = { it.scheduleEndTime },
    )
}

internal fun studentDashboardTodaySchedule(
    classes: List<StudentClass>,
    now: LocalDateTime = LocalDateTime.now(PhilippineZoneId),
): StudentDashboardTodaySchedule {
    return dashboardTodaySchedule(
        classes = classes,
        now = now,
        classId = { it.id },
        subjectName = { it.displayClassName },
        subjectCode = { it.displaySubjectCode },
        scheduleDays = { it.scheduleDays },
        scheduleStartTime = { it.scheduleStartTime },
        scheduleEndTime = { it.scheduleEndTime },
    )
}

internal fun dashboardScheduleRefreshDelayMillis(
    entries: List<DashboardScheduleEntry<*>>,
    now: LocalDateTime = LocalDateTime.now(PhilippineZoneId),
): Long {
    val nowTime = now.toLocalTime()
    val today = now.toLocalDate()
    val nextClassBoundary = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .filter { boundary -> boundary.isAfter(nowTime) }
        .minOrNull()
        ?.let { boundary -> today.atTime(boundary).plusSeconds(1) }

    val nextMidnight = today.plusDays(1).atStartOfDay().plusSeconds(1)
    val nextRefreshAt = listOfNotNull(nextClassBoundary, nextMidnight).minOrNull() ?: nextMidnight
    return Duration.between(now, nextRefreshAt).toMillis().coerceAtLeast(60_000L)
}

private fun <T> dashboardTodaySchedule(
    classes: List<T>,
    now: LocalDateTime,
    classId: (T) -> String,
    subjectName: (T) -> String,
    subjectCode: (T) -> String,
    scheduleDays: (T) -> List<String>,
    scheduleStartTime: (T) -> String?,
    scheduleEndTime: (T) -> String?,
): DashboardTodaySchedule<T> {
    val todayValue = now.dayOfWeek.toClassScheduleDayValue()
    val entries = classes.mapNotNull { classItem ->
        val start = parseClassScheduleTime(scheduleStartTime(classItem))
        val end = parseClassScheduleTime(scheduleEndTime(classItem))
        val happensToday = todayValue in orderedClassScheduleDays(scheduleDays(classItem))
        if (!happensToday || start == null || end == null) {
            null
        } else {
            val code = subjectCode(classItem).ifBlank { "Scheduled class" }
            DashboardScheduleEntry(
                classItem = classItem,
                classId = classId(classItem),
                subjectName = subjectName(classItem).ifBlank { code },
                subjectCode = code,
                startTime = start,
                endTime = end,
            )
        }
    }.sortedWith(
        compareBy<DashboardScheduleEntry<T>> { it.startTime }
            .thenBy { it.subjectCode }
            .thenBy { it.subjectName },
    )

    val nowTime = now.toLocalTime()
    val current = entries.firstOrNull { entry ->
        !nowTime.isBefore(entry.startTime) && nowTime.isBefore(entry.endTime)
    }
    val next = entries.firstOrNull { entry -> entry.startTime.isAfter(nowTime) }

    return DashboardTodaySchedule(
        dayLabel = scheduleDayFullLabel(todayValue),
        scheduledCount = entries.size,
        current = current,
        next = next,
        entries = entries,
    )
}
