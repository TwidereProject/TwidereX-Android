
import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose_jb
    kotlin("plugin.serialization") version Versions.Kotlin.lang
    id("com.android.library")
    id("com.google.devtools.ksp").version(Versions.ksp)
    id("dev.icerock.mobile.multiplatform-resources") version Versions.moko
    id("com.squareup.sqldelight")
    id("com.codingfeline.buildkonfig") version "0.11.0"
}

sqldelight {
    database("SqlDelightAppDatabase") {
        packageName = "${Package.id}.sqldelight"
        sourceFolders = listOf("sqldelight/app")
    }
    database("SqlDelightCacheDatabase") {
        packageName = "${Package.id}.sqldelight"
        sourceFolders = listOf("sqldelight/cache")
    }
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
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                implementation(projects.services)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
                api("androidx.paging:paging-common:${Versions.paging}")
                api("androidx.datastore:datastore-core:${Versions.datastore}")
                api("androidx.datastore:datastore-preferences-core:${Versions.datastore}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${Versions.Kotlin.serialization}")
                api("io.insert-koin:koin-core:${Versions.koin}")
                implementation("com.twitter.twittertext:twitter-text:3.1.0")
                implementation("org.jsoup:jsoup:1.14.3")
                implementation(projects.routeProcessor)
                kspAll(projects.routeProcessor)
                implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}")
                api("dev.icerock.moko:resources:${Versions.moko}")
                implementation("app.cash.turbine:turbine:0.7.0")
                implementation("ca.gosyer:accompanist-pager:${Versions.accompanist_jb}")
                implementation("ca.gosyer:accompanist-pager-indicators:${Versions.accompanist_jb}")
                api("com.github.Tlaster.KFilePicker:KFilePicker:${Versions.kFilePicker}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:${Versions.koin}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}")
                implementation("io.mockk:mockk:1.12.1")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
                implementation("androidx.savedstate:savedstate-ktx:1.1.0")
                implementation("androidx.core:core-ktx:1.8.0-alpha02")
                implementation("io.insert-koin:koin-android:${Versions.koin}")
                implementation("io.insert-koin:koin-androidx-workmanager:${Versions.koin}")
                implementation("androidx.room:room-runtime:${Versions.room}")
                implementation("androidx.room:room-ktx:${Versions.room}")
                implementation("androidx.room:room-paging:${Versions.room}")
                kspAndroid("androidx.room:room-compiler:${Versions.room}")
                implementation("io.coil-kt:coil-base:${Versions.coil}")
                implementation("io.coil-kt:coil-compose:${Versions.coil}")
                implementation("io.coil-kt:coil-gif:${Versions.coil}")
                implementation("io.coil-kt:coil-svg:${Versions.coil}")
                implementation("com.google.android.exoplayer:exoplayer:${Versions.exoplayer}")
                implementation("com.google.android.exoplayer:extension-okhttp:${Versions.exoplayer}")
                implementation("androidx.datastore:datastore:${Versions.datastore}")
                implementation("androidx.datastore:datastore-preferences:${Versions.datastore}")
                implementation("androidx.exifinterface:exifinterface:${Versions.androidx_exifinterface}")
                implementation("androidx.startup:startup-runtime:${Versions.startup}")
                implementation("com.google.accompanist:accompanist-insets:${Versions.accompanist}")
                implementation("androidx.browser:browser:${Versions.browser}")
                implementation("androidx.vectordrawable:vectordrawable:1.2.0-alpha02")
                implementation("androidx.activity:activity-compose:${Versions.activity}")
                implementation("com.github.android:renderscript-intrinsics-replacement-toolkit:b6363490c3")
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
                implementation("com.squareup.sqldelight:android-driver:${Versions.sqlDelight}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("uk.co.caprica:vlcj:4.7.1")
                implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")
                implementation("de.huxhorn.lilith:de.huxhorn.lilith.3rdparty.junique:1.0.4")
                implementation("org.javassist:javassist:3.28.0-GA")
                implementation("org.ocpsoft.prettytime:prettytime:5.0.2.Final")
            }
        }
        val desktopTest by getting
    }
}

buildkonfig {
    packageName = Package.id
    objectName = "BuildConfig"
    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "VERSION_NAME", Package.versionName)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "APPLICATION_ID", Package.id)
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "APPLICATION_NAME", Package.name)
        val apiKeyProperties = rootProject.file("apiKey.properties")
        val hasApiKeyProps = apiKeyProperties.exists()
        if (hasApiKeyProps) {
            val apiKeyProp = loadProperties(apiKeyProperties.absolutePath)
            buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "CONSUMERKEY", apiKeyProp.getProperty("ConsumerKey"))
            buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "CONSUMERSECRET", apiKeyProp.getProperty("ConsumerSecret"))
            buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "GIPHYKEY", apiKeyProp.getProperty("GiphyKey"))
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = Package.id
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kspAll(dependencyNotation: Any) {
    kspAndroid(dependencyNotation)
    kspDesktop(dependencyNotation)
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kspDesktop(dependencyNotation: Any) {
    configurations["kspDesktop"].dependencies.add(project.dependencies.create(dependencyNotation))
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kspAndroid(dependencyNotation: Any) {
    configurations["kspAndroid"].dependencies.add(project.dependencies.create(dependencyNotation))
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
    }

    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
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

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
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

val updateJvmLocalizationFileName by tasks.registering {
    doLast {
        val generatedDir = File(project.buildDir, "generated/moko")
        generatedDir.walkTopDown().filter { file ->
            file.name.endsWith(".properties")
        }.forEach {
            it.renameTo(File(it.parent, it.name.replace("-r", "_")))
        }
    }
}

afterEvaluate {
    tasks.getByName("generateMRdesktopMain").finalizedBy(updateJvmLocalizationFileName)
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
