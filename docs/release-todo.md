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

- Run `./gradlew :app:lintRelease`.
- Run `./gradlew :app:bundleRelease`.
- Verify the generated release artifact.
- Add a signing setup that reads upload-key values from local, untracked Gradle properties.
- Bump `versionCode` and `versionName` before every Play upload.
- Add release notes for the first public build.
- Run manual QA on at least:
  - Samsung Android 16 phone
  - Android 13+ device for direct tile placement
  - Android 7-12 device or emulator for manual tile-placement fallback
- Confirm `WRITE_SETTINGS` behavior survives reboot and tile refresh.
- Confirm the app collects no data and has no accidental network permission.

## Nice To Have Before Public Launch

- Better branded launcher foreground art.
- In-app privacy/permission explanation link.
- Tiny FAQ in README and store listing:
  - why the permission is needed
  - what setting changes
  - how to remove the tile
  - how to uninstall
- Optional website landing page with privacy policy and screenshots.
