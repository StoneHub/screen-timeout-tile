# Screen Timeout Tile

Screen Timeout Tile is a small Android app that adds a Quick Settings tile for switching the device screen timeout between **30 seconds** and **10 minutes**.

It is built for the common Android workflow where you want the screen to stay awake while reading, debugging, cooking, presenting, or using your phone as a reference display, but you still want a quick way back to a short timeout afterward.

## What It Does

- Adds a **Screen Timeout Tile** to Android Quick Settings.
- Toggles `Settings.System.SCREEN_OFF_TIMEOUT` between 30 seconds and 10 minutes.
- Shows the active timeout in the tile subtitle.
- Opens the Android **Modify system settings** permission screen when access is missing.
- Includes a setup screen for permission status, current timeout, and tile placement.
- Supports Android 13+ direct tile placement through the system prompt.
- Keeps Android 7-12 manual Quick Settings edit instructions available.

## Why This Exists

Android has the screen timeout setting buried in system settings. This project makes that setting available as a single Quick Settings tap without adding accounts, analytics, networking, or background services.

## Requirements

- Android 7.0 or newer, API 24+
- Android Studio Giraffe or newer recommended
- Android Gradle Plugin 8.13.0
- Kotlin 2.0.21
- Java 11 toolchain

## Install A Debug Build

```bash
./gradlew assembleDebug
./gradlew installDebug
```

If your shell cannot find the Android SDK, set the SDK path for the command:

```bash
ANDROID_HOME=/Users/monroe/Library/Android/sdk ANDROID_SDK_ROOT=/Users/monroe/Library/Android/sdk ./gradlew installDebug
```

## Setup On A Device

1. Launch **Screen Timeout Tile**.
2. Tap **Open system settings**.
3. Grant **Modify system settings** access for the app.
4. Add the Quick Settings tile:
   - Android 13+: tap **Add tile** and accept the system prompt.
   - Android 7-12: open Quick Settings edit mode and drag the tile into an active slot.
5. Tap the tile to switch between 30 seconds and 10 minutes.

## Permission And Privacy

The app requests only:

```xml
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
```

That permission is required because Android protects writes to `Settings.System.SCREEN_OFF_TIMEOUT`. The user must grant it manually from Android system settings.

This app does not declare internet access, does not create accounts, does not run analytics, and does not collect personal data.

## Development

Run focused JVM tests:

```bash
./gradlew :app:testDebugUnitTest
```

Run the broader local build:

```bash
./gradlew build
```

Run instrumentation tests with a connected device or emulator:

```bash
./gradlew connectedAndroidTest
```

Before publishing a release build, also run:

```bash
./gradlew :app:lintRelease
./gradlew :app:bundleRelease
```

## Project Structure

```text
app/src/main/java/com/stonecode/screentimeouttile/
  MainActivity.kt                  Setup screen and tile placement flow
  TimeoutTileService.kt            Quick Settings tile implementation
  ScreenTimeoutController.kt       Reads and writes the Android timeout setting
  ScreenTimeoutSettingsStore.kt    Settings.System abstraction
  TimeoutTogglePolicy.kt           30-second / 10-minute toggle policy
  TimeoutLabelFormatter.kt         Human-readable timeout labels

app/src/main/res/
  layout/activity_main.xml         Setup screen layout
  drawable/                        Quick Settings and launcher icon assets
  values/strings.xml               User-facing strings
  xml/                             Backup and data extraction rules

app/src/test/java/                 JVM unit tests
app/src/androidTest/java/          Instrumentation smoke tests
```

## Current Status

The core tile behavior, setup screen, Android 13+ tile request flow, and JVM tests are in place. Release packaging and Play Store publishing are still tracked in [docs/release-todo.md](docs/release-todo.md).

## Contributing

Issues and pull requests are welcome. Useful contributions include:

- Testing tile behavior on different Android versions and OEM skins.
- Improving accessibility and large-font layout behavior.
- Adding release engineering for signed builds.
- Tightening the privacy policy and store listing docs.

For code changes, keep behavior covered by JVM tests where possible and use instrumentation tests for device-specific setup or tile-placement flows.

## License

No license has been selected yet. Until a license is added, this public repository is available for reading and contribution, but reuse rights are not formally granted.
