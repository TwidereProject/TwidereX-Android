package twiderex.plugin

import Package
import Versions
import enableGoogleVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import twiderex.androidApplication
import twiderex.implementation
import twiderex.libs

class AndroidApplicationPlugin : Plugin<Project> {
  @Suppress("KotlinConstantConditions")
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
        apply("org.jetbrains.compose")
        apply("twiderex.spotless")
        apply("twiderex.detekt")
        if (enableGoogleVariant) {
          apply("twiderex.firebase")
        }
      }
      dependencies {
        implementation(libs.findLibrary("androidx.activity.compose"))
        if (Versions.Android.min < 26) {
          add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:1.2.0")
        }
      }
      androidApplication {
        compileSdk = Versions.Android.compile
        defaultConfig {
          applicationId = Package.id
          minSdk = Versions.Android.min
          targetSdk = Versions.Android.target
          versionCode = Package.versionCode
          versionName = Package.versionName
          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
          sourceCompatibility = Versions.Java.java
          targetCompatibility = Versions.Java.java
        }
        if (Versions.Android.min < 26) {
          compileOptions {
            isCoreLibraryDesugaringEnabled = true
          }
        }
        if (!enableGoogleVariant) {
          @Suppress("UnstableApiUsage")
          sourceSets {
            getByName("main") {
              java.srcDir("src/main/debug")
            }
          }
        }
      }
    }
  }
}
