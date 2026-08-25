# Release Signing

Release signing identities are intentionally stored outside the repository.

## Distribution identities

- GitHub APK updates use the existing Screen Timeout Tile release keystore documented below.
- Google Play will use [Play App Signing](https://developer.android.com/studio/publish/app-signing) for the store signing identity and a separate upload key for Monroe's submissions.
- The Play upload key has not been created or connected to this project yet.

Do not treat a signed GitHub APK, a signed Play upload bundle, and a Play-distributed artifact as the same proof gate.

## Local source of truth

- Keystore: `~/Library/Application Support/StoneHub/ScreenTimeoutTile/signing/screen-timeout-tile-release.jks`
- Alias: `screen-timeout-tile`
- Keychain service: `StoneHub Screen Timeout Tile release signing`
- Keychain account: `screen-timeout-tile`
- Certificate SHA-256: `EC:61:E0:4B:3C:48:E5:7B:25:A2:FC:A4:47:6F:D0:C7:8E:B6:73:CA:ED:E7:F4:88:04:00:8C:FC:DB:0E:F3:01`
- Certificate validity: August 18, 2026 through January 3, 2054

## Build a signed release

Load the secret from macOS Keychain without printing it:

```bash
export SCREEN_TIMEOUT_RELEASE_STORE_FILE="$HOME/Library/Application Support/StoneHub/ScreenTimeoutTile/signing/screen-timeout-tile-release.jks"
export SCREEN_TIMEOUT_RELEASE_KEY_ALIAS="screen-timeout-tile"
export SCREEN_TIMEOUT_RELEASE_STORE_PASSWORD="$(security find-generic-password -s 'StoneHub Screen Timeout Tile release signing' -a 'screen-timeout-tile' -w)"
export SCREEN_TIMEOUT_RELEASE_KEY_PASSWORD="$SCREEN_TIMEOUT_RELEASE_STORE_PASSWORD"
./gradlew :app:assembleRelease
unset SCREEN_TIMEOUT_RELEASE_STORE_FILE SCREEN_TIMEOUT_RELEASE_KEY_ALIAS SCREEN_TIMEOUT_RELEASE_STORE_PASSWORD SCREEN_TIMEOUT_RELEASE_KEY_PASSWORD
```

Verify the resulting APK with Android SDK `apksigner` before publishing it.

## Recovery requirement

Before publishing version 1.1.1 or any later GitHub update, verify a second encrypted backup of both the `.jks` keystore and its password in a separate physical location. This is a hard release blocker. GitHub-distributed updates must keep using this signing key.

Keep the Play upload key and its recovery record under the same two-backup rule once created. Never commit a keystore, password, or `release-signing.properties`.
