import java.util.Properties

buildscript {
    repositories {
        google()
    }

    dependencies {

        if (file("google-services.json").exists()) {
            // START Non-FOSS component
            classpath("com.google.gms:google-services:4.3.14")
            classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
            // END Non-FOSS component
        }
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("twiderex.android.application")
}

if (file("google-services.json").exists()) {
    // START Non-FOSS component
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
    // END Non-FOSS component
}

android {
    lint {
        disable.add("MissingTranslation")
    }
    flavorDimensions.add("channel")
    productFlavors {
        if (file("google-services.json").exists()) {
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
            manifestPlaceholders.apply {
                put("appIcon", "@mipmap/ic_launcher")
                put("appIconRound", "@mipmap/ic_launcher_round")
            }
        }
        release {
            if (hasSigningProps) {
                signingConfig = signingConfigs.getByName("twidere")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders.apply {
                put("appIcon", "@mipmap/ic_launcher")
                put("appIconRound", "@mipmap/ic_launcher_round")
            }
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
    packagingOptions {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/AL2.0",
                    "META-INF/LGPL2.1",
                    "DebugProbesKt.bin"
                )
            )
        }
    }
}

dependencies {
    implementation(projects.common)
    implementation(libs.androidx.startup)
    if (file("google-services.json").exists()) {
        // START Non-FOSS component
        val googleImplementation by configurations
        googleImplementation(platform("com.google.firebase:firebase-bom:31.1.1"))
        googleImplementation("com.google.firebase:firebase-analytics-ktx")
        googleImplementation("com.google.firebase:firebase-crashlytics-ktx")
        googleImplementation("com.google.android.play:core-ktx:1.8.1")
        // END Non-FOSS component
    }
}
