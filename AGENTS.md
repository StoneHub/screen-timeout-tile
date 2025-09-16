# Repository Guidelines

## Project Structure & Module Organization
The Android project lives at the root `screenTimeoutTile` with the single `app` module. Application code and resources belong under `app/src/main`, split into `java` for Kotlin sources and `res` for drawables, mipmaps, strings, and XML service definitions. Unit tests reside in `app/src/test/java`, while instrumentation tests target `app/src/androidTest/java`. Build scripts are Kotlin DSL (`build.gradle.kts` in the root and module) backed by the version catalog in `gradle/libs.versions.toml`.

## Build, Test, and Development Commands
Use `./gradlew assembleDebug` for a fast debug APK and `./gradlew build` when you need lint, unit tests, and packaging together. Run JVM unit tests with `./gradlew test` and device or emulator suites with `./gradlew connectedAndroidTest`. During active development, `./gradlew installDebug` pushes the tile build to a connected device for manual checks.

## Coding Style & Naming Conventions
Write Kotlin using the official style: four-space indentation, trailing commas for multiline collections, and expression-bodied functions when they improve clarity. Name classes and files in PascalCase (e.g., `ScreenTimeoutTileService.kt`), functions and properties in camelCase, and XML resources in lowercase snake_case (e.g., `res/xml/service_config.xml`). Keep imports sorted automatically and rely on Android Studio’s “Reformat Code” and “Optimize Imports” before committing.

## Testing Guidelines
Add JVM tests alongside production packages in `app/src/test` using JUnit4, naming methods in the `shouldDoSomething_whenCondition` form. Instrumentation and UI checks belong in `app/src/androidTest` with descriptive `@Test` display names. All new tile behaviors need at least one unit test covering edge cases plus instrumentation coverage when user interaction is involved. Run `./gradlew test connectedAndroidTest` before submitting significant changes.

## Commit & Pull Request Guidelines
The current snapshot lacks Git history, so adopt Conventional Commit prefixes (`feat:`, `fix:`, `chore:`) for clarity. Keep the first line under 72 characters in the imperative mood and link issues using `Refs #123` in the body. Pull requests should summarize functional changes, call out affected components (e.g., tile service, resources), include screenshots or screen recordings when UI changes occur, and list verification steps such as emulator/API levels tested.
