import org.jetbrains.compose.desktop.application.dsl.TargetFormat

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("twiderex.kmp.compose")
    alias(libs.plugins.javafx)
}

group = Package.group
version = Package.versionName

kotlin {
    jvm {
        setupJvm()
        withJava()
        javafx {
            version = Versions.Java.java.toString()
            modules = listOf("javafx.controls", "javafx.swing", "javafx.media")
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.common)
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
            jvmArgs += listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
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
