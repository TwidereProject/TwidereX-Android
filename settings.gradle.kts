pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    // https://youtrack.jetbrains.com/issue/KT-51379
    // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

include(
    ":android",
    ":desktop",
    ":common",
    ":services",
)
includeBuild("build-logic")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "TwidereX"
