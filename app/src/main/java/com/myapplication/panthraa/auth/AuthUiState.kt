package com.myapplication.panthraa.auth

import com.myapplication.panthraa.model.AppUser
import com.myapplication.panthraa.model.ConnectivityStatus

enum class AuthMode {
    Login,
    Register,
    ForgotPassword,
}

enum class EmailAuthStage {
    Form,
    VerifyOtp,
    SetPassword,
}

enum class UserRole(val label: String, val value: String) {
    Student("Student", "student"),
    Professor("Faculty", "professor"),
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val selectedRole: UserRole? = null,
    val idNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val otp: String = "",
    val emailAuthStage: EmailAuthStage = EmailAuthStage.Form,
    val isLoading: Boolean = false,
    val isRestoringSession: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val profile: AppUser? = null,
    val connectivityStatus: ConnectivityStatus = ConnectivityStatus.Connecting,
    val isOfflineMode: Boolean = false,
    val shouldShowAuthOnLaunch: Boolean = false,
)
