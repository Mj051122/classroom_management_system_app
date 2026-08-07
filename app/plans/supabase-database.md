# Supabase Database

## Live Status

Last reviewed/applied from Codex on 2026-07-14.

- Project ref: `ulxbeelcvbawkpcutaom`
- Status: `ACTIVE_HEALTHY`
- Postgres: `17.6`
- Latest recorded migration: `20260713050932_fix_assignment_schedule_manila_time`
- Latest functional transaction: `supabase_functional_contract_fix.sql` (applied on 2026-07-14 through `execute_sql`; it is not an entry in Supabase migration history)

## Required Patches

- `../../supabase_read_optimization_batch_rpc_patch.sql`
- `../../supabase_submission_format_file_required_patch.sql`
- `../../supabase_assignment_schedule_manila_fix.sql`
- `../../supabase_functional_contract_fix.sql` (current contract; apply last)

## Required Tables

- `public.app_users`
- `public.classes`
- `public.class_enrollments`
- `public.class_join_requests`
- `public.class_assignments`
- `public.assignment_submissions`
- `public.class_announcements`
- `public.announcement_reads`
- `public.attendance`
- `storage.buckets`
- `storage.objects`

## Required Assignment Columns

`public.class_assignments`:

- `assignment_type text not null default 'task'`
- `file_url text`
- `submission_format text not null default 'pdf'`
- `requires_file boolean not null default true`

`public.assignment_submissions`:

- `submission_file_url text`
- `edit_attempts integer`
- `score integer`

## Required RPCs

- `create_class_assignment(..., p_assignment_type, p_submission_format, p_file_url, p_requires_file)`
- `update_class_assignment(..., p_assignment_type, p_submission_format, p_file_url, p_requires_file)`
- `get_class_assignments(uuid, uuid)` returning `submission_format` and `requires_file`
- `get_student_pending_assignments(uuid)` returning `submission_format` and `requires_file`
- `submit_assignment(uuid, uuid, text, text)` requiring a file URL only when the assignment has `requires_file = true`
- `delete_professor_class(uuid, uuid)` checking professor ownership before deletion
- `get_latest_assignment_statuses(uuid[])`
- `get_class_join_requests_for_classes(uuid[], uuid)`

## Assignment Schedule Contract

- Assignment `date` and `time` columns are Asia/Manila wall-clock values.
- RPCs use an explicit Asia/Manila clock; they do not depend on the database session timezone.
- A task is released exactly at its start boundary.
- A submission or attendance scan remains allowed exactly at its end boundary and closes immediately after it.
- A time without its matching date is ignored.
- Pending excludes unreleased and submitted work, excludes materials, and retains overdue unsubmitted work.
- Professor assignment lists keep scheduled uploads visible, while missing-work status excludes unreleased tasks.

## Change and Backup Ledger

For every future Supabase change, append a dated entry here. Record the project ref, forward SQL, pre-change rollback backup, SHA-256 checksums, application method or migration ID, affected rows, verification, and anything intentionally left unchanged. Never overwrite an older ledger entry.

### 2026-07-14 - Functional assignment contract repair

- Status: applied and verified on project `ulxbeelcvbawkpcutaom`.
- Forward SQL: `../../supabase_functional_contract_fix.sql`
  - SHA-256: `05bc9c8606467c13c5b34c863fd3b4859ed34439d4decb8d5e849008136e19bb`
  - Size: 22,522 bytes; 693 lines.
- Pre-change database rollback backup: `../../output/supabase_functional_backup_20260714_230912.sql`
  - SHA-256: `f42496abf568b03a32bb6e31443f4d1a14ec7eee07a685fd6449a3873bc633ab`
  - Size: 20,246 bytes; 460 lines.
  - Restores the five original assignment RPC definitions and removes the new column/signatures/delete RPC. Rolling back drops any `requires_file` values created after this repair.
- Application method: one explicit transaction through Supabase `execute_sql`, with lock/statement timeouts and a PostgREST schema reload notification. It was not added to Supabase migration history because the CLI was unavailable; the canonical forward and rollback SQL files above are the audit record.
- Database changes:
  - Added `public.class_assignments.requires_file boolean not null default true`.
  - Updated `create_class_assignment` and `update_class_assignment` to accept/store `p_requires_file`, appended after the prior arguments for compatibility.
  - Updated `get_class_assignments` and `get_student_pending_assignments` to return `requires_file` while preserving Asia/Manila release scheduling.
  - Updated `submit_assignment` so text-only submission works when `requires_file = false`, while file-required assignments still reject a blank file URL.
  - Added ownership-checked `delete_professor_class`; existing foreign keys perform the related-row cascade.
  - Preserved RPC execute access for `PUBLIC`, `anon`, `authenticated`, and `service_role`.
- Existing data impact: all 18 pre-existing assignments were backfilled to `requires_file = true`; no assignment rows were deleted. Two existing non-material submissions without files were preserved unchanged.
- Android changes:
  - `../src/main/java/com/myapplication/panthraa/ui/PendingAssignmentState.kt` now carries `requiresFile` when a pending item is mapped to a class assignment.
  - `../src/test/java/com/myapplication/panthraa/ui/PendingAssignmentStateTest.kt` verifies that a text-only pending assignment remains text-only after mapping.
- Verification:
  - Full SQL transaction dry-run completed and rolled back cleanly before application.
  - Live catalog verified the column, six RPC contracts, grants, and Asia/Manila logic.
  - A transactional live behavior test passed create, both student/professor reads, text-only submit, required-file rejection, update, and cascading class deletion; the test was rolled back, leaving zero temporary users/classes.
  - Direct PostgREST named-argument preflight matched the new schema and returned the expected ownership error rather than an RPC/schema-cache missing error.
  - Android `testDebugUnitTest assembleDebug` passed: 27 tests, 0 failures, 0 errors, 0 skipped. Debug APK: `../build/outputs/apk/debug/app-debug.apk`.
- Intentionally unchanged: authentication, users, RLS, storage policies, and backend authentication logic. Two unreferenced assignment storage objects were not deleted. Safe upload cleanup is deferred until the authentication/storage-policy review because adding public delete permission would be unsafe.

## Safety Rules

- Keep RLS enabled on exposed public tables.
- Do not expose service role keys in Android code.
- Prefer RPCs for ownership-sensitive writes.
- After RPC signature changes, run:

```sql
notify pgrst, 'reload schema';
```
