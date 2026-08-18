package com.myapplication.panthraa.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.panthraa.model.AppUser
import java.util.Locale

@Composable
internal fun FacultyPeopleScreen(
    users: List<AppUser>,
    isLoading: Boolean,
    internetRequired: Boolean,
    isUpdating: Boolean,
    onLoadUsers: () -> Unit,
    onRefreshUsers: () -> Unit,
    onSetIrregular: (String, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val students = remember(users) {
        users.filter { it.role.equals("student", ignoreCase = true) }
    }
    val filteredStudents = remember(students, searchQuery) {
        val query = searchQuery.trim().lowercase(Locale.getDefault())
        if (query.isEmpty()) {
            students
        } else {
            students.filter { student ->
                student.fullName.lowercase(Locale.getDefault()).contains(query) ||
                    student.idNumber.lowercase(Locale.getDefault()).contains(query)
            }
        }
    }
    val irregularCount = remember(students) { students.count { it.isIrregular } }

    LaunchedEffect(Unit) {
        onLoadUsers()
    }

    PanthraaPullRefresh(
        isRefreshing = false,
        onRefresh = onRefreshUsers,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .panthraaScreenBackground(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ProfessorPageHeader(
                    title = "People",
                    subtitle = "Search students and manage irregular status.",
                    onBack = onBack,
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "$irregularCount of ${students.size} students marked irregular",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Irregular students can request to join classes outside their year, section, or track, as long as the department matches.",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                        )
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search by name or ID number") },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF64748B))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PanthraaBlue,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                            ),
                        )
                        if (internetRequired) {
                            InternetRequiredHint()
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            PanthraaLoadingAnimation(size = 144.dp)
                        }
                    }
                }
                students.isEmpty() -> {
                    item {
                        PanthraaEmptyState(
                            icon = Icons.Outlined.PeopleAlt,
                            title = "No students yet",
                            subtitle = "Students who register in the app will appear here so you can manage their irregular status.",
                        )
                    }
                }
                filteredStudents.isEmpty() -> {
                    item {
                        PanthraaEmptyState(
                            icon = Icons.Outlined.SearchOff,
                            title = "No match found",
                            subtitle = "No student matches \"$searchQuery\".",
                        )
                    }
                }
                else -> {
                    items(filteredStudents, key = { it.id }) { student ->
                        PeopleStudentCard(
                            student = student,
                            enabled = !internetRequired && !isUpdating,
                            onToggleIrregular = { onSetIrregular(student.id, !student.isIrregular) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeopleStudentCard(
    student: AppUser,
    enabled: Boolean,
    onToggleIrregular: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (student.isIrregular) Color(0xFFFFFBF1) else Color.White,
        ),
        border = BorderStroke(
            1.dp,
            if (student.isIrregular) Color(0xFFFDE68A) else Color(0xFFE2E8F0),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                imageUrl = student.profilePictureUrl,
                name = student.fullName,
                modifier = Modifier.size(48.dp),
                placeholderColor = Color(0xFFE5E7EB),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = student.fullName.ifBlank { "Unknown student" },
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "ID: ${student.idNumber.ifBlank { "N/A" }}",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (student.isIrregular) {
                        IrregularBadge()
                    }
                }
                Text(
                    text = listOfNotNull(
                        profileYearLabel(student.year),
                        cleanRequestDetail(student.course),
                        cleanRequestDetail(student.section),
                    ).distinct().joinToString(" · ").ifBlank { "No academic details yet" },
                    color = Color(0xFF475569),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = student.isIrregular,
                    onCheckedChange = { onToggleIrregular() },
                    enabled = enabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFF59E0B),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFCBD5E1),
                        uncheckedBorderColor = Color.Transparent,
                    ),
                )
                Text(
                    text = "Irregular",
                    color = if (student.isIrregular) Color(0xFFB45309) else Color(0xFF64748B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
internal fun IrregularBadge(modifier: Modifier = Modifier) {
    Text(
        text = "IRREGULAR",
        modifier = modifier
            .background(Color(0xFFFFE9C7), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        color = Color(0xFFB45309),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 9.sp,
        letterSpacing = 0.5.sp,
    )
}