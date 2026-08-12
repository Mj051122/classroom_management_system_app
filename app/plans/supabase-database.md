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
- `../../supabase_auth_phone_reset_patch.sql` (auth patch; idempotent, apply to any existing database)

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

`public.app_users`:

- `phone_number text` (unique when non-blank; required at registration)

## Required RPCs

- `register_app_user(text, text, text, text, text)` taking `p_phone_number` (validates PH format, raises on duplicate phone)
- `reset_app_user_password(text, text, text)` matching ID number + registered phone before setting a new password
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

### 2026-08-10 - Auth: phone number on registration + password reset

- Status: SQL authored and app-aligned; NOT yet applied to project `ulxbeelcvbawkpcutaom` (apply `supabase_auth_phone_reset_patch.sql` in the Supabase SQL editor to deploy).
- Forward SQL: `../../supabase_auth_phone_reset_patch.sql`
  - SHA-256: `0F07D3449A2A74FEE08A6FB2A04AB86B26226A9F58B8F3F1C124B538EB4F6F64`
  - Size: 4,470 bytes; 131 lines.
- Pre-change database rollback backup: not taken yet. Take a backup or export `public.app_users` before applying if existing phone values must be preserved.
- Application method: paste the patch into the Supabase SQL editor and run (single script, idempotent). Ends with `notify pgrst, 'reload schema';`.
- Database changes:
  - Ensured `public.app_users.phone_number text` exists (`add column if not exists`).
  - Added partial unique index `app_users_phone_number_key` (unique only when phone is non-blank, so legacy rows without phones are untouched).
  - Replaced `register_app_user` with a 5-argument version that stores and validates `p_phone_number` (`09XXXXXXXXX` or `+639XXXXXXXXX`), raising `Phone number already registered.` on duplicate.
  - Added `reset_app_user_password(p_id_number, p_phone_number, p_new_password)`; verifies ID + phone match and account is not blocked, then sets the trimmed new password. Single generic error message does not reveal which accounts exist.
  - Kept plaintext (trimmed) password storage to match the current scheme; hashing is a separate future migration.
- Mirror files kept in sync so future resets do not diverge: `supabase_fresh_reset_schema.sql` (drop list, table, index, both RPCs) and `supabase_login_fix.sql` (register RPC signature).
- Android changes:
  - `../src/main/java/com/myapplication/panthraa/auth/AuthUiState.kt` adds `phone` and `AuthMode.ForgotPassword`.
  - `../src/main/java/com/myapplication/panthraa/auth/AuthRepository.kt` passes phone to `register` and adds `resetPassword`.
  - `../src/main/java/com/myapplication/panthraa/auth/AuthViewModel.kt` adds phone state/validation, a forgot-password submit flow, and user-friendly error mapping.
  - `../src/main/java/com/myapplication/panthraa/auth/AuthScreen.kt` adds the phone field (register), the forgot-password view, and a "Forgot password?" link, reusing the existing card/field/button design.
  - `../src/main/java/com/myapplication/panthraa/MainActivity.kt` wires the new callbacks.
  - `../src/test/java/com/myapplication/panthraa/auth/AuthPhoneValidationTest.kt` covers phone format validation.
- Verification:
  - Android `testDebugUnitTest assembleDebug` passed: 30 tests, 0 failures, 0 errors, 0 skipped. 
  - SQL patch is idempotent and was reviewed against `supabase_fresh_reset_schema.sql` and `supabase_login_fix.sql` for signature consistency.
  - Live Supabase behavior test not yet performed (patch not yet applied).
- Intentionally unchanged: UUID `app_users.id` generation, login semantics, RLS, and all assignment/grading/attendance RPCs.

### 2026-08-12 - One account per ID number

- Status: SQL authored and app-aligned; NOT yet applied to project `ulxbeelcvbawkpcutaom` (run SECTION 1 audit first, then apply sections 3-4 of `supabase_id_number_unique_patch.sql` in the Supabase SQL editor; SECTION 2 only if the audit finds duplicates).
- Forward SQL: `../../supabase_id_number_unique_patch.sql`
- Application method: paste into the Supabase SQL editor, section by section. Ends with `notify pgrst, 'reload schema';`.
- Database changes:
  - Added partial unique index `app_users_id_number_ci_key` (unique on `lower(trim(id_number))` only when non-blank, so rows without ID numbers are untouched).
  - Hardened `complete_email_registration` with an explicit duplicate-ID guard that raises `ID number is already registered. Log in with the email account that owns it.` before the index would throw a raw unique-violation error.
  - The unique index remains the hard guard for concurrent registrations racing past the friendly check.
- Android changes:
  - `../src/main/java/com/myapplication/panthraa/auth/AuthViewModel.kt` maps the RPC's duplicate message and Postgres `unique_violation` to a user-friendly screen message.
- Mirror files kept in sync so future resets do not diverge: `supabase_id_number_unique_patch.sql` is the canonical forward file; rollback is `drop index app_users_id_number_ci_key;` plus re-running the previous `complete_email_registration` from the staged migration.
- Verification:
  - Android `:app:compileDebugKotlin` and `:app:compileDebugUnitTestKotlin` pass after the Kotlin change.
  - Live Supabase audit and application not yet performed.
- Intentionally unchanged: auth users table, phone/email indexes, login semantics, RLS, and all assignment/grading/attendance RPCs.

### 2026-08-12 - Pre-flight ID check before verification email

- Status: SQL authored and app-aligned; NOT yet applied to project `ulxbeelcvbawkpcutaom` (run SECTION 5 of `supabase_id_number_unique_patch.sql` in the Supabase SQL editor to deploy).
- Forward SQL: `../../supabase_id_number_unique_patch.sql` (SECTION 5 only; sections 1-4 already applied).
- Application method: paste the SECTION 5 block into the Supabase SQL editor and run. Ends with `notify pgrst, 'reload schema';`.
- Database changes:
  - Added `check_id_number_available(p_id_number)` — returns `true` when the ID is free, raises the friendly duplicate message when it is taken. Granted to `anon` and `authenticated`.
- Android changes:
  - `../src/main/java/com/myapplication/panthraa/auth/AuthRepository.kt` `beginRegistration` now calls `check_id_number_available` **before** `signUpWith(Email)`, so a taken ID is rejected before any verification email is sent and no stranded `auth.users` entry is created.
  - `../src/main/java/com/myapplication/panthraa/auth/AuthViewModel.kt` passes the ID number into `beginRegistration`.
- Verification:
  - Android `:app:compileDebugKotlin` and `:app:compileDebugUnitTestKotlin` pass after the Kotlin change.
  - Live Supabase behavior test not yet performed (patch not yet applied).
- Intentionally unchanged: `complete_email_registration` duplicate guard, unique index, login semantics, RLS.

## Safety Rules

- Keep RLS enabled on exposed public tables.
- Do not expose service role keys in Android code.
- Prefer RPCs for ownership-sensitive writes.
- After RPC signature changes, run:

```sql
notify pgrst, 'reload schema';
```
