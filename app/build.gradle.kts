import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val releaseSigningPropertiesFile = rootProject.file("release-signing.properties")
val releaseSigningProperties = Properties().apply {
    if (releaseSigningPropertiesFile.isFile) {
        releaseSigningPropertiesFile.inputStream().use(::load)
    }
}

fun releaseSigningValue(propertyName: String, environmentName: String): String? =
    releaseSigningProperties.getProperty(propertyName)?.takeIf(String::isNotBlank)
        ?: providers.environmentVariable(environmentName).orNull?.takeIf(String::isNotBlank)

val releaseStoreFile = releaseSigningValue(
    propertyName = "storeFile",
    environmentName = "SCREEN_TIMEOUT_RELEASE_STORE_FILE",
)
val releaseStorePassword = releaseSigningValue(
    propertyName = "storePassword",
    environmentName = "SCREEN_TIMEOUT_RELEASE_STORE_PASSWORD",
)
val releaseKeyAlias = releaseSigningValue(
    propertyName = "keyAlias",
    environmentName = "SCREEN_TIMEOUT_RELEASE_KEY_ALIAS",
)
val releaseKeyPassword = releaseSigningValue(
    propertyName = "keyPassword",
    environmentName = "SCREEN_TIMEOUT_RELEASE_KEY_PASSWORD",
)
val releaseSigningValues = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
)
val hasReleaseSigning = releaseSigningValues.all { it != null }

check(releaseSigningValues.all { it == null } || hasReleaseSigning) {
    "Release signing is partially configured. Provide all four signing values."
}

android {
    namespace = "com.stonecode.screentimeouttile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.stonecode.screentimeouttile"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.1.0"
        manifestPlaceholders["appLabel"] = "Screen Timeout Tile"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(requireNotNull(releaseStoreFile))
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            manifestPlaceholders["appLabel"] = "Screen Timeout Tile (Debug)"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
