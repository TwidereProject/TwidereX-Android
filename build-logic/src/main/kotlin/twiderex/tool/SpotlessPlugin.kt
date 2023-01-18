package twiderex.tool

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import twiderex.libs

@Suppress("unused")
class SpotlessPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.diffplug.spotless")

      extensions.configure<SpotlessExtension> {
        kotlin {
          target("**/*.kt")
          targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "build-logic/**/*.kt")
          ktlint(libs.findVersion("ktlint").get().toString())
            .editorConfigOverride(
              mapOf(
                "indent_size" to 2,
                "continuation_indent_size" to 2,
                // rules: https://github.com/pinterest/ktlint/blob/master/README.md#standard-rules
                "disabled_rules" to "filename,enum-entry-name-case,trailing-comma",
              ),
            )
          licenseHeaderFile(rootProject.file("spotless/license"))
        }
        kotlinGradle {
          target("**/*.kts")
          targetExclude("**/build/**/*.kts")
        }
      }
    }
  }
}
