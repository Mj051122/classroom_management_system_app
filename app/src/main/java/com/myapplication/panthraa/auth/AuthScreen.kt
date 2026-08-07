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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
private val ProfessorTeal = Color(0xFF0F766E)
private val ProfessorSoft = Color(0xFFF0FDFA)
private val StudentSoft = Color(0xFFF5F8FF)
private val ProfessorOrange = Color(0xFFF97316)

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    onRoleSelected: (UserRole) -> Unit,
    onIdNumberChanged: (String) -> Unit,
    onFullNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
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
                onFullNameChanged = onFullNameChanged,
                onPasswordChanged = onPasswordChanged,
                onConfirmPasswordChanged = onConfirmPasswordChanged,
                onSubmit = onSubmit,
                onToggleMode = onToggleMode,
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
    onFullNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleMode: () -> Unit,
) {
    val isRegister = uiState.mode == AuthMode.Register
    val isOnline = uiState.connectivityStatus == ConnectivityStatus.Online
    val fieldsEnabled = isOnline && uiState.selectedRole != null && !uiState.isLoading
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
                text = if (isRegister) "Register" else "Login",
                color = SlateText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            PanthraTextField(
                value = uiState.idNumber,
                onValueChange = onIdNumberChanged,
                label = "ID Number",
                placeholder = "24-6072",
                enabled = fieldsEnabled,
                keyboardType = KeyboardType.Text,
            )

            if (isRegister) {
                Spacer(modifier = Modifier.height(14.dp))
                PanthraTextField(
                    value = uiState.fullName,
                    onValueChange = onFullNameChanged,
                    label = "Full Name",
                    placeholder = "Juan Dela Cruz",
                    enabled = fieldsEnabled,
                    keyboardType = KeyboardType.Text,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            PanthraTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = "Password",
                placeholder = "password0322",
                enabled = fieldsEnabled,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
            )

            if (isRegister) {
                Spacer(modifier = Modifier.height(14.dp))
                PanthraTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    label = "Confirm Password",
                    placeholder = "password0322",
                    enabled = fieldsEnabled,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            GradientAuthButton(
                text = when {
                    !isOnline -> "INTERNET REQUIRED"
                    isRegister -> "REGISTER"
                    else -> "LOGIN"
                },
                accent = accentColor,
                isLoading = uiState.isLoading,
                enabled = isOnline && !uiState.isLoading,
                onClick = onSubmit,
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                enabled = isOnline && !uiState.isLoading,
                onClick = onToggleMode,
            ) {
                Text(
                    text = if (isRegister) {
                        "Already have an account? Login"
                    } else {
                        "Don't have an account? Register"
                    },
                    color = PanthraBlue,
                    textAlign = TextAlign.Center,
                )
            }
        }
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
        UserRole.Professor -> "Professor access selected"
    }
    val helper = when (role) {
        UserRole.Student -> "Classes, submissions, QR, and attendance viewing."
        UserRole.Professor -> "Class management, activities, attendance scanning."
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
) {
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
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
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
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MutedText.copy(alpha = 0.55f),
                            fontSize = 16.sp,
                        )
                    }
                    innerTextField()
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
                            listOf(accent, if (accent == ProfessorTeal) Color(0xFF14B8A6) else PanthraLightBlue)
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
        UserRole.Professor -> Color(0xFFF2FBF8)
    }
}
