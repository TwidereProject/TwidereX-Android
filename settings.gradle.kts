pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "TwidereX"
include(":app", ":services", ":assistedProcessor", ":routeProcessor")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
