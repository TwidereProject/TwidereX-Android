import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose_jb
}

group = Package.group
version = Package.versionName

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose {
    desktop {
        application {
            mainClass = "com.twidere.twiderex.MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
                packageName = "TwidereX"
                packageVersion = Package.versionName.split("-").firstOrNull()
                modules("java.sql") // https://github.com/JetBrains/compose-jb/issues/381
                macOS {
                    infoPlist {
                        extraKeysRawXml = extraInfoPlistKeys
                    }
                }
            }
        }
    }
}
val deeplinkScheme = "twiderex"

val extraInfoPlistKeys = """
  <key>CFBundleURLTypes</key>
  <array>
    <dict>
      <key>CFBundleURLName</key>
      <string>TwidereXDeeplink</string>
      <key>CFBundleURLSchemes</key>
      <array>
        <string>$deeplinkScheme</string>
      </array>
    </dict>
  </array>"""
