package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.ClassAssignment
import com.myapplication.panthraa.model.PendingAssignment
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class PendingAssignmentStateTest {
    @Test
    fun pendingAssignmentHydratesSubmissionCriticalFields() {
        val pending = pendingAssignment(
            assignmentId = "assignment-1",
            classId = "class-1",
            submissionFormat = "docx",
            requiresFile = false,
            startDate = "2026-07-13",
            startTime = "14:00:00",
        )

        val assignment = pending.toStudentClassAssignment()

        assertEquals("assignment-1", assignment.id)
        assertEquals("class-1", assignment.classId)
        assertEquals("docx", assignment.submissionFormat)
        assertFalse(assignment.requiresFile)
        assertEquals("2026-07-13", assignment.startDate)
        assertEquals("14:00:00", assignment.startTime)
        assertEquals("quiz", assignment.assignmentType)
    }

    @Test
    fun hydrationScopesStateToPendingClassAndAddsMissingAssignment() {
        val existingSameClass = ClassAssignment(id = "existing", classId = "class-1")
        val unrelated = ClassAssignment(id = "other", classId = "class-2")

        val result = hydratePendingAssignment(
            currentAssignments = listOf(unrelated, existingSameClass),
            pending = pendingAssignment(assignmentId = "pending", classId = "class-1"),
        )

        assertEquals(listOf("existing", "pending"), result.map { it.id })
        assertTrue(result.all { it.classId == "class-1" })
    }

    @Test
    fun hydrationKeepsExistingFullAssignmentInsteadOfReplacingIt() {
        val existing = ClassAssignment(
            id = "assignment-1",
            classId = "class-1",
            hasSubmitted = true,
            submissionFormat = "xlsx",
            submissionFileUrl = "https://example.test/submission.xlsx",
        )

        val result = hydratePendingAssignment(
            currentAssignments = listOf(existing),
            pending = pendingAssignment(
                assignmentId = "assignment-1",
                classId = "class-1",
                submissionFormat = "pdf",
            ),
        )

        assertEquals(1, result.size)
        assertSame(existing, result.single())
    }

    @Test
    fun successfulSubmissionRemovesOnlyMatchingPendingItem() {
        val result = removeSubmittedPendingAssignment(
            pendingAssignments = listOf(
                pendingAssignment(assignmentId = "submitted", classId = "class-1"),
                pendingAssignment(assignmentId = "keep", classId = "class-2"),
            ),
            assignmentId = "submitted",
        )

        assertEquals(listOf("keep"), result.map { it.assignmentId })
    }

    @Test
    fun scheduleWindowUsesExplicitManilaLocalDateAndTimeInputs() {
        val assignment = ClassAssignment(
            id = "scheduled",
            startDate = "2026-07-13",
            startTime = "08:00:00",
            endDate = "2026-07-13",
            endTime = "17:00:00",
        )
        val date = LocalDate.of(2026, 7, 13)

        assertTrue(
            isAssignmentLocked(
                assignment = assignment,
                currentDate = date,
                currentTime = LocalTime.of(7, 59),
            ),
        )
        assertFalse(
            isAssignmentLocked(
                assignment = assignment,
                currentDate = date,
                currentTime = LocalTime.of(8, 0),
            ),
        )
        assertFalse(
            isAssignmentExpired(
                assignment = assignment,
                currentDate = date,
                currentTime = LocalTime.of(17, 0),
            ),
        )
        assertTrue(
            isAssignmentExpired(
                assignment = assignment,
                currentDate = date,
                currentTime = LocalTime.of(17, 1),
            ),
        )
    }

    private fun pendingAssignment(
        assignmentId: String,
        classId: String,
        submissionFormat: String = "pdf",
        requiresFile: Boolean = true,
        startDate: String? = null,
        startTime: String? = null,
    ): PendingAssignment {
        return PendingAssignment(
            assignmentId = assignmentId,
            classId = classId,
            title = "Scheduled quiz",
            instructions = "Upload the required file.",
            category = "laboratory",
            targetPoints = 50,
            startDate = startDate,
            endDate = "2026-07-13",
            startTime = startTime,
            endTime = "17:00:00",
            assignmentType = "quiz",
            submissionFormat = submissionFormat,
            requiresFile = requiresFile,
            createdAt = "2026-07-13T03:00:00Z",
        )
    }
}
