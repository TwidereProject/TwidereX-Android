@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("twiderex.project.kmp")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

group = Package.group
version = Package.versionName

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bundles.kotlinx)
                implementation(libs.bundles.reftrofit2)
                implementation(libs.bundles.ktor)
                implementation(libs.square.okhttp)
                implementation("com.github.Tlaster:Hson:0.1.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    namespace = "${Package.id}.services"
}
