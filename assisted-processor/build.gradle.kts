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
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.32-1.0.0-alpha07")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
