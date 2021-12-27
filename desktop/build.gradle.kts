import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.compose_jb
    id("org.openjfx.javafxplugin") version Versions.javafx
}

group = Package.group
version = Package.versionName

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
        javafx {
            version = "15" // MediaPlayer doesn't work well with 11, use Versions.Java.jvmTarget after Versions.Java.jvmTarget updated
            modules = listOf("javafx.controls", "javafx.swing", "javafx.media")
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
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = Package.name
                packageVersion = "${Package.Version.main}.${Package.Version.mirror}.${Package.versionCode}"
                copyright = Package.copyright
                licenseFile.set(rootProject.file("LICENSE"))
                modules("java.sql") // https://github.com/JetBrains/compose-jb/issues/381
                modules("jdk.unsupported")
                modules("jdk.unsupported.desktop")
                macOS {
                    bundleID = Package.id
                    infoPlist {
                        extraKeysRawXml = macExtraPlistKeys
                    }
                    iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.icns"))
                }
                linux {
                    iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.png"))
                }
                windows {
                    shortcut = true
                    menu = true
                    iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.ico"))
                }
            }
        }
    }
}
// register deeplinks
val macExtraPlistKeys: String
    get() = """
      <key>CFBundleURLTypes</key>
      <array>
        <dict>
          <key>CFBundleURLName</key>
          <string>TwidereXUrl</string>
          <key>CFBundleURLSchemes</key>
          <array>
            <string>twiderex</string>
          </array>
        </dict>
      </array>
    """
