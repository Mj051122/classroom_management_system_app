package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.ClassJoinRequest
import com.myapplication.panthraa.model.ProfessorClass
import java.util.Locale

internal data class ProfessorReviewRequestFilters(
    val subject: String? = null,
    val year: String? = null,
    val department: String? = null,
    val section: String? = null,
) {
    val hasActiveFilters: Boolean
        get() = listOf(subject, year, department, section).any { !it.isNullOrBlank() }
}

internal fun filterProfessorReviewRequests(
    requests: List<ClassJoinRequest>,
    classes: List<ProfessorClass>,
    filters: ProfessorReviewRequestFilters,
): List<ClassJoinRequest> {
    if (!filters.hasActiveFilters) return requests

    val classesById = classes.associateBy { it.id.normalizedReviewRequestValue() }
    val subject = filters.subject.normalizedReviewRequestValue()
    val year = filters.year.normalizedReviewRequestValue()
    val department = filters.department.normalizedReviewRequestValue()
    val section = filters.section.normalizedReviewRequestValue()

    return requests.filter { request ->
        val professorClass = classesById[request.classId.normalizedReviewRequestValue()]

        (subject.isEmpty() || request.matchesSubject(subject, professorClass)) &&
            (year.isEmpty() || professorClass?.yearLevel.normalizedReviewRequestValue() == year) &&
            (department.isEmpty() || professorClass?.department.normalizedReviewRequestValue() == department) &&
            (section.isEmpty() || professorClass?.section.normalizedReviewRequestValue() == section)
    }
}

internal fun mergeProfessorJoinRequestScope(
    current: List<ClassJoinRequest>,
    refreshed: List<ClassJoinRequest>,
    refreshedClassIds: Collection<String>,
): List<ClassJoinRequest> {
    val refreshedScope = refreshedClassIds
        .mapTo(linkedSetOf()) { it.normalizedReviewRequestValue() }
        .filterTo(linkedSetOf()) { it.isNotEmpty() }

    if (refreshedScope.isEmpty()) return current

    val retained = current.filterNot {
        it.classId.normalizedReviewRequestValue() in refreshedScope
    }
    val scopedRefresh = refreshed.filter {
        it.classId.normalizedReviewRequestValue() in refreshedScope
    }
    return retained + scopedRefresh
}

private fun ClassJoinRequest.matchesSubject(
    normalizedSubject: String,
    professorClass: ProfessorClass?,
): Boolean {
    val classSubject = professorClass?.displaySubjectCode.normalizedReviewRequestValue()
    val requestSubject = subjectCode.normalizedReviewRequestValue()
    return normalizedSubject == classSubject.ifEmpty { requestSubject }
}

private val ReviewRequestWhitespace = Regex("\\s+")

private fun String?.normalizedReviewRequestValue(): String =
    orEmpty()
        .trim()
        .replace(ReviewRequestWhitespace, " ")
        .lowercase(Locale.ROOT)
