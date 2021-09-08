
import java.util.Properties

buildscript {
    repositories {
        google()
    }

    dependencies {

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
    id("org.jetbrains.compose").version(Versions.compose_jb)
    kotlin("plugin.serialization").version(Versions.Kotlin.lang)
    id("com.google.devtools.ksp").version(Versions.ksp)
}

if (enableGoogleVariant) {
    // START Non-FOSS component
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
    // END Non-FOSS component
}

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

// TODO: workaround for https://github.com/google/ksp/issues/518
evaluationDependsOn(":assistedProcessor")
evaluationDependsOn(":routeProcessor")

dependencies {
    android()
    kotlinSerialization()
    kotlinCoroutines()
    implementation(projects.services)
    implementation(projects.common)
    // ksp(projects.assistedProcessor)
    implementation(projects.routeProcessor)
    ksp(projects.routeProcessor)
    compose()
    paging()
    datastore()
    accompanist()
    widget()
    misc()

    if (enableGoogleVariant) {
        // START Non-FOSS component
        val googleImplementation by configurations
        googleImplementation(platform("com.google.firebase:firebase-bom:26.1.0"))
        googleImplementation("com.google.firebase:firebase-analytics-ktx")
        googleImplementation("com.google.firebase:firebase-crashlytics-ktx")
        googleImplementation("com.google.android.play:core-ktx:1.8.1")
        // END Non-FOSS component
    }

    junit4()
    mockito()
    androidTest()
}
