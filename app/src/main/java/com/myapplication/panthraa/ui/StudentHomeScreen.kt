package com.myapplication.panthraa.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.myapplication.panthraa.model.Profile

@Composable
fun StudentHomeScreen(
    profile: Profile,
    onLogout: () -> Unit,
) {
    TemporaryHomeScreen(
        title = "Student Home",
        profile = profile,
        onLogout = onLogout,
    )
}

@Composable
private fun TemporaryHomeScreen(
    title: String,
    profile: Profile,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .panthraaScreenBackground()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(text = profile.fullName, modifier = Modifier.padding(top = 8.dp))
        Text(text = profile.idNumber, color = Color(0xFF64748B))
        Button(
            onClick = onLogout,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text("Log out")
        }
    }
}
