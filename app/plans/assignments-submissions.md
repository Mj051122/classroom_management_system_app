# Assignments And Submissions

## Goal

Professor uploads are optional. Student submissions always require a file matching the professor-selected format.

## Key Files

- `model/ClassAssignment.kt`
- `data/ClassRepository.kt`
- `ui/MainViewModel.kt`
- `ui/MainScreen.kt`
- `../supabase_submission_format_file_required_patch.sql`

## Current Behavior

- Professor creates title/instructions.
- Professor file upload is optional.
- Professor chooses required student submission format.
- Student notes are optional.
- Student file is required.
- Client validates file format before upload.
- `submit_assignment` rejects missing file URL in Supabase.
- Professor can review submitted file links.

## Supported Formats

- `pdf`
- `docx`
- `pptx`
- `xlsx`
- `image`
- `zip`

## Storage

- Bucket: `assignment-files`
- Expected public read.
- MIME list must support PDF, Word, PowerPoint, Excel, images, ZIP, and octet-stream fallback.

## Change Checklist

When adding a new format:

- Add UI option in `MainScreen.kt`.
- Add validation in `ClassRepository.kt`.
- Add SQL check value.
- Add storage MIME type.
- Update `supabase-database.md`.
