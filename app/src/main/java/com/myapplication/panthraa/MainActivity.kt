package com.myapplication.panthraa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.myapplication.panthraa.auth.AuthScreen
import com.myapplication.panthraa.auth.AuthViewModel
import com.myapplication.panthraa.auth.AuthViewModelFactory
import com.myapplication.panthraa.data.NetworkMonitor
import com.myapplication.panthraa.data.OfflineReviewCacheStore
import com.myapplication.panthraa.data.session.SessionStore
import com.myapplication.panthraa.ui.MainScreen
import com.myapplication.panthraa.ui.theme.PanthraaTheme

import androidx.activity.SystemBarStyle

class MainActivity : ComponentActivity() {

    private var openScheduleSignal = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readOpenScheduleIntent(intent)
        val systemSurface = android.graphics.Color.parseColor("#F8FAFC")
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(systemSurface, systemSurface),
            navigationBarStyle = SystemBarStyle.light(systemSurface, systemSurface)
        )
        setContent {
            PanthraaTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PanthraaApp(openScheduleSignal = openScheduleSignal.intValue)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        readOpenScheduleIntent(intent)
    }

    private fun readOpenScheduleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_OPEN_SCHEDULE, false) == true) {
            openScheduleSignal.intValue++
            intent.removeExtra(EXTRA_OPEN_SCHEDULE)
        }
    }

    companion object {
        const val EXTRA_OPEN_SCHEDULE = "OPEN_SCHEDULE"
    }
}

@Composable
private fun PanthraaApp(openScheduleSignal: Int = 0) {
    val context = LocalContext.current
    val sessionStore = remember { SessionStore(context) }
    val offlineCacheStore = remember { OfflineReviewCacheStore(context) }
    val networkMonitor = remember { NetworkMonitor(context) }
    val networkStatus by remember(networkMonitor) { networkMonitor.statusFlow() }
        .collectAsState(initial = networkMonitor.currentStatus())
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            sessionStore = sessionStore,
            offlineCacheStore = offlineCacheStore,
            networkMonitor = networkMonitor,
        ),
    )
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.profile
    var showAuth by remember { mutableStateOf(false) }

    LaunchedEffect(networkStatus) {
        viewModel.onConnectivityChanged(networkStatus)
    }

    LaunchedEffect(uiState.shouldShowAuthOnLaunch) {
        if (uiState.shouldShowAuthOnLaunch) {
            showAuth = true
        }
    }

    when {
        uiState.isRestoringSession -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center,
            ) {
                SessionRestoreLoader()
            }
        }
        profile != null -> MainScreen(
            currentUser = profile,
            networkStatus = networkStatus,
            startedOffline = uiState.isOfflineMode,
            openScheduleSignal = openScheduleSignal,
            onLogout = {
                viewModel.logout()
                showAuth = false
            },
        )
        else -> Box(modifier = Modifier.fillMaxSize()) {
            if (showAuth) {
                AuthScreen(
                    uiState = uiState,
                    onRoleSelected = viewModel::selectRole,
                    onIdNumberChanged = viewModel::updateIdNumber,
                    onFullNameChanged = viewModel::updateFullName,
                    onEmailChanged = viewModel::updateEmail,
                    onCurrentPasswordChanged = viewModel::updateCurrentPassword,
                    onOtpChanged = viewModel::updateOtp,
                    onPasswordChanged = viewModel::updatePassword,
                    onConfirmPasswordChanged = viewModel::updateConfirmPassword,
                    onSubmit = viewModel::submit,
                    onToggleMode = viewModel::toggleMode,
                    onOpenForgotPassword = viewModel::openForgotPassword,
                    onOpenExistingAccountUpgrade = viewModel::openExistingAccountUpgrade,
                    onMessageShown = viewModel::clearMessages,
                )
            }

            AnimatedVisibility(
                visible = !showAuth,
                enter = fadeIn(animationSpec = tween(180)) +
                    slideInVertically(animationSpec = tween(220)) { height -> height / 16 },
                exit = fadeOut(animationSpec = tween(120)) +
                    slideOutVertically(animationSpec = tween(180)) { height -> -height / 12 },
            ) {
                LandingScreen(onLoginClick = { showAuth = true })
            }
        }
    }
}

@Composable
private fun SessionRestoreLoader() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_session))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(280.dp),
        )
}

@Composable
private fun LandingScreen(onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFEAF0FF),
                        Color(0xFFFDF7FB),
                    ),
                ),
            ),
    ) {
        LandingBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                modifier = Modifier.size(172.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.78f)),
                shadowElevation = 10.dp,
            ) {
                Box(
                    modifier = Modifier.padding(18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.landing_logo),
                        contentDescription = "Panthraa logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "panthraa",
                color = Color(0xFF0F172A),
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "One calm place for classes, tasks, and submissions.",
                modifier = Modifier.widthIn(max = 310.dp),
                color = Color(0xFF475569),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .widthIn(min = 220.dp, max = 280.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 0.sp,
                )
            }
        }
    }
}

@Composable
private fun LandingBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val blue = Color(0xFF2563EB)
        val ink = Color(0xFF0F172A)
        val coral = Color(0xFFFF8BA7)
        val mint = Color(0xFF2DD4BF)

        rotate(-16f, pivot = center) {
            drawRoundRect(
                color = blue.copy(alpha = 0.10f),
                topLeft = Offset(-size.width * 0.16f, size.height * 0.12f),
                size = Size(size.width * 0.78f, 52.dp.toPx()),
                cornerRadius = CornerRadius(26.dp.toPx()),
            )
            drawRoundRect(
                color = ink.copy(alpha = 0.06f),
                topLeft = Offset(size.width * 0.62f, size.height * 0.16f),
                size = Size(size.width * 0.52f, 38.dp.toPx()),
                cornerRadius = CornerRadius(19.dp.toPx()),
            )
        }

        rotate(18f, pivot = center) {
            drawRoundRect(
                color = coral.copy(alpha = 0.13f),
                topLeft = Offset(size.width * 0.56f, size.height * 0.70f),
                size = Size(size.width * 0.66f, 54.dp.toPx()),
                cornerRadius = CornerRadius(27.dp.toPx()),
            )
            drawRoundRect(
                color = mint.copy(alpha = 0.12f),
                topLeft = Offset(-size.width * 0.14f, size.height * 0.78f),
                size = Size(size.width * 0.54f, 42.dp.toPx()),
                cornerRadius = CornerRadius(21.dp.toPx()),
            )
        }

        val dashWidth = 46.dp.toPx()
        val dashHeight = 8.dp.toPx()
        repeat(7) { index ->
            val x = size.width * 0.14f + index * size.width * 0.12f
            val y = size.height * 0.27f + if (index % 2 == 0) 0f else 18.dp.toPx()
            drawRoundRect(
                color = blue.copy(alpha = 0.08f),
                topLeft = Offset(x, y),
                size = Size(dashWidth, dashHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )
        }
    }
}
