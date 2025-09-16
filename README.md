# Screen Timeout Tile

A lightweight Android app that adds a Quick Settings tile for rapidly switching the system screen timeout between a short preset (30 seconds) and a long preset (10 minutes). The companion activity helps you grant the required permission and shows the currently applied timeout.

## Features
- Quick Settings tile labeled "Screen Timeout" with dynamic subtitle showing the active timeout.
- Toggles between 30 s and 10 min using `Settings.System.SCREEN_OFF_TIMEOUT`.
- Prompts the user for **Modify system settings** permission when this access is missing.
- Home activity displays permission status, the current timeout value, and a shortcut to the system settings screen.

## Getting Started
1. Open the project in Android Studio (Giraffe or newer) with the Android Gradle Plugin 8.13.0 and Kotlin 2.0.21 toolchain.
2. Connect a device or start an emulator running Android 7.0 (API 24) or newer.
3. Run `./gradlew assembleDebug` or use **Run ▶ Run 'app'** in Android Studio to install the debug build.

## Using the Tile
1. Launch the app and tap **Open system settings** to grant "Modify system settings" access.
2. Open Quick Settings, tap the edit/pencil button, and drag the **Screen Timeout** tile into an active slot.
3. Tap the tile:
   - Without permission: the tile stays inactive and the activity highlights the missing access.
   - With permission: the tile flips between 30-second and 10-minute timeouts and updates its subtitle accordingly.

## Testing & Verification
- JVM unit tests: `./gradlew test`.
- Instrumented tests (optional): `./gradlew connectedAndroidTest` with a device attached.

## Project Structure
- `app/src/main/java/com/stonecode/screentimeouttile/`: Activity, tile service, and timeout controller classes.
- `app/src/main/res/layout/activity_main.xml`: Simple UI guiding permission setup.
- `app/src/main/AndroidManifest.xml`: Declares the tile service and WRITE_SETTINGS permission.
