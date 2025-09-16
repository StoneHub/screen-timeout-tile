# Screen Timeout Quick Settings Toggle App

## Goal
A minimal Android app that adds a Quick Settings tile. Tapping the tile toggles the device screen timeout between short (e.g., 30 seconds) and long (e.g., 10 minutes).

---

## Features
* Adds a Quick Settings tile labeled **Screen Timeout**.
* On tap, reads the current timeout value and toggles between two presets.
* Updates tile state when toggled.
* Requests **Modify system settings** permission if not yet granted.

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

### `MainActivity` (optional)
* Provides app entry point if user opens the app from the launcher.
* Can explain what the tile does and link to permission settings.

---

## Manifest
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission
        android:name="android.permission.WRITE_SETTINGS"
        tools:ignore="ProtectedPermissions" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.ScreenTimeoutTile">

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".TimeoutTileService"
            android:label="Screen Timeout"
            android:permission="android.permission.BIND_QUICK_SETTINGS_TILE">
            <intent-filter>
                <action android:name="android.service.quicksettings.action.QS_TILE" />
            </intent-filter>
        </service>

    </application>
</manifest>
```

---

## Example Implementation
```kotlin
@RequiresApi(Build.VERSION_CODES.N)
class TimeoutTileService : TileService() {

    override fun onClick() {
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            return
        }

        val current = Settings.System.getInt(
            contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            30000
        )

        val newTimeout = if (current > 30000) 30000 else 600000 // 30 s vs 10 min
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, newTimeout)

        qsTile.state = if (newTimeout > 30000) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
```

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
* Show current timeout in tile subtitle.
* Option to auto-reset after a period.
* Settings screen for custom timeout values.

---

## Deployment Checklist
* [ ] Implement `TimeoutTileService`.
* [ ] Add manifest entries.
* [ ] Test on API 24+ devices (Quick Settings available).
* [ ] Handle permission flow.
* [ ] Build release APK/AAB.
