import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose_jb
    kotlin("plugin.serialization") version Versions.Kotlin.lang
    id("com.android.library")
    kotlin("kapt")
}

group = Package.group
version = Package.versionName

repositories {
    google()
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(projects.services)
                api("androidx.paging:paging-common:${Versions.paging}")
                api("androidx.datastore:datastore-core:${Versions.datastore}")
                api("androidx.datastore:datastore-preferences-core:${Versions.datastore}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${Versions.Kotlin.serialization}")
                api("io.insert-koin:koin-core:${Versions.koin}")
                implementation("com.twitter.twittertext:twitter-text:3.1.0")
                implementation("org.jsoup:jsoup:1.13.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:${Versions.koin}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}")
                implementation("org.mockito:mockito-core:3.11.2")
                implementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("io.insert-koin:koin-android:${Versions.koin}")
                api("io.insert-koin:koin-androidx-workmanager:${Versions.koin}")
                implementation("androidx.room:room-runtime:${Versions.room}")
                implementation("androidx.room:room-ktx:${Versions.room}")
                implementation("androidx.room:room-paging:${Versions.room}")
                kapt("androidx.room:room-compiler:${Versions.room}")
                implementation("io.coil-kt:coil-base:${Versions.coil}")
            }
        }
        val androidAndroidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("androidx.arch.core:core-testing:2.1.0")
                implementation("androidx.test:core:${Versions.androidx_test}")
                implementation("androidx.test:runner:${Versions.androidx_test}")
                implementation("androidx.test.ext:junit-ktx:${Versions.extJUnitVersion}")
                implementation("androidx.test.espresso:espresso-core:${Versions.espressoVersion}")
                implementation("androidx.room:room-testing:${Versions.room}")
            }
        }
        val desktopMain by getting
        val desktopTest by getting
    }
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kapt(dependencyNotation: String) {
    configurations["kapt"].dependencies.add(project.dependencies.create(dependencyNotation))
}

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["androidTest"].assets.srcDirs("$projectDir/schemas")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    packagingOptions {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/AL2.0",
                    "META-INF/LGPL2.1",
                    "DebugProbesKt.bin",
                    "win32-x86-64/attach_hotspot_windows.dll",
                    "win32-x86/attach_hotspot_windows.dll",
                    "META-INF/licenses/ASM"
                )
            )
        }
    }
}
