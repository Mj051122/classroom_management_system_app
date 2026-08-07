# Architecture Map

## Flow

1. `MainActivity.kt` starts the app.
2. `auth/AuthScreen.kt`, `auth/AuthViewModel.kt`, and `auth/AuthRepository.kt` handle login/signup.
3. `ui/MainScreen.kt` owns the app shell, bottom tabs, and top-level navigation.
4. `ui/MainViewModel.kt` coordinates loading, refresh, mutations, and selected state.
5. Repositories in `data/` talk to Supabase RPCs/storage.

## Main Files

- `ui/MainScreen.kt` - app shell, bottom tabs, top-level navigation, dashboard reset behavior.
- `ui/PanthraaSharedUi.kt` - shared Compose primitives, loader, pull-to-refresh, notices, avatars, dialogs, shared colors.
- `ui/DashboardScreens.kt` - student/professor dashboards, profile detail views, dashboard schedule card, pending assignments, grades.
- `ui/ClassesScreens.kt` - classes flow, professor hierarchy, class detail pages, assignments, submissions, attendance, join requests.
- `ui/TasksMenuScreens.kt` - news feed/tasks, menu/settings/profile helpers, classmates/students pages, announcements.
- `ui/MainViewModel.kt` - state orchestration and repository calls.
- `ui/MainUiState.kt` - shared state and `RefreshSurface`.
- `data/ClassRepository.kt` - classes, assignments, submissions, grades, attendance, join requests.
- `data/AnnouncementRepository.kt` - announcements/news feed.
- `data/AppUserRepository.kt` - people/profile reads.
- `data/SupabaseReadGuard.kt` - cache/coalescing.
- `model/ClassAssignment.kt` - assignment/submission models.

## UI File Ownership

- Keep `MainScreen.kt` focused on app-level navigation and shell behavior.
- Put shared reusable UI in `PanthraaSharedUi.kt` only when more than one feature needs it.
- Put dashboard-only changes in `DashboardScreens.kt`.
- Put class/assignment/attendance/join-request changes in `ClassesScreens.kt`.
- Put tasks/news/menu/profile-list/announcement changes in `TasksMenuScreens.kt`.
- Avoid adding new feature screens back into `MainScreen.kt`.

## Dashboard Ownership Notes

- Student and professor dashboard schedule cards currently live in `ui/DashboardScreens.kt`.
- Professor schedule helpers: `professorDashboardTodaySchedule`, `ProfessorTodayScheduleCard`, and shared dashboard schedule helpers.
- Student schedule helpers: `studentDashboardTodaySchedule`, `StudentTodayScheduleCard`, and shared dashboard schedule helpers.
- Both schedule cards depend on class `scheduleDays`, `scheduleStartTime`, and `scheduleEndTime`; no extra SQL/table is required.
- Dashboard shortcut navigation is handled in `MainScreen` with a dedicated dashboard navigation helper so Android Back returns to Dashboard.

## Class Workspace Notes

- Professor class workspace layout, upload feed rows, and upload empty state live in `ui/ClassesScreens.kt` near `ProfessorClassroomPage`.
