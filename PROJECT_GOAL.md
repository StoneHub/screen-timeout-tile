# Screen Timeout Quick Settings Toggle App

## Goal
A minimal Android app that adds a Quick Settings tile. Tapping the tile toggles the device screen timeout between short (e.g., 30 seconds) and long (e.g., 10 minutes).

---

## Features
* Adds a Quick Settings tile labeled **Screen Timeout**.
* On tap, reads the current timeout value and toggles between two presets.
* Updates tile state when toggled.
* Requests **Modify system settings** permission if not yet granted.
* On Android 13+, requests direct tile placement through the system prompt.

---

## Architecture
* **TileService** (`TimeoutTileService`) implements the Quick Settings tile behavior.
* **Settings.System API** reads and writes `SCREEN_OFF_TIMEOUT`.
* **MainActivity** can serve as a landing page (optional).

### Permissions
* Requires `WRITE_SETTINGS` permission.
* User must manually approve in system settings (cannot be auto-granted).

---

## Data Flow
1. User taps Quick Settings tile.
2. `TimeoutTileService.onClick()` triggers.
3. Service checks if the app can write system settings.
   * If not, launches system screen to request it.
4. If granted, service reads current `SCREEN_OFF_TIMEOUT`.
5. Compares against threshold (e.g., > 30 seconds).
6. Writes new timeout (30 seconds or 10 minutes).
7. Updates tile appearance.

---

## Key Classes
### `TimeoutTileService`
* Extends `TileService`.
* Handles `onClick` to toggle timeout.
* Uses `qsTile.state` to reflect ON/OFF.

### `MainActivity`
* Provides the setup entry point if user opens the app from the launcher.
* Shows permission status, current timeout, next action, and tile-placement controls.
* Uses Android 13+ direct tile placement when available, with manual instructions as fallback.

---

## Manifest
The production manifest is the source of truth. It declares only the protected
`WRITE_SETTINGS` permission, the exported launcher activity, and the exported
tile service guarded by `BIND_QUICK_SETTINGS_TILE`. App backup is disabled
because the app stores no user data.

---

## Implementation
`TimeoutTileService` owns the Android tile lifecycle, while
`ScreenTimeoutController`, `ScreenTimeoutSettingsStore`, and
`TimeoutTogglePolicy` keep settings access and toggle behavior independently
testable. Refer to the production Kotlin sources instead of duplicating an
implementation sample here.

---

## User Flow
1. Install the app.
2. Pull down the Quick Settings panel.
3. Tap the edit button and add the **Screen Timeout** tile.
4. Tap the tile:
   * If permission not yet granted → app prompts to allow modifying system settings.
   * If granted → screen timeout toggles instantly.

---

## Future Improvements
* More than two timeout presets (cycle through list).
* Preset labels or names beyond the current timeout subtitle.
* Option to auto-reset after a period.
* Settings screen for custom timeout values.

---

## Deployment Checklist
* [x] Implement `TimeoutTileService`.
* [x] Add manifest entries.
* [ ] Test on API 24-32 devices for manual tile placement.
* [ ] Test on API 33+ devices for direct tile placement.
* [x] Handle permission flow.
* [x] Build and verify the release APK/AAB for version 1.1.0.
