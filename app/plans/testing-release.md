# Testing And Release

## Commands

Run from `C:\kotlin`:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

## Automated Coverage

- `src/test/java/com/myapplication/panthraa/data/SupabaseReadGuardTest.kt`
- `src/test/java/com/myapplication/panthraa/ExampleUnitTest.kt`

## Manual Checks

- Pull-to-refresh on all major screens.
- Rapid repeated pull.
- Offline pull, reconnect, pull again.
- Professor creates task with no file.
- Professor selects each submission format.
- Student wrong format shows error.
- Student correct format uploads successfully.
- Professor sees submitted file.

## Artifact

Debug APK:

- `build/outputs/apk/debug/app-debug.apk`
