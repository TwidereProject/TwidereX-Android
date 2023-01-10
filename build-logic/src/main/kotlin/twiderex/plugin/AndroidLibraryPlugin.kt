package twiderex.plugin

import Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import twiderex.android

class AndroidLibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
      }
      android {
        compileSdk = Versions.Android.compile
        defaultConfig {
          minSdk = Versions.Android.min
          targetSdk = Versions.Android.target
          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          consumerProguardFiles("consumer-rules.pro")
        }
        compileOptions {
          sourceCompatibility = Versions.Java.java
          targetCompatibility = Versions.Java.java
        }
        @Suppress("UnstableApiUsage")
        sourceSets {
          getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
          }
          getByName("debug") {
            java.srcDir("build/generated/ksp/android/androidDebug/kotlin")
          }
        }
      }
    }
  }
}
