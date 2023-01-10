package twiderex.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpComposePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.compose")
        apply("twiderex.kmp")
        apply("twiderex.detekt")
      }
    }
  }
}
