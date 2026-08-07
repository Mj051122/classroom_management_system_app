package com.myapplication.panthraa.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapplication.panthraa.data.NetworkMonitor
import com.myapplication.panthraa.data.OfflineReviewCacheStore
import com.myapplication.panthraa.data.SupabaseReadGuard
import com.myapplication.panthraa.data.session.Credentials
import com.myapplication.panthraa.data.session.SessionStore
import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.ConnectivityStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val sessionStore: SessionStore,
    private val offlineCacheStore: OfflineReviewCacheStore,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isRestoringSession = true))
    val uiState: StateFlow<AuthUiState> = _uiState

    private var pendingSessionCredentials: Credentials? = null
    private var sessionRestoreJob: Job? = null
    private var sessionGeneration: Long = 0L
    private var latestConnectivityStatus: ConnectivityStatus = ConnectivityStatus.Connecting
    private var connectivityVersion: Long = 0L

    init {
        restoreSessionIfPresent()
    }

    private fun restoreSessionIfPresent() {
        val generation = sessionGeneration
        sessionRestoreJob = viewModelScope.launch {
            try {
                // Read the encrypted session first. A cached profile alone must never log a user in.
                val credentials = withContext(Dispatchers.IO) { sessionStore.getCredentials() }
                if (generation != sessionGeneration) return@launch

                val status = resolveCurrentConnectivity()
                if (generation != sessionGeneration) return@launch
                _uiState.update { it.copy(connectivityStatus = status) }

                if (credentials == null) {
                    _uiState.update {
                        it.copy(
                            isRestoringSession = false,
                            isOfflineMode = status != ConnectivityStatus.Online,
                            shouldShowAuthOnLaunch = status != ConnectivityStatus.Online,
                            errorMessage = if (status != ConnectivityStatus.Online) {
                                "Internet required for first login."
                            } else {
                                null
                            },
                        )
                    }
                    return@launch
                }

                pendingSessionCredentials = credentials
                _uiState.update {
                    it.copy(
                        isRestoringSession = true,
                        selectedRole = credentials.role,
                        idNumber = credentials.idNumber,
                        shouldShowAuthOnLaunch = false,
                        errorMessage = null,
                    )
                }

                if (status == ConnectivityStatus.Online) {
                    restoreOnlineSession(credentials, generation)
                    return@launch
                }

                val cachedUser = withContext(Dispatchers.IO) { offlineCacheStore.getLastUser() }
                    ?.takeIf { it.matches(credentials) }
                if (generation != sessionGeneration) return@launch

                if (cachedUser != null) {
                    _uiState.update {
                        it.copy(
                            isRestoringSession = false,
                            profile = cachedUser,
                            isOfflineMode = true,
                            shouldShowAuthOnLaunch = false,
                            successMessage = "Offline mode: showing last saved data.",
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isRestoringSession = false,
                            profile = null,
                            isOfflineMode = true,
                            shouldShowAuthOnLaunch = true,
                            errorMessage = "Connect to the internet to restore your saved login.",
                        )
                    }
                }

                // The network can become ready while the encrypted stores are being read. Recheck
                // after installing the offline state so that an early Online event is not lost.
                val latestStatus = resolveCurrentConnectivity()
                if (generation != sessionGeneration) return@launch
                _uiState.update { it.copy(connectivityStatus = latestStatus) }
                if (latestStatus == ConnectivityStatus.Online) {
                    restoreOnlineSession(credentials, generation)
                }
            } finally {
                if (generation == sessionGeneration) {
                    sessionRestoreJob = null
                }
            }
        }
    }

    private fun resumePendingSessionRestore() {
        val credentials = pendingSessionCredentials ?: return
        if (sessionRestoreJob?.isActive == true) return

        val generation = sessionGeneration
        sessionRestoreJob = viewModelScope.launch {
            try {
                restoreOnlineSession(credentials, generation)
            } finally {
                if (generation == sessionGeneration) {
                    sessionRestoreJob = null
                }
            }
        }
    }

    private suspend fun restoreOnlineSession(credentials: Credentials, generation: Long) {
        _uiState.update {
            it.copy(
                isRestoringSession = it.profile == null,
                shouldShowAuthOnLaunch = false,
                errorMessage = null,
            )
        }

        try {
            val profile = repository.login(credentials.idNumber, credentials.password, credentials.role)
            if (generation != sessionGeneration) return

            withContext(Dispatchers.IO) { offlineCacheStore.saveUser(profile) }
            if (generation != sessionGeneration) return

            pendingSessionCredentials = null
            _uiState.update {
                it.copy(
                    isRestoringSession = false,
                    profile = profile,
                    isOfflineMode = false,
                    shouldShowAuthOnLaunch = false,
                    successMessage = null,
                    errorMessage = null,
                )
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (throwable: Throwable) {
            handleSessionRestoreFailure(credentials, throwable, generation)
        }
    }

    private suspend fun handleSessionRestoreFailure(
        credentials: Credentials,
        throwable: Throwable,
        generation: Long,
    ) {
        if (generation != sessionGeneration) return

        if (throwable.isStoredSessionAuthError()) {
            withContext(Dispatchers.IO) {
                sessionStore.clear()
                offlineCacheStore.clear()
            }
            if (generation != sessionGeneration) return

            pendingSessionCredentials = null
            _uiState.update {
                it.copy(
                    isRestoringSession = false,
                    profile = null,
                    selectedRole = null,
                    idNumber = "",
                    password = "",
                    isOfflineMode = false,
                    shouldShowAuthOnLaunch = true,
                    successMessage = null,
                    errorMessage = "Your saved login is no longer valid. Please log in again.",
                )
            }
            return
        }

        val cachedUser = withContext(Dispatchers.IO) { offlineCacheStore.getLastUser() }
            ?.takeIf { it.matches(credentials) }
        if (generation != sessionGeneration) return

        if (cachedUser != null) {
            _uiState.update {
                it.copy(
                    isRestoringSession = false,
                    profile = cachedUser,
                    isOfflineMode = true,
                    shouldShowAuthOnLaunch = false,
                    successMessage = "Offline mode: showing last saved data.",
                    errorMessage = null,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isRestoringSession = false,
                    profile = null,
                    isOfflineMode = true,
                    shouldShowAuthOnLaunch = true,
                    successMessage = null,
                    errorMessage = "Couldn't restore your saved login. Check your connection and try again.",
                )
            }
        }
    }

    private suspend fun resolveCurrentConnectivity(): ConnectivityStatus {
        val versionBeforeCheck = connectivityVersion
        val checkedStatus = withContext(Dispatchers.IO) { networkMonitor.currentStatus() }
        return if (connectivityVersion != versionBeforeCheck) {
            latestConnectivityStatus
        } else {
            checkedStatus
        }
    }

    fun selectRole(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    fun updateIdNumber(value: String) {
        _uiState.update { it.copy(idNumber = value.take(MAX_ID_NUMBER_LENGTH)) }
    }

    fun updateFullName(value: String) {
        _uiState.update { it.copy(fullName = value.take(MAX_FULL_NAME_LENGTH)) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                mode = if (it.mode == AuthMode.Login) AuthMode.Register else AuthMode.Login,
                password = "",
                confirmPassword = "",
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.connectivityStatus != ConnectivityStatus.Online) {
            showError("Internet required.")
            return
        }
        val role = state.selectedRole
        if (role == null) {
            showError("Please select a role.")
            return
        }

        val validationError = if (state.mode == AuthMode.Login) {
            validateLogin(state)
        } else {
            validateRegister(state)
        }
        if (validationError != null) {
            showError(validationError)
            return
        }

        invalidateSessionRestore()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            runCatching {
                if (state.mode == AuthMode.Login) {
                    repository.login(state.idNumber, state.password, role)
                } else {
                    repository.register(state.idNumber, state.fullName, state.password, role)
                    null
                }
            }.onSuccess { profile ->
                if (profile != null) {
                    // Persist credentials so the next launch can silently re-login.
                    withContext(Dispatchers.IO) {
                        sessionStore.saveCredentials(state.idNumber, state.password, role)
                        offlineCacheStore.saveUser(profile)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            isOfflineMode = false,
                            shouldShowAuthOnLaunch = false,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            mode = AuthMode.Login,
                            password = "",
                            confirmPassword = "",
                            isLoading = false,
                            successMessage = "Registration successful. Please log in.",
                        )
                    }
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.userFriendlyMessage(),
                    )
                }
            }
        }
    }

    fun logout() {
        val restoreJob = invalidateSessionRestore()
        viewModelScope.launch {
            restoreJob?.join()
            runCatching { repository.signOut() }
            withContext(Dispatchers.IO) {
                sessionStore.clear()
                offlineCacheStore.clear()
                SupabaseReadGuard.shared.clear()
            }
            val currentStatus = resolveCurrentConnectivity()
            _uiState.value = AuthUiState(
                connectivityStatus = currentStatus,
                isOfflineMode = currentStatus != ConnectivityStatus.Online,
            )
        }
    }

    fun onConnectivityChanged(status: ConnectivityStatus) {
        latestConnectivityStatus = status
        connectivityVersion += 1L
        _uiState.update { it.copy(connectivityStatus = status) }
        if (status == ConnectivityStatus.Online && pendingSessionCredentials != null) {
            resumePendingSessionRestore()
        }
    }

    private fun invalidateSessionRestore(): Job? {
        val activeJob = sessionRestoreJob
        sessionGeneration += 1L
        activeJob?.cancel()
        sessionRestoreJob = null
        pendingSessionCredentials = null
        return activeJob
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message, successMessage = null) }
    }

    private fun validateLogin(state: AuthUiState): String? {
        return when {
            state.idNumber.isBlank() -> "ID number is required."
            state.password.isBlank() -> "Password is required."
            else -> null
        }
    }

    private fun validateRegister(state: AuthUiState): String? {
        return when {
            state.idNumber.isBlank() -> "ID number is required."
            !ID_NUMBER_PATTERN.matches(state.idNumber.trim()) -> "ID number must use XX-XXXX format."
            state.fullName.isBlank() -> "Full name is required."
            state.password.isBlank() -> "Password is required."
            state.password.length < 6 -> "Password must be at least 6 characters."
            state.confirmPassword != state.password -> "Passwords do not match."
            else -> null
        }
    }

    private fun Throwable.userFriendlyMessage(): String {
        val rawMessage = message.orEmpty()
        return when {
            rawMessage.contains("Invalid login credentials", ignoreCase = true) ->
                "Invalid ID number or password."
            rawMessage.contains("Invalid ID number, password, or role", ignoreCase = true) ->
                "Invalid ID number, password, or selected role."
            rawMessage.contains("already registered", ignoreCase = true) ->
                "That ID number is already registered."
            rawMessage.isBlank() ->
                "Something went wrong. Please try again."
            else -> rawMessage
        }
    }

    private fun Throwable.isStoredSessionAuthError(): Boolean {
        val rawMessage = message.orEmpty()
        return rawMessage.contains("Invalid ID number", ignoreCase = true) ||
            rawMessage.contains("Invalid login credentials", ignoreCase = true) ||
            rawMessage.contains("Selected role does not match", ignoreCase = true)
    }

    private fun AppUser.matches(credentials: Credentials): Boolean {
        return idNumber.trim().equals(credentials.idNumber.trim(), ignoreCase = true) &&
            role.trim().equals(credentials.role.value, ignoreCase = true)
    }

    companion object {
        private const val MAX_ID_NUMBER_LENGTH = 7
        private const val MAX_FULL_NAME_LENGTH = 30
        private val ID_NUMBER_PATTERN = Regex("^\\d{2}-\\d{4}$")
    }
}

/**
 * Factory so AuthViewModel can receive a SessionStore (which needs the Application
 * context for the Android keystore) via the standard viewModel() integration.
 */
class AuthViewModelFactory(
    private val sessionStore: SessionStore,
    private val offlineCacheStore: OfflineReviewCacheStore,
    private val networkMonitor: NetworkMonitor,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            sessionStore = sessionStore,
            offlineCacheStore = offlineCacheStore,
            networkMonitor = networkMonitor,
        ) as T
    }
}
