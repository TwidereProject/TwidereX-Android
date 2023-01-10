import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("twiderex.android.application")
}

dependencies {
    implementation(projects.common)
    implementation(libs.androidx.startup)
}

android {
    lint {
        disable.add("MissingTranslation")
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
            isMinifyEnabled = true
            isShrinkResources = true
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
