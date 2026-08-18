package com.myapplication.panthraa.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.myapplication.panthraa.model.ProfessorClass
import java.util.Locale

private data class ReviewRequestFilterOption(
    val value: String,
    val label: String,
)

@Composable
internal fun ProfessorReviewRequestsScreen(
    uiState: MainUiState,
    classes: List<ProfessorClass>,
    innerPadding: PaddingValues,
    internetRequired: Boolean,
    onLoadJoinRequests: (List<String>) -> Unit,
    onRefreshJoinRequests: (List<String>) -> Unit,
    onApproveRequest: (String, List<String>) -> Unit,
    onRejectRequest: (String, List<String>, String) -> Unit,
    onBack: () -> Unit,
) {
    val classIds = remember(classes) { classes.map { it.id }.distinct().sorted() }
    val classIdSet = remember(classIds) { classIds.toSet() }
    val requests = remember(uiState.classJoinRequests, classIdSet) {
        uiState.classJoinRequests.filter { it.classId in classIdSet }
    }
    val requestClassIds = remember(requests) { requests.map { it.classId }.toSet() }
    val requestClasses = remember(classes, requestClassIds) {
        classes.filter { it.id in requestClassIds }
    }

    var filters by remember { mutableStateOf(ProfessorReviewRequestFilters()) }
    val filteredRequests = remember(requests, classes, filters) {
        filterProfessorReviewRequests(
            requests = requests,
            classes = classes,
            filters = filters,
        )
    }
    val subjectOptions = remember(requestClasses, requests) {
        val classesById = requestClasses.associateBy { it.id }
        requests.mapNotNull { request ->
            val value = classesById[request.classId]
                ?.displaySubjectCode
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: request.subjectCode.trim().takeIf { it.isNotBlank() }
            value?.let { ReviewRequestFilterOption(value = it, label = it) }
        }.distinctFilterOptions()
    }
    val yearOptions = remember(requestClasses) {
        requestClasses.mapNotNull { classItem ->
            val value = classItem.yearLevel?.trim()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val label = ProfessorClassYearOptions
                .firstOrNull { it.value.equals(value, ignoreCase = true) }
                ?.label
                ?: academicGroupLabel(value)
            ReviewRequestFilterOption(value = value, label = label)
        }.distinctFilterOptions()
    }
    val departmentOptions = remember(requestClasses) {
        requestClasses.mapNotNull { classItem ->
            classItem.department?.trim()?.takeIf { it.isNotBlank() }?.let {
                ReviewRequestFilterOption(value = it, label = it)
            }
        }.distinctFilterOptions()
    }
    val sectionOptions = remember(requestClasses) {
        requestClasses.mapNotNull { classItem ->
            classItem.section?.trim()?.takeIf { it.isNotBlank() }?.let {
                ReviewRequestFilterOption(value = it, label = it)
            }
        }.distinctFilterOptions()
    }

    LaunchedEffect(classIds) {
        onLoadJoinRequests(classIds)
    }

    PanthraaPullRefresh(
        isRefreshing = RefreshSurface.JoinRequests in uiState.refreshingSurfaces,
        onRefresh = { onRefreshJoinRequests(classIds) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .panthraaScreenBackground(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 12.dp,
                start = 14.dp,
                end = 14.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ProfessorPageHeader(
                    title = "Review requests",
                    subtitle = if (requests.isEmpty()) {
                        "All pending student requests"
                    } else {
                        "${requests.size} pending across ${requestClassIds.size} ${if (requestClassIds.size == 1) "class" else "classes"}"
                    },
                    onBack = onBack,
                )
                if (internetRequired) {
                    InternetRequiredHint()
                }
            }

            if (requests.isNotEmpty()) {
                item {
                    ProfessorReviewRequestFiltersPanel(
                        totalCount = requests.size,
                        visibleCount = filteredRequests.size,
                        filters = filters,
                        subjectOptions = subjectOptions,
                        yearOptions = yearOptions,
                        departmentOptions = departmentOptions,
                        sectionOptions = sectionOptions,
                        onFiltersChange = { filters = it },
                    )
                }
            }

            when {
                uiState.isLoadingJoinRequests -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        PanthraaLoadingAnimation(size = 144.dp)
                    }
                }

                requests.isEmpty() -> item {
                    EmptyClassState("No pending student requests across your classes.")
                }

                filteredRequests.isEmpty() -> item {
                    NoMatchingReviewRequests(onClearFilters = { filters = ProfessorReviewRequestFilters() })
                }

                else -> items(filteredRequests, key = { it.id }) { request ->
                    JoinRequestCard(
                        request = request,
                        classIdsToRefresh = classIds,
                        isUpdating = uiState.isUpdatingJoinRequest,
                        internetRequired = internetRequired,
                        onApproveRequest = onApproveRequest,
                        onRejectRequest = onRejectRequest,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfessorReviewRequestFiltersPanel(
    totalCount: Int,
    visibleCount: Int,
    filters: ProfessorReviewRequestFilters,
    subjectOptions: List<ReviewRequestFilterOption>,
    yearOptions: List<ReviewRequestFilterOption>,
    departmentOptions: List<ReviewRequestFilterOption>,
    sectionOptions: List<ReviewRequestFilterOption>,
    onFiltersChange: (ProfessorReviewRequestFilters) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Filter requests",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = if (filters.hasActiveFilters) {
                            "Showing $visibleCount of $totalCount"
                        } else {
                            "Showing all $totalCount"
                        },
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                    )
                }
                if (filters.hasActiveFilters) {
                    TextButton(onClick = { onFiltersChange(ProfessorReviewRequestFilters()) }) {
                        Text("Clear all", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReviewRequestFilterField(
                    label = "Subject",
                    allLabel = "All subjects",
                    selectedValue = filters.subject,
                    options = subjectOptions,
                    onSelect = { onFiltersChange(filters.copy(subject = it)) },
                    modifier = Modifier.weight(1f),
                )
                ReviewRequestFilterField(
                    label = "Year",
                    allLabel = "All years",
                    selectedValue = filters.year,
                    options = yearOptions,
                    onSelect = { onFiltersChange(filters.copy(year = it)) },
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReviewRequestFilterField(
                    label = "Department",
                    allLabel = "All departments",
                    selectedValue = filters.department,
                    options = departmentOptions,
                    onSelect = { onFiltersChange(filters.copy(department = it)) },
                    modifier = Modifier.weight(1f),
                )
                ReviewRequestFilterField(
                    label = "Section",
                    allLabel = "All sections",
                    selectedValue = filters.section,
                    options = sectionOptions,
                    onSelect = { onFiltersChange(filters.copy(section = it)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ReviewRequestFilterField(
    label: String,
    allLabel: String,
    selectedValue: String?,
    options: List<ReviewRequestFilterOption>,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options
        .firstOrNull { it.value.equals(selectedValue, ignoreCase = true) }
        ?.label
        ?: selectedValue
        ?: allLabel
    val enabled = options.isNotEmpty()

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .clickable(enabled = enabled) { expanded = true },
            color = if (selectedValue == null) Color(0xFFF8FAFC) else Color(0xFFF2F5FF),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (selectedValue == null) Color(0xFFD8E1EC) else Color(0xFF9BB0FF),
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        maxLines = 1,
                    )
                    Text(
                        text = selectedLabel,
                        color = if (enabled) Color(0xFF0F172A) else Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "$label filter",
                    tint = if (enabled) Color(0xFF475569) else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = allLabel,
                        fontWeight = if (selectedValue == null) FontWeight.ExtraBold else FontWeight.Medium,
                    )
                },
                onClick = {
                    onSelect(null)
                    expanded = false
                },
            )
            options.forEach { option ->
                val selected = option.value.equals(selectedValue, ignoreCase = true)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = if (selected) PanthraaBlue else Color(0xFF0F172A),
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        )
                    },
                    onClick = {
                        onSelect(option.value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun NoMatchingReviewRequests(onClearFilters: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "No requests match these filters.",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Try another combination or clear the filters to see the full queue.",
                color = Color(0xFF475569),
                fontSize = 13.sp,
            )
            TextButton(
                onClick = onClearFilters,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Clear filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun List<ReviewRequestFilterOption>.distinctFilterOptions(): List<ReviewRequestFilterOption> {
    return distinctBy { it.value.trim().lowercase(Locale.ROOT) }
        .sortedBy { it.label.lowercase(Locale.ROOT) }
}
