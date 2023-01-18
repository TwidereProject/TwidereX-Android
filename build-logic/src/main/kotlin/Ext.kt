import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

val Project.enableGoogleVariant: Boolean
  get() = file("google-services.json").exists()

fun KotlinJvmTarget.setupJvm() {
  compilations.all {
    kotlinOptions.jvmTarget = Versions.Java.jvmTarget
  }
}

fun DependencyHandlerScope.kspAll(dependencyNotation: Any) {
  add("kspCommonMainMetadata", dependencyNotation)
  add("kspAndroid", dependencyNotation)
  add("kspDesktop", dependencyNotation)
  // add("kspIosArm64", dependencyNotation)
  // add("kspIosX64", dependencyNotation)
  // add("kspMacosArm64", dependencyNotation)
  // add("kspMacosX64", dependencyNotation)
}

fun DependencyHandlerScope.kspAndroid(dependencyNotation: Any) {
  add("kspAndroid", dependencyNotation)
}
