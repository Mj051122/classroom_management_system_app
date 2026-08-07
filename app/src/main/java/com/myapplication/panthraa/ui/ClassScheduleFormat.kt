package com.myapplication.panthraa.ui

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

internal data class ClassScheduleDay(
    val value: String,
    val shortLabel: String,
    val fullLabel: String,
)

internal val ClassScheduleDays = listOf(
    ClassScheduleDay("monday", "M", "MONDAY"),
    ClassScheduleDay("tuesday", "T", "TUESDAY"),
    ClassScheduleDay("wednesday", "W", "WEDNESDAY"),
    ClassScheduleDay("thursday", "TH", "THURSDAY"),
    ClassScheduleDay("friday", "F", "FRIDAY"),
    ClassScheduleDay("saturday", "SAT", "SATURDAY"),
)

internal fun orderedClassScheduleDays(days: Collection<String>): List<String> {
    val normalized = days.map { it.trim().lowercase(Locale.getDefault()) }.toSet()
    return ClassScheduleDays.map { it.value }.filter { it in normalized }
}

internal fun compactClassScheduleLabel(
    days: Collection<String>,
    startTime: String?,
    endTime: String?,
): String? {
    val orderedDays = orderedClassScheduleDays(days)
    val start = parseClassScheduleTime(startTime)
    val end = parseClassScheduleTime(endTime)
    if (orderedDays.isEmpty() || start == null || end == null) return null

    val dayText = orderedDays.joinToString(separator = "") { day ->
        ClassScheduleDays.first { it.value == day }.shortLabel
    }
    return "$dayText ${scheduleTimeRangeLabel(start, end)}"
}

internal fun scheduleDayFullLabel(day: String): String {
    val normalized = day.trim().lowercase(Locale.getDefault())
    return ClassScheduleDays.firstOrNull { it.value == normalized }?.fullLabel
        ?: day.trim().uppercase(Locale.getDefault())
}

internal fun DayOfWeek.toClassScheduleDayValue(): String = when (this) {
    DayOfWeek.MONDAY -> "monday"
    DayOfWeek.TUESDAY -> "tuesday"
    DayOfWeek.WEDNESDAY -> "wednesday"
    DayOfWeek.THURSDAY -> "thursday"
    DayOfWeek.FRIDAY -> "friday"
    DayOfWeek.SATURDAY -> "saturday"
    DayOfWeek.SUNDAY -> "sunday"
}

internal fun normalizeClassScheduleTimeInput(value: String): String? {
    val parsed = parseClassScheduleTime(value)
    return parsed?.format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault()))
}

internal fun isClassScheduleRangeValid(startTime: String, endTime: String): Boolean {
    val start = parseClassScheduleTime(startTime)
    val end = parseClassScheduleTime(endTime)
    return start != null && end != null && start.isBefore(end)
}

internal fun classScheduleRangesOverlap(
    firstStart: String?,
    firstEnd: String?,
    secondStart: String?,
    secondEnd: String?,
): Boolean {
    val startA = parseClassScheduleTime(firstStart)
    val endA = parseClassScheduleTime(firstEnd)
    val startB = parseClassScheduleTime(secondStart)
    val endB = parseClassScheduleTime(secondEnd)
    return startA != null && endA != null && startB != null && endB != null &&
        startA.isBefore(endB) && endA.isAfter(startB)
}

internal fun parseClassScheduleTime(value: String?): LocalTime? {
    val raw = value?.trim().orEmpty()
    if (raw.isBlank()) return null

    val cleaned = raw
        .replace(".", "")
        .replace(Regex("\\s+"), " ")
        .uppercase(Locale.getDefault())
    val spacedMeridiem = cleaned.replace(Regex("(?<=\\d)(AM|PM)$"), " $1")
    val candidates = listOf(spacedMeridiem, cleaned).distinct()

    return candidates.firstNotNullOfOrNull { candidate ->
        classScheduleTimeFormatters.firstNotNullOfOrNull { formatter ->
            runCatching { LocalTime.parse(candidate, formatter) }.getOrNull()
        }
    }
}

internal fun scheduleTimeRangeLabel(start: LocalTime, end: LocalTime): String {
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

private val classScheduleTimeFormatters = listOf(
    "H:mm:ss",
    "H:mm",
    "HH:mm:ss",
    "HH:mm",
    "h:mm a",
    "hh:mm a",
).map { pattern ->
    DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern(pattern)
        .toFormatter(Locale.getDefault())
}
