import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "0.3.1"
    id("com.android.library")
    id("kotlin-android-extensions")
}

group = "me.jrein"
version = "1.0"

repositories {
    google()
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("org.jsoup:jsoup:1.13.1")
                api("io.reactivex.rxjava3:rxjava:3.0.13-RC4")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0-RC")
            }
        }
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.3.0-rc01")
                api("androidx.core:core-ktx:1.3.2")
                api("com.google.accompanist:accompanist-glide:0.8.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {

            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }
}