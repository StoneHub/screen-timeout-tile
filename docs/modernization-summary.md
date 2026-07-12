# Screen Timeout Tile Modernization Summary

## What Changed

- Refactored timeout behavior behind `ScreenTimeoutSettingsStore` and pure policy/state models so core behavior is covered by JVM tests.
- Reworked the launcher setup screen into a compact dark UI that fits the tested phone at `font_scale=1.3`.
- Added Android 13+ direct Quick Settings tile placement from the setup screen.
- Added custom Quick Settings tile icons for short timeout, long timeout, and missing-permission states.
- Added target-SDK-36 edge-to-edge inset handling and accessible live status announcements.
- Added a dedicated short tile label, toggle metadata, and explicit unavailable state for read failures.
- Updated the project to JDK 17, AppCompat 1.7.1, and Material Components 1.14.0.
- Replaced placeholder unit/instrumentation tests with focused behavior and launch smoke tests.
- Updated README and project goal notes to match the current setup flow.

## Device Verification

- Device: `SM_F956U1`
- ADB serial: `RFCX61FNYPY`
- Package: `com.stonecode.screentimeouttile`
- Installed version: `versionName=1.0`, `versionCode=1`
- Visual authority: installed app on the device; local screenshots are intentionally not tracked.

## Checks Run

```bash
ANDROID_HOME=/Users/monroe/Library/Android/sdk ANDROID_SDK_ROOT=/Users/monroe/Library/Android/sdk ./gradlew :app:testDebugUnitTest :app:compileDebugAndroidTestKotlin :app:installDebug --console=plain
git diff --check
```
