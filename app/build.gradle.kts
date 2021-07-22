import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.gradle.kotlin.dsl.support.unzipTo
import org.json.JSONObject
import java.util.Properties

buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")

        if (enableGoogleVariant) {
            // START Non-FOSS component
            classpath("com.google.gms:google-services:4.3.5")
            classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.2")
            // END Non-FOSS component
        }
    }
}

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.protobuf").version("0.8.17")
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

if (enableGoogleVariant) {
    // START Non-FOSS component
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
    // END Non-FOSS component
}
apply(plugin = "dagger.hilt.android.plugin")

android {
    compileSdk = AndroidSdk.compile
    buildToolsVersion = AndroidSdk.buildTools

    defaultConfig {
        applicationId = Package.id
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target
        versionCode = Package.versionCode
        versionName = Package.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas")
            }
        }
        val apiKeyProperties = rootProject.file("apiKey.properties")
        val hasApiKeyProps = apiKeyProperties.exists()
        if (hasApiKeyProps) {
            val apiKeyProp = Properties()
            apiKeyProp.load(apiKeyProperties.inputStream())
            buildConfigField("String", "CONSUMERKEY", apiKeyProp.getProperty("ConsumerKey"))
            buildConfigField("String", "CONSUMERSECRET", apiKeyProp.getProperty("ConsumerSecret"))
        }
    }

    lint {
        disable("MissingTranslation")
    }

    flavorDimensions.add("channel")
    productFlavors {
        if (enableGoogleVariant) {
            // START Non-FOSS component
            create("google") {
                dimension = "channel"
            }
            // END Non-FOSS component
        }
        create("fdroid") {
            dimension = "channel"
        }
    }

    val file = rootProject.file("signing.properties")
    val hasSigningProps = file.exists()

    signingConfigs {
        if (hasSigningProps) {
            create("twidere") {
                val signingProp = Properties()
                signingProp.load(file.inputStream())
                storeFile = rootProject.file(signingProp.getProperty("storeFile"))
                storePassword = signingProp.getProperty("storePassword")
                keyAlias = signingProp.getProperty("keyAlias")
                keyPassword = signingProp.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            if (hasSigningProps) {
                signingConfig = signingConfigs.getByName("twidere")
            }
        }
        release {
            if (hasSigningProps) {
                signingConfig = signingConfigs.getByName("twidere")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    sourceSets.forEach {
        it.res {
            srcDirs(project.files("src/${it.name}/res-localized"))
        }
        it.java {
            srcDirs("src/${it.name}/kotlin")
        }
    }
    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }
    sourceSets {
        findByName("androidTest")?.let {
            it.assets {
                srcDirs(files("$projectDir/schemas"))
            }
        }
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }

    packagingOptions {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/AL2.0",
                    "META-INF/LGPL2.1",
                    "DebugProbesKt.bin",
                )
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.protobuf}"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

// TODO: workaround for https://github.com/google/ksp/issues/518
evaluationDependsOn(":assistedProcessor")
evaluationDependsOn(":routeProcessor")

dependencies {
    android()
    kotlinSerialization()
    kotlinCoroutines()
    implementation(projects.services)
    ksp(projects.assistedProcessor)
    implementation(projects.routeProcessor)
    ksp(projects.routeProcessor)
    compose()
    paging()
    datastore()
    hilt()
    accompanist()
    widget()
    misc()

    if (enableGoogleVariant) {
        // START Non-FOSS component
        val googleImplementation by configurations
        googleImplementation(platform("com.google.firebase:firebase-bom:26.1.0"))
        googleImplementation("com.google.firebase:firebase-analytics-ktx")
        googleImplementation("com.google.firebase:firebase-crashlytics-ktx")
        // END Non-FOSS component
    }

    junit4()
    mockito()
    androidTest()
}

tasks.register("generateTranslation") {
    val localizationFolder = File(rootDir, "localization")
    val appJson = File(localizationFolder, "app.json")
    val target = project.file("src/main/res/values/strings.xml")
    generateLocalization(appJson, target)
}

tasks.register("generateTranslationFromZip") {
    val zip = File(rootProject.buildDir, "Twidere X (translations).zip")
    val unzipTarget = File(rootProject.buildDir, "translation").apply {
        mkdirs()
    }
    unzipTo(unzipTarget, zip)
    unzipTarget.listFiles()?.forEach { file ->
        val source = File(file, "app.json")
        val target = project.file(
            "src/main/res-localized" + "/values-" + file.name.split('_')
                .first() + "-r" + file.name.split('_').last() + "/strings.xml"
        )
        generateLocalization(source, target)
    }
}

fun generateLocalization(appJson: File, target: File) {
    val json = appJson.readText(Charsets.UTF_8)
    val obj = JSONObject(json)
    val result = flattenJson(obj).filter {
        it.value.isNotEmpty() && it.value.isNotBlank()
    }
    if (result.isNotEmpty()) {
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

fun flattenJson(obj: JSONObject): Map<String, String> {
    return obj.toMap().toList().flatMap { it ->
        val (key, value) = it
        when (value) {
            is JSONObject -> {
                flattenJson(value).map {
                    "${key}_${it.key}" to it.value
                }.toList()
            }
            is Map<*, *> -> {
                flattenJson(JSONObject(value)).map {
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
