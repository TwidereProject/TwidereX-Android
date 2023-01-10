package twiderex.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import setupJvm
import twiderex.kotlin

class ProjectKmpPlugin : Plugin<Project> {
  @Suppress("UNUSED_VARIABLE")
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("twiderex.kmp")
        apply("twiderex.android")
      }
      kotlin {
        android()
        jvm("desktop") {
          setupJvm()
        }
        // ios()
        // macosX64()
        // macosArm64()

        sourceSets.apply {
          val commonMain = getByName("commonMain") {
            dependencies {
            }
          }
          val commonTest = getByName("commonTest") {
            dependencies {
              implementation(kotlin("test"))
            }
          }
          val jvmMain = maybeCreate("jvmMain").apply {
            dependsOn(commonMain)
          }
          val androidMain = getByName("androidMain") {
            dependsOn(jvmMain)
          }
          val desktopMain = getByName("desktopMain") {
            dependsOn(jvmMain)
          }
          // val darwinMain = maybeCreate("darwinMain").apply {
          //   dependsOn(commonMain)
          // }
          // val iosMain = getByName("iosMain") {
          //   dependsOn(darwinMain)
          // }
          // val macosMain = maybeCreate("macosMain").apply {
          //   dependsOn(darwinMain)
          // }
          // val macosTest = maybeCreate("macosTest").apply {
          //   dependsOn(commonTest)
          // }
          // val macosX64Main = getByName("macosX64Main") {
          //   dependsOn(macosMain)
          // }
          // val macosArm64Main = getByName("macosArm64Main") {
          //   dependsOn(macosMain)
          // }
          // val macosX64Test = getByName("macosX64Test") {
          //   dependsOn(macosMain)
          // }
          // val macosArm64Test = getByName("macosArm64Test") {
          //   dependsOn(macosMain)
          // }
        }
      }
    }
  }
}
