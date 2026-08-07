package com.myapplication.panthraa.ui

import com.myapplication.panthraa.model.ClassJoinRequest
import com.myapplication.panthraa.model.ProfessorClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfessorReviewRequestFilteringTest {
    private val classes = listOf(
        professorClass(
            id = "class-a",
            subjectName = "Data Structures",
            subjectCode = "CS 201",
            year = "First Year",
            department = "Computer Science",
            section = "A",
        ),
        professorClass(
            id = "class-b",
            subjectName = "Database Systems",
            subjectCode = "IT 202",
            year = "Second Year",
            department = "Information Technology",
            section = "B",
        ),
        professorClass(
            id = "class-c",
            subjectName = "Data Structures",
            subjectCode = "CS 201",
            year = "Second Year",
            department = "Computer Science",
            section = "C",
        ),
    )

    private val requests = listOf(
        joinRequest(id = "request-a", classId = "class-a"),
        joinRequest(id = "request-b", classId = "class-b"),
        joinRequest(id = "request-c", classId = "class-c"),
        joinRequest(
            id = "request-without-class",
            classId = "missing-class",
            className = "Network Security",
            subjectCode = "NET 101",
        ),
    )

    @Test
    fun noFiltersReturnsEveryRequest() {
        val filters = ProfessorReviewRequestFilters()

        val result = filterProfessorReviewRequests(requests, classes, filters)

        assertFalse(filters.hasActiveFilters)
        assertSame(requests, result)
    }

    @Test
    fun subjectOnlyFiltersByClassMetadataAndFallsBackToRequest() {
        assertEquals(
            listOf("request-a", "request-c"),
            filterProfessorReviewRequests(
                requests = requests,
                classes = classes,
                filters = ProfessorReviewRequestFilters(subject = "CS 201"),
            ).map { it.id },
        )
        assertEquals(
            listOf("request-without-class"),
            filterProfessorReviewRequests(
                requests = requests,
                classes = classes,
                filters = ProfessorReviewRequestFilters(subject = "NET 101"),
            ).map { it.id },
        )
        val legacyCodeClass = ProfessorClass(id = "legacy-class", classCode = "LEG 100")
        val legacyCodeRequest = joinRequest(id = "legacy-request", classId = "legacy-class")
        assertEquals(
            listOf(legacyCodeRequest),
            filterProfessorReviewRequests(
                requests = listOf(legacyCodeRequest),
                classes = listOf(legacyCodeClass),
                filters = ProfessorReviewRequestFilters(subject = "LEG 100"),
            ),
        )
    }

    @Test
    fun yearDepartmentAndSectionFiltersWorkIndependently() {
        assertEquals(
            listOf("request-b", "request-c"),
            filteredIds(ProfessorReviewRequestFilters(year = "Second Year")),
        )
        assertEquals(
            listOf("request-a", "request-c"),
            filteredIds(ProfessorReviewRequestFilters(department = "Computer Science")),
        )
        assertEquals(
            listOf("request-b"),
            filteredIds(ProfessorReviewRequestFilters(section = "B")),
        )
    }

    @Test
    fun activeFiltersAreCombinedWithAnd() {
        val filters = ProfessorReviewRequestFilters(
            subject = "CS 201",
            year = "Second Year",
            department = "Computer Science",
            section = "C",
        )

        assertTrue(filters.hasActiveFilters)
        assertEquals(listOf("request-c"), filteredIds(filters))
    }

    @Test
    fun comparisonsIgnoreCaseAndRepeatedOrSurroundingWhitespace() {
        val spacedClass = professorClass(
            id = "  CLASS-SPACED  ",
            subjectName = "  Mobile   Application  Development ",
            subjectCode = "  MAD   301 ",
            year = " Third   Year ",
            department = "  Computer   Science ",
            section = " Section   Alpha ",
        )
        val spacedRequest = joinRequest(id = "spaced", classId = "class-spaced")

        val result = filterProfessorReviewRequests(
            requests = listOf(spacedRequest),
            classes = listOf(spacedClass),
            filters = ProfessorReviewRequestFilters(
                subject = "mad 301",
                year = "third year",
                department = "computer science",
                section = "section alpha",
            ),
        )

        assertEquals(listOf(spacedRequest), result)
    }

    @Test
    fun unmatchedFilterReturnsEmptyList() {
        assertEquals(
            emptyList<ClassJoinRequest>(),
            filterProfessorReviewRequests(
                requests = requests,
                classes = classes,
                filters = ProfessorReviewRequestFilters(section = "Not a section"),
            ),
        )
    }

    @Test
    fun scopedMergeReplacesOnlyRequestsInRefreshedClasses() {
        val current = listOf(
            joinRequest(id = "old-a", classId = "class-a"),
            joinRequest(id = "keep-b", classId = "class-b"),
            joinRequest(id = "keep-c", classId = "class-c"),
        )
        val refreshed = listOf(
            joinRequest(id = "new-a-1", classId = "CLASS-A"),
            joinRequest(id = "new-a-2", classId = "class-a"),
            joinRequest(id = "ignore-outside-scope", classId = "class-b"),
        )

        val merged = mergeProfessorJoinRequestScope(
            current = current,
            refreshed = refreshed,
            refreshedClassIds = listOf("  class-a  "),
        )

        assertEquals(
            listOf("keep-b", "keep-c", "new-a-1", "new-a-2"),
            merged.map { it.id },
        )
    }

    @Test
    fun scopedMergeCanClearOneScopeWithoutClearingOtherRequests() {
        val current = listOf(
            joinRequest(id = "remove-a", classId = "class-a"),
            joinRequest(id = "keep-b", classId = "class-b"),
        )

        assertEquals(
            listOf("keep-b"),
            mergeProfessorJoinRequestScope(
                current = current,
                refreshed = emptyList(),
                refreshedClassIds = listOf("class-a"),
            ).map { it.id },
        )
        assertSame(
            current,
            mergeProfessorJoinRequestScope(
                current = current,
                refreshed = emptyList(),
                refreshedClassIds = emptyList(),
            ),
        )
    }

    private fun filteredIds(filters: ProfessorReviewRequestFilters): List<String> =
        filterProfessorReviewRequests(requests, classes, filters).map { it.id }

    private fun professorClass(
        id: String,
        subjectName: String,
        subjectCode: String,
        year: String,
        department: String,
        section: String,
    ) = ProfessorClass(
        id = id,
        subjectName = subjectName,
        subjectCode = subjectCode,
        yearLevel = year,
        department = department,
        section = section,
    )

    private fun joinRequest(
        id: String,
        classId: String,
        className: String = "",
        subjectCode: String = "",
    ) = ClassJoinRequest(
        id = id,
        classId = classId,
        className = className,
        subjectCode = subjectCode,
    )
}
