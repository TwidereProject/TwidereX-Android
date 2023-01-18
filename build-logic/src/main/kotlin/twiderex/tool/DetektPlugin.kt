package twiderex.tool

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import twiderex.detektPlugins
import twiderex.libs

@Suppress("unused")
class DetektPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("io.gitlab.arturbosch.detekt")
      }
      dependencies {
        detektPlugins(libs.findLibrary("twitterComposeRulesDetekt"))
      }
      tasks.withType<Detekt> {
        parallel = true
        config.setFrom(files("${rootProject.rootDir}/detekt.yml"))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
        reports {
          txt {
            required.set(true)
          }
          sarif {
            required.set(false)
          }
          xml {
            required.set(false)
          }
          md {
            required.set(false)
          }
          html {
            required.set(false)
          }
        }
      }
      if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        extensions.configure<DetektExtension> {
          source = files(
            "src/commonMain/kotlin",
            "src/jvmMain/kotlin",
            "src/androidMain/kotlin",
            "src/desktopMain/kotlin",
            "src/darwinMain/kotlin",
            "src/iosMain/kotlin",
            "src/uikitMain/kotlin",
            "src/macosMain/kotlin",
          )
        }
      }
    }
  }
}
