plugins {
    id("org.jetbrains.compose") version "0.3.1"
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

group = "me.jrein"
version = "1.0"

repositories {
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("com.github.jakepurple13.HelpfulTools:gsonutils:10.6.3")
    implementation(kotlin("test-junit"))
    implementation("junit:junit:4.13.2")
    implementation("com.google.code.gson:gson:2.8.6")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "me.jrein.android"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}