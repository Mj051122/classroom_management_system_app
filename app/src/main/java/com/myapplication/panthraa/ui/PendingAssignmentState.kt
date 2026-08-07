package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.ClassAssignment
import com.myapplication.panthraa.model.PendingAssignment

/**
 * Builds the assignment state required by the student detail and submission flows.
 *
 * Pending assignments come from a separate RPC, so a scheduled item can be absent
 * from the class-feed response even though the dashboard can open it.
 */
internal fun PendingAssignment.toStudentClassAssignment(): ClassAssignment {
    return ClassAssignment(
        id = assignmentId,
        classId = classId,
        title = title,
        instructions = instructions,
        category = category,
        targetPoints = targetPoints,
        startDate = startDate,
        endDate = endDate,
        startTime = startTime,
        endTime = endTime,
        assignmentType = assignmentType,
        submissionFormat = submissionFormat,
        requiresFile = requiresFile,
        createdAt = createdAt,
    )
}

internal fun hydratePendingAssignment(
    currentAssignments: List<ClassAssignment>,
    pending: PendingAssignment,
): List<ClassAssignment> {
    val assignmentsForClass = currentAssignments.filter { it.classId == pending.classId }
    return if (assignmentsForClass.any { it.id == pending.assignmentId }) {
        assignmentsForClass
    } else {
        assignmentsForClass + pending.toStudentClassAssignment()
    }
}

internal fun removeSubmittedPendingAssignment(
    pendingAssignments: List<PendingAssignment>,
    assignmentId: String,
): List<PendingAssignment> {
    return pendingAssignments.filterNot { it.assignmentId == assignmentId }
}
