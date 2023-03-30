package twiderex.tool

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Locale
import org.gradle.kotlin.dsl.withType

@Suppress("unused")
class VersionsCheckPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.github.ben-manes.versions")

      tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
          candidate.version.isNonStable() && !currentVersion.isNonStable()
        }
        checkForGradleUpdate = true
        outputFormatter = "text"
        outputDir = project.rootProject.buildDir.resolve("reports/dependency-updates").absolutePath
        reportfileName = "report"
      }
    }
  }
}

private fun String.isNonStable(): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase(Locale.US).contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(this)
  return isStable.not()
}
