// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.diffplug.spotless") version Versions.spotless
    id("com.github.ben-manes.versions") version "0.44.0"
    id("com.dipien.byebyejetifier") version "1.2.2"
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin.lang))
        classpath("com.android.tools.build:gradle:${Versions.agp}")
    }
}

allprojects {
    configRepository()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.Java.jvmTarget
            allWarningsAsErrors = true
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all",
                "-Xskip-prerelease-check"
            )
        }
    }
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "buildSrc/**/*.kt")
            ktlint(Versions.ktlint).editorConfigOverride(
                mapOf(
                    "indent_size" to 2,
                    "continuation_indent_size" to 2,
                    // rules: https://github.com/pinterest/ktlint/blob/master/README.md#standard-rules
                    "disabled_rules" to "filename,enum-entry-name-case,trailing-comma"
                )
            )
            licenseHeaderFile(rootProject.file("spotless/license"))
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(Versions.ktlint)
        }
        java {
            target("**/*.java")
            targetExclude("$buildDir/**/*.java", "bin/**/*.java")
            licenseHeaderFile(rootProject.file("spotless/license"))
        }
    }

    configurations.all {
        resolutionStrategy {
            force("org.objenesis:objenesis:3.2")
        }
    }

    tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        rejectVersionIf {
            candidate.version.isNonStable() && !currentVersion.isNonStable()
        }
        checkForGradleUpdate = true
        outputFormatter = "text"
        outputDir = project.rootProject.buildDir.resolve("reports/dependency-updates").absolutePath
        reportfileName = "report"
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
                )
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
                )
            }
        }
    }
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase(java.util.Locale.US).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}
