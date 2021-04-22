plugins {
    kotlin("jvm")
}

group = "com.twidere"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
