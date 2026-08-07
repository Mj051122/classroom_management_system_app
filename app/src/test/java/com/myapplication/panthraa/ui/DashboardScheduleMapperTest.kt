package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.StudentClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class DashboardScheduleMapperTest {
    @Test
    fun professorScheduleDetectsCurrentAndNextClass() {
        val now = LocalDateTime.of(2026, 7, 6, 10, 30)
        val schedule = professorDashboardTodaySchedule(
            classes = listOf(
                professorClass(id = "current", subjectName = "Mobile Development", start = "09:00", end = "11:00"),
                professorClass(id = "next", subjectName = "Web Systems", start = "13:00", end = "14:30"),
            ),
            now = now,
        )

        assertEquals(2, schedule.scheduledCount)
        assertEquals("current", schedule.current?.classId)
        assertEquals("next", schedule.next?.classId)
    }

    @Test
    fun studentScheduleDetectsNextClassWhenNothingIsCurrent() {
        val now = LocalDateTime.of(2026, 7, 6, 7, 30)
        val schedule = studentDashboardTodaySchedule(
            classes = listOf(
                studentClass(id = "first", subjectName = "Data Structures", start = "08:00", end = "09:30"),
            ),
            now = now,
        )

        assertNull(schedule.current)
        assertEquals("first", schedule.next?.classId)
    }

    @Test
    fun scheduleIgnoresMalformedTimes() {
        val now = LocalDateTime.of(2026, 7, 6, 8, 0)
        val schedule = professorDashboardTodaySchedule(
            classes = listOf(
                professorClass(id = "bad", subjectName = "Broken", start = "soon", end = "later"),
                professorClass(id = "good", subjectName = "Networks", start = "10:00", end = "11:00"),
            ),
            now = now,
        )

        assertEquals(1, schedule.scheduledCount)
        assertEquals("good", schedule.entries.single().classId)
    }

    @Test
    fun scheduleReturnsEmptyWhenNoClassesToday() {
        val now = LocalDateTime.of(2026, 7, 7, 8, 0)
        val schedule = professorDashboardTodaySchedule(
            classes = listOf(
                professorClass(id = "monday", subjectName = "Research", start = "10:00", end = "11:00"),
            ),
            now = now,
        )

        assertEquals(0, schedule.scheduledCount)
        assertNull(schedule.current)
        assertNull(schedule.next)
    }

    @Test
    fun scheduleRefreshDelayUsesNextBoundary() {
        val now = LocalDateTime.of(2026, 7, 6, 10, 30)
        val schedule = professorDashboardTodaySchedule(
            classes = listOf(
                professorClass(id = "current", subjectName = "Mobile Development", start = "09:00", end = "11:00"),
            ),
            now = now,
        )

        val delay = dashboardScheduleRefreshDelayMillis(schedule.entries, now)

        assertNotNull(delay)
        assertEquals(30 * 60 * 1000L + 1000L, delay)
    }

    private fun professorClass(
        id: String,
        subjectName: String,
        start: String,
        end: String,
    ) = ProfessorClass(
        id = id,
        subjectName = subjectName,
        subjectCode = subjectName.take(3).uppercase(),
        scheduleDays = listOf("monday"),
        scheduleStartTime = start,
        scheduleEndTime = end,
    )

    private fun studentClass(
        id: String,
        subjectName: String,
        start: String,
        end: String,
    ) = StudentClass(
        id = id,
        subjectName = subjectName,
        subjectCode = subjectName.take(3).uppercase(),
        scheduleDays = listOf("monday"),
        scheduleStartTime = start,
        scheduleEndTime = end,
    )
}
