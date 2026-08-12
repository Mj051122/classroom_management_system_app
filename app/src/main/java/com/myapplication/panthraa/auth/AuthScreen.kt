package com.myapplication.panthraa.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.myapplication.panthraa.R
import com.myapplication.panthraa.model.ConnectivityStatus

private val ScreenBackground = Color(0xFFF8FAFC)
private val PanthraBlue = Color(0xFF0034DE)
private val PanthraLightBlue = Color(0xFF006EFF)
private val BorderColor = Color(0xFFE2E8F0)
private val SlateText = Color(0xFF0F172A)
private val MutedText = Color(0xFF64748B)
private val FieldBackground = Color(0xFFF8FAFC)
private val CapsuleBackground = Color(0xFFF1F5F9)
private val ProfessorTeal = Color(0xFF1E3A8A)
private val ProfessorSoft = Color(0xFFF0F4FF)
private val StudentSoft = Color(0xFFF5F8FF)
private val ProfessorOrange = Color(0xFFF97316)

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onRoleSelected: (UserRole) -> Unit,
    onIdNumberChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onOtpChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
    onOpenForgotPassword: () -> Unit,
    onMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnMessageShown by rememberUpdatedState(onMessageShown)
    val message = uiState.errorMessage ?: uiState.successMessage

    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            currentOnMessageShown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(uiState.selectedRole?.screenBackground() ?: ScreenBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        RoleBackgroundElements(role = uiState.selectedRole)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthCard(
                uiState = uiState,
                onRoleSelected = onRoleSelected,
                onIdNumberChanged = onIdNumberChanged,
                onFirstNameChanged = onFirstNameChanged,
                onLastNameChanged = onLastNameChanged,
                onEmailChanged = onEmailChanged,
                onPasswordChanged = onPasswordChanged,
                onConfirmPasswordChanged = onConfirmPasswordChanged,
                onOtpChanged = onOtpChanged,
                onSubmit = onSubmit,
                onToggleMode = onToggleMode,
                onOpenForgotPassword = onOpenForgotPassword,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        )
    }
}

@Composable
private fun RoleBackgroundElements(role: UserRole?) {
    if (role == null) return

    val accent = role.accentColor()
    val secondary = when (role) {
        UserRole.Student -> PanthraLightBlue
        UserRole.Professor -> ProfessorOrange
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawLine(
            color = accent.copy(alpha = 0.16f),
            start = Offset(width * 0.08f, height * 0.18f),
            end = Offset(width * 0.92f, height * 0.05f),
            strokeWidth = 18f,
        )
        drawLine(
            color = secondary.copy(alpha = 0.13f),
            start = Offset(width * 0.04f, height * 0.82f),
            end = Offset(width * 0.96f, height * 0.70f),
            strokeWidth = 22f,
        )
        repeat(5) { index ->
            val left = width * (0.10f + index * 0.18f)
            val top = if (index % 2 == 0) height * 0.11f else height * 0.86f
            drawRoundRect(
                color = accent.copy(alpha = 0.08f),
                topLeft = Offset(left, top),
                size = Size(42f, 42f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
            )
        }
        if (role == UserRole.Professor) {
            repeat(4) { index ->
                drawLine(
                    color = secondary.copy(alpha = 0.10f),
                    start = Offset(width * (0.18f + index * 0.18f), height * 0.24f),
                    end = Offset(width * (0.10f + index * 0.18f), height * 0.36f),
                    strokeWidth = 10f,
                )
            }
        }
    }
}

@Composable
private fun AuthCard(
    uiState: AuthUiState,
    onRoleSelected: (UserRole) -> Unit,
    onIdNumberChanged: (String) -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onOtpChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
    onOpenForgotPassword: () -> Unit,
) {
    val isRegister = uiState.mode == AuthMode.Register
    val isForgotPassword = uiState.mode == AuthMode.ForgotPassword
    val isLogin = uiState.mode == AuthMode.Login
    val isOnline = uiState.connectivityStatus == ConnectivityStatus.Online
    val needsRole = isRegister && uiState.emailAuthStage == EmailAuthStage.Form
    val fieldsEnabled = isOnline && !uiState.isLoading && (!needsRole || uiState.selectedRole != null)
    val selectedRole = uiState.selectedRole
    val accentColor = selectedRole?.accentColor() ?: PanthraBlue

    Surface(
        modifier = Modifier.widthIn(max = 420.dp),
        shape = RoundedCornerShape(22.dp),
        color = selectedRole?.cardBackground() ?: Color.White,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, selectedRole?.accentColor()?.copy(alpha = 0.35f) ?: BorderColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = when {
                    isForgotPassword && uiState.emailAuthStage == EmailAuthStage.SetPassword -> "Choose New Password"
                    isForgotPassword -> "Reset Password"
                    isRegister -> "Register"
                    else -> "Login"
                },
                color = SlateText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isRegister && uiState.emailAuthStage == EmailAuthStage.Form) {
                Text(
                    text = "Select a role",
                    color = MutedText,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    UserRole.entries.forEach { role ->
                        RoleCapsule(
                            role = role,
                            selected = uiState.selectedRole == role,
                            enabled = isOnline && !uiState.isLoading,
                            onClick = { onRoleSelected(role) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            if (!isOnline) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Internet required for first login.",
                    color = Color(0xFFB91C1C),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (uiState.mode) {
                AuthMode.Login -> {
                    EmailField(uiState.email, onEmailChanged, fieldsEnabled)
                    Spacer(modifier = Modifier.height(14.dp))
                    PasswordField(uiState.password, onPasswordChanged, "Password", fieldsEnabled)
                }
                AuthMode.Register -> when (uiState.emailAuthStage) {
                    EmailAuthStage.Form -> {
                        IdNumberField(uiState.idNumber, onIdNumberChanged, fieldsEnabled)
                        Spacer(modifier = Modifier.height(14.dp))
                        PanthraTextField(
                            value = uiState.firstName,
                            onValueChange = onFirstNameChanged,
                            label = "First Name",
                            placeholder = "Juan",
                            enabled = fieldsEnabled,
                            keyboardType = KeyboardType.Text,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        PanthraTextField(
                            value = uiState.lastName,
                            onValueChange = onLastNameChanged,
                            label = "Last Name",
                            placeholder = "Dela Cruz",
                            enabled = fieldsEnabled,
                            keyboardType = KeyboardType.Text,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        EmailField(uiState.email, onEmailChanged, fieldsEnabled)
                        Spacer(modifier = Modifier.height(14.dp))
                        PasswordField(uiState.password, onPasswordChanged, "Password", fieldsEnabled)
                        Spacer(modifier = Modifier.height(14.dp))
                        PasswordField(uiState.confirmPassword, onConfirmPasswordChanged, "Confirm Password", fieldsEnabled)
                    }
                    EmailAuthStage.VerifyOtp -> EmailOtpField(uiState.otp, onOtpChanged, fieldsEnabled)
                    EmailAuthStage.SetPassword -> Unit
                }
                AuthMode.ForgotPassword -> when (uiState.emailAuthStage) {
                    EmailAuthStage.Form -> EmailField(uiState.email, onEmailChanged, fieldsEnabled)
                    EmailAuthStage.VerifyOtp -> EmailOtpField(uiState.otp, onOtpChanged, fieldsEnabled)
                    EmailAuthStage.SetPassword -> {
                        PasswordField(uiState.password, onPasswordChanged, "New Password", fieldsEnabled)
                        Spacer(modifier = Modifier.height(14.dp))
                        PasswordField(uiState.confirmPassword, onConfirmPasswordChanged, "Confirm Password", fieldsEnabled)
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            GradientAuthButton(
                text = when {
                    !isOnline -> "INTERNET REQUIRED"
                    isRegister && uiState.emailAuthStage == EmailAuthStage.Form -> "SEND VERIFICATION CODE"
                    isRegister -> "VERIFY EMAIL"
                    isForgotPassword && uiState.emailAuthStage == EmailAuthStage.Form -> "SEND RESET CODE"
                    isForgotPassword && uiState.emailAuthStage == EmailAuthStage.VerifyOtp -> "VERIFY CODE"
                    isForgotPassword -> "RESET PASSWORD"
                    else -> "LOGIN"
                },
                accent = accentColor,
                isLoading = uiState.isLoading,
                enabled = isOnline && !uiState.isLoading,
                onClick = onSubmit,
            )

            if (isLogin) {
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    enabled = isOnline && !uiState.isLoading,
                    onClick = onOpenForgotPassword,
                ) {
                    Text(
                        text = "Forgot password?",
                        color = PanthraBlue,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                enabled = isOnline && !uiState.isLoading,
                onClick = onToggleMode,
            ) {
                Text(
                    text = when {
                        isForgotPassword -> "Back to login"
                        isRegister -> "Already have an account? Login"
                        else -> "Don't have an account? Register"
                    },
                    color = PanthraBlue,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun IdNumberField(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    PanthraTextField(
        value = value,
        onValueChange = onValueChange,
        label = "ID Number",
        placeholder = "24-6072",
        enabled = enabled,
        keyboardType = KeyboardType.Text,
    )
}

@Composable
private fun EmailField(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    PanthraTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Email Address",
        placeholder = "name@example.com",
        enabled = enabled,
        keyboardType = KeyboardType.Email,
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
) {
    PanthraTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = "password0322",
        enabled = enabled,
        keyboardType = KeyboardType.Password,
        visualTransformation = PasswordVisualTransformation(),
        passwordVisibilityToggle = true,
    )
}

@Composable
private fun EmailOtpField(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Enter the 8-digit code sent to your email.",
            color = MutedText,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PanthraTextField(
            value = value,
            onValueChange = onValueChange,
            label = "Email Verification Code",
            placeholder = "12345678",
            enabled = enabled,
            keyboardType = KeyboardType.Number,
        )
    }
}

@Composable
private fun RoleCapsule(
    role: UserRole,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val roleAccent = role.accentColor()
    val background = if (selected) roleAccent else CapsuleBackground
    val textColor = if (selected) Color.White else MutedText

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = if (selected) roleAccent else BorderColor,
                shape = RoundedCornerShape(999.dp),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = role.label,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun RoleSelectionBanner(role: UserRole) {
    val accent = role.accentColor()
    val label = when (role) {
        UserRole.Student -> "Student access selected"
        UserRole.Professor -> "Faculty access selected"
    }
    val helper = when (role) {
        UserRole.Student -> "Classes, submissions, and attendance tracking."
        UserRole.Professor -> "Class management, activities, and attendance recording."
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(1.dp, accent.copy(alpha = 0.24f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = role.label.take(1),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                color = SlateText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
            Text(
                text = helper,
                color = MutedText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                lineHeight = 12.sp,
            )
        }
    }
}

@Composable
private fun PanthraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    enabled: Boolean,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    passwordVisibilityToggle: Boolean = false,
) {
    val isPasswordVisible = remember { mutableStateOf(false) }
    val editableText = remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }
    val syncedEditableText = editableText.value.let { currentValue ->
        if (currentValue.text == value) {
            currentValue
        } else {
            val selection = (currentValue.selection.end + value.length - currentValue.text.length)
                .coerceIn(0, value.length)
            TextFieldValue(text = value, selection = TextRange(selection))
        }
    }
    LaunchedEffect(value) {
        editableText.value = syncedEditableText
    }
    val keyboardOptions = remember(keyboardType) {
        KeyboardOptions(keyboardType = keyboardType)
    }
    val borderColor = when {
        !enabled -> BorderColor
        value.isNotEmpty() -> SlateText
        else -> BorderColor
    }
    val textColor = if (enabled) SlateText else MutedText

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text(
            text = label,
            color = if (value.isNotEmpty()) SlateText else MutedText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
        BasicTextField(
            value = syncedEditableText,
            onValueChange = { updatedValue ->
                editableText.value = updatedValue
                onValueChange(updatedValue.text)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (passwordVisibilityToggle && isPasswordVisible.value) {
                VisualTransformation.None
            } else {
                visualTransformation
            },
            cursorBrush = Brush.verticalGradient(listOf(PanthraBlue, PanthraBlue)),
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (enabled) FieldBackground else Color(0xFFF1F5F9))
                        .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                        .padding(start = 16.dp, end = if (passwordVisibilityToggle) 4.dp else 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MutedText.copy(alpha = 0.55f),
                            fontSize = 16.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = if (passwordVisibilityToggle) 48.dp else 0.dp),
                    ) {
                        innerTextField()
                    }
                    if (passwordVisibilityToggle) {
                        IconButton(
                            onClick = { isPasswordVisible.value = !isPasswordVisible.value },
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible.value) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = if (isPasswordVisible.value) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },
                                tint = MutedText,
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun GradientAuthButton(
    text: String,
    accent: Color,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.74f),
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = if (enabled) {
                            listOf(accent, if (accent == ProfessorTeal) Color(0xFF3B5FCC) else PanthraLightBlue)
                        } else if (isLoading) {
                            listOf(Color.White, Color.White)
                        } else {
                            listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1))
                        },
                    ),
                )
                .then(
                    if (isLoading) {
                        Modifier.border(2.dp, PanthraBlue, RoundedCornerShape(14.dp))
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                LoginLoadingAnimation()
            } else {
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 0.sp,
                )
            }
        }
    }
}

@Composable
private fun LoginLoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_dots_blue))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(width = 340.dp, height = 68.dp),
    )
}

private fun UserRole.accentColor(): Color {
    return when (this) {
        UserRole.Student -> PanthraBlue
        UserRole.Professor -> ProfessorTeal
    }
}

private fun UserRole.cardBackground(): Color {
    return when (this) {
        UserRole.Student -> StudentSoft
        UserRole.Professor -> ProfessorSoft
    }
}

private fun UserRole.screenBackground(): Color {
    return when (this) {
        UserRole.Student -> Color(0xFFF3F7FF)
        UserRole.Professor -> Color(0xFFF0F4FF)
    }
}
