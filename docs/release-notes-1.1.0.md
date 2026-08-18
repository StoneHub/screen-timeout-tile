# Screen Timeout Tile 1.1.0

- First signed public GitHub APK release.
- Adds an in-app legend explaining the 10-minute, 30-second, and permission icons.
- Keeps missing or unreadable timeout values visibly unavailable instead of reporting a false 30-second value.
- Explains the narrow use of Android’s Modify system settings access.
- Handles OEM failures when opening system settings or requesting tile placement without crashing.
- Shows explicit tile errors when a timeout write fails.
- Improves Quick Settings accessibility state and Android 7-9 labels.
- Adds isolated debug installs, optional local upload-key signing, CI, and Gradle wrapper checksum verification.
