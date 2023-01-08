plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.gradlePlugin.android)
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.compose)

    implementation(libs.gradlePlugin.spotless)
    implementation(libs.gradlePlugin.detekt)
    implementation(libs.gradlePlugin.versionsCheck)

    implementation(libs.gradlePlugin.gms)
    implementation(libs.gradlePlugin.firebase)
    implementation(libs.gradlePlugin.firebaseAppdistribution)
}

gradlePlugin {
    plugins {
        register("kmp") {
            id = "twiderex.kmp"
            implementationClass = "twiderex.plugin.KmpPlugin"
        }
        register("kmp.compose") {
            id = "twiderex.kmp.compose"
            implementationClass = "twiderex.plugin.KmpComposePlugin"
        }
        register("android") {
            id = "twiderex.android"
            implementationClass = "twiderex.plugin.AndroidLibraryPlugin"
        }
        register("android.application") {
            id = "twiderex.android.application"
            implementationClass = "twiderex.plugin.AndroidApplicationPlugin"
        }
        register("project.kmp") {
            id = "twiderex.project.kmp"
            implementationClass = "twiderex.plugin.ProjectKmpPlugin"
        }
        register("project.kmp.compose") {
            id = "twiderex.project.kmp.compose"
            implementationClass = "twiderex.plugin.ProjectKmpComposePlugin"
        }
        //
        // // Tool
        //
        register("spotless") {
            id = "twiderex.spotless"
            implementationClass = "twiderex.tool.SpotlessPlugin"
        }
        register("detekt") {
            id = "twiderex.detekt"
            implementationClass = "twiderex.tool.DetektPlugin"
        }
        register("versionsCheck") {
            id = "twiderex.versionsCheck"
            implementationClass = "twiderex.tool.VersionsCheckPlugin"
        }
        register("firebase") {
            id = "twiderex.firebase"
            implementationClass = "twiderex.tool.FirebasePlugin"
        }
        register("composeMetrics") {
            id = "twiderex.composeMetrics"
            implementationClass = "twiderex.tool.ComposeMetricsPlugin"
        }
        // register("composeMendable") {
        //     id = "twiderex.mendableBuild"
        //     implementationClass = "twiderex.tool.MendableBuildPlugin"
        // }
    }
}
