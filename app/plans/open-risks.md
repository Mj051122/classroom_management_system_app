# Open Risks

Protect these during future changes:

- Persisting elapsed-time timestamps across device reboots.
- Duplicating derived data such as pending assignments.
- Deleting valid cached rows during paginated refresh.
- Migrating old snapshots without proven ownership.
- Launching refreshes accidentally from repeated Flow collection.

## Current Mitigations

- `SupabaseReadGuard` coalesces duplicate reads.
- `RefreshSurface` blocks duplicate pull refresh per surface.
- Mutations invalidate affected cache groups.
- Pull refresh uses `CachePolicy.FORCE_REFRESH`.
- Visible data is kept during refresh, offline, and failure states.

## Future Guardrails

- Prefer source-of-truth rows over stored derived snapshots.
- Make ownership explicit before migrating/deleting rows.
- Avoid clearing UI lists at refresh start.
- Treat pagination refresh as merge/replace-by-owned-page, not blind deletion.
