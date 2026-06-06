# Screen Timeout Tile

A lightweight Android app that adds a Quick Settings tile for rapidly switching the system screen timeout between a short preset (30 seconds) and a long preset (10 minutes). The companion activity helps you grant the required permission and shows the currently applied timeout.

## Features
- Quick Settings tile labeled "Screen Timeout" with dynamic subtitle showing the active timeout.
- Toggles between 30 s and 10 min using `Settings.System.SCREEN_OFF_TIMEOUT`.
- Prompts the user for **Modify system settings** permission when this access is missing.
- Home activity displays permission status, the current timeout value, and the next setup action.
- On Android 13+, the home activity can ask the system to add the Quick Settings tile directly.
- On Android 7-12, the home activity keeps manual Quick Settings edit instructions visible.

## Getting Started
1. Open the project in Android Studio (Giraffe or newer) with the Android Gradle Plugin 8.13.0 and Kotlin 2.0.21 toolchain.
2. Connect a device or start an emulator running Android 7.0 (API 24) or newer.
3. Run `./gradlew assembleDebug` or use **Run ▶ Run 'app'** in Android Studio to install the debug build.

## Setup Flow
1. Launch the app and tap **Open system settings** to grant "Modify system settings" access.
2. Add the tile:
   - Android 13+: tap **Add Quick Settings tile** and accept the system prompt.
   - Android 7-12: open Quick Settings, tap edit/pencil, and drag **Screen Timeout Tile** into an active slot.
3. Tap the Quick Settings tile:
   - Without permission: the tile stays inactive and the activity highlights the missing access.
   - With permission: the tile flips between 30-second and 10-minute timeouts and updates its subtitle accordingly.

## Testing & Verification
- Focused JVM unit tests: `./gradlew :app:testDebugUnitTest`.
- Instrumented tests (optional): `./gradlew connectedAndroidTest` with a device attached.
- If the command cannot find the SDK in a shell, set `ANDROID_HOME=/Users/monroe/Library/Android/sdk` for the command or create a local, untracked `local.properties`.

## Manual Verification Checklist
- Fresh install opens the setup screen.
- Without **Modify system settings**, the tile routes to the permission flow and stays inactive.
- With permission granted, tapping the tile switches between 30 seconds and 10 minutes.
- On Android 13+, **Add Quick Settings tile** shows the system placement prompt and reports the result.
- On Android 7-12, the setup screen shows manual Quick Settings edit instructions.

## Project Structure
- `app/src/main/java/com/stonecode/screentimeouttile/`: Activity, tile service, and timeout controller classes.
- `app/src/main/res/layout/activity_main.xml`: Simple UI guiding permission setup.
- `app/src/main/AndroidManifest.xml`: Declares the tile service and WRITE_SETTINGS permission.
