plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.pipefall.pressure"
    compileSdk = 35
    buildToolsVersion = "35.0.1"

    defaultConfig {
        applicationId = "com.pipefall.pressure"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        buildConfig = false
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
