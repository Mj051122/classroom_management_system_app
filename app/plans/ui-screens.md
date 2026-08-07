# UI Screens

## Key File

- `ui/MainScreen.kt`

## Major Screens

- Dashboard/home
- Classes list
- Class detail
- Assignments/tasks/materials
- Pending assignments
- Grades
- Announcements/news feed
- People list
- Classmates/students
- Submissions
- Attendance
- Join requests
- Menu/profile/settings

## UX Rules

- Do not add new refresh buttons unless requested.
- Pull-to-refresh is the main refresh UX.
- Keep existing manual refresh buttons only where already present.
- Use existing loader animation where possible.
- Keep content visible during refresh.
- Pull-to-refresh should feel like the screen content is physically pulled down; avoid floating old-style refresh pills.
- Keep headers fixed when refreshing only content unless a screen intentionally uses full-content pull motion.
- Classes root should not show a second in-content loader while pull-to-refresh is active; use the shared pull indicator only.
- Pull refresh indicators should align across dashboard and classes, preserve the app's Lottie loader, and avoid an extra card background behind the animation.
- Professor class-management cards should stay restrained: clean white surfaces, small accent details, clear counts, and no decorative blur layers over content.
- Create Class should use inline fixed-option grids for common Course and Section values, with custom add kept as the escape hatch instead of database-driven dropdown menus.
- Professor Work Queue uses "Grade monitor" as the grade shortcut; it opens a student-first monitor with subject grade rows and a proof detail screen for scores, submissions, files, and timestamps.
- Keep dimensions stable for cards, buttons, tabs, and repeated rows.
- Avoid redesigning navigation unless requested.

## Dashboard Schedule UX

- Student and professor dashboards show a current-day schedule card near the top of the dashboard.
- The schedule card follows the dashboard card system: `Today's schedule` section header, an academic white/slate rounded surface, and only actual class schedule rows inside the card.
- The card should not show duplicate day labels or placeholder `upcoming/current/previous` rows.
- Current classes use a restrained blue state, not green.
- Each scheduled row must include the subject/class name and the schedule duration/time range.
- Tapping a scheduled row opens that exact class/subject.
- When there are no classes today, show one compact empty state without navigation affordances.
- Professor schedule uses `ProfessorTodayScheduleCard`, `professorDashboardTodaySchedule`, and the shared dashboard schedule card helpers.
- Student schedule uses `StudentTodayScheduleCard`, `studentDashboardTodaySchedule`, and the shared dashboard schedule card helpers.

## Dashboard Navigation

- Dashboard shortcuts into Classes or News Feed must preserve Android Back behavior.
- Expected flow: Dashboard -> shortcut target -> Android Back -> Dashboard.
- Do not use tab-state restoration for dashboard shortcuts if it causes Back to skip the dashboard.
- Bottom navigation tab taps may still use normal tab state restoration.
- Dashboard tab taps are special: they must clear dashboard/class drill-in state and return to the dashboard root instead of restoring a nested subpage.

## Professor Class Workspace

- Opening a professor class uses a single scrollable workspace, not a fixed header plus nested list.
- The workspace starts with a white rounded hero card containing back navigation, class identity, student/upload/join-code stats, and the primary upload action.
- Uploads render as a class feed with compact white activity rows, status chips, type icons, and a clear attendance action for assignments.
- Empty uploads should show a helpful card with an upload action instead of a bare placeholder.
