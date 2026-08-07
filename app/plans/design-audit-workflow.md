# Design Audit Workflow

Use this file when planning or running a whole-app UI/UX review for Panthraa. The goal is better clarity, reliability, and polish while preserving the current Material/Compose style.

## Skill Order

1. Inspect the target screen and existing Compose patterns first.
2. Use `ui-ux-pro-max` as the UX checklist: accessibility, contrast, spacing, touch targets, typography, layout, animation, and mobile behavior.
3. Use `impeccable` as the production polish pass: visual hierarchy, cognitive load, empty/error/loading states, edge cases, and copy quality.
4. Use `grill-me` as the hard-question pass before recommending changes.
5. Produce a prioritized backlog before editing UI code.

## Target Surfaces

- Dashboard
- Classes
- Class detail
- Assignments
- Student submission flow
- Grades
- News Feed
- People
- Attendance
- Join Requests
- Auth

## Audit Rules

- Do not do a full redesign unless explicitly requested.
- Do not add new refresh buttons; pull-to-refresh remains the primary refresh UX.
- Keep the existing loader animation and navigation model.
- Preserve Panthraa's current brand feel and Material/Compose structure.
- Improve screen-by-screen, starting with the highest-risk user flow.
- Prefer clarity, stability, and accessibility over trendy visual effects.

## Finding Format

Each finding should include:

- Severity: `P0`, `P1`, `P2`, or `P3`.
- Surface: affected screen or flow.
- Issue: what is wrong or risky.
- Fix: exact recommended change.
- Test: manual or automated check needed after implementation.

## Audit Focus

- Accessibility: contrast, labels, touch targets, dynamic text, safe areas.
- UX reliability: loading, offline, errors, empty states, duplicate actions.
- Visual polish: spacing rhythm, hierarchy, cards, buttons, typography, icon consistency.
- Mobile behavior: small phone, large phone, landscape, bottom nav clearance.
- Assignment flow: required file clarity, wrong-format errors, professor review state.

## Grill-Me Questions

Ask these before accepting any proposed UI change:

- Does this solve a real student/professor problem, or just make the screen look different?
- Can the user still complete the task offline, after failure, or after a slow refresh?
- Does this create a new gesture/button/state that competes with existing behavior?
- Does this improve the smallest phone layout without hurting larger screens?
- Is the implementation consistent with existing Compose patterns?
- What exact test proves this did not regress refresh, assignment submission, or navigation?

## Test Plan For Future UI Changes

After future UI changes, run from `C:\kotlin`:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Manual checks for changed screens:

- Small phone size.
- Normal phone size.
- Landscape where relevant.
- Offline state.
- Repeated pull-to-refresh.
- Loading, empty, error, and success states.

## Defaults

- Keep current Panthraa style.
- Avoid bold experimental visuals unless explicitly requested.
- First audit output should be a prioritized improvement list, not immediate UI rewrites.
