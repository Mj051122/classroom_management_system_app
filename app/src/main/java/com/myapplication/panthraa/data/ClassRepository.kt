package com.myapplication.panthraa.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.myapplication.panthraa.model.AssignmentComment
import com.myapplication.panthraa.model.AssignmentStatus
import com.myapplication.panthraa.model.AssignmentSubmission
import com.myapplication.panthraa.model.AttendanceStudent
import com.myapplication.panthraa.model.ClassAssignment
import com.myapplication.panthraa.model.ClassJoinRequest
import com.myapplication.panthraa.model.Classmate
import com.myapplication.panthraa.model.PendingAssignment
import com.myapplication.panthraa.model.ProfessorClass
import com.myapplication.panthraa.model.RecordAttendanceResult
import com.myapplication.panthraa.model.StudentClass
import com.myapplication.panthraa.model.StudentGrade
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.ContentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

class ClassRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client,
    private val readGuard: SupabaseReadGuard = SupabaseReadGuard.shared,
) {
    suspend fun getStudentClasses(
        studentId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<StudentClass> {
        return readGuard.read(
            key = classListKey(userId = studentId, role = "student"),
            groups = setOf(classListGroup(userId = studentId, role = "student")),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_student_classes",
                parameters = buildJsonObject {
                    put("p_student_id", studentId)
                },
            ).decodeList<StudentClass>()
        }
    }

    suspend fun joinClassByCode(studentId: String, classCode: String) {
        val cleanCode = classCode.trim()
        if (cleanCode.isBlank()) {
            error("Join code is required.")
        }

        client.postgrest.rpc(
            function = "join_class_by_code",
            parameters = buildJsonObject {
                put("p_student_id", studentId)
                put("p_class_code", cleanCode)
            },
        )
        invalidateClassList(userId = studentId, role = "student")
    }

    suspend fun getProfessorClasses(
        professorId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<ProfessorClass> {
        return readGuard.read(
            key = classListKey(userId = professorId, role = "professor"),
            groups = setOf(classListGroup(userId = professorId, role = "professor")),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_professor_classes",
                parameters = buildJsonObject {
                    put("p_professor_id", professorId)
                },
            ).decodeList<ProfessorClass>()
        }
    }

    suspend fun getClassmates(
        classId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<Classmate> {
        return readGuard.read(
            key = "classmates:v=$CACHE_SCHEMA_VERSION:class=$classId",
            groups = setOf(classmatesGroup(classId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_classmates",
                parameters = buildJsonObject {
                    put("p_class_id", classId)
                },
            ).decodeList<Classmate>()
        }
    }

    suspend fun getClassAssignments(
        classId: String,
        studentId: String? = null,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<ClassAssignment> {
        return readGuard.read(
            key = "class_assignments:v=$CACHE_SCHEMA_VERSION:class=$classId:student=${studentId ?: "professor"}",
            groups = setOf(assignmentsGroup(classId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_class_assignments",
                parameters = buildJsonObject {
                    put("p_class_id", classId)
                    if (studentId == null) {
                        put("p_student_id", JsonNull)
                    } else {
                        put("p_student_id", studentId)
                    }
                },
            ).decodeList<ClassAssignment>()
        }
    }

    suspend fun getStudentPendingAssignments(
        studentId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<PendingAssignment> {
        val cleanStudentId = requireUuid(studentId, "Student ID")
        return readGuard.read(
            key = "pending_assignments:v=$CACHE_SCHEMA_VERSION:student=$cleanStudentId",
            groups = setOf(pendingAssignmentsGroup(cleanStudentId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_student_pending_assignments",
                parameters = buildJsonObject {
                    put("p_student_id", cleanStudentId)
                },
            ).decodeList<PendingAssignment>()
        }
    }

    suspend fun getStudentGrades(
        studentId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<StudentGrade> {
        val cleanStudentId = requireUuid(studentId, "Student ID")
        return readGuard.read(
            key = "grades:v=$CACHE_SCHEMA_VERSION:student=$cleanStudentId",
            groups = setOf(gradesGroup(cleanStudentId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_student_grades",
                parameters = buildJsonObject {
                    put("p_student_id", cleanStudentId)
                },
            ).decodeList<StudentGrade>()
        }
    }

    suspend fun getAssignmentSubmissions(
        assignmentId: String,
        professorId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<AssignmentSubmission> {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        return readGuard.read(
            key = "submissions:v=$CACHE_SCHEMA_VERSION:assignment=$cleanAssignmentId:professor=$cleanProfessorId",
            groups = setOf(submissionsGroup(cleanAssignmentId, cleanProfessorId)),
            ttlMillis = THIRTY_SECONDS_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_assignment_submissions",
                parameters = buildJsonObject {
                    put("p_assignment_id", cleanAssignmentId)
                    put("p_professor_id", cleanProfessorId)
                },
            ).decodeList<AssignmentSubmission>()
        }
    }

    suspend fun gradeAssignmentSubmission(
        submissionId: String,
        professorId: String,
        score: Int,
        targetPoints: Int,
    ): AssignmentSubmission {
        val cleanTargetPoints = targetPoints.coerceAtLeast(1)
        if (score !in 0..cleanTargetPoints) {
            error("Earned points must be from 0 to $cleanTargetPoints.")
        }

        val submission = client.postgrest.rpc(
            function = "grade_assignment_submission",
            parameters = buildJsonObject {
                put("p_submission_id", requireUuid(submissionId, "Submission ID"))
                put("p_professor_id", requireUuid(professorId, "Professor ID"))
                put("p_score", score)
            },
        ).decodeAs<AssignmentSubmission>()
        invalidateSubmissions(assignmentId = submission.assignmentId, professorId = professorId)
        readGuard.invalidateGroup(gradesGroup(submission.studentId))
        return submission
    }

    suspend fun createClassAssignment(
        classId: String,
        professorId: String,
        title: String,
        instructions: String,
        category: String,
        targetPoints: Int,
        startDate: String? = null,
        endDate: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        assignmentType: String = "task",
        submissionFormat: String = "pdf",
        requiresFile: Boolean = true,
        allowComments: Boolean = true,
        fileUrl: String? = null,
    ): ClassAssignment {
        val cleanClassId = requireUuid(classId, "Class ID")
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val cleanTitle = title.trim()
        val cleanCategory = category.trim().lowercase().takeIf { it in ASSIGNMENT_CATEGORIES } ?: "lecture"
        val cleanTargetPoints = targetPoints.coerceAtLeast(1)
        if (cleanTitle.isBlank()) {
            error("Assignment title is required.")
        }

        val assignment = client.postgrest.rpc(
            function = "create_class_assignment",
            parameters = buildJsonObject {
                put("p_class_id", cleanClassId)
                put("p_professor_id", cleanProfessorId)
                put("p_title", cleanTitle)
                put("p_instructions", instructions.trim())
                put("p_category", cleanCategory)
                put("p_target_points", cleanTargetPoints)
                put("p_assignment_type", assignmentType.toBackendAssignmentType())
                put("p_submission_format", submissionFormat.toSubmissionFormat())
                put("p_requires_file", requiresFile)
                put("p_allow_comments", allowComments)
                if (startDate != null) put("p_start_date", startDate)
                if (endDate != null) put("p_end_date", endDate)
                if (startTime != null) put("p_start_time", startTime)
                if (endTime != null) put("p_end_time", endTime)
                if (fileUrl != null) put("p_file_url", fileUrl)
            },
        ).decodeAs<ClassAssignment>()
        invalidateAssignmentCaches(cleanClassId)
        return assignment
    }

    suspend fun updateClassAssignment(
        assignmentId: String,
        professorId: String,
        title: String,
        instructions: String,
        category: String,
        targetPoints: Int,
        startDate: String? = null,
        endDate: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        assignmentType: String = "task",
        submissionFormat: String = "pdf",
        requiresFile: Boolean = true,
        allowComments: Boolean = true,
        fileUrl: String? = null,
    ): ClassAssignment {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val cleanTitle = title.trim()
        val cleanCategory = category.trim().lowercase().takeIf { it in ASSIGNMENT_CATEGORIES } ?: "lecture"
        val cleanTargetPoints = targetPoints.coerceAtLeast(1)
        if (cleanTitle.isBlank()) {
            error("Assignment title is required.")
        }

        val assignment = client.postgrest.rpc(
            function = "update_class_assignment",
            parameters = buildJsonObject {
                put("p_assignment_id", cleanAssignmentId)
                put("p_professor_id", cleanProfessorId)
                put("p_title", cleanTitle)
                put("p_instructions", instructions.trim())
                put("p_category", cleanCategory)
                put("p_target_points", cleanTargetPoints)
                put("p_assignment_type", assignmentType.toBackendAssignmentType())
                put("p_submission_format", submissionFormat.toSubmissionFormat())
                put("p_requires_file", requiresFile)
                put("p_allow_comments", allowComments)
                if (startDate != null) put("p_start_date", startDate)
                if (endDate != null) put("p_end_date", endDate)
                if (startTime != null) put("p_start_time", startTime)
                if (endTime != null) put("p_end_time", endTime)
                if (fileUrl != null) put("p_file_url", fileUrl)
            },
        ).decodeAs<ClassAssignment>()
        invalidateAssignmentCaches(assignment.classId)
        return assignment
    }

    suspend fun uploadAssignmentFile(
        context: Context,
        classId: String,
        fileUri: Uri,
    ): String {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(fileUri) ?: error("Unsupported file type.")
        if (mimeType != "application/pdf") {
            error("Only PDF files are allowed.")
        }
        val bytes = resolver.openInputStream(fileUri)?.use { it.readBytes() }
            ?: error("Unable to read selected file.")
        val path = "$classId/material_${UUID.randomUUID()}.pdf"

        client.storage.from(ASSIGNMENT_FILES_BUCKET).upload(path, bytes) {
            contentType = ContentType.parse(mimeType)
        }
        return client.storage.from(ASSIGNMENT_FILES_BUCKET).publicUrl(path)
    }

    suspend fun uploadSubmissionFile(
        context: Context,
        assignmentId: String,
        studentId: String,
        requiredFormat: String,
        fileUri: Uri,
    ): String {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanStudentId = requireUuid(studentId, "Student ID")
        val format = requiredFormat.toSubmissionFormat()
        val resolver = context.contentResolver
        val mimeType = resolver.getType(fileUri)
        val fileName = getDisplayName(context, fileUri).orEmpty()
        val extension = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        if (!isAllowedSubmissionFile(format, mimeType, extension)) {
            error("File format doesn't match. Required: ${submissionFormatLabel(format)}.")
        }
        val bytes = resolver.openInputStream(fileUri)?.use { it.readBytes() }
            ?: error("Unable to read selected file.")
        val cleanExtension = extension.ifBlank { defaultExtensionForFormat(format) }
        val path = "submissions/$cleanAssignmentId/$cleanStudentId/${UUID.randomUUID()}.$cleanExtension"

        client.storage.from(ASSIGNMENT_FILES_BUCKET).upload(path, bytes) {
            contentType = ContentType.parse(mimeType ?: "application/octet-stream")
        }
        return client.storage.from(ASSIGNMENT_FILES_BUCKET).publicUrl(path)
    }

    suspend fun submitAssignment(
        assignmentId: String,
        studentId: String,
        responseText: String,
        submissionFileUrl: String? = null,
    ): AssignmentSubmission {
        val cleanResponse = responseText.trim()

        val submission = client.postgrest.rpc(
            function = "submit_assignment",
            parameters = buildJsonObject {
                put("p_assignment_id", assignmentId)
                put("p_student_id", studentId)
                put("p_response_text", cleanResponse)
                put("p_submission_file_url", submissionFileUrl ?: "")
            },
        ).decodeAs<AssignmentSubmission>()
        invalidateStudentSummaries(studentId)
        return submission
    }

    suspend fun recordMaterialView(assignmentId: String, studentId: String): AssignmentSubmission {
        return client.postgrest.rpc(
            function = "record_material_view",
            parameters = buildJsonObject {
                put("p_assignment_id", requireUuid(assignmentId, "Assignment ID"))
                put("p_student_id", requireUuid(studentId, "Student ID"))
            },
        ).decodeAs<AssignmentSubmission>()
    }

    suspend fun getAssignmentComments(
        assignmentId: String,
        viewerId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<AssignmentComment> {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanViewerId = requireUuid(viewerId, "Viewer ID")
        return readGuard.read(
            key = "comments:v=$CACHE_SCHEMA_VERSION:assignment=$cleanAssignmentId:viewer=$cleanViewerId",
            groups = setOf(commentsGroup(cleanAssignmentId)),
            ttlMillis = THIRTY_SECONDS_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_assignment_comments",
                parameters = buildJsonObject {
                    put("p_assignment_id", cleanAssignmentId)
                    put("p_viewer_id", cleanViewerId)
                },
            ).decodeList<AssignmentComment>()
        }
    }

    suspend fun createAssignmentComment(
        assignmentId: String,
        authorId: String,
        content: String,
        visibility: String,
    ): AssignmentComment {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanAuthorId = requireUuid(authorId, "Author ID")
        val cleanContent = content.trim()
        if (cleanContent.isBlank()) {
            error("Comment cannot be empty.")
        }
        val comment = client.postgrest.rpc(
            function = "create_assignment_comment",
            parameters = buildJsonObject {
                put("p_assignment_id", cleanAssignmentId)
                put("p_author_id", cleanAuthorId)
                put("p_content", cleanContent)
                put("p_visibility", if (visibility.equals("private", ignoreCase = true)) "private" else "public")
            },
        ).decodeAs<AssignmentComment>()
        invalidateComments(comment.assignmentId)
        return comment
    }

    suspend fun deleteAssignmentComment(commentId: String, authorId: String) {
        val cleanCommentId = requireUuid(commentId, "Comment ID")
        val cleanAuthorId = requireUuid(authorId, "Author ID")
        val result = client.postgrest.rpc(
            function = "delete_assignment_comment",
            parameters = buildJsonObject {
                put("p_comment_id", cleanCommentId)
                put("p_author_id", cleanAuthorId)
            },
        ).decodeAs<AssignmentComment>()
        invalidateComments(result.assignmentId)
    }

    suspend fun hideAssignmentComment(commentId: String, professorId: String) {
        val cleanCommentId = requireUuid(commentId, "Comment ID")
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val result = client.postgrest.rpc(
            function = "hide_assignment_comment",
            parameters = buildJsonObject {
                put("p_comment_id", cleanCommentId)
                put("p_professor_id", cleanProfessorId)
            },
        ).decodeAs<AssignmentComment>()
        invalidateComments(result.assignmentId)
    }

    suspend fun unhideAssignmentComment(commentId: String, professorId: String) {
        val cleanCommentId = requireUuid(commentId, "Comment ID")
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val result = client.postgrest.rpc(
            function = "unhide_assignment_comment",
            parameters = buildJsonObject {
                put("p_comment_id", cleanCommentId)
                put("p_professor_id", cleanProfessorId)
            },
        ).decodeAs<AssignmentComment>()
        invalidateComments(result.assignmentId)
    }

    suspend fun deleteClassAssignment(assignmentId: String, professorId: String) {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanProfessorId = requireUuid(professorId, "Professor ID")

        client.postgrest.rpc(
            function = "delete_class_assignment",
            parameters = buildJsonObject {
                put("p_assignment_id", cleanAssignmentId)
                put("p_professor_id", cleanProfessorId)
            },
        )
    }

    suspend fun deleteProfessorSubject(
        professorId: String,
        yearLevel: String,
        department: String,
        section: String,
        track: String?,
        subjectCode: String,
    ) {
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val cleanYearLevel = yearLevel.trim().lowercase()
        val cleanDepartment = department.trim()
        val cleanSection = section.trim()
        val cleanTrack = track?.trim().orEmpty()
        val cleanSubjectCode = subjectCode.trim().uppercase()

        if (cleanYearLevel !in YEAR_LEVELS) {
            error("Year level is required.")
        }
        if (cleanDepartment.isBlank()) {
            error("Department is required.")
        }
        if (cleanSection.isBlank()) {
            error("Section is required.")
        }
        if (cleanSubjectCode.isBlank()) {
            error("Subject code is required.")
        }

        client.postgrest.rpc(
            function = "delete_professor_subject",
            parameters = buildJsonObject {
                put("p_professor_id", cleanProfessorId)
                put("p_year_level", cleanYearLevel)
                put("p_department", cleanDepartment)
                put("p_section", cleanSection)
                put("p_track", cleanTrack)
                put("p_subject_code", cleanSubjectCode)
            },
        )
    }

    suspend fun deleteProfessorClass(
        professorId: String,
        classId: String,
    ) {
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val cleanClassId = requireUuid(classId, "Class ID")

        client.postgrest.rpc(
            function = "delete_professor_class",
            parameters = buildJsonObject {
                put("p_professor_id", cleanProfessorId)
                put("p_class_id", cleanClassId)
            },
        )
    }

    suspend fun updateProfessorClass(
        professorId: String,
        classId: String,
        className: String,
        subjectCode: String,
        section: String,
        track: String,
    ): ProfessorClass {
        val cleanProfessorId = requireUuid(professorId, "Professor ID")
        val cleanClassId = requireUuid(classId, "Class ID")
        val cleanClassName = className.trim()
        val cleanSubjectCode = subjectCode.trim().uppercase()
        val cleanSection = section.trim()
        val cleanTrack = track.trim()

        if (cleanClassName.isBlank()) {
            error("Class name is required.")
        }
        if (cleanSubjectCode.isBlank()) {
            error("Subject code is required.")
        }

        val updatedClass = client.postgrest.rpc(
            function = "update_class_details",
            parameters = buildJsonObject {
                put("p_class_id", cleanClassId)
                put("p_professor_id", cleanProfessorId)
                put("p_class_name", cleanClassName)
                put("p_subject_code", cleanSubjectCode)
                put("p_section", cleanSection)
                put("p_track", cleanTrack)
            },
        ).decodeAs<ProfessorClass>()
        invalidateClassList(userId = professorId, role = "professor")
        return updatedClass
    }

    suspend fun getLatestAssignmentStatus(
        classId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): AssignmentStatus? {
        return readGuard.read(
            key = "assignment_status:v=$CACHE_SCHEMA_VERSION:class=$classId",
            groups = setOf(assignmentStatusGroup(classId)),
            ttlMillis = THIRTY_SECONDS_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_latest_assignment_status",
                parameters = buildJsonObject {
                    put("p_class_id", classId)
                },
            ).decodeSingleOrNull<AssignmentStatus>() ?: NoAssignmentStatus
        }.takeUnless { it === NoAssignmentStatus }
    }

    suspend fun getLatestAssignmentStatuses(
        classIds: List<String>,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): Map<String, AssignmentStatus> {
        val uniqueClassIds = classIds
            .map { requireUuid(it, "Class ID") }
            .distinct()
            .sorted()
        if (uniqueClassIds.isEmpty()) return emptyMap()

        return uniqueClassIds
            .chunked(MAX_BATCH_CLASS_IDS)
            .flatMap { chunk ->
                readGuard.read(
                    key = "assignment_statuses:v=$CACHE_SCHEMA_VERSION:classes=${chunk.joinToString(",")}",
                    groups = chunk.map { assignmentStatusGroup(it) }.toSet(),
                    ttlMillis = THIRTY_SECONDS_MILLIS,
                    policy = cachePolicy,
                ) {
                    client.postgrest.rpc(
                        function = "get_latest_assignment_statuses",
                        parameters = buildJsonObject {
                            put("p_class_ids", JsonArray(chunk.map { JsonPrimitive(it) }))
                        },
                    ).decodeList<AssignmentStatus>()
                }
            }
            .associateBy { it.classId }
    }

    suspend fun getProfessorClassStudents(
        classId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<com.myapplication.panthraa.model.ClassStudent> {
        return readGuard.read(
            key = "students:v=$CACHE_SCHEMA_VERSION:class=$classId",
            groups = setOf(studentsGroup(classId)),
            ttlMillis = ONE_MINUTE_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_professor_class_students",
                parameters = buildJsonObject {
                    put("p_class_id", classId)
                },
            ).decodeList<com.myapplication.panthraa.model.ClassStudent>()
        }
    }

    suspend fun getAssignmentAttendance(
        assignmentId: String,
        userId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<AttendanceStudent> {
        val cleanAssignmentId = requireUuid(assignmentId, "Assignment ID")
        val cleanUserId = requireUuid(userId, "User ID")
        return readGuard.read(
            key = "attendance:v=$CACHE_SCHEMA_VERSION:assignment=$cleanAssignmentId:user=$cleanUserId",
            groups = setOf(attendanceGroup(cleanAssignmentId, cleanUserId)),
            ttlMillis = THIRTY_SECONDS_MILLIS,
            policy = cachePolicy,
        ) {
            client.postgrest.rpc(
                function = "get_assignment_attendance",
                parameters = buildJsonObject {
                    put("p_assignment_id", cleanAssignmentId)
                    put("p_user_id", cleanUserId)
                },
            ).decodeList<AttendanceStudent>()
        }
    }

    suspend fun recordAssignmentAttendanceByIdNumber(
        assignmentId: String,
        professorId: String,
        idNumber: String,
    ): RecordAttendanceResult {
        val cleanIdNumber = idNumber.trim()
        if (cleanIdNumber.isBlank()) {
            error("Student ID number is required.")
        }

        val result = client.postgrest.rpc(
            function = "record_assignment_attendance_by_id_number",
            parameters = buildJsonObject {
                put("p_assignment_id", requireUuid(assignmentId, "Assignment ID"))
                put("p_professor_id", requireUuid(professorId, "Professor ID"))
                put("p_id_number", cleanIdNumber)
            },
        ).decodeAs<RecordAttendanceResult>()
        invalidateAttendance(assignmentId = assignmentId, userId = professorId)
        return result
    }

    suspend fun getClassJoinRequests(
        classIds: List<String>,
        professorId: String,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ): List<ClassJoinRequest> {
        val uniqueClassIds = classIds
            .map { requireUuid(it, "Class ID") }
            .distinct()
            .sorted()
        if (uniqueClassIds.isEmpty()) return emptyList()

        return uniqueClassIds
            .chunked(MAX_BATCH_CLASS_IDS)
            .flatMap { chunk ->
                readGuard.read(
                    key = "join_requests:v=$CACHE_SCHEMA_VERSION:professor=$professorId:classes=${chunk.joinToString(",")}",
                    groups = chunk.map { joinRequestsGroup(classId = it, professorId = professorId) }.toSet(),
                    ttlMillis = THIRTY_SECONDS_MILLIS,
                    policy = cachePolicy,
                ) {
                    client.postgrest.rpc(
                        function = "get_class_join_requests_for_classes",
                        parameters = buildJsonObject {
                            put("p_class_ids", JsonArray(chunk.map { JsonPrimitive(it) }))
                            put("p_professor_id", professorId)
                        },
                    ).decodeList<ClassJoinRequest>()
                }
            }
    }

    suspend fun approveClassJoinRequest(requestId: String, professorId: String) {
        client.postgrest.rpc(
            function = "approve_class_join_request",
            parameters = buildJsonObject {
                put("p_request_id", requestId)
                put("p_professor_id", professorId)
            },
        )
    }

    suspend fun rejectClassJoinRequest(requestId: String, professorId: String) {
        client.postgrest.rpc(
            function = "reject_class_join_request",
            parameters = buildJsonObject {
                put("p_request_id", requestId)
                put("p_professor_id", professorId)
            },
        )
    }

    suspend fun createProfessorClass(
        context: Context,
        professorId: String,
        className: String,
        subjectCode: String,
        joinCode: String,
        yearLevel: String,
        department: String,
        section: String,
        track: String?,
        coverImageUri: Uri?,
        themeColor: String,
        scheduleDays: List<String>,
        scheduleStartTime: String,
        scheduleEndTime: String,
    ): ProfessorClass {
        val cleanClassName = className.trim()
        val cleanSubjectCode = subjectCode.trim().uppercase()
        val cleanJoinCode = joinCode.trim()
        val cleanYearLevel = yearLevel.trim().lowercase()
        val cleanDepartment = department.trim()
        val cleanSection = section.trim()
        val cleanTrack = track?.trim().orEmpty()
        val cleanThemeColor = themeColor.trim().lowercase().ifBlank { DEFAULT_THEME_COLOR }
        val cleanScheduleDays = scheduleDays
            .map { it.trim().lowercase() }
            .filter { it in SCHEDULE_DAYS }
            .distinct()
        val cleanScheduleStartTime = scheduleStartTime.trim()
        val cleanScheduleEndTime = scheduleEndTime.trim()

        if (cleanClassName.isBlank()) {
            error("Class name is required.")
        }
        if (cleanSubjectCode.isBlank()) {
            error("Subject code is required.")
        }
        if (cleanYearLevel !in YEAR_LEVELS) {
            error("Year level is required.")
        }
        if (cleanDepartment.isBlank()) {
            error("Department is required.")
        }
        if (cleanSection.isBlank()) {
            error("Section is required.")
        }
        if (!JOIN_CODE_PATTERN.matches(cleanJoinCode)) {
            error("Join code must be exactly 6 digits.")
        }
        if (cleanScheduleDays.isEmpty()) {
            error("Set schedule is required.")
        }
        if (cleanScheduleStartTime.isBlank() || cleanScheduleEndTime.isBlank()) {
            error("Schedule time range is required.")
        }

        val coverImageUrl = coverImageUri?.let { uploadClassCover(context, professorId, it) }.orEmpty()

        val createdClass = client.postgrest.rpc(
            function = "create_class",
            parameters = buildJsonObject {
                put("p_professor_id", professorId)
                put("p_class_name", cleanClassName)
                put("p_subject_code", cleanSubjectCode)
                put("p_join_code", cleanJoinCode)
                put("p_year_level", cleanYearLevel)
                put("p_department", cleanDepartment)
                put("p_section", cleanSection)
                put("p_track", cleanTrack)
                put("p_cover_image_url", coverImageUrl)
                put("p_theme_color", cleanThemeColor)
                put("p_schedule_days", JsonArray(cleanScheduleDays.map { JsonPrimitive(it) }))
                put("p_schedule_start_time", cleanScheduleStartTime)
                put("p_schedule_end_time", cleanScheduleEndTime)
            },
        ).decodeAs<ProfessorClass>()
        invalidateClassList(userId = professorId, role = "professor")
        return createdClass
    }

    fun invalidateClassList(userId: String, role: String) {
        readGuard.invalidateGroup(classListGroup(userId = userId, role = role))
    }

    fun invalidateAssignmentCaches(classId: String) {
        readGuard.invalidateGroup(assignmentsGroup(classId))
        readGuard.invalidateGroup(assignmentStatusGroup(classId))
    }

    fun invalidateJoinRequests(classIds: Iterable<String>, professorId: String) {
        classIds.forEach { classId ->
            readGuard.invalidateGroup(joinRequestsGroup(classId = classId, professorId = professorId))
        }
    }

    fun invalidateSubmissions(assignmentId: String, professorId: String) {
        readGuard.invalidateGroup(submissionsGroup(assignmentId = assignmentId, professorId = professorId))
    }

    fun invalidateComments(assignmentId: String) {
        readGuard.invalidateGroup(commentsGroup(assignmentId))
    }

    fun invalidateAttendance(assignmentId: String, userId: String) {
        readGuard.invalidateGroup(attendanceGroup(assignmentId = assignmentId, userId = userId))
    }

    fun invalidateStudentSummaries(studentId: String) {
        readGuard.invalidateGroup(pendingAssignmentsGroup(studentId))
        readGuard.invalidateGroup(gradesGroup(studentId))
    }

    fun clearReadCache() {
        readGuard.clear()
    }

    private suspend fun uploadClassCover(context: Context, professorId: String, imageUri: Uri): String {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(imageUri) ?: error("Unsupported cover image type.")
        val extension = when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> error("Only JPEG, PNG, or WEBP cover images are allowed.")
        }

        val size = getFileSize(context, imageUri)
        if (size > MAX_COVER_IMAGE_BYTES) {
            error("Cover image must be 3MB or smaller.")
        }

        val bytes = resolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: error("Unable to read selected cover image.")
        if (bytes.size > MAX_COVER_IMAGE_BYTES) {
            error("Cover image must be 3MB or smaller.")
        }

        val path = "classes/$professorId/cover_${System.currentTimeMillis()}.$extension"
        client.storage.from(CLASS_COVER_BUCKET).upload(path, bytes) {
            contentType = ContentType.parse(mimeType)
        }

        return client.storage.from(CLASS_COVER_BUCKET).publicUrl(path)
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex >= 0 && cursor.moveToFirst()) {
                return cursor.getLong(sizeIndex)
            }
        }
        return context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
    }

    private fun getDisplayName(context: Context, uri: Uri): String? {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
        return uri.lastPathSegment
    }

    private fun requireUuid(value: String, fieldName: String): String {
        val cleanValue = value.trim()
        if (cleanValue.isBlank()) {
            error("$fieldName is missing.")
        }
        runCatching { UUID.fromString(cleanValue) }
            .getOrElse { error("$fieldName is invalid: $cleanValue") }
        return cleanValue
    }

    companion object {
        private const val CLASS_COVER_BUCKET = "class-covers"
        private const val ASSIGNMENT_FILES_BUCKET = "assignment-files"
        private const val DEFAULT_THEME_COLOR = "blue"
        private const val MAX_COVER_IMAGE_BYTES = 3_145_728L
        private const val CACHE_SCHEMA_VERSION = 2
        private const val THIRTY_SECONDS_MILLIS = 30_000L
        private const val ONE_MINUTE_MILLIS = 60_000L
        private const val MAX_BATCH_CLASS_IDS = 50
        private val JOIN_CODE_PATTERN = Regex("^\\d{6}$")
        private val YEAR_LEVELS = setOf("first", "second", "third", "fourth")
        private val SCHEDULE_DAYS = setOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
        private val ASSIGNMENT_CATEGORIES = setOf("lecture", "laboratory")
        private val SUBMISSION_FORMATS = setOf("pdf", "docx", "pptx", "xlsx", "image", "zip")
        private const val TAG = "ClassRepository"
        private val NoAssignmentStatus = AssignmentStatus(classId = "__no_assignment_status__")

        private fun String.toSubmissionFormat(): String {
            return when (trim().lowercase()) {
                "doc", "word", "docx" -> "docx"
                "ppt", "powerpoint", "pptx" -> "pptx"
                "xls", "excel", "xlsx" -> "xlsx"
                "gif", "heic", "heif", "jpg", "jpeg", "png", "webp", "image", "photo", "picture" -> "image"
                "zip", "compressed" -> "zip"
                else -> trim().lowercase().takeIf { it in SUBMISSION_FORMATS } ?: "pdf"
            }
        }

        private fun isAllowedSubmissionFile(format: String, mimeType: String?, extension: String): Boolean {
            val cleanMime = mimeType.orEmpty().lowercase()
            val cleanExtension = extension.lowercase()
            return when (format.toSubmissionFormat()) {
                "pdf" -> cleanExtension == "pdf" || cleanMime == "application/pdf"
                "docx" -> cleanExtension in setOf("doc", "docx") ||
                    cleanMime in setOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                "pptx" -> cleanExtension in setOf("ppt", "pptx") ||
                    cleanMime in setOf("application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
                "xlsx" -> cleanExtension in setOf("xls", "xlsx") ||
                    cleanMime in setOf("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                "image" -> cleanExtension in setOf("gif", "heic", "heif", "jpg", "jpeg", "png", "webp") || cleanMime.startsWith("image/")
                "zip" -> cleanExtension == "zip" || cleanMime in setOf("application/zip", "application/x-zip-compressed")
                else -> false
            }
        }

        private fun submissionFormatLabel(format: String): String {
            return when (format.toSubmissionFormat()) {
                "pdf" -> "PDF"
                "docx" -> "Word document"
                "pptx" -> "PowerPoint"
                "xlsx" -> "Excel spreadsheet"
                "image" -> "picture"
                "zip" -> "ZIP archive"
                else -> "PDF"
            }
        }

        private fun defaultExtensionForFormat(format: String): String {
            return when (format.toSubmissionFormat()) {
                "docx" -> "docx"
                "pptx" -> "pptx"
                "xlsx" -> "xlsx"
                "image" -> "jpg"
                "zip" -> "zip"
                else -> "pdf"
            }
        }

        private fun classListKey(userId: String, role: String): String {
            return "classes:v=$CACHE_SCHEMA_VERSION:user=$userId:role=${role.lowercase()}"
        }

        private fun classListGroup(userId: String, role: String): String {
            return "classes:user=$userId:role=${role.lowercase()}"
        }

        private fun assignmentsGroup(classId: String): String {
            return "assignments:class=$classId"
        }

        private fun assignmentStatusGroup(classId: String): String {
            return "assignment_status:class=$classId"
        }

        private fun classmatesGroup(classId: String): String {
            return "classmates:class=$classId"
        }

        private fun studentsGroup(classId: String): String {
            return "students:class=$classId"
        }

        private fun pendingAssignmentsGroup(studentId: String): String {
            return "pending_assignments:student=$studentId"
        }

        private fun gradesGroup(studentId: String): String {
            return "grades:student=$studentId"
        }

        private fun submissionsGroup(assignmentId: String, professorId: String): String {
            return "submissions:professor=$professorId:assignment=$assignmentId"
        }

        private fun commentsGroup(assignmentId: String): String {
            return "comments:assignment=$assignmentId"
        }

        private fun attendanceGroup(assignmentId: String, userId: String): String {
            return "attendance:user=$userId:assignment=$assignmentId"
        }

        private fun joinRequestsGroup(classId: String, professorId: String): String {
            return "join_requests:professor=$professorId:class=$classId"
        }

        private fun String.toBackendAssignmentType(): String {
            return if (equals("task", ignoreCase = true)) "assignment" else trim().lowercase()
        }

        fun readableError(throwable: Throwable): String {
            Log.e(TAG, "Class feature error", throwable)
            val message = throwable.message.orEmpty()
            val restException = throwable as? RestException
            val restText = listOfNotNull(
                restException?.error,
                restException?.description,
                message,
            ).joinToString(" ")
            return when {
                throwable is HttpRequestTimeoutException || restText.isConnectivityMessage() ->
                    "Refresh failed. Showing last saved data."
                restText.contains("Materials do not accept submissions", ignoreCase = true) ->
                    "This upload is a material, not a task. Stay on the material for 10 seconds to mark it viewed."
                restText.contains("Comments are disabled", ignoreCase = true) ->
                    "Comments are disabled for this upload."
                restText.contains("comment", ignoreCase = true) &&
                    (restText.contains("does not exist", ignoreCase = true) ||
                        restText.contains("Could not find the function", ignoreCase = true)) ->
                    "Supabase comment RPC is missing or outdated. Run supabase_assignment_comments_patch.sql."
                restText.contains("Target points cannot be lower", ignoreCase = true) ->
                    "Target points cannot be lower than an existing student score."
                restText.contains("Submission is not yet open", ignoreCase = true) ->
                    "This assignment is not open yet. Check its scheduled start date and time."
                restText.contains("Submission window has ended", ignoreCase = true) ->
                    "The submission window has ended."
                restText.contains("Schedule conflicts", ignoreCase = true) ->
                    "Schedule conflicts with an existing class for this year, course, section, and track."
                restText.contains("Set schedule", ignoreCase = true) ||
                    restText.contains("Schedule time range", ignoreCase = true) ||
                    restText.contains("Schedule end time", ignoreCase = true) ->
                    message.ifBlank { "Set schedule is required." }
                restText.contains("get_assignment_attendance", ignoreCase = true) ||
                    restText.contains("record_assignment_attendance_by_id_number", ignoreCase = true) ->
                    "Supabase attendance RPC is missing or outdated. Run supabase_qr_attendance_patch.sql."
                restText.contains("attendance", ignoreCase = true) &&
                    restText.contains("does not exist", ignoreCase = true) ->
                    "Supabase attendance table is missing. Run supabase_qr_attendance_patch.sql."
                restText.contains("row-level security", ignoreCase = true) &&
                    restText.contains("class_assignments", ignoreCase = true) ->
                    "Assignment upload is blocked by Supabase security rules. Run supabase_assignment_types_migration.sql."
                restText.contains("assignment-files", ignoreCase = true) &&
                    (restText.contains("bucket", ignoreCase = true) || restText.contains("storage", ignoreCase = true)) ->
                    "Assignment file storage is missing or blocked. Run supabase_assignment_types_migration.sql."
                restText.contains("assignment_id", ignoreCase = true) &&
                    restText.contains("does not exist", ignoreCase = true) ->
                    "Supabase attendance column is missing. Run supabase_qr_attendance_patch.sql."
                restText.contains("relation", ignoreCase = true) &&
                    restText.contains("does not exist", ignoreCase = true) ->
                    "Supabase table is missing. Make sure app_users, classes, class_enrollments, class_assignments, and assignment_submissions exist."
                restText.contains("column", ignoreCase = true) &&
                    restText.contains("does not exist", ignoreCase = true) ->
                    "Supabase column is missing or renamed. Check the assignment SQL schema."
                restText.contains("Could not find the function", ignoreCase = true) ||
                    restText.contains("schema cache", ignoreCase = true) ||
                    restText.contains("PGRST202", ignoreCase = true) -> {
                    if (restText.contains("get_student_pending_assignments", ignoreCase = true)) {
                        "Supabase RPC is missing or outdated. Run supabase_dashboard_newsfeed_patch.sql."
                    } else if (
                        restText.contains("get_student_grades", ignoreCase = true) ||
                        restText.contains("grade_assignment_submission", ignoreCase = true)
                    ) {
                        "Supabase grade RPC is missing or outdated. Run supabase_grading_system_patch.sql."
                    } else if (
                        restText.contains("create_class", ignoreCase = true) ||
                        restText.contains("get_professor_classes", ignoreCase = true) ||
                        restText.contains("get_student_classes", ignoreCase = true) ||
                        restText.contains("p_schedule", ignoreCase = true) ||
                        restText.contains("schedule_days", ignoreCase = true)
                    ) {
                        "Supabase class schedule RPC is missing or outdated. Run supabase_class_schedule_patch.sql."
                    } else if (restText.contains("record_material_view", ignoreCase = true)) {
                        "Supabase material view RPC is missing or outdated. Run supabase_material_view_patch.sql."
                    } else if (restText.contains("update_class_assignment", ignoreCase = true)) {
                        "Supabase assignment edit RPC is missing or outdated. Run supabase_assignment_edit_patch.sql."
                    } else if (
                        restText.contains("submit_assignment", ignoreCase = true) ||
                        restText.contains("get_class_assignments", ignoreCase = true)
                    ) {
                        "Supabase assignment RPC signature is missing or outdated. Run supabase_submission_format_file_required_patch.sql."
                    } else {
                        "Supabase RPC is missing or outdated. Run the assignment SQL for create_class_assignment, get_class_assignments, submit_assignment, grade_assignment_submission, delete_class_assignment, and get_latest_assignment_status."
                    }
                }
                message.contains("Class ID", ignoreCase = true) -> message
                message.contains("Professor ID", ignoreCase = true) -> message
                message.contains("Assignment ID", ignoreCase = true) -> message
                message.contains("duplicate", ignoreCase = true) ||
                    message.contains("unique", ignoreCase = true) ||
                    message.contains("join_code", ignoreCase = true) -> "Join code is already taken."
                message.contains("Class name", ignoreCase = true) -> "Class name is required."
                message.contains("Subject code", ignoreCase = true) -> "Subject code is required."
                message.contains("Subject not found", ignoreCase = true) -> "Subject not found for this year."
                message.contains("Year level", ignoreCase = true) -> "Year level is required."
                message.contains("Department", ignoreCase = true) -> message
                message.contains("Section", ignoreCase = true) -> message
                message.contains("academic profile", ignoreCase = true) -> "Complete your academic profile before joining a class."
                message.contains("profile department", ignoreCase = true) -> message
                message.contains("profile section", ignoreCase = true) -> message
                message.contains("profile track", ignoreCase = true) -> message
                message.contains("Assignment title", ignoreCase = true) -> "Assignment title is required."
                message.contains("Submission text", ignoreCase = true) -> "Submission text is required."
                message.contains("6 digits", ignoreCase = true) -> "Join code must be exactly 6 digits."
                message.contains("3MB", ignoreCase = true) -> "Cover image must be 3MB or smaller."
                message.contains("JPEG", ignoreCase = true) -> "Only JPEG, PNG, or WEBP cover images are allowed."
                message.contains("Join code", ignoreCase = true) -> message
                message.contains("request", ignoreCase = true) -> message
                message.isNotBlank() -> "Debug error: $message"
                else -> "Debug error: ${throwable::class.simpleName ?: "Unknown error"}"
            }
        }

        private fun String.isConnectivityMessage(): Boolean {
            val text = lowercase()
            return listOf(
                "unable to resolve host",
                "no address associated with hostname",
                "failed to connect",
                "connect timed out",
                "request timed out",
                "unknownhost",
                "unresolvedaddress",
                "network is unreachable",
            ).any { marker -> text.contains(marker) }
        }
    }
}
