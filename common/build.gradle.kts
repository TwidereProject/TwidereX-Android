import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose_jb
    kotlin("plugin.serialization") version Versions.Kotlin.lang
    id("com.android.library")
    kotlin("kapt")
    id("com.google.devtools.ksp").version(Versions.ksp)
    id("dev.icerock.mobile.multiplatform-resources") version Versions.moko
}

group = Package.group
version = Package.versionName

repositories {
    google()
}

// TODO: workaround for https://github.com/google/ksp/issues/518
evaluationDependsOn(":routeProcessor")

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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
                api("androidx.paging:paging-common:${Versions.paging}")
                api("androidx.datastore:datastore-core:${Versions.datastore}")
                api("androidx.datastore:datastore-preferences-core:${Versions.datastore}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${Versions.Kotlin.serialization}")
                api("io.insert-koin:koin-core:${Versions.koin}")
                implementation("com.twitter.twittertext:twitter-text:3.1.0")
                implementation("org.jsoup:jsoup:1.13.1")
                implementation(projects.routeProcessor)
                ksp(projects.routeProcessor)
                api("dev.icerock.moko:resources:${Versions.moko}")
                implementation("app.cash.turbine:turbine:0.6.1")
                implementation("ca.gosyer:accompanist-pager:${Versions.accompanist_jb}")
                implementation("ca.gosyer:accompanist-pager-indicators:${Versions.accompanist_jb}")
                api("com.github.Tlaster.KFilePicker:KFilePicker:1.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:${Versions.koin}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}")
                implementation("io.mockk:mockk:1.12.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
                implementation("androidx.savedstate:savedstate-ktx:1.1.0")
                implementation("androidx.core:core-ktx:1.7.0-alpha01")
                implementation("io.insert-koin:koin-android:${Versions.koin}")
                implementation("io.insert-koin:koin-androidx-workmanager:${Versions.koin}")
                implementation("androidx.room:room-runtime:${Versions.room}")
                implementation("androidx.room:room-ktx:${Versions.room}")
                implementation("androidx.room:room-paging:${Versions.room}")
                kapt("androidx.room:room-compiler:${Versions.room}")
                implementation("io.coil-kt:coil-base:${Versions.coil}")
                implementation("io.coil-kt:coil-compose:${Versions.coil}")
                implementation("io.coil-kt:coil-gif:${Versions.coil}")
                implementation("io.coil-kt:coil-svg:${Versions.coil}")
                implementation("androidx.datastore:datastore:${Versions.datastore}")
                implementation("androidx.datastore:datastore-preferences:${Versions.datastore}")
                implementation("androidx.exifinterface:exifinterface:${Versions.androidx_exifinterface}")
                implementation("androidx.startup:startup-runtime:${Versions.startup}")
                implementation("com.google.accompanist:accompanist-insets:${Versions.accompanist}")
                implementation("androidx.browser:browser:${Versions.browser}")
            }
        }
        val androidAndroidTest by getting {
            dependencies {
                implementation("androidx.arch.core:core-testing:2.1.0")
                implementation("androidx.test:core:${Versions.androidx_test}")
                implementation("androidx.test:runner:${Versions.androidx_test}")
                implementation("androidx.test.ext:junit-ktx:${Versions.extJUnitVersion}")
                implementation("androidx.test.espresso:espresso-core:${Versions.espressoVersion}")
                implementation("androidx.room:room-testing:${Versions.room}")
            }
        }
        val desktopMain by getting {
            dependencies {
            }
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = Package.id
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kapt(dependencyNotation: String) {
    configurations["kapt"].dependencies.add(project.dependencies.create(dependencyNotation))
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.ksp(dependencyNotation: org.gradle.accessors.dm.RouteProcessorProjectDependency) {
    configurations["ksp"].dependencies.add(projects.routeProcessor)
}

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["androidTest"].assets.srcDirs("$projectDir/schemas")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["notPackage"] = "com.twidere.twiderex.viewmodel"

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

tasks.create("generateTranslation") {
    doLast {
        val localizationFolder = File(rootDir, "localization")
        val appJson = File(localizationFolder, "app.json")
        val target = project.file("src/commonMain/resources/MR/base/strings.xml").apply {
            ensureParentDirsCreated()
            if (!exists()) {
                createNewFile()
            }
        }
        generateLocalization(appJson, target)
    }
}

tasks.create("generateTranslationFromZip") {
    doLast {
        val zip = File(rootProject.buildDir, "Twidere X (translations).zip")
        val unzipTarget = rootProject.buildDir
        org.gradle.kotlin.dsl.support.unzipTo(unzipTarget, zip)
        File(unzipTarget, "translation").listFiles()?.forEach { file ->
            val source = File(file, "app.json")
            val target = project.file(
                "src/commonMain/resources/MR/" + file.name.split('_')
                    .first() + "-r" + file.name.split('_').last() + "/strings.xml"
            )
            generateLocalization(source, target)
        }
    }
}

fun generateLocalization(appJson: File, target: File) {
    val json = appJson.readText(Charsets.UTF_8)
    val obj = org.json.JSONObject(json)
    val result = flattenJson(obj).filter {
        it.value.isNotEmpty() && it.value.isNotBlank()
    }
    if (result.isNotEmpty()) {
        target.apply {
            ensureParentDirsCreated()
            if (!exists()) {
                createNewFile()
            }
        }
        val xml =
            """<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">""" + System.lineSeparator() +
                result.map {
                    "    <string name=\"${it.key}\">${
                    it.value.replace("'", "\\'").replace(System.lineSeparator(), "\\n")
                    }</string>"
                }.joinToString(System.lineSeparator()) + System.lineSeparator() +
                "</resources>"
        target.writeText(xml)
    }
}

fun flattenJson(obj: org.json.JSONObject): Map<String, String> {
    return obj.toMap().toList().flatMap { it ->
        val (key, value) = it
        when (value) {
            is org.json.JSONObject -> {
                flattenJson(value).map {
                    "${key}_${it.key}" to it.value
                }.toList()
            }
            is Map<*, *> -> {
                flattenJson(org.json.JSONObject(value)).map {
                    "${key}_${it.key}" to it.value
                }.toList()
            }
            is String -> {
                listOf(key to value)
            }
            else -> {
                listOf(key to value.toString())
            }
        }
    }.toMap()
}
