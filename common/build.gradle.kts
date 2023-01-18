import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.konan.properties.loadProperties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("twiderex.project.kmp.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.multiplatformResources)
}

group = Package.group
version = Package.versionName

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

kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/commonMain/third")
            dependencies {
                implementation(projects.services)
                api(libs.bundles.compose)
                implementation(libs.bundles.kotlinx)
                implementation(libs.bundles.reftrofit2)
                implementation(libs.bundles.androidx.common)
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.square.okhttp)
                api(libs.square.okio)
                api(libs.koin.core)
                api(libs.mokoResources)
                api(libs.kfilepicker)
                api(libs.twitterParser)
                api(libs.cache4k)
                api(libs.napier)
                implementation("com.twitter.twittertext:twitter-text:3.1.0")
                implementation("org.jsoup:jsoup:1.15.3")
                implementation("app.cash.turbine:turbine:0.12.1")
                implementation("com.eygraber:uri-kmp:0.0.9")
                api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
            }
            configurations.all {
                // some dependencies contains it, this causes an exception to initialize the Main dispatcher in desktop
                exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-android")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation("io.mockk:mockk:1.13.3")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.bundles.androidx)
                implementation(libs.bundles.room)
                implementation(libs.bundles.exoplayer)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.workmanager)
                implementation(libs.compose.accompanist.permissions)
                implementation(libs.androidx.work.rumtime.ktx)
                implementation("com.github.android:renderscript-intrinsics-replacement-toolkit:b6363490c3")
            }
        }
        val androidAndroidTest by getting {
            dependencies {
                implementation(libs.bundles.test.android)
                implementation(libs.sqldelight.android.driver)
                implementation(libs.room.test)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.sqldelight.sqlite.driver)
                implementation("uk.co.caprica:vlcj:4.8.2")
                implementation("de.huxhorn.lilith:de.huxhorn.lilith.3rdparty.junique:1.0.4")
                implementation("org.javassist:javassist:3.29.2-GA")
                implementation("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
                implementation("com.mayakapps.compose:window-styler:0.3.2")
            }
        }
        val desktopTest by getting
    }
}

dependencies {
    kspAll(libs.compose.precompose.ksp)
    kspAndroid(libs.room.ksp)
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
        val localProperties = rootProject.file("local.properties")
        if (localProperties.exists()) {
            val localProp = loadProperties(localProperties.absolutePath)
            buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "Debug", localProp.getProperty("debug", "false"))
        } else {
            buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "Debug", "false")
        }
    }
}

android {
    sourceSets["androidTest"].assets.srcDirs("$projectDir/schemas")
    defaultConfig {
        testInstrumentationRunnerArguments["notPackage"] = "com.twidere.twiderex.viewmodel"
    }

    lint {
        disable.add("MissingTranslation")
        disable.add("JavascriptInterface")
    }

    packagingOptions {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/*",
                    "DebugProbesKt.bin",
                    "win32-x86-64/attach_hotspot_windows.dll",
                    "win32-x86/attach_hotspot_windows.dll"
                )
            )
        }
    }

    // @see https://github.com/icerockdev/moko-resources/issues/353
    sourceSets["main"].apply {
        assets.srcDir(File(buildDir, "generated/moko/androidMain/assets"))
        res.srcDir(File(buildDir, "generated/moko/androidMain/res"))
    }
}

multiplatformResources {
    multiplatformResourcesPackage = Package.id
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
