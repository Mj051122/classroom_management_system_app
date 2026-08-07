# AI Safety Rules for This Repository

These instructions apply to every AI coding assistant working in this repository.

## Protect Existing Work

- Never delete, reset, replace, regenerate, or rewrite working code unless the user explicitly requests it.
- Never discard existing changes or "start over" as a shortcut.
- Treat all existing files and uncommitted changes as user-owned work.
- When the request is formatting-only, change whitespace and formatting only. Do not change identifiers, literals, expressions, control flow, imports, APIs, or behavior.

## No Unsafe Bulk Rewrites

- Never recursively rewrite source files for a simple replacement or formatting task.
- Never run `Get-ChildItem ... | Get-Content ... | Set-Content` over source code.
- Never use `Set-Content -NoNewline`, because it can concatenate every line and flatten a file.
- Do not use shell loops, regular-expression replacements, generated scripts, or IDE-wide replacement across multiple source files without first listing the exact target files and receiving explicit user approval.
- Use a targeted patch for each file. Preserve its encoding, line endings, final newline behavior, comments, and unrelated content.
- Apply and verify multi-file work one file at a time. Stop immediately if a file becomes one line, empty, unexpectedly smaller, or syntactically invalid.

## Required Safety Checks

Before a risky or multi-file edit:

1. Inspect the current files and identify the smallest possible edit set.
2. Tell the user exactly which files will change and whether any logic will change.
3. If version control cannot provide a rollback, create a timestamped backup of only the affected files.

After every edit:

1. Re-read the changed file and confirm package, imports, declarations, and line structure remain valid.
2. Confirm unrelated files were not modified.
3. Scan for empty or accidentally flattened source files.
4. Run the relevant tests and build when available.
5. Report exactly what changed and any verification that could not be completed.

## Database and Backend Protection

- Do not modify Supabase, SQL, migrations, RPC functions, RLS policies, database schemas, Edge Functions, authentication configuration, backend configuration, or production data without the user's explicit approval immediately before the change.
- Reading and diagnosing these areas is allowed, but clearly describe any proposed adjustment before applying it.
- Never execute destructive database commands or remote deployments unless the user explicitly authorizes the exact action.

## If Something Goes Wrong

- Stop making changes immediately.
- Do not attempt broad automatic repairs or additional rewrites.
- Preserve the damaged state and available history, create a backup, explain the exact command and affected files, and ask the user before recovery.

## Prohibited Incident Pattern

The following pattern previously flattened all Kotlin files and must never be used:

```powershell
Get-ChildItem -Recurse -Filter "*.kt" | ForEach-Object {
    (Get-Content $_.FullName) | Set-Content $_.FullName -NoNewline
}
```

For a small replacement, use a precise per-file patch and verify the file immediately afterward.
