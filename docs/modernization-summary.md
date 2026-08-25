# Screen Timeout Tile Modernization Summary

## What Changed

- Refactored timeout behavior behind `ScreenTimeoutSettingsStore` and pure policy/state models so core behavior is covered by JVM tests.
- Reworked the launcher setup screen into a compact UI that fits the tested phone at `font_scale=1.3`.
- Added Android 13+ direct Quick Settings tile placement from the setup screen.
- Added custom Quick Settings tile icons for short timeout, long timeout, and missing-permission states.
- Added target-SDK-36 edge-to-edge inset handling and accessible live status announcements.
- Added a dedicated short tile label, toggle metadata, and explicit unavailable state for read failures.
- Updated the project to JDK 17, AppCompat 1.7.1, and Material Components 1.14.0.
- Replaced placeholder unit/instrumentation tests with focused behavior and launch smoke tests.
- Updated README and project goal notes to match the current setup flow.

## Historical Version 1.0 Device Verification

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

## August 13, 2026 Production Hardening

- Version `1.1.0` / code `2` uses a separate `.debug` application ID for dogfood installs.
- Missing settings values remain unavailable instead of silently becoming 30 seconds.
- OEM settings/tile-request failures and timeout write failures are surfaced without crashing.
- Permission rationale, tile state accessibility, local release-signing plumbing, CI, privacy policy, and wrapper checksum verification were added.
- Logged JVM tests, Android-test compilation, release lint, debug assembly, and unsigned release bundling passed.
- The exact debug APK was installed and cold-launched on Gina’s `SM-F766U1`; visual QA, permission approval, and tile placement remain blocked by the phone lock/human-consent gate.

## August 24, 2026 Guided Setup

- Replaced the combined status panel with the selected three-step guided setup screen.
- Kept the existing `SetupUiState` and `ScreenTimeoutController` interfaces. The change is presentation-only.
- Preserved Android 13+ direct tile placement and Android 7-12 manual tile instructions.
- Fixed the instrumentation package assertion for the isolated `.debug` application ID.
- Verified production Kotlin and Android-test compilation with the documented local Android SDK.
