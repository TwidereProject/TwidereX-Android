package twiderex.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import twiderex.compose
import twiderex.kotlin

class ProjectKmpComposePlugin : Plugin<Project> {
  @Suppress("UNUSED_VARIABLE")
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.compose")
        apply("twiderex.project.kmp")
        apply("twiderex.detekt")
      }
      kotlin {
        sourceSets.apply {
          val commonMain = getByName("commonMain") {
            dependencies {
              api(compose.ui)
              api(compose.runtime)
              api(compose.foundation)
              api(compose.material)
              api(compose.materialIconsExtended)
            }
          }
          val androidMain = getByName("androidMain") {
            dependencies {
            }
          }
        }
      }
    }
  }
}
