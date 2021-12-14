// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.diffplug.spotless").version(Versions.spotless)
    id("com.github.ben-manes.versions").version("0.39.0")
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin.lang))
        classpath("com.android.tools.build:gradle:${Versions.agp}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}")
    }
}

allprojects {
    configRepository()

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.Java.jvmTarget
            allWarningsAsErrors = true
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all",
            )
        }
    }
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "buildSrc/**/*.kt")
            ktlint(Versions.ktlint)
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
}
