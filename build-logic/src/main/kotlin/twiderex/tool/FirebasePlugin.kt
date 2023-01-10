package twiderex.tool

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import twiderex.implementation

@Suppress("unused")
class FirebasePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.google.gms.google-services")
        apply("com.google.firebase.crashlytics")
      }
      dependencies {
        implementation(platform("com.google.firebase:firebase-bom:30.4.1"))
        implementation("com.google.firebase:firebase-analytics-ktx")
        implementation("com.google.firebase:firebase-crashlytics-ktx")
        implementation("com.google.firebase:firebase-appdistribution-api-ktx:16.0.0-beta04")
      }
    }
  }
}
