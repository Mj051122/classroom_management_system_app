package com.myapplication.panthraa.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.myapplication.panthraa.data.NetworkMonitor
import com.myapplication.panthraa.data.OfflineReviewCacheStore
import com.myapplication.panthraa.data.SupabaseReadGuard
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
                val status = resolveCurrentConnectivity()
                if (generation != sessionGeneration) return@launch
                _uiState.update { it.copy(connectivityStatus = status) }

                if (status == ConnectivityStatus.Online) {
                    // Remove only credentials written by the former prototype flow.
                    withContext(Dispatchers.IO) { sessionStore.clear() }
                    val profile = runCatching { repository.restoreAuthenticatedProfileOrNull() }
                        .getOrNull()
                    if (generation != sessionGeneration) return@launch

                    if (profile != null) {
                        withContext(Dispatchers.IO) { offlineCacheStore.saveUser(profile) }
                        _uiState.update {
                            it.copy(
                                isRestoringSession = false,
                                selectedRole = profile.toUserRole(),
                                profile = profile,
                                isOfflineMode = false,
                                shouldShowAuthOnLaunch = false,
                                errorMessage = null,
                            )
                        }
                    } else {
                        showLoginForm(status)
                    }
                    return@launch
                }

                val cachedUser = withContext(Dispatchers.IO) { offlineCacheStore.getLastUser() }
                if (generation != sessionGeneration) return@launch
                _uiState.update {
                    it.copy(
                        isRestoringSession = false,
                        selectedRole = cachedUser?.toUserRole(),
                        profile = cachedUser,
                        isOfflineMode = true,
                        shouldShowAuthOnLaunch = cachedUser == null,
                        errorMessage = if (cachedUser == null) {
                            "Connect to the internet to sign in."
                        } else {
                            null
                        },
                        successMessage = if (cachedUser != null) {
                            "Offline mode: showing last saved data."
                        } else {
                            null
                        },
                    )
                }
            } finally {
                if (generation == sessionGeneration) {
                    sessionRestoreJob = null
                }
            }
        }
    }

    fun selectRole(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    fun updateIdNumber(value: String) {
        _uiState.update { currentState ->
            val digits = value.filter(Char::isDigit).take(MAX_ID_NUMBER_DIGIT_COUNT)
            val shouldShowSeparator = digits.length > ID_NUMBER_PREFIX_LENGTH ||
                (digits.length == ID_NUMBER_PREFIX_LENGTH && currentState.idNumber.length < value.length)
            val formatted = if (shouldShowSeparator) {
                "${digits.take(ID_NUMBER_PREFIX_LENGTH)}-${digits.drop(ID_NUMBER_PREFIX_LENGTH)}"
            } else {
                digits
            }
            currentState.copy(idNumber = formatted)
        }
    }

    fun updateFullName(value: String) {
        _uiState.update { it.copy(fullName = value.take(MAX_FULL_NAME_LENGTH)) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value.take(MAX_EMAIL_LENGTH)) }
    }

    fun updateCurrentPassword(value: String) {
        _uiState.update { it.copy(currentPassword = value) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun updateConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun updateOtp(value: String) {
        _uiState.update { it.copy(otp = value.filter(Char::isDigit).take(EMAIL_OTP_LENGTH)) }
    }

    fun toggleMode() {
        _uiState.update { state ->
            state.clearedAuthFields(
                mode = when (state.mode) {
                    AuthMode.Login -> AuthMode.Register
                    AuthMode.Register,
                    AuthMode.ForgotPassword,
                    AuthMode.UpgradeExistingAccount,
                    -> AuthMode.Login
                },
            )
        }
    }

    fun openForgotPassword() {
        _uiState.update { it.clearedAuthFields(mode = AuthMode.ForgotPassword, keepEmail = true) }
    }

    fun openExistingAccountUpgrade() {
        _uiState.update { it.clearedAuthFields(mode = AuthMode.UpgradeExistingAccount) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.connectivityStatus != ConnectivityStatus.Online) {
            showError("Internet required.")
            return
        }

        val validationError = validationErrorFor(state)
        if (validationError != null) {
            showError(validationError)
            return
        }

        invalidateSessionRestore()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                when (state.mode) {
                    AuthMode.Login -> completeSignIn(
                        repository.signIn(state.email, state.password),
                        "Welcome back.",
                    )

                    AuthMode.Register -> submitRegistration(state)
                    AuthMode.ForgotPassword -> submitPasswordReset(state)
                    AuthMode.UpgradeExistingAccount -> submitExistingAccountUpgrade(state)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = throwable.userFriendlyMessage())
                }
            }
        }
    }

    private suspend fun submitRegistration(state: AuthUiState) {
        when (state.emailAuthStage) {
            EmailAuthStage.Form -> {
                repository.beginRegistration(state.email, state.password)
                moveToOtp("Verification code sent. Check your email.")
            }
            EmailAuthStage.VerifyOtp -> {
                val role = state.selectedRole ?: error("Please select a role.")
                completeSignIn(
                    repository.completeRegistration(
                        email = state.email,
                        otp = state.otp,
                        idNumber = state.idNumber,
                        fullName = state.fullName,
                        role = role,
                    ),
                    "Registration complete.",
                )
            }
            EmailAuthStage.SetPassword -> error("Invalid registration step.")
        }
    }

    private suspend fun submitPasswordReset(state: AuthUiState) {
        when (state.emailAuthStage) {
            EmailAuthStage.Form -> {
                repository.requestPasswordReset(state.email)
                moveToOtp("If that email has an account, a reset code was sent.")
            }
            EmailAuthStage.VerifyOtp -> {
                repository.verifyPasswordResetCode(state.email, state.otp)
                _uiState.update {
                    it.copy(
                        emailAuthStage = EmailAuthStage.SetPassword,
                        otp = "",
                        isLoading = false,
                        successMessage = "Code verified. Choose a new password.",
                    )
                }
            }
            EmailAuthStage.SetPassword -> {
                repository.updatePassword(state.password)
                _uiState.update {
                    it.clearedAuthFields(mode = AuthMode.Login).copy(
                        isLoading = false,
                        successMessage = "Password reset successful. Please log in.",
                    )
                }
            }
        }
    }

    private suspend fun submitExistingAccountUpgrade(state: AuthUiState) {
        when (state.emailAuthStage) {
            EmailAuthStage.Form -> {
                repository.beginLegacyAccountUpgrade(
                    idNumber = state.idNumber,
                    currentPassword = state.currentPassword,
                    email = state.email,
                    newPassword = state.password,
                )
                moveToOtp("Verification code sent. Check your email.")
            }
            EmailAuthStage.VerifyOtp -> completeSignIn(
                repository.completeLegacyAccountUpgrade(state.email, state.otp),
                "Email sign-in is ready.",
            )
            EmailAuthStage.SetPassword -> error("Invalid existing-account upgrade step.")
        }
    }

    private suspend fun completeSignIn(profile: AppUser, message: String) {
        withContext(Dispatchers.IO) {
            sessionStore.clear()
            offlineCacheStore.saveUser(profile)
        }
        _uiState.update {
            it.clearedAuthFields(mode = AuthMode.Login).copy(
                selectedRole = profile.toUserRole(),
                profile = profile,
                isLoading = false,
                isOfflineMode = false,
                shouldShowAuthOnLaunch = false,
                successMessage = message,
            )
        }
    }

    private fun moveToOtp(message: String) {
        _uiState.update {
            it.copy(
                emailAuthStage = EmailAuthStage.VerifyOtp,
                otp = "",
                isLoading = false,
                successMessage = message,
            )
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
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message, successMessage = null) }
    }

    private fun showLoginForm(status: ConnectivityStatus) {
        _uiState.update {
            it.copy(
                isRestoringSession = false,
                profile = null,
                isOfflineMode = status != ConnectivityStatus.Online,
                shouldShowAuthOnLaunch = true,
                errorMessage = if (status != ConnectivityStatus.Online) {
                    "Internet required for first login."
                } else {
                    null
                },
            )
        }
    }

    private suspend fun resolveCurrentConnectivity(): ConnectivityStatus {
        val versionBeforeCheck = connectivityVersion
        val checkedStatus = withContext(Dispatchers.IO) { networkMonitor.currentStatus() }
        return if (connectivityVersion != versionBeforeCheck) latestConnectivityStatus else checkedStatus
    }

    private fun invalidateSessionRestore(): Job? {
        val activeJob = sessionRestoreJob
        sessionGeneration += 1L
        activeJob?.cancel()
        sessionRestoreJob = null
        return activeJob
    }

    private fun validationErrorFor(state: AuthUiState): String? {
        return when (state.mode) {
            AuthMode.Login -> validateEmailAndPassword(state)
            AuthMode.Register -> when (state.emailAuthStage) {
                EmailAuthStage.Form -> validateRegistration(state)
                EmailAuthStage.VerifyOtp -> validateOtp(state)
                EmailAuthStage.SetPassword -> "Invalid registration step."
            }
            AuthMode.ForgotPassword -> when (state.emailAuthStage) {
                EmailAuthStage.Form -> validateEmail(state.email)
                EmailAuthStage.VerifyOtp -> validateOtp(state)
                EmailAuthStage.SetPassword -> validateNewPassword(state)
            }
            AuthMode.UpgradeExistingAccount -> when (state.emailAuthStage) {
                EmailAuthStage.Form -> validateExistingAccountUpgrade(state)
                EmailAuthStage.VerifyOtp -> validateOtp(state)
                EmailAuthStage.SetPassword -> "Invalid existing-account upgrade step."
            }
        }
    }

    private fun validateEmailAndPassword(state: AuthUiState): String? {
        return validateEmail(state.email) ?: when {
            state.password.isBlank() -> "Password is required."
            else -> null
        }
    }

    private fun validateRegistration(state: AuthUiState): String? {
        return when {
            state.selectedRole == null -> "Please select a role."
            state.idNumber.isBlank() -> "ID number is required."
            !ID_NUMBER_PATTERN.matches(state.idNumber.trim()) -> "ID number must use XX-XXXX format."
            state.fullName.isBlank() -> "Full name is required."
            validateEmail(state.email) != null -> validateEmail(state.email)
            else -> validateNewPassword(state)
        }
    }

    private fun validateExistingAccountUpgrade(state: AuthUiState): String? {
        return when {
            state.idNumber.isBlank() -> "ID number is required."
            !ID_NUMBER_PATTERN.matches(state.idNumber.trim()) -> "ID number must use XX-XXXX format."
            state.currentPassword.isBlank() -> "Current password is required."
            validateEmail(state.email) != null -> validateEmail(state.email)
            else -> validateNewPassword(state)
        }
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required."
        !isValidEmail(email) -> "Enter a valid email address."
        else -> null
    }

    private fun validateOtp(state: AuthUiState): String? = when {
        state.otp.length != EMAIL_OTP_LENGTH -> "Enter the $EMAIL_OTP_LENGTH-digit code from your email."
        else -> null
    }

    private fun validateNewPassword(state: AuthUiState): String? = when {
        state.password.length < MIN_PASSWORD_LENGTH ->
            "Password must be at least $MIN_PASSWORD_LENGTH characters."
        state.password.none(Char::isLetter) || state.password.none(Char::isDigit) ->
            "Password must include letters and numbers."
        state.confirmPassword != state.password -> "Passwords do not match."
        else -> null
    }

    private fun Throwable.userFriendlyMessage(): String {
        val rawMessage = message.orEmpty()
        return when {
            rawMessage.contains("Invalid login credentials", ignoreCase = true) ->
                "Invalid email or password."
            rawMessage.contains("not connected to an app profile", ignoreCase = true) ->
                "This email has not been linked yet. Use Upgrade existing account."
            rawMessage.contains("OTP", ignoreCase = true) || rawMessage.contains("token", ignoreCase = true) ->
                "That code is invalid or expired. Request a new one."
            rawMessage.isBlank() -> "Something went wrong. Please try again."
            else -> rawMessage
        }
    }

    private fun AppUser.toUserRole(): UserRole? =
        UserRole.entries.firstOrNull { it.value.equals(role, ignoreCase = true) }

    private fun AuthUiState.clearedAuthFields(
        mode: AuthMode,
        keepEmail: Boolean = false,
    ): AuthUiState = copy(
        mode = mode,
        selectedRole = if (mode == AuthMode.Register) selectedRole else null,
        idNumber = "",
        fullName = "",
        email = if (keepEmail) email else "",
        currentPassword = "",
        password = "",
        confirmPassword = "",
        otp = "",
        emailAuthStage = EmailAuthStage.Form,
        errorMessage = null,
        successMessage = null,
    )

    companion object {
        private const val ID_NUMBER_PREFIX_LENGTH = 2
        private const val MAX_ID_NUMBER_DIGIT_COUNT = 6
        private const val MAX_FULL_NAME_LENGTH = 30
        private const val MAX_EMAIL_LENGTH = 254
        private const val EMAIL_OTP_LENGTH = 8
        private const val MIN_PASSWORD_LENGTH = 8
        private val ID_NUMBER_PATTERN = Regex("^\\d{2}-\\d{4}$")
        private val PHONE_PATTERN = Regex("^(09\\d{9}|\\+639\\d{9})$")
        private val EMAIL_PATTERN = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

        internal fun isValidPhilippinePhone(value: String): Boolean = PHONE_PATTERN.matches(value)

        internal fun isValidEmail(value: String): Boolean = EMAIL_PATTERN.matches(value.trim())
    }
}

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
