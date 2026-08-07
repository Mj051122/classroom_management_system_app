# Data Cache And Refresh

## Goal

Keep the app fast and reliable by avoiding duplicate Supabase reads while still allowing urgent user refresh.

## Key Files

- `data/SupabaseReadGuard.kt`
- `data/ClassRepository.kt`
- `data/AnnouncementRepository.kt`
- `data/AppUserRepository.kt`
- `ui/MainViewModel.kt`
- `ui/MainUiState.kt`
- `ui/MainScreen.kt`
- `src/test/java/com/myapplication/panthraa/data/SupabaseReadGuardTest.kt`

## Cache Rules

- Normal loads use `CachePolicy.USE_FRESH`.
- Pull-to-refresh and post-mutation verification use `CachePolicy.FORCE_REFRESH`.
- `SupabaseReadGuard` coalesces matching in-flight reads.
- Repository cache keys include `CACHE_SCHEMA_VERSION`.
- Mutations invalidate affected cache groups.
- Logout clears shared cache.

## Pull-To-Refresh Rules

- UI wrapper: `PanthraaPullRefresh`.
- Refresh state: `RefreshSurface` and `refreshingSurfaces`.
- Duplicate pulls for the same surface are ignored.
- Pull gesture should move the screen content down and reveal the loader space.
- Loader should use the existing Panthraa animation when refresh is active.
- Avoid small/floating refresh pills that do not feel connected to the pulled content.
- Refresh should only commit after the release threshold.
- Header should stay fixed when refreshing only content unless the screen intentionally uses full-content pull motion.
- Old content remains visible during refresh.

## Refresh Surfaces

- Dashboard
- Classes
- Assignments
- Pending assignments
- Grades
- Announcements/tasks
- People
- Classmates/students
- Submissions
- Attendance
- Join requests

## Do Not Break

- Failed refresh must keep old data.
- Offline refresh must not clear visible content.
- Do not accidentally launch duplicate reads from repeated Flow collection.
