import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose_jb
    kotlin("plugin.serialization") version Versions.Kotlin.lang
    id("com.android.library")
    kotlin("kapt")
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

// TODO: workaround for https://github.com/google/ksp/issues/518
evaluationDependsOn(":routeProcessor")

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
                implementation("org.jsoup:jsoup:1.13.1")
                implementation(projects.routeProcessor)
                ksp(projects.routeProcessor)
                implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}")
                api("dev.icerock.moko:resources:${Versions.moko}")
                implementation("app.cash.turbine:turbine:0.6.1")
                implementation("ca.gosyer:accompanist-pager:${Versions.accompanist_jb}")
                implementation("ca.gosyer:accompanist-pager-indicators:${Versions.accompanist_jb}")
                api("com.github.Tlaster.KFilePicker:KFilePicker:1.0.2")
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
                implementation("androidx.core:core-ktx:1.7.0-rc01")
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
                implementation("com.google.android.exoplayer:exoplayer:${Versions.exoplayer}")
                implementation("com.google.android.exoplayer:extension-okhttp:${Versions.exoplayer}")
                implementation("com.squareup.sqldelight:android-driver:${Versions.sqlDelight}")
                implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")
                implementation("androidx.datastore:datastore:${Versions.datastore}")
                implementation("androidx.datastore:datastore-preferences:${Versions.datastore}")
                implementation("androidx.exifinterface:exifinterface:${Versions.androidx_exifinterface}")
                implementation("androidx.startup:startup-runtime:${Versions.startup}")
                implementation("com.google.accompanist:accompanist-insets:${Versions.accompanist}")
                implementation("androidx.browser:browser:${Versions.browser}")
                implementation("androidx.vectordrawable:vectordrawable:1.1.0")
                implementation("androidx.activity:activity-compose:1.4.0-rc01")
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
        val androidTest by getting
        val desktopMain by getting {
            dependencies {
                implementation("uk.co.caprica:vlcj:4.7.1")
                implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")
                implementation("de.huxhorn.lilith:de.huxhorn.lilith.3rdparty.junique:1.0.4")
                implementation("org.javassist:javassist:3.28.0-GA")
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

gradle.taskGraph.afterTask {
    if(name == "generateMRdesktopMain") {
        println("Renaming desktop resources...")
        val resources = project.file("../common/build/generated/moko/desktopMain/comtwideretwiderex/res/localization/")
        if (resources.exists()) {
            resources.listFiles().forEach { file ->
                if (file.name.contains("-r")) file.renameTo(File(file.path.replace("-r", "_")))
            }
        }
    }
}
