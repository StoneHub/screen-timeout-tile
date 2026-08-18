# Release TODO

## Monroe To Do

- Pick the public app name: keep `Screen Timeout Tile` or choose a shorter store name.
- Decide whether the Play listing should be free-only, donation-supported, or paid.
- Create/confirm the Google Play Console app entry.
- Create a simple privacy policy page:
  - no ads
  - no analytics
  - no accounts
  - no network access
  - no personal data collected
  - app only changes `Settings.System.SCREEN_OFF_TIMEOUT` after the user grants Modify system settings
- Capture store screenshots on a real phone:
  - setup screen before permission
  - setup screen after permission
  - Quick Settings tile short-timeout mode
  - Quick Settings tile long-timeout mode
- Get a screenshot of the low-contrast active Quick Settings tile state if it still happens after the bold icon update.
- Write the store short description and full description.
- Decide whether to publish under `StoneHub`/`StoneCode` branding.
- Create an upload key and enable Play App Signing.

## Code / Release Engineering To Do

- [x] Run `./gradlew :app:lintRelease` for version 1.1.0 (zero errors on August 13, 2026).
- [x] Run `./gradlew :app:bundleRelease` for version 1.1.0.
- [x] Verify the generated release metadata and confirm the bundle remains unsigned without owner credentials.
- [x] Add signing setup that reads upload-key values from local, untracked Gradle properties.
- [x] Bump to `versionCode=2` and `versionName=1.1.0`.
- [x] Add release notes for 1.1.0.
- [x] Create the permanent GitHub APK signing key and store its password in macOS Keychain.
- [ ] Back up the GitHub APK signing keystore and Keychain password in a second secure location.
- [x] Build and verify the production-signed APK and AAB for version 1.1.0.
- [ ] Publish GitHub Release `v1.1.0` with the signed APK and checksums.
- Run manual QA on at least:
  - Samsung Android 16 phone
  - Android 13+ device for direct tile placement
  - Android 7-12 device or emulator for manual tile-placement fallback
- Confirm `WRITE_SETTINGS` behavior survives reboot and tile refresh.
- [x] Confirm the merged debug manifest has no internet permission or data collection SDK.

## Nice To Have Before Public Launch

- Better branded launcher foreground art.
- In-app privacy/permission explanation link.
- Tiny FAQ in README and store listing:
  - why the permission is needed
  - what setting changes
  - how to remove the tile
  - how to uninstall
- Optional website landing page with privacy policy and screenshots.
