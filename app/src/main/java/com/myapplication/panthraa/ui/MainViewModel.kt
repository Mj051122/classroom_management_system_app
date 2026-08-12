package com.myapplication.panthraa.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapplication.panthraa.data.AppUserRepository
import com.myapplication.panthraa.data.CachePolicy
import com.myapplication.panthraa.data.ClassRepository
import com.myapplication.panthraa.data.OfflineImageCache
import com.myapplication.panthraa.data.OfflineReviewCacheStore
import com.myapplication.panthraa.data.TaskRepository
import com.myapplication.panthraa.data.AnnouncementRepository
import com.myapplication.panthraa.data.UploadNotificationHelper
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.ConnectivityStatus
import com.myapplication.panthraa.model.PendingAssignment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AppUserRepository = AppUserRepository(),
    private val classRepository: ClassRepository = ClassRepository(),
    private val taskRepository: TaskRepository = TaskRepository(),
    private val announcementRepository: AnnouncementRepository = AnnouncementRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState
    private var offlineCacheStore: OfflineReviewCacheStore? = null
    private var appContext: Context? = null
    private val activeMaterialViewRequests = mutableSetOf<String>()

    fun setCurrentUser(user: AppUser) {
        val previousUser = _uiState.value.currentUser
        if (previousUser != null && (previousUser.id != user.id || !previousUser.role.equals(user.role, ignoreCase = true))) {
            classRepository.clearReadCache()
        }
        _uiState.update { it.copy(currentUser = user) }
    }

    fun preparePendingAssignment(pending: PendingAssignment) {
        _uiState.update { state ->
            state.copy(
                classAssignments = hydratePendingAssignment(
                    currentAssignments = state.classAssignments,
                    pending = pending,
                ),
            )
        }
    }

    fun initializeOfflineSupport(
        context: Context,
        user: AppUser,
        connectivityStatus: ConnectivityStatus,
        startedOffline: Boolean,
    ) {
        appContext = context.applicationContext
        if (offlineCacheStore == null) {
            offlineCacheStore = OfflineReviewCacheStore(context.applicationContext)
        }

        val cached = offlineCacheStore?.getCacheForUser(user.id)
        _uiState.update { state ->
            state.copy(
                currentUser = user,
                studentClasses = cached?.studentClasses ?: state.studentClasses,
                professorClasses = cached?.professorClasses ?: state.professorClasses,
                classAssignments = cached?.classAssignments ?: state.classAssignments,
                assignmentStatuses = cached?.assignmentStatuses ?: state.assignmentStatuses,
                pendingAssignments = cached?.pendingAssignments ?: state.pendingAssignments,
                studentGrades = cached?.studentGrades ?: state.studentGrades,
                taskReminders = cached?.taskReminders ?: state.taskReminders,
                classAnnouncements = cached?.classAnnouncements ?: state.classAnnouncements,
                connectivityStatus = connectivityStatus,
                isOfflineMode = startedOffline || connectivityStatus != ConnectivityStatus.Online,
                message = if (connectivityStatus == ConnectivityStatus.Online) {
                    state.message
                } else {
                    "Offline mode: showing last saved data."
                },
            )
        }
        cacheImageUrls(listOf(user.profilePictureUrl))

        if (connectivityStatus == ConnectivityStatus.Online) {
            // Offline-first architecture: only auto-refresh if we have absolutely no cache
            if (cached == null) {
                refreshOnlineData(context)
            }
        }
    }

    fun setConnectivityStatus(context: Context, status: ConnectivityStatus) {
        val wasOffline = _uiState.value.isOfflineMode
        _uiState.update {
            it.copy(
                connectivityStatus = status,
                isOfflineMode = status != ConnectivityStatus.Online,
                message = if (status == ConnectivityStatus.Online) {
                    it.message
                } else {
                    "Offline mode: showing last saved data."
                },
            )
        }
        if (status == ConnectivityStatus.Online && wasOffline) {
            // Offline-first architecture: do not auto-refresh when regaining connection.
            // Wait for user to explicitly pull-to-refresh.
        }
    }

    fun refreshOnlineData(context: Context) {
        refreshDashboard(context)
    }

    fun refreshDashboard(context: Context) {
        val user = _uiState.value.currentUser ?: return
        launchPullRefresh(
            surface = RefreshSurface.Dashboard,
            onStart = { it.copy(isRefreshingOnlineData = true) },
            onFinish = { it.copy(isRefreshingOnlineData = false) },
        ) {
            refreshClassesNow(user, cachePolicy = CachePolicy.FORCE_REFRESH)
            if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) return@launchPullRefresh
            refreshTaskRemindersNow(context, user)
            if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) return@launchPullRefresh
            refreshAnnouncementsNow(user, cachePolicy = CachePolicy.FORCE_REFRESH)
            if (user.role.equals("student", ignoreCase = true)) {
                if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) return@launchPullRefresh
                refreshPendingAssignmentsNow(user, cachePolicy = CachePolicy.FORCE_REFRESH)
                if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) return@launchPullRefresh
                refreshStudentGradesNow(user, cachePolicy = CachePolicy.FORCE_REFRESH)
            }
            _uiState.update {
                it.copy(
                    lastOnlineRefreshFailed = false,
                    isOfflineMode = false,
                )
            }
        }
    }

    fun refreshClasses() {
        val user = _uiState.value.currentUser ?: return
        launchPullRefresh(RefreshSurface.Classes) {
            refreshClassesNow(user, cachePolicy = CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshClassAssignments(classId: String) {
        launchPullRefresh(RefreshSurface.Assignments) {
            refreshClassAssignmentsNow(classId, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshAssignmentSubmissions(assignmentId: String) {
        if (_uiState.value.currentUser == null) return
        launchPullRefresh(RefreshSurface.Submissions) {
            refreshAssignmentSubmissionsNow(assignmentId, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshClassmates(classId: String) {
        launchPullRefresh(RefreshSurface.Classmates) {
            refreshClassmatesNow(classId, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshProfessorStudents(classId: String) {
        launchPullRefresh(RefreshSurface.Students) {
            refreshProfessorStudentsNow(classId, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshAssignmentAttendance(assignmentId: String) {
        if (_uiState.value.currentUser == null) return
        launchPullRefresh(RefreshSurface.Attendance) {
            refreshAssignmentAttendanceNow(assignmentId, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshClassJoinRequests(classIds: List<String>) {
        if (classIds.isEmpty() || _uiState.value.currentUser == null) return
        launchPullRefresh(RefreshSurface.JoinRequests) {
            refreshClassJoinRequestsNow(classIds, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshUsers() {
        launchPullRefresh(RefreshSurface.People) {
            refreshUsersNow(CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshAnnouncements(user: AppUser? = _uiState.value.currentUser) {
        val targetUser = user ?: return
        launchPullRefresh(RefreshSurface.Announcements) {
            refreshAnnouncementsNow(targetUser, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshPendingAssignments(user: AppUser? = _uiState.value.currentUser) {
        val targetUser = user ?: return
        launchPullRefresh(RefreshSurface.PendingAssignments) {
            refreshPendingAssignmentsNow(targetUser, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshStudentGrades(user: AppUser? = _uiState.value.currentUser) {
        val targetUser = user ?: return
        launchPullRefresh(RefreshSurface.Grades) {
            refreshStudentGradesNow(targetUser, CachePolicy.FORCE_REFRESH)
        }
    }

    fun refreshProfessorGradeMonitor(user: AppUser? = _uiState.value.currentUser) {
        val targetUser = user?.takeIf { it.role.equals("professor", ignoreCase = true) } ?: return
        launchPullRefresh(RefreshSurface.Grades) {
            refreshProfessorGradeMonitorNow(targetUser, CachePolicy.FORCE_REFRESH)
        }
    }

    fun showPeople() {
        _uiState.update { it.copy(showPeople = true) }
        loadUsers()
    }

    fun hidePeople() {
        _uiState.update { it.copy(showPeople = false) }
    }

    fun loadClasses(
        user: AppUser? = _uiState.value.currentUser,
        allowCacheFallback: Boolean = true,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ) {
        val targetUser = user ?: return
        if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) {
            val cached = offlineCacheStore?.getCacheForUser(targetUser.id)
            _uiState.update {
                it.copy(
                    studentClasses = cached?.studentClasses ?: it.studentClasses,
                    professorClasses = cached?.professorClasses ?: it.professorClasses,
                    classAssignments = cached?.classAssignments ?: it.classAssignments,
                    assignmentStatuses = cached?.assignmentStatuses ?: it.assignmentStatuses,
                    pendingAssignments = cached?.pendingAssignments ?: it.pendingAssignments,
                    studentGrades = cached?.studentGrades ?: it.studentGrades,
                    isLoadingClasses = false,
                    isOfflineMode = true,
                    message = "Offline mode: showing last saved data.",
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClasses = true, message = null) }
            if (targetUser.role.equals("student", ignoreCase = true)) {
                runCatching { classRepository.getStudentClasses(targetUser.id, cachePolicy) }
                    .onSuccess { classes ->
                        val statuses = runCatching {
                            classRepository.getLatestAssignmentStatuses(classes.map { it.id }, cachePolicy)
                        }.getOrDefault(emptyMap())
                        _uiState.update {
                            it.copy(
                                studentClasses = classes,
                                assignmentStatuses = statuses,
                                isLoadingClasses = false,
                                isOfflineMode = false,
                                lastOnlineRefreshFailed = false,
                            )
                        }
                        offlineCacheStore?.saveClassSnapshot(
                            user = targetUser,
                            studentClasses = classes,
                            professorClasses = emptyList(),
                            assignmentStatuses = statuses,
                        )
                        cacheUserClassImages(
                            user = targetUser,
                            studentClasses = classes,
                            professorClasses = emptyList(),
                        )
                    }
                    .onFailure { error ->
                        AppUserRepository.readableError(error)
                        val connectivityFailed = error.isConnectivityFailure()
                        val cached = offlineCacheStore?.getCacheForUser(targetUser.id)
                        _uiState.update {
                            if (allowCacheFallback && cached != null) {
                                it.copy(
                                    studentClasses = cached.studentClasses,
                                    classAssignments = cached.classAssignments,
                                    assignmentStatuses = cached.assignmentStatuses,
                                    studentGrades = cached.studentGrades,
                                    isLoadingClasses = false,
                                    connectivityStatus = if (connectivityFailed) ConnectivityStatus.Offline else it.connectivityStatus,
                                    isOfflineMode = true,
                                    lastOnlineRefreshFailed = true,
                                    message = "Refresh failed. Showing last saved data.",
                                )
                            } else {
                                it.copy(
                                    isLoadingClasses = false,
                                    connectivityStatus = if (connectivityFailed) ConnectivityStatus.Offline else it.connectivityStatus,
                                    isOfflineMode = connectivityFailed || it.isOfflineMode,
                                    lastOnlineRefreshFailed = true,
                                    message = if (connectivityFailed) {
                                        "Refresh failed. Showing last saved data."
                                    } else {
                                        "Failed to load classes. Please try again."
                                    },
                                )
                            }
                        }
                    }
            } else if (targetUser.role.equals("professor", ignoreCase = true)) {
                runCatching { classRepository.getProfessorClasses(targetUser.id, cachePolicy) }
                    .onSuccess { classes ->
                        val statuses = runCatching {
                            classRepository.getLatestAssignmentStatuses(classes.map { it.id }, cachePolicy)
                        }.getOrDefault(emptyMap())
                        val joinRequests = if (classes.isEmpty()) {
                            emptyList()
                        } else {
                            runCatching {
                                classRepository.getClassJoinRequests(
                                    classIds = classes.map { it.id },
                                    professorId = targetUser.id,
                                )
                            }.getOrDefault(emptyList())
                        }
                        _uiState.update {
                            it.copy(
                                professorClasses = classes,
                                assignmentStatuses = statuses,
                                classJoinRequests = joinRequests,
                                isLoadingClasses = false,
                                isOfflineMode = false,
                                lastOnlineRefreshFailed = false,
                            )
                        }
                        offlineCacheStore?.saveClassSnapshot(
                            user = targetUser,
                            studentClasses = emptyList(),
                            professorClasses = classes,
                            assignmentStatuses = statuses,
                        )
                        cacheImageUrls(joinRequests.map { it.photoUrl })
                        cacheUserClassImages(
                            user = targetUser,
                            studentClasses = emptyList(),
                            professorClasses = classes,
                        )
                    }
                    .onFailure { error ->
                        AppUserRepository.readableError(error)
                        val connectivityFailed = error.isConnectivityFailure()
                        val cached = offlineCacheStore?.getCacheForUser(targetUser.id)
                        _uiState.update {
                            if (allowCacheFallback && cached != null) {
                                it.copy(
                                    professorClasses = cached.professorClasses,
                                    classAssignments = cached.classAssignments,
                                    assignmentStatuses = cached.assignmentStatuses,
                                    isLoadingClasses = false,
                                    connectivityStatus = if (connectivityFailed) ConnectivityStatus.Offline else it.connectivityStatus,
                                    isOfflineMode = true,
                                    lastOnlineRefreshFailed = true,
                                    message = "Refresh failed. Showing last saved data.",
                                )
                            } else {
                                it.copy(
                                    isLoadingClasses = false,
                                    connectivityStatus = if (connectivityFailed) ConnectivityStatus.Offline else it.connectivityStatus,
                                    isOfflineMode = connectivityFailed || it.isOfflineMode,
                                    lastOnlineRefreshFailed = true,
                                    message = if (connectivityFailed) {
                                        "Refresh failed. Showing last saved data."
                                    } else {
                                        "Failed to load classes. Please try again."
                                    },
                                )
                            }
                        }
                    }
            } else {
                _uiState.update { it.copy(isLoadingClasses = false) }
            }
        }
    }

    fun loadClassAssignments(classId: String) {
        loadClassAssignments(classId, CachePolicy.USE_FRESH)
    }

    private fun loadClassAssignments(
        classId: String,
        cachePolicy: CachePolicy,
    ) {
        val user = _uiState.value.currentUser
        val studentId = user?.takeIf { it.role.equals("student", ignoreCase = true) }?.id
        if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) {
            val cachedAssignments = user?.let { offlineCacheStore?.getCacheForUser(it.id)?.classAssignments }
                ?.filter { it.classId == classId }
                ?: emptyList()
            _uiState.update {
                it.copy(
                    classAssignments = cachedAssignments,
                    isLoadingAssignments = false,
                    isOfflineMode = true,
                    message = "Offline mode: showing last saved data.",
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAssignments = true, message = null) }
            runCatching { classRepository.getClassAssignments(classId, studentId, cachePolicy) }
                .onSuccess { assignments ->
                    _uiState.update {
                        it.copy(
                            classAssignments = assignments,
                            isLoadingAssignments = false,
                            lastOnlineRefreshFailed = false,
                        )
                    }
                    if (user != null) {
                        offlineCacheStore?.saveAssignmentsSnapshot(user, classId, assignments)
                    }
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(isLoadingAssignments = false, message = ClassRepository.readableError(error))
                    }
                }
        }
    }

    fun loadAssignmentSubmissions(assignmentId: String) {
        val professor = _uiState.value.currentUser ?: return
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to load submissions.")) {
            _uiState.update { it.copy(isLoadingAssignmentSubmissions = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingAssignmentSubmissions = true,
                    message = null,
                )
            }
            runCatching { classRepository.getAssignmentSubmissions(assignmentId, professor.id) }
                .onSuccess { submissions ->
                    _uiState.update {
                        it.copy(
                            assignmentSubmissions = submissions,
                            isLoadingAssignmentSubmissions = false,
                        )
                    }
                    cacheImageUrls(submissions.mapNotNull { it.photoUrl })
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(
                            isLoadingAssignmentSubmissions = false,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun loadProfessorGradeMonitor(user: AppUser? = _uiState.value.currentUser) {
        val targetUser = user?.takeIf { it.role.equals("professor", ignoreCase = true) } ?: return
        if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) {
            _uiState.update {
                it.copy(
                    isLoadingProfessorGradeMonitor = false,
                    isOfflineMode = true,
                    message = "Internet required to load grade monitor.",
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProfessorGradeMonitor = true, message = null) }
            runCatching { buildProfessorGradeMonitor(targetUser, CachePolicy.USE_FRESH) }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            professorGradeMonitorEnrollments = result.first,
                            professorGradeMonitorSubmissions = result.second,
                            isLoadingProfessorGradeMonitor = false,
                            lastOnlineRefreshFailed = false,
                        )
                    }
                    cacheImageUrls(
                        result.first.mapNotNull { it.photoUrl } +
                            result.second.mapNotNull { it.photoUrl },
                    )
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(
                            isLoadingProfessorGradeMonitor = false,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun gradeAssignmentSubmission(submissionId: String, score: Int, targetPoints: Int) {
        val professor = _uiState.value.currentUser ?: return
        if (_uiState.value.isScoringSubmission) return
        if (isOfflineWriteBlocked()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isScoringSubmission = true, message = null) }
            runCatching {
                classRepository.gradeAssignmentSubmission(
                    submissionId = submissionId,
                    professorId = professor.id,
                    score = score,
                    targetPoints = targetPoints,
                )
            }.onSuccess { updatedSubmission ->
                loadAssignmentSubmissions(updatedSubmission.assignmentId)
                loadClasses(professor)
                loadProfessorGradeMonitor(professor)
                _uiState.update { it.copy(isScoringSubmission = false, message = "Score saved.") }
            }.onFailure { error ->
                if (handleConnectivityFailure(error)) return@onFailure
                _uiState.update {
                    it.copy(
                        isScoringSubmission = false,
                        message = ClassRepository.readableError(error),
                    )
                }
            }
        }
    }

    fun loadClassmates(classId: String) {
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to load classmates.")) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingClassmates = true, message = null) }
            runCatching { classRepository.getClassmates(classId) }
                .onSuccess { classmates ->
                    _uiState.update {
                        it.copy(classmates = classmates, isLoadingClassmates = false)
                    }
                    cacheImageUrls(classmates.map { it.photoUrl })
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(isLoadingClassmates = false, message = ClassRepository.readableError(error))
                    }
                }
        }
    }

    fun loadProfessorStudents(classId: String) {
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to load students.")) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProfessorStudents = true, message = null) }
            runCatching { classRepository.getProfessorClassStudents(classId) }
                .onSuccess { students ->
                    _uiState.update {
                        it.copy(professorStudents = students, isLoadingProfessorStudents = false)
                    }
                    cacheImageUrls(students.map { it.photoUrl })
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(isLoadingProfessorStudents = false, message = ClassRepository.readableError(error))
                    }
                }
        }
    }

    fun loadAssignmentAttendance(assignmentId: String) {
        val professor = _uiState.value.currentUser ?: return
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to load attendance.")) {
            _uiState.update { it.copy(attendanceStudents = emptyList(), attendanceError = "Internet required to load attendance.", isLoadingAttendance = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAttendance = true, attendanceError = null, message = null) }
            runCatching { classRepository.getAssignmentAttendance(assignmentId, professor.id) }
                .onSuccess { students ->
                    _uiState.update {
                        it.copy(
                            attendanceStudents = students,
                            attendanceError = null,
                            isLoadingAttendance = false,
                        )
                    }
                    cacheImageUrls(students.mapNotNull { it.photoUrl })
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    val readableMessage = ClassRepository.readableError(error)
                    _uiState.update {
                        it.copy(
                            attendanceStudents = emptyList(),
                            attendanceError = readableMessage,
                            isLoadingAttendance = false,
                            message = readableMessage,
                        )
                    }
                }
        }
    }

    fun recordAttendanceByQr(assignmentId: String, scannedIdNumber: String) {
        val professor = _uiState.value.currentUser ?: return
        if (isOfflineWriteBlocked()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRecordingAttendance = true, message = null) }
            runCatching {
                classRepository.recordAssignmentAttendanceByIdNumber(
                    assignmentId = assignmentId,
                    professorId = professor.id,
                    idNumber = scannedIdNumber,
                )
            }.onSuccess { result ->
                _uiState.update { state ->
                    state.copy(
                        attendanceStudents = state.attendanceStudents.map { student ->
                            if (student.studentId == result.studentId) {
                                student.copy(status = "present")
                            } else {
                                student
                            }
                        },
                        isRecordingAttendance = false,
                        message = if (result.alreadyRecorded) {
                            "Attendance already recorded."
                        } else {
                            result.message.ifBlank { "Attendance recorded." }
                        },
                    )
                }
                loadAssignmentAttendance(assignmentId)
            }.onFailure { error ->
                if (handleConnectivityFailure(error)) return@onFailure
                _uiState.update {
                    it.copy(
                        isRecordingAttendance = false,
                        message = ClassRepository.readableError(error),
                    )
                }
            }
        }
    }

    fun loadClassJoinRequests(classIds: List<String>) {
        val professor = _uiState.value.currentUser ?: return
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to load join requests.")) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingJoinRequests = true, message = null) }
            runCatching { classRepository.getClassJoinRequests(classIds, professor.id) }
                .onSuccess { requests ->
                    _uiState.update {
                        it.copy(
                            classJoinRequests = mergeProfessorJoinRequestScope(
                                current = it.classJoinRequests,
                                refreshed = requests,
                                refreshedClassIds = classIds,
                            ),
                            isLoadingJoinRequests = false,
                        )
                    }
                    cacheImageUrls(requests.map { it.photoUrl })
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(isLoadingJoinRequests = false, message = ClassRepository.readableError(error))
                    }
                }
        }
    }

    fun approveClassJoinRequest(requestId: String, classIdsToRefresh: List<String>) {
        val professor = _uiState.value.currentUser ?: return
        if (isOfflineWriteBlocked()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingJoinRequest = true, message = null) }
            runCatching { classRepository.approveClassJoinRequest(requestId, professor.id) }
                .onSuccess {
                    loadClasses(professor)
                    classRepository.invalidateJoinRequests(classIdsToRefresh, professor.id)
                    runCatching { classRepository.getClassJoinRequests(classIdsToRefresh, professor.id) }
                        .onSuccess { requests ->
                            _uiState.update {
                                it.copy(
                                    classJoinRequests = mergeProfessorJoinRequestScope(
                                        current = it.classJoinRequests,
                                        refreshed = requests,
                                        refreshedClassIds = classIdsToRefresh,
                                    ),
                                    isUpdatingJoinRequest = false,
                                    message = "Join request approved.",
                                )
                            }
                        }
                        .onFailure {
                            _uiState.update { state ->
                                state.copy(
                                    classJoinRequests = state.classJoinRequests.filterNot { request -> request.id == requestId },
                                    isUpdatingJoinRequest = false,
                                    message = "Join request approved.",
                                )
                            }
                        }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isUpdatingJoinRequest = false, message = ClassRepository.readableError(error))
                    }
                }
        }
    }

    fun rejectClassJoinRequest(requestId: String, classIdsToRefresh: List<String>) {
        val professor = _uiState.value.currentUser ?: return
        if (isOfflineWriteBlocked()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingJoinRequest = true, message = null) }
            runCatching { classRepository.rejectClassJoinRequest(requestId, professor.id) }
                .onSuccess {
                    classRepository.invalidateJoinRequests(classIdsToRefresh, professor.id)
                    runCatching { classRepository.getClassJoinRequests(classIdsToRefresh, professor.id) }
                        .onSuccess { requests ->
                            _uiState.update {
                                it.copy(
                                    classJoinRequests = mergeProfessorJoinRequestScope(
                                        current = it.classJoinRequests,
                                        refreshed = requests,
                                        refreshedClassIds = classIdsToRefresh,
                                    ),
                                    isUpdatingJoinRequest = false,
                                    message = "Join request rejected.",
                                )
                            }
                        }
                        .onFailure {
                            _uiState.update { state ->
                                state.copy(
                                    classJoinRequests = state.classJoinRequests.filterNot { request -> request.id == requestId },
                                    isUpdatingJoinRequest = false,
                                    message = "Join request rejected.",
                                )
                            }
                        }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isUpdatingJoinRequest = false, message = ClassRepository.readableError(error))
                    }
                }
        }
    }

    fun createClassAssignment(
        classId: String,
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
        fileUri: Uri? = null,
    ) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingAssignment = true, message = null) }
            runCatching {
                val fileUrl = if (fileUri != null) {
                    val ctx = appContext ?: error("App not initialized")
                    classRepository.uploadAssignmentFile(ctx, classId, fileUri)
                } else null
                classRepository.createClassAssignment(
                    classId = classId,
                    professorId = user.id,
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
                    fileUrl = fileUrl,
                )
            }.onSuccess { assignment ->
                classRepository.invalidateAssignmentCaches(classId)
                val status = runCatching {
                    classRepository.getLatestAssignmentStatus(classId, CachePolicy.FORCE_REFRESH)
                }.getOrNull()
                _uiState.update { state ->
                    state.copy(
                        classAssignments = state.classAssignments.filterNot { it.id == assignment.id } + assignment,
                        assignmentStatuses = if (status != null) {
                            state.assignmentStatuses + (classId to status)
                        } else {
                            state.assignmentStatuses
                        },
                        isCreatingAssignment = false,
                        message = "Assignment uploaded.",
                    )
                }
                offlineCacheStore?.saveAssignmentsSnapshot(
                    user = user,
                    classId = classId,
                    assignments = _uiState.value.classAssignments.filter { it.classId == classId },
                )
                offlineCacheStore?.saveClassSnapshot(
                    user = user,
                    studentClasses = _uiState.value.studentClasses,
                    professorClasses = _uiState.value.professorClasses,
                    assignmentStatuses = _uiState.value.assignmentStatuses,
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isCreatingAssignment = false, message = ClassRepository.readableError(error))
                }
            }
        }
    }

    fun updateClassAssignment(
        assignmentId: String,
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
        fileUri: Uri? = null,
    ) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        val currentAssignment = _uiState.value.classAssignments.firstOrNull { it.id == assignmentId }
        val classId = currentAssignment?.classId
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAssignment = true, message = null) }
            runCatching {
                val fileUrl = if (fileUri != null && classId != null) {
                    val ctx = appContext ?: error("App not initialized")
                    classRepository.uploadAssignmentFile(ctx, classId, fileUri)
                } else null
                classRepository.updateClassAssignment(
                    assignmentId = assignmentId,
                    professorId = user.id,
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
                    fileUrl = fileUrl,
                )
            }.onSuccess { updatedAssignment ->
                val status = classId?.let {
                    classRepository.invalidateAssignmentCaches(it)
                    runCatching { classRepository.getLatestAssignmentStatus(it, CachePolicy.FORCE_REFRESH) }.getOrNull()
                }
                _uiState.update { state ->
                    state.copy(
                        classAssignments = state.classAssignments.map { assignment ->
                            if (assignment.id == updatedAssignment.id) updatedAssignment else assignment
                        },
                        assignmentStatuses = if (classId != null && status != null) {
                            state.assignmentStatuses + (classId to status)
                        } else {
                            state.assignmentStatuses
                        },
                        isUpdatingAssignment = false,
                        message = "Upload updated.",
                    )
                }
                if (classId != null) {
                    offlineCacheStore?.saveAssignmentsSnapshot(
                        user = user,
                        classId = classId,
                        assignments = _uiState.value.classAssignments.filter { it.classId == classId },
                    )
                    offlineCacheStore?.saveClassSnapshot(
                        user = user,
                        studentClasses = _uiState.value.studentClasses,
                        professorClasses = _uiState.value.professorClasses,
                        assignmentStatuses = _uiState.value.assignmentStatuses,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isUpdatingAssignment = false, message = ClassRepository.readableError(error))
                }
            }
        }
    }

    fun submitAssignment(assignmentId: String, responseText: String, submissionFileUri: Uri?) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        val currentAssignment = _uiState.value.classAssignments.firstOrNull { it.id == assignmentId }
            ?: run {
                _uiState.update {
                    it.copy(message = "Assignment details are unavailable. Reopen the task and try again.")
                }
                return
            }
        val classId = currentAssignment.classId

        if (currentAssignment.assignmentType.equals("material", ignoreCase = true)) {
            _uiState.update { it.copy(message = "Materials are read-only. Stay on the material for 10 seconds to mark it viewed.") }
            return
        }

        if (isAssignmentLocked(currentAssignment)) {
            _uiState.update {
                it.copy(message = "Submission is locked until its scheduled start date and time.")
            }
            return
        }

        if (isAssignmentExpired(currentAssignment)) {
            _uiState.update { it.copy(message = "The submission window has ended.") }
            return
        }

        if (currentAssignment.requiresFile && submissionFileUri == null && currentAssignment.submissionFileUrl.isNullOrBlank()) {
            _uiState.update { it.copy(message = "Submission file is required.") }
            return
        }

        if (
            submissionFileUri == null &&
            currentAssignment.hasSubmitted &&
            currentAssignment.submissionText?.trim() == responseText.trim()
        ) {
            _uiState.update { it.copy(message = "No changes to submit.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingAssignment = true, message = null) }
            runCatching {
                val submissionFileUrl = if (submissionFileUri != null) {
                    val ctx = appContext ?: error("App not initialized")
                    classRepository.uploadSubmissionFile(
                        context = ctx,
                        assignmentId = assignmentId,
                        studentId = user.id,
                        requiredFormat = currentAssignment.submissionFormat,
                        fileUri = submissionFileUri,
                    )
                } else {
                    currentAssignment.submissionFileUrl
                }
                classRepository.submitAssignment(
                    assignmentId = assignmentId,
                    studentId = user.id,
                    responseText = responseText,
                    submissionFileUrl = submissionFileUrl,
                )
            }.onSuccess { submission ->
                classRepository.invalidateAssignmentCaches(classId)
                val status = runCatching {
                    classRepository.getLatestAssignmentStatus(classId, CachePolicy.FORCE_REFRESH)
                }.getOrNull()
                _uiState.update { state ->
                    val updatedAssignments = state.classAssignments.map { assignment ->
                        if (assignment.id == assignmentId) {
                            val submittedCount = if (assignment.hasSubmitted) {
                                assignment.submittedCount
                            } else {
                                assignment.submittedCount + 1
                            }
                            val newEditAttempts = if (assignment.hasSubmitted) {
                                (assignment.editAttempts ?: 0) + 1
                            } else {
                                assignment.editAttempts ?: 0
                            }
                            assignment.copy(
                                hasSubmitted = true,
                                submissionText = submission.responseText,
                                submittedAt = submission.submittedAt,
                                submittedCount = submittedCount,
                                editAttempts = newEditAttempts,
                                submissionFileUrl = submission.submissionFileUrl,
                            )
                        } else {
                            assignment
                        }
                    }
                    val classAssignments = updatedAssignments.filter { it.classId == classId }
                    val total = classAssignments.size
                    val submitted = classAssignments.count { it.hasSubmitted }
                    val progress = if (total == 0) 0 else ((submitted * 100) / total).coerceIn(0, 100)
                    state.copy(
                        classAssignments = updatedAssignments,
                        pendingAssignments = removeSubmittedPendingAssignment(
                            pendingAssignments = state.pendingAssignments,
                            assignmentId = assignmentId,
                        ),
                        studentClasses = state.studentClasses.map { classItem ->
                            if (classItem.id == classId) {
                                classItem.copy(
                                    progressPercentage = progress,
                                    completedAssignments = submitted,
                                    totalAssignments = total,
                                )
                            } else {
                                classItem
                            }
                        },
                        assignmentStatuses = if (status != null) {
                            state.assignmentStatuses + (classId to status)
                        } else {
                            state.assignmentStatuses
                        },
                        isSubmittingAssignment = false,
                        message = "Submission saved.",
                    )
                }
                offlineCacheStore?.saveAssignmentsSnapshot(
                    user = user,
                    classId = classId,
                    assignments = _uiState.value.classAssignments.filter { it.classId == classId },
                )
                offlineCacheStore?.saveClassSnapshot(
                    user = user,
                    studentClasses = _uiState.value.studentClasses,
                    professorClasses = _uiState.value.professorClasses,
                    assignmentStatuses = _uiState.value.assignmentStatuses,
                )
                offlineCacheStore?.savePendingAssignmentsSnapshot(
                    user = user,
                    pendingAssignments = _uiState.value.pendingAssignments,
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isSubmittingAssignment = false, message = ClassRepository.readableError(error))
                }
            }
        }
    }

    fun recordMaterialView(assignmentId: String) {
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to mark material as viewed.")) return
        val user = _uiState.value.currentUser ?: return
        val currentAssignment = _uiState.value.classAssignments.firstOrNull { it.id == assignmentId }
        if (currentAssignment?.assignmentType?.equals("material", ignoreCase = true) != true) return
        if (currentAssignment.hasSubmitted) return
        val classId = currentAssignment.classId
        val requestKey = "${user.id}:$assignmentId"
        if (!activeMaterialViewRequests.add(requestKey)) return

        viewModelScope.launch {
            try {
                runCatching {
                    classRepository.recordMaterialView(
                        assignmentId = assignmentId,
                        studentId = user.id,
                    )
                }.onSuccess { submission ->
                    classRepository.invalidateAssignmentCaches(classId)
                    _uiState.update { state ->
                        val updatedAssignments = state.classAssignments.map { assignment ->
                            if (assignment.id == assignmentId) {
                                val submittedCount = if (assignment.hasSubmitted) {
                                    assignment.submittedCount
                                } else {
                                    assignment.submittedCount + 1
                                }
                                assignment.copy(
                                    hasSubmitted = true,
                                    submissionText = submission.responseText,
                                    submittedAt = submission.submittedAt,
                                    submittedCount = submittedCount,
                                    editAttempts = submission.editAttempts,
                                    submissionFileUrl = submission.submissionFileUrl,
                                )
                            } else {
                                assignment
                            }
                        }
                        val classAssignments = updatedAssignments.filter { it.classId == classId }
                        val total = classAssignments.size
                        val completed = classAssignments.count { it.hasSubmitted }
                        val progress = if (total == 0) 0 else ((completed * 100) / total).coerceIn(0, 100)
                        state.copy(
                            classAssignments = updatedAssignments,
                            studentClasses = state.studentClasses.map { classItem ->
                                if (classItem.id == classId) {
                                    classItem.copy(
                                        progressPercentage = progress,
                                        completedAssignments = completed,
                                        totalAssignments = total,
                                    )
                                } else {
                                    classItem
                                }
                            },
                            message = "Material marked as viewed.",
                        )
                    }
                    offlineCacheStore?.saveAssignmentsSnapshot(
                        user = user,
                        classId = classId,
                        assignments = _uiState.value.classAssignments.filter { it.classId == classId },
                    )
                    offlineCacheStore?.saveClassSnapshot(
                        user = user,
                        studentClasses = _uiState.value.studentClasses,
                        professorClasses = _uiState.value.professorClasses,
                        assignmentStatuses = _uiState.value.assignmentStatuses,
                    )
                }.onFailure { error ->
                    _uiState.update { it.copy(message = ClassRepository.readableError(error)) }
                }
            } finally {
                activeMaterialViewRequests.remove(requestKey)
            }
        }
    }

    fun deleteClassAssignment(assignmentId: String) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        val classId = _uiState.value.classAssignments.firstOrNull { it.id == assignmentId }?.classId
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAssignment = true, message = null) }
            runCatching {
                classRepository.deleteClassAssignment(
                    assignmentId = assignmentId,
                    professorId = user.id,
                )
            }.onSuccess {
                val status = classId?.let {
                    classRepository.invalidateAssignmentCaches(it)
                    runCatching { classRepository.getLatestAssignmentStatus(it, CachePolicy.FORCE_REFRESH) }.getOrNull()
                }
                _uiState.update { state ->
                    state.copy(
                        classAssignments = state.classAssignments.filterNot { it.id == assignmentId },
                        assignmentStatuses = if (classId != null) {
                            if (status != null) {
                                state.assignmentStatuses + (classId to status)
                            } else {
                                state.assignmentStatuses - classId
                            }
                        } else {
                            state.assignmentStatuses
                        },
                        isDeletingAssignment = false,
                        message = "Assignment deleted.",
                    )
                }
                if (classId != null) {
                    offlineCacheStore?.saveAssignmentsSnapshot(
                        user = user,
                        classId = classId,
                        assignments = _uiState.value.classAssignments.filter { it.classId == classId },
                    )
                    offlineCacheStore?.saveClassSnapshot(
                        user = user,
                        studentClasses = _uiState.value.studentClasses,
                        professorClasses = _uiState.value.professorClasses,
                        assignmentStatuses = _uiState.value.assignmentStatuses,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isDeletingAssignment = false, message = ClassRepository.readableError(error))
                }
            }
        }
    }

    fun joinClass(classCode: String) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isJoiningClass = true, message = null) }
            runCatching {
                classRepository.joinClassByCode(user.id, classCode)
            }
                .onSuccess {
                    classRepository.invalidateClassList(userId = user.id, role = user.role)
                    _uiState.update {
                        it.copy(
                            isJoiningClass = false,
                            message = "Join request sent. Wait for professor approval.",
                        )
                    }
                }
                .onFailure { error ->
                    val message = ClassRepository.readableError(error)
                    _uiState.update {
                        it.copy(isJoiningClass = false, message = message)
                    }
                }
        }
    }

    fun createClass(
        context: Context,
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
    ) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingClass = true, message = null) }
            runCatching {
                classRepository.createProfessorClass(
                    context = context,
                    professorId = user.id,
                    className = className,
                    subjectCode = subjectCode,
                    joinCode = joinCode,
                    yearLevel = yearLevel,
                    department = department,
                    section = section,
                    track = track,
                    coverImageUri = coverImageUri,
                    themeColor = themeColor,
                    scheduleDays = scheduleDays,
                    scheduleStartTime = scheduleStartTime,
                    scheduleEndTime = scheduleEndTime,
                )
            }
                .onSuccess { createdClass ->
                    _uiState.update { state ->
                        state.copy(
                            professorClasses = state.professorClasses.filterNot { classItem ->
                                classItem.id == createdClass.id
                            } + createdClass,
                            isCreatingClass = false,
                            message = "Class created.",
                        )
                    }
                    offlineCacheStore?.saveClassSnapshot(
                        user = user,
                        studentClasses = _uiState.value.studentClasses,
                        professorClasses = _uiState.value.professorClasses,
                        assignmentStatuses = _uiState.value.assignmentStatuses,
                    )
                    cacheUserClassImages(
                        user = user,
                        studentClasses = _uiState.value.studentClasses,
                        professorClasses = _uiState.value.professorClasses,
                    )
                }
                .onFailure { error ->
                    val message = ClassRepository.readableError(error)
                    _uiState.update {
                        it.copy(isCreatingClass = false, message = message)
                    }
                }
        }
    }

    fun deleteProfessorSubject(
        yearLevel: String,
        department: String,
        section: String,
        track: String?,
        subjectCode: String,
    ) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        if (!user.role.equals("professor", ignoreCase = true)) return

        val cleanYear = normalizeYearLevel(yearLevel)
        val cleanDepartment = normalizeAcademicText(department)
        val cleanSection = normalizeAcademicText(section)
        val cleanTrack = normalizeAcademicText(track.orEmpty())
        val cleanSubject = subjectCode.trim().uppercase()
        val affectedClassIds = _uiState.value.professorClasses
            .filter {
                normalizeYearLevel(it.yearLevel.orEmpty()) == cleanYear &&
                    normalizeAcademicText(it.department.orEmpty()) == cleanDepartment &&
                    normalizeAcademicText(it.section.orEmpty()) == cleanSection &&
                    normalizeAcademicText(it.track.orEmpty()) == cleanTrack &&
                    it.displaySubjectCode.equals(cleanSubject, ignoreCase = true)
            }
            .map { it.id }
            .toSet()

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingSubject = true, message = null) }
            runCatching {
                classRepository.deleteProfessorSubject(
                    professorId = user.id,
                    yearLevel = cleanYear,
                    department = department,
                    section = section,
                    track = track,
                    subjectCode = cleanSubject,
                )
            }.onSuccess {
                classRepository.invalidateClassList(userId = user.id, role = user.role)
                affectedClassIds.forEach { classRepository.invalidateAssignmentCaches(it) }
                classRepository.invalidateJoinRequests(affectedClassIds, user.id)
                _uiState.update { state ->
                    state.copy(
                        professorClasses = state.professorClasses.filterNot { it.id in affectedClassIds },
                        classAssignments = state.classAssignments.filterNot { it.classId in affectedClassIds },
                        assignmentStatuses = state.assignmentStatuses.filterKeys { it !in affectedClassIds },
                        classJoinRequests = state.classJoinRequests.filterNot { it.classId in affectedClassIds },
                        isDeletingSubject = false,
                        message = "Subject deleted.",
                    )
                }
                offlineCacheStore?.saveClassSnapshot(
                    user = user,
                    studentClasses = _uiState.value.studentClasses,
                    professorClasses = _uiState.value.professorClasses,
                    assignmentStatuses = _uiState.value.assignmentStatuses,
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isDeletingSubject = false, message = ClassRepository.readableError(error))
                }
            }
        }
    }

    fun deleteProfessorClass(classId: String) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        if (!user.role.equals("professor", ignoreCase = true)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingSubject = true, message = null) }
            runCatching {
                classRepository.deleteProfessorClass(
                    professorId = user.id,
                    classId = classId,
                )
            }.onSuccess {
                classRepository.invalidateClassList(userId = user.id, role = user.role)
                classRepository.invalidateAssignmentCaches(classId)
                classRepository.invalidateJoinRequests(listOf(classId), user.id)
                _uiState.update { state ->
                    state.copy(
                        professorClasses = state.professorClasses.filterNot { it.id == classId },
                        classAssignments = state.classAssignments.filterNot { it.classId == classId },
                        assignmentStatuses = state.assignmentStatuses.filterKeys { it != classId },
                        classJoinRequests = state.classJoinRequests.filterNot { it.classId == classId },
                        isDeletingSubject = false,
                        message = "Class deleted.",
                    )
                }
                offlineCacheStore?.saveClassSnapshot(
                    user = user,
                    studentClasses = _uiState.value.studentClasses,
                    professorClasses = _uiState.value.professorClasses,
                    assignmentStatuses = _uiState.value.assignmentStatuses,
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isDeletingSubject = false, message = ClassRepository.readableError(error))
                }
            }
        }
    }

    fun loadUsers() {
        if (isOfflineWriteBlocked(readOnlyMessage = "Internet required to load people.")) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUsers = true, message = null) }
            runCatching { repository.getAllUsers() }
                .onSuccess { users ->
                    _uiState.update { it.copy(users = users, isLoadingUsers = false) }
                    cacheImageUrls(users.map { it.profilePictureUrl })
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoadingUsers = false, message = AppUserRepository.readableError(error))
                    }
                }
        }
    }

    fun loadTaskReminders(context: Context, user: AppUser? = _uiState.value.currentUser) {
        val targetUser = user ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTaskReminders = true, message = null) }
            runCatching { taskRepository.getTaskReminders(context, targetUser.id) }
                .onSuccess { reminders ->
                    _uiState.update {
                        it.copy(
                            taskReminders = reminders,
                            isLoadingTaskReminders = false,
                        )
                    }
                    offlineCacheStore?.saveTaskRemindersSnapshot(targetUser, reminders)
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(
                            isLoadingTaskReminders = false,
                            message = TaskRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun loadAnnouncements(
        user: AppUser? = _uiState.value.currentUser,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ) {
        val targetUser = user ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAnnouncements = true, message = null) }
            runCatching { 
                if (targetUser.role.equals("student", ignoreCase = true)) {
                    announcementRepository.getStudentAnnouncements(targetUser.id, cachePolicy)
                } else {
                    announcementRepository.getProfessorAnnouncements(targetUser.id, cachePolicy)
                }
            }
                .onSuccess { announcements ->
                    _uiState.update {
                        it.copy(
                            classAnnouncements = announcements,
                            isLoadingAnnouncements = false,
                        )
                    }
                    offlineCacheStore?.saveAnnouncementsSnapshot(targetUser, announcements)
                    cacheImageUrls(
                        announcements.mapNotNull { it.professorProfilePicUrl } +
                            announcements.mapNotNull { it.imageUrl }
                    )
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(
                            isLoadingAnnouncements = false,
                            message = AnnouncementRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun loadPendingAssignments(
        user: AppUser? = _uiState.value.currentUser,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ) {
        val targetUser = user ?: return
        if (!targetUser.role.equals("student", ignoreCase = true)) return
        if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) {
            val cached = offlineCacheStore?.getCacheForUser(targetUser.id)
            _uiState.update {
                it.copy(
                    pendingAssignments = cached?.pendingAssignments ?: it.pendingAssignments,
                    isLoadingPendingAssignments = false,
                    isOfflineMode = true,
                    message = "Offline mode: showing last saved data.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPendingAssignments = true, message = null) }
            runCatching { classRepository.getStudentPendingAssignments(targetUser.id, cachePolicy) }
                .onSuccess { pending ->
                    _uiState.update {
                        it.copy(
                            pendingAssignments = pending,
                            isLoadingPendingAssignments = false,
                            lastOnlineRefreshFailed = false,
                        )
                    }
                    offlineCacheStore?.savePendingAssignmentsSnapshot(targetUser, pending)
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(
                            isLoadingPendingAssignments = false,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun loadStudentGrades(
        user: AppUser? = _uiState.value.currentUser,
        cachePolicy: CachePolicy = CachePolicy.USE_FRESH,
    ) {
        val targetUser = user ?: return
        if (!targetUser.role.equals("student", ignoreCase = true)) return
        if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) {
            val cached = offlineCacheStore?.getCacheForUser(targetUser.id)
            _uiState.update {
                it.copy(
                    studentGrades = cached?.studentGrades ?: it.studentGrades,
                    isLoadingStudentGrades = false,
                    isOfflineMode = true,
                    message = "Offline mode: showing last saved data.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStudentGrades = true, message = null) }
            runCatching { classRepository.getStudentGrades(targetUser.id, cachePolicy) }
                .onSuccess { grades ->
                    _uiState.update {
                        it.copy(
                            studentGrades = grades,
                            isLoadingStudentGrades = false,
                            lastOnlineRefreshFailed = false,
                        )
                    }
                    offlineCacheStore?.saveStudentGradesSnapshot(targetUser, grades)
                }
                .onFailure { error ->
                    if (handleConnectivityFailure(error)) return@onFailure
                    _uiState.update {
                        it.copy(
                            isLoadingStudentGrades = false,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun toggleAnnouncementReadStatus(announcementId: String, isRead: Boolean) {
        val user = _uiState.value.currentUser ?: return
        if (!user.role.equals("student", ignoreCase = true)) return
        if (isOfflineWriteBlocked()) return
        // Optimistically update UI
        _uiState.update { state ->
            val updatedAnnouncements = state.classAnnouncements.map { 
                if (it.id == announcementId) it.copy(isRead = isRead) else it
            }
            state.copy(classAnnouncements = updatedAnnouncements)
        }
        viewModelScope.launch {
            runCatching { announcementRepository.toggleAnnouncementReadStatus(user.id, announcementId, isRead) }
                .onFailure { error ->
                    // Revert on failure
                    _uiState.update { state ->
                        val revertedAnnouncements = state.classAnnouncements.map { 
                            if (it.id == announcementId) it.copy(isRead = !isRead) else it
                        }
                        state.copy(
                            classAnnouncements = revertedAnnouncements,
                            message = AnnouncementRepository.readableError(error)
                        )
                    }
                }
        }
    }

    fun createProfessorAnnouncement(context: Context, title: String, subtitle: String, content: String, targetYears: List<String>, imageUri: Uri?) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        if (!user.role.equals("professor", ignoreCase = true)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingAnnouncement = true, message = null) }
            runCatching {
                announcementRepository.createProfessorAnnouncement(
                    context = context,
                    professorId = user.id,
                    title = title,
                    subtitle = subtitle,
                    content = content,
                    targetYears = targetYears,
                    imageUri = imageUri,
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isCreatingAnnouncement = false,
                        message = "Announcement posted.",
                    )
                }
                loadAnnouncements(user)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isCreatingAnnouncement = false,
                        message = AnnouncementRepository.readableError(error),
                    )
                }
            }
        }
    }

    fun updateProfessorAnnouncement(context: Context, announcementId: String, title: String, subtitle: String, content: String, targetYears: List<String>, imageUri: Uri?, existingImageUrl: String?) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        if (!user.role.equals("professor", ignoreCase = true)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingAnnouncement = true, message = null) }
            runCatching {
                announcementRepository.updateProfessorAnnouncement(
                    context = context,
                    professorId = user.id,
                    announcementId = announcementId,
                    title = title,
                    subtitle = subtitle,
                    content = content,
                    targetYears = targetYears,
                    imageUri = imageUri,
                    existingImageUrl = existingImageUrl,
                )
            }.onSuccess { updated ->
                _uiState.update { state ->
                    state.copy(
                        classAnnouncements = state.classAnnouncements.map {
                            if (it.id == announcementId) updated else it
                        },
                        isUpdatingAnnouncement = false,
                        message = "Announcement updated.",
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isUpdatingAnnouncement = false,
                        message = AnnouncementRepository.readableError(error),
                    )
                }
            }
        }
    }

    fun deleteProfessorAnnouncement(announcementId: String) {
        if (isOfflineWriteBlocked()) return
        val user = _uiState.value.currentUser ?: return
        if (!user.role.equals("professor", ignoreCase = true)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAnnouncement = true, message = null) }
            runCatching {
                announcementRepository.deleteProfessorAnnouncement(
                    professorId = user.id,
                    announcementId = announcementId,
                )
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        classAnnouncements = state.classAnnouncements.filterNot { it.id == announcementId },
                        isDeletingAnnouncement = false,
                        message = "Announcement deleted.",
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDeletingAnnouncement = false,
                        message = AnnouncementRepository.readableError(error),
                    )
                }
            }
        }
    }

    fun createTaskReminder(
        context: Context,
        kind: String,
        title: String,
        details: String,
        reminderDate: String,
        reminderTime: String?,
        themeColor: String = "#0034DE",
    ) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingTaskReminder = true, message = null) }
            runCatching {
                taskRepository.createTaskReminder(
                    context = context,
                    userId = user.id,
                    kind = kind,
                    title = title,
                    details = details,
                    reminderDate = reminderDate,
                    reminderTime = reminderTime,
                    themeColor = themeColor,
                )
            }.onSuccess { createdReminder ->
                _uiState.update { state ->
                    state.copy(
                        taskReminders = listOf(createdReminder) + state.taskReminders.filterNot {
                            it.id == createdReminder.id
                        },
                        isSavingTaskReminder = false,
                        message = "Reminder saved.",
                    )
                }
                _uiState.value.currentUser?.let { user ->
                    offlineCacheStore?.saveTaskRemindersSnapshot(user, _uiState.value.taskReminders)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSavingTaskReminder = false,
                        message = TaskRepository.readableError(error),
                    )
                }
            }
        }
    }

    fun deleteTaskReminder(context: Context, reminderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingTaskReminder = true, message = null) }
            runCatching { taskRepository.deleteTaskReminder(context, reminderId) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            taskReminders = state.taskReminders.filterNot { it.id == reminderId },
                            isDeletingTaskReminder = false,
                            message = "Reminder deleted.",
                        )
                    }
                    _uiState.value.currentUser?.let { user ->
                        offlineCacheStore?.saveTaskRemindersSnapshot(user, _uiState.value.taskReminders)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeletingTaskReminder = false,
                            message = TaskRepository.readableError(error),
                        )
                    }
                }
        }
    }

    fun toggleTaskCompletion(context: Context, reminderId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            runCatching { taskRepository.toggleTaskCompletion(context, reminderId, isCompleted) }
                .onSuccess {
                    _uiState.update { state ->
                        val updatedReminders = state.taskReminders.map {
                            if (it.id == reminderId) it.copy(isCompleted = isCompleted) else it
                        }
                        state.copy(taskReminders = updatedReminders)
                    }
                    _uiState.value.currentUser?.let { user ->
                        offlineCacheStore?.saveTaskRemindersSnapshot(user, _uiState.value.taskReminders)
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(message = "Failed to update completion status.") }
                }
        }
    }

    fun updateProfilePicture(context: Context, imageUri: Uri) {
        if (isOfflineWriteBlocked()) return
        val userId = _uiState.value.currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingProfilePicture = true, message = null) }
            runCatching { repository.updateProfilePicture(context, userId, imageUri) }
                .onSuccess { publicUrl ->
                    var cachedUser: AppUser? = null
                    _uiState.update { state ->
                        val updatedUser = state.currentUser?.copy(profilePictureUrl = publicUrl)
                        cachedUser = updatedUser
                        state.copy(
                            currentUser = updatedUser,
                            users = state.users.map { if (it.id == userId) it.copy(profilePictureUrl = publicUrl) else it },
                            isUploadingProfilePicture = false,
                        )
                    }
                    cachedUser?.let { offlineCacheStore?.saveUser(it) }
                    cacheImageUrls(listOf(publicUrl))
                }
                .onFailure { error ->
                    AppUserRepository.readableError(error)
                    _uiState.update {
                        it.copy(
                            isUploadingProfilePicture = false,
                            message = "Failed to update profile photo.",
                        )
                    }
                }
        }
    }

    fun updateProfileDetails(
        fullName: String, 
        course: String?,
        year: String?,
        section: String?,
        track: String?,
        bio: String?,
        phoneNumber: String?
    ) {
        if (isOfflineWriteBlocked()) return
        val userId = _uiState.value.currentUser?.id ?: return
        val cleanName = fullName.trim()
        if (cleanName.any(Char::isDigit)) {
            _uiState.update { it.copy(message = "Name cannot contain numbers.", isUpdatingProfileDetails = false) }
            return
        }
        val cleanCourse = course?.trim()?.takeIf { it.isNotBlank() }
        val cleanYear = year?.trim()?.takeIf { it.isNotBlank() }
        val cleanSection = section?.trim()?.takeIf { it.isNotBlank() }
        val cleanTrack = track?.trim()?.takeIf { it.isNotBlank() }
        val cleanBio = bio?.trim()?.takeIf { it.isNotBlank() }
        val cleanPhoneNumber = phoneNumber?.trim()?.takeIf { it.isNotBlank() }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdatingProfileDetails = true,
                    message = null,
                )
            }
            runCatching {
                repository.updateProfileDetails(
                    userId = userId,
                    fullName = cleanName,
                    course = cleanCourse,
                    year = cleanYear,
                    section = cleanSection,
                    track = cleanTrack,
                    bio = cleanBio,
                    phoneNumber = cleanPhoneNumber,
                )
            }.onSuccess { cleanName ->
                var cachedUser: AppUser? = null
                _uiState.update { state ->
                    val updatedUser = state.currentUser?.copy(
                        fullName = cleanName, 
                        course = cleanCourse,
                        year = cleanYear,
                        section = cleanSection,
                        track = cleanTrack,
                        bio = cleanBio,
                        phoneNumber = cleanPhoneNumber
                    )
                    cachedUser = updatedUser
                    state.copy(
                        currentUser = updatedUser,
                        users = state.users.map {
                            if (it.id == userId) updatedUser!! else it
                        },
                        isUpdatingProfileDetails = false,
                        message = "Profile updated.",
                    )
                }
                cachedUser?.let { offlineCacheStore?.saveUser(it) }
            }.onFailure { error ->
                AppUserRepository.readableError(error)
                _uiState.update {
                    it.copy(
                        isUpdatingProfileDetails = false,
                        message = "Failed to update profile.",
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun launchPullRefresh(
        surface: RefreshSurface,
        onStart: (MainUiState) -> MainUiState = { it },
        onFinish: (MainUiState) -> MainUiState = { it },
        block: suspend () -> Unit,
    ) {
        if (surface in _uiState.value.refreshingSurfaces) return

        _uiState.update { it.copy(refreshingSurfaces = it.refreshingSurfaces + surface) }
        viewModelScope.launch {
            _uiState.update { onStart(it) }
            try {
                if (isRefreshOfflineBlocked()) return@launch
                block()
            } finally {
                _uiState.update { state ->
                    val finished = onFinish(state)
                    finished.copy(refreshingSurfaces = finished.refreshingSurfaces - surface)
                }
            }
        }
    }

    private fun isRefreshOfflineBlocked(): Boolean {
        if (_uiState.value.connectivityStatus == ConnectivityStatus.Online) return false

        _uiState.update {
            it.copy(
                isOfflineMode = true,
                lastOnlineRefreshFailed = true,
                message = "Internet required.",
            )
        }
        return true
    }

    private suspend fun refreshClassesNow(
        targetUser: AppUser,
        cachePolicy: CachePolicy,
    ) {
        _uiState.update { it.copy(message = null) }
        if (targetUser.role.equals("student", ignoreCase = true)) {
            runCatching { classRepository.getStudentClasses(targetUser.id, cachePolicy) }
                .onSuccess { classes ->
                    val statuses = runCatching {
                        classRepository.getLatestAssignmentStatuses(classes.map { it.id }, cachePolicy)
                    }.getOrDefault(emptyMap())
                    _uiState.update {
                        it.copy(
                            studentClasses = classes,
                            assignmentStatuses = statuses,
                            isLoadingClasses = false,
                            isOfflineMode = false,
                            lastOnlineRefreshFailed = false,
                        )
                    }
                    offlineCacheStore?.saveClassSnapshot(
                        user = targetUser,
                        studentClasses = classes,
                        professorClasses = emptyList(),
                        assignmentStatuses = statuses,
                    )
                    cacheUserClassImages(
                        user = targetUser,
                        studentClasses = classes,
                        professorClasses = emptyList(),
                    )
                }
                .onFailure { error ->
                    if (!handleConnectivityFailure(error)) {
                        _uiState.update {
                            it.copy(
                                isLoadingClasses = false,
                                lastOnlineRefreshFailed = true,
                                message = "Failed to load classes. Please try again.",
                            )
                        }
                    }
                }
        } else if (targetUser.role.equals("professor", ignoreCase = true)) {
            runCatching { classRepository.getProfessorClasses(targetUser.id, cachePolicy) }
                .onSuccess { classes ->
                    val statuses = runCatching {
                        classRepository.getLatestAssignmentStatuses(classes.map { it.id }, cachePolicy)
                    }.getOrDefault(emptyMap())
                    val joinRequests = if (classes.isEmpty()) {
                        emptyList()
                    } else {
                        runCatching {
                            classRepository.getClassJoinRequests(
                                classIds = classes.map { it.id },
                                professorId = targetUser.id,
                                cachePolicy = cachePolicy,
                            )
                        }.getOrDefault(emptyList())
                    }
                    _uiState.update {
                        it.copy(
                            professorClasses = classes,
                            assignmentStatuses = statuses,
                            classJoinRequests = joinRequests,
                            isLoadingClasses = false,
                            isOfflineMode = false,
                            lastOnlineRefreshFailed = false,
                        )
                    }
                    offlineCacheStore?.saveClassSnapshot(
                        user = targetUser,
                        studentClasses = emptyList(),
                        professorClasses = classes,
                        assignmentStatuses = statuses,
                    )
                    cacheImageUrls(joinRequests.map { it.photoUrl })
                    cacheUserClassImages(
                        user = targetUser,
                        studentClasses = emptyList(),
                        professorClasses = classes,
                    )
                }
                .onFailure { error ->
                    if (!handleConnectivityFailure(error)) {
                        _uiState.update {
                            it.copy(
                                isLoadingClasses = false,
                                lastOnlineRefreshFailed = true,
                                message = "Failed to load classes. Please try again.",
                            )
                        }
                    }
                }
        } else {
            _uiState.update { it.copy(isLoadingClasses = false) }
        }
    }

    private suspend fun refreshClassAssignmentsNow(
        classId: String,
        cachePolicy: CachePolicy,
    ) {
        val user = _uiState.value.currentUser
        val studentId = user?.takeIf { it.role.equals("student", ignoreCase = true) }?.id
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getClassAssignments(classId, studentId, cachePolicy) }
            .onSuccess { assignments ->
                _uiState.update {
                    it.copy(
                        classAssignments = assignments,
                        isLoadingAssignments = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                if (user != null) {
                    offlineCacheStore?.saveAssignmentsSnapshot(user, classId, assignments)
                }
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingAssignments = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshProfessorGradeMonitorNow(
        targetUser: AppUser,
        cachePolicy: CachePolicy,
    ) {
        if (!targetUser.role.equals("professor", ignoreCase = true)) return
        _uiState.update { it.copy(isLoadingProfessorGradeMonitor = true, message = null) }
        runCatching { buildProfessorGradeMonitor(targetUser, cachePolicy) }
            .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        professorGradeMonitorEnrollments = result.first,
                        professorGradeMonitorSubmissions = result.second,
                        isLoadingProfessorGradeMonitor = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(
                    result.first.mapNotNull { it.photoUrl } +
                        result.second.mapNotNull { it.photoUrl },
                )
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingProfessorGradeMonitor = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun buildProfessorGradeMonitor(
        professor: AppUser,
        cachePolicy: CachePolicy,
    ): Pair<List<ProfessorGradeMonitorEnrollment>, List<ProfessorGradeMonitorSubmission>> {
        val classes = if (cachePolicy == CachePolicy.FORCE_REFRESH || _uiState.value.professorClasses.isEmpty()) {
            classRepository.getProfessorClasses(professor.id, cachePolicy)
        } else {
            _uiState.value.professorClasses
        }

        val enrollments = mutableListOf<ProfessorGradeMonitorEnrollment>()
        val gradeSubmissions = mutableListOf<ProfessorGradeMonitorSubmission>()

        classes.forEach { classItem ->
            val students = runCatching {
                classRepository.getProfessorClassStudents(classItem.id, cachePolicy)
            }.getOrDefault(emptyList())
            students.forEach { student ->
                enrollments += ProfessorGradeMonitorEnrollment(
                    classId = classItem.id,
                    className = classItem.displayClassName,
                    subjectCode = classItem.displaySubjectCode,
                    yearLevel = classItem.yearLevel.orEmpty(),
                    section = classItem.section.orEmpty(),
                    track = classItem.track.orEmpty(),
                    department = classItem.department.orEmpty(),
                    studentId = student.id,
                    studentName = student.name,
                    idNumber = student.idNumber,
                    photoUrl = student.photoUrl,
                )
            }

            val assignments = runCatching {
                classRepository.getClassAssignments(classItem.id, studentId = null, cachePolicy = cachePolicy)
            }.getOrDefault(emptyList())
                .filterNot { it.assignmentType.equals("material", ignoreCase = true) }

            assignments.forEach { assignment ->
                val submissions = runCatching {
                    classRepository.getAssignmentSubmissions(assignment.id, professor.id, cachePolicy)
                }.getOrDefault(emptyList())
                submissions.forEach { submission ->
                    gradeSubmissions += ProfessorGradeMonitorSubmission(
                        classId = classItem.id,
                        className = classItem.displayClassName,
                        subjectCode = classItem.displaySubjectCode,
                        assignmentId = assignment.id,
                        assignmentTitle = assignment.title.ifBlank { "Assignment" },
                        assignmentType = assignment.assignmentType,
                        category = submission.category.ifBlank { assignment.category },
                        submissionId = submission.id,
                        studentId = submission.studentId,
                        studentName = submission.studentName,
                        idNumber = submission.idNumber,
                        photoUrl = submission.photoUrl,
                        responseText = submission.responseText,
                        submittedAt = submission.submittedAt,
                        editAttempts = submission.editAttempts,
                        score = submission.score,
                        targetPoints = submission.targetPoints.coerceAtLeast(1),
                        rawPercent = submission.rawPercent,
                        convertedGrade = submission.convertedGrade,
                        submissionFileUrl = submission.submissionFileUrl,
                    )
                }
            }
        }

        return enrollments.distinctBy { "${it.classId}:${it.studentId}" } to
            gradeSubmissions.sortedWith(
                compareBy<ProfessorGradeMonitorSubmission> { it.studentName.lowercase() }
                    .thenBy { it.subjectCode.lowercase() }
                    .thenByDescending { it.submittedAt.orEmpty() },
            )
    }

    private suspend fun refreshAssignmentSubmissionsNow(
        assignmentId: String,
        cachePolicy: CachePolicy,
    ) {
        val professor = _uiState.value.currentUser ?: return
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getAssignmentSubmissions(assignmentId, professor.id, cachePolicy) }
            .onSuccess { submissions ->
                _uiState.update {
                    it.copy(
                        assignmentSubmissions = submissions,
                        isLoadingAssignmentSubmissions = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(submissions.mapNotNull { it.photoUrl })
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingAssignmentSubmissions = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshClassmatesNow(
        classId: String,
        cachePolicy: CachePolicy,
    ) {
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getClassmates(classId, cachePolicy) }
            .onSuccess { classmates ->
                _uiState.update {
                    it.copy(
                        classmates = classmates,
                        isLoadingClassmates = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(classmates.map { it.photoUrl })
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingClassmates = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshProfessorStudentsNow(
        classId: String,
        cachePolicy: CachePolicy,
    ) {
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getProfessorClassStudents(classId, cachePolicy) }
            .onSuccess { students ->
                _uiState.update {
                    it.copy(
                        professorStudents = students,
                        isLoadingProfessorStudents = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(students.map { it.photoUrl })
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingProfessorStudents = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshAssignmentAttendanceNow(
        assignmentId: String,
        cachePolicy: CachePolicy,
    ) {
        val professor = _uiState.value.currentUser ?: return
        _uiState.update { it.copy(attendanceError = null, message = null) }
        runCatching { classRepository.getAssignmentAttendance(assignmentId, professor.id, cachePolicy) }
            .onSuccess { students ->
                _uiState.update {
                    it.copy(
                        attendanceStudents = students,
                        attendanceError = null,
                        isLoadingAttendance = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(students.mapNotNull { it.photoUrl })
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    val readableMessage = ClassRepository.readableError(error)
                    _uiState.update {
                        it.copy(
                            attendanceError = readableMessage,
                            isLoadingAttendance = false,
                            lastOnlineRefreshFailed = true,
                            message = readableMessage,
                        )
                    }
                }
            }
    }

    private suspend fun refreshClassJoinRequestsNow(
        classIds: List<String>,
        cachePolicy: CachePolicy,
    ) {
        val professor = _uiState.value.currentUser ?: return
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getClassJoinRequests(classIds, professor.id, cachePolicy) }
            .onSuccess { requests ->
                _uiState.update {
                    it.copy(
                        classJoinRequests = mergeProfessorJoinRequestScope(
                            current = it.classJoinRequests,
                            refreshed = requests,
                            refreshedClassIds = classIds,
                        ),
                        isLoadingJoinRequests = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(requests.map { it.photoUrl })
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingJoinRequests = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshUsersNow(cachePolicy: CachePolicy) {
        _uiState.update { it.copy(message = null) }
        runCatching { repository.getAllUsers(cachePolicy) }
            .onSuccess { users ->
                _uiState.update {
                    it.copy(
                        users = users,
                        isLoadingUsers = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                cacheImageUrls(users.map { it.profilePictureUrl })
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingUsers = false,
                            lastOnlineRefreshFailed = true,
                            message = AppUserRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshTaskRemindersNow(context: Context, targetUser: AppUser) {
        _uiState.update { it.copy(message = null) }
        runCatching { taskRepository.getTaskReminders(context, targetUser.id) }
            .onSuccess { reminders ->
                _uiState.update {
                    it.copy(
                        taskReminders = reminders,
                        isLoadingTaskReminders = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                offlineCacheStore?.saveTaskRemindersSnapshot(targetUser, reminders)
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingTaskReminders = false,
                            lastOnlineRefreshFailed = true,
                            message = TaskRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshAnnouncementsNow(
        targetUser: AppUser,
        cachePolicy: CachePolicy,
    ) {
        _uiState.update { it.copy(message = null) }
        runCatching {
            if (targetUser.role.equals("student", ignoreCase = true)) {
                announcementRepository.getStudentAnnouncements(targetUser.id, cachePolicy)
            } else {
                announcementRepository.getProfessorAnnouncements(targetUser.id, cachePolicy)
            }
        }
            .onSuccess { announcements ->
                _uiState.update {
                    it.copy(
                        classAnnouncements = announcements,
                        isLoadingAnnouncements = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                offlineCacheStore?.saveAnnouncementsSnapshot(targetUser, announcements)
                cacheImageUrls(
                    announcements.mapNotNull { it.professorProfilePicUrl } +
                        announcements.mapNotNull { it.imageUrl }
                )
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingAnnouncements = false,
                            lastOnlineRefreshFailed = true,
                            message = AnnouncementRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshPendingAssignmentsNow(
        targetUser: AppUser,
        cachePolicy: CachePolicy,
    ) {
        if (!targetUser.role.equals("student", ignoreCase = true)) return
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getStudentPendingAssignments(targetUser.id, cachePolicy) }
            .onSuccess { pending ->
                val ctx = appContext
                if (ctx != null && cachePolicy == CachePolicy.FORCE_REFRESH) {
                    val newAssignments = UploadNotificationHelper.detectNewUploads(ctx, pending)
                    if (newAssignments.isNotEmpty()) {
                        UploadNotificationHelper.showUploadNotifications(ctx, newAssignments)
                    }
                }
                _uiState.update {
                    it.copy(
                        pendingAssignments = pending,
                        isLoadingPendingAssignments = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                offlineCacheStore?.savePendingAssignmentsSnapshot(targetUser, pending)
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingPendingAssignments = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private suspend fun refreshStudentGradesNow(
        targetUser: AppUser,
        cachePolicy: CachePolicy,
    ) {
        if (!targetUser.role.equals("student", ignoreCase = true)) return
        _uiState.update { it.copy(message = null) }
        runCatching { classRepository.getStudentGrades(targetUser.id, cachePolicy) }
            .onSuccess { grades ->
                _uiState.update {
                    it.copy(
                        studentGrades = grades,
                        isLoadingStudentGrades = false,
                        lastOnlineRefreshFailed = false,
                    )
                }
                offlineCacheStore?.saveStudentGradesSnapshot(targetUser, grades)
            }
            .onFailure { error ->
                if (!handleConnectivityFailure(error)) {
                    _uiState.update {
                        it.copy(
                            isLoadingStudentGrades = false,
                            lastOnlineRefreshFailed = true,
                            message = ClassRepository.readableError(error),
                        )
                    }
                }
            }
    }

    private fun cacheUserClassImages(
        user: AppUser,
        studentClasses: List<com.myapplication.panthraa.model.StudentClass>,
        professorClasses: List<com.myapplication.panthraa.model.ProfessorClass>,
    ) {
        val urls = buildList {
            add(user.profilePictureUrl)
            studentClasses.forEach { classItem ->
                add(classItem.coverImageUrl)
                add(classItem.professorPhotoUrl)
            }
            professorClasses.forEach { classItem ->
                add(classItem.coverImageUrl)
                add(classItem.professorPhotoUrl)
            }
        }
        cacheImageUrls(urls)
    }

    private fun cacheImageUrls(urls: Iterable<String?>) {
        val context = appContext ?: return
        if (_uiState.value.connectivityStatus != ConnectivityStatus.Online) return
        viewModelScope.launch {
            OfflineImageCache.cacheUrls(context, urls)
        }
    }

    private fun handleConnectivityFailure(
        throwable: Throwable,
        readOnlyMessage: String = "Refresh failed. Showing last saved data.",
    ): Boolean {
        if (!throwable.isConnectivityFailure()) return false

        _uiState.update {
            it.copy(
                connectivityStatus = ConnectivityStatus.Offline,
                isOfflineMode = true,
                lastOnlineRefreshFailed = true,
                isRefreshingOnlineData = false,
                isLoadingUsers = false,
                isLoadingClasses = false,
                isLoadingAssignments = false,
                isLoadingAssignmentSubmissions = false,
                isLoadingClassmates = false,
                isLoadingProfessorStudents = false,
                isLoadingAttendance = false,
                isRecordingAttendance = false,
                isLoadingJoinRequests = false,
                isLoadingTaskReminders = false,
                isLoadingAnnouncements = false,
                isLoadingPendingAssignments = false,
                isLoadingStudentGrades = false,
                isScoringSubmission = false,
                isCreatingAnnouncement = false,
                isUpdatingAnnouncement = false,
                isDeletingAnnouncement = false,
                message = readOnlyMessage,
            )
        }
        return true
    }

    private fun Throwable.isConnectivityFailure(): Boolean {
        val details = buildString {
            var current: Throwable? = this@isConnectivityFailure
            while (current != null) {
                append(current::class.simpleName.orEmpty())
                append(' ')
                append(current.message.orEmpty())
                append(' ')
                current = current.cause
            }
        }.lowercase()

        return listOf(
            "unable to resolve host",
            "no address associated with hostname",
            "failed to connect",
            "connect timed out",
            "request timed out",
            "sockettimeout",
            "unknownhost",
            "unresolvedaddress",
            "network is unreachable",
        ).any { marker -> details.contains(marker) }
    }

    private fun normalizeYearLevel(yearLevel: String): String {
        return when (yearLevel.trim().lowercase()) {
            "first", "first year", "1", "1st", "1st year" -> "first"
            "second", "second year", "2", "2nd", "2nd year" -> "second"
            "third", "third year", "3", "3rd", "3rd year" -> "third"
            "fourth", "fourth year", "4", "4th", "4th year" -> "fourth"
            else -> yearLevel.trim().lowercase()
        }
    }

    private fun normalizeAcademicText(value: String): String {
        return value.trim().lowercase()
    }

    private fun isOfflineWriteBlocked(readOnlyMessage: String = "Internet required."): Boolean {
        if (_uiState.value.connectivityStatus == ConnectivityStatus.Online && !_uiState.value.isOfflineMode) {
            return false
        }
        _uiState.update {
            it.copy(
                isLoadingUsers = false,
                isLoadingClassmates = false,
                isLoadingProfessorStudents = false,
                isJoiningClass = false,
                isCreatingClass = false,
                isDeletingSubject = false,
                isCreatingAssignment = false,
                isSubmittingAssignment = false,
                isDeletingAssignment = false,
                isScoringSubmission = false,
                isLoadingAttendance = false,
                isRecordingAttendance = false,
                isLoadingPendingAssignments = false,
                isCreatingAnnouncement = false,
                isUpdatingAnnouncement = false,
                isDeletingAnnouncement = false,
                isUploadingProfilePicture = false,
                isUpdatingProfileDetails = false,
                message = readOnlyMessage,
            )
        }
        return true
    }
}
